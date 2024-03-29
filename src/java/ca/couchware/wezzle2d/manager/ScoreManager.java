package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.event.GameEvent;
import ca.couchware.wezzle2d.event.IGameListener;
import ca.couchware.wezzle2d.event.ILevelListener;
import ca.couchware.wezzle2d.event.LevelEvent;
import ca.couchware.wezzle2d.event.ScoreEvent;
import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.tile.TileType;
import ca.couchware.wezzle2d.util.Rational;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Kevin
 */
public class ScoreManager implements IResettable, ISaveable, IGameListener, ILevelListener
{
    /** The different types of scores. */
    public enum ScoreType
    {
        LINE,
        BOMB,
        STAR,
        ROCKET
    }

    /**
     * The amount of point per tile in a line.
     */
    final private static int POINTS_PER_LINE_TILE = 50;

    /** 
     * The amount of point per tile in a piece.
     */
    final private static int POINTS_PER_PIECE_TILE = 10;

    /**
     * The minimal score for which the font size is minimal.
     */
    private static int FLOAT_TEXT_SCORE_MIN = 50;

    /**
     * The maximal score for which the font size is maximal.
     */
    private static int FLOAT_TEXT_SCORE_MAX = 3000;

    /**
     * The minimum font size for a score pop-up.
     */
    private static float FLOAT_TEXT_FONT_SIZE_MIN = 20.0f;

    /**
     * The maximum font size for a score pop-up.
     */
    private static float FLOAT_TEXT_FONT_SIZE_MAX = 50.0f;

    /**
     * The hash map keys for storing the score manager state.
     */
    private static enum Keys
    {
        LEVEL_SCORE,
        TOTAL_SCORE,
        LEVEL_TARGET,
        TOTAL_TARGET,
        HIGH_SCORE

    }
    /**
     * The hash map used to save the score manager's state.
     */
    private EnumMap<Keys, Object> managerState;
   
    final private Game game;
    final private ManagerHub hub;

    /**
     * The score this level.
     */
    private int levelScore;

    /** 
     * The total score. 
     */
    private int totalScore;

    /**
     * The target score for this level.
     */
    private int targetLevelScore;

    /**
     * The target total score.
     */
    private int targetTotalScore;

    /**
     * The high score.
     */
    private int highScore;

    /**
     * The constructor.
     * @param properties A property manager to load properties from.
     */
    private ScoreManager(Game game, ManagerHub hub)
    {
        if ( game == null )
            throw new IllegalArgumentException( "Game cannot be null" );

        if ( hub == null )
            throw new IllegalArgumentException( "Hub cannot be null" );

        this.game = game;
        this.hub  = hub;

        // Create the save state.
        managerState = new EnumMap<Keys, Object>( Keys.class );      

        // Initialize the scores. 
        this.totalScore = 0;
        this.levelScore = 0;
        this.highScore = hub.highScoreMan.getHighestScore();
    }

    // Public API.
    public static ScoreManager newInstance(Game game, ManagerHub hub)
    {
        return new ScoreManager( game, hub );
    }

    /**
     * An enum map used to track the amounts of each tile type in a line.
     */
    EnumMap<TileType, Integer> countMap = new EnumMap<TileType, Integer>( TileType.class );

    /**
     * Calculates the score of the line, given the tiles in the line, the type
     * of score, and the length of the chain.
     * 
     * @param indexSet
     * @param lineType
     * @param cascadeCount
     * @return The change in score.
     */
    public int calculateLineScore(Set<Integer> indexSet, ScoreType type, int chainCount)
    {
        if ( indexSet == null )
        {
            throw new IllegalArgumentException( "IndexSet cannot be null" );
        }
        
        if ( type == null )
        {
            throw new IllegalArgumentException( "Type cannot be null" );
        }

        if ( chainCount < 0 )
        {
            throw new IllegalArgumentException( "Chain count must be non-negative" );
        }

        // Reset the count map.
        for ( TileType tt : TileType.values() )
        {
            countMap.put( tt, 0 );
        }

        // Cycle through the set counting the pieces.
        for ( Iterator<Integer> it = indexSet.iterator(); it.hasNext(); )
        {
            // Grab the tile.
            Tile t = hub.boardMan.getTile( it.next() );

            // Count it.
            countMap.put( t.getType(), countMap.get( t.getType() ) + 1 );
        } // end for

        // Change this when we add bombs and multipliers.
        int deltaScore =
                (int) (calculateLineTilePoints( indexSet.size(), type ) * Math.
                pow( 2, countMap.get( TileType.X2 ) ) * Math.pow( 3, countMap.
                get( TileType.X3 ) ) * Math.pow( 4, countMap.get( TileType.X4 ) ) * chainCount);

        CouchLogger.get().recordMessage( this.getClass(), "chainCount = " + chainCount );

        // Return the delta score.
        Rational modifier = game.getDifficulty().getStrategy().getScoreModifier();
        return (deltaScore * modifier.getNumerator()) / modifier.getDenominator();
    }

    /**
     * Requires documenation.
     * 
     * @param numTotal
     * @param lineType
     * @return
     */
    private int calculateLineTilePoints(int numTotal, ScoreType type)
    {
        if ( numTotal <= 0 )
        {
            throw new IllegalArgumentException( "Total number of tiles must be greater than 0" );
        }
        if ( type == null )
        {
            throw new IllegalArgumentException( "Type must not be null" );
        }

        // If we have a line with a star tile, then the line is worth half.
        if ( type == ScoreType.STAR )
        {
            return (numTotal * POINTS_PER_LINE_TILE) / 2;
        }
        // If we have a minimal line, it's just 4 times the points/tile.
        else if ( numTotal <= 4 || type == ScoreType.BOMB || type == ScoreType.ROCKET )
        {
            return numTotal * POINTS_PER_LINE_TILE;
        }

        // If we have more, 4 times the points/tile + 100 + 150 + 200 + ...
        int score = 4 * POINTS_PER_LINE_TILE;

        for ( int i = 0; i < numTotal - 4; i++ )
        {
            score += (i + 2) * POINTS_PER_LINE_TILE;
        }

        return score;
    }

    /**
     * Calculates the score of the passes tiles, adds it to score manager's
     * state and then returns it.
     * 
     * @param set A set of tile indices.
     */
    public int calculatePieceScore(Set indexSet)
    {
        // Sanity check.
        if ( indexSet == null )
        {
            throw new IllegalArgumentException( "Index set must not be null." );
        }
        // Initilize deltaScore variable.
        int deltaScore = 0;

        // Cycle through the set counting the pieces.
        for ( Iterator it = indexSet.iterator(); it.hasNext(); )
        {
            if ( it.next() != null )
            {
                deltaScore += POINTS_PER_PIECE_TILE;
            }
        }

        // Return the delta score.
        Rational modifier = game.getDifficulty().getStrategy().getScoreModifier();
        return (deltaScore * modifier.getNumerator()) / modifier.getDenominator();
    }

    /**
     * A method to generate a target score given the level.
     *
     * @param currentLevel The level to generate the score for.
     * @return The score.
     */
    public int generateTargetLevelScore(int level)
    {
        // Level is at least 1.
        if ( level <= 0 )
        {
            throw new IllegalArgumentException( "Level must be greater than 0" );
        }

        return level * 1200;
    }

    /**
     * Requires documentation.
     * @return
     */
    public int getHighScore()
    {
        return highScore;
    }

    /**
     * Requires documentation.
     * @param highScore
     */
    public void setHighScore(int highScore)
    {
        this.highScore = highScore;
    }

    /**
     * @return The levelScore.
     */
    public int getLevelScore()
    {
        return levelScore;
    }

    /**
     * @param levelScore The levelScore to set.
     */
    public void setLevelScore(int levelScore)
    {
        this.levelScore = levelScore;
        hub.listenerMan.notifyScoreChanged( new ScoreEvent( this, levelScore ) );
    }

    public int getTotalScore()
    {
        return this.totalScore;
    }

    public void setTotalScore(int totalScore)
    {
        if ( totalScore < 0 )
        {
            throw new IllegalArgumentException( "Total score must be non-negative" );
        }
        this.totalScore = totalScore;
    }

    // Resets all the scores to 0.
    public void resetScore()
    {
        setLevelScore( 0 );
        setTotalScore( 0 );
        hub.listenerMan.notifyScoreChanged( new ScoreEvent( this, 0 ) );
    }

    /**
     * @return The targetLevelScore.
     */
    public int getTargetLevelScore()
    {
        return targetLevelScore;
    }

    /**
     * @param targetLevelScore The targetLevelScore to set.
     */
    public void setTargetLevelScore(int targetLevelScore)
    {
        this.targetLevelScore = targetLevelScore;
        this.targetTotalScore += targetLevelScore;

        // Fire event.
        hub.listenerMan.notifyTargetScoreChanged( new ScoreEvent( this, targetLevelScore ) );
    }

    /**
     * @return The targetTotalScore.
     */
    public int getTargetTotalScore()
    {
        return targetTotalScore;
    }

    /**
     * @param targetTotalScore The targetTotalScore to set.
     */
    public void setTargetTotalScore(int targetTotalScore)
    {
        if ( targetTotalScore < 0 )
        {
            throw new IllegalArgumentException( "Target total score must be non-negative" );
        }

        this.targetTotalScore = targetTotalScore;
    }

    /**
     * Increases the score by the given amount.
     * 
     * @param deltaScore The change in the score.
     */
    public void incrementScore(int deltaScore)
    {
        // Update the level score.
        levelScore += deltaScore;

        // Update the target score.
        totalScore += deltaScore;

        // See if we have a new high score.
        if ( totalScore > highScore )
        {
            setHighScore( totalScore );
        }

        // Notify all listeners.
        hub.listenerMan.notifyScoreIncreased( new ScoreEvent( this, deltaScore ) );
        hub.listenerMan.notifyScoreChanged( new ScoreEvent( this, levelScore ) );
    }

    /**
     * A method for calculating the font size for a float text based on
     * the magnitude of the score.
     * 
     * @param deltaScore The score to use.
     * @return The font size appropriate for the passed score.
     */
    public int determineFontSize(final int deltaScore)
    {
        // Determine font size.
        double fontSize = 0.0;

        if ( deltaScore < FLOAT_TEXT_SCORE_MIN )
        {
            fontSize = FLOAT_TEXT_FONT_SIZE_MIN;
        }
        else if ( deltaScore > FLOAT_TEXT_SCORE_MAX )
        {
            fontSize = FLOAT_TEXT_FONT_SIZE_MAX;
        }
        else
        {
            double scorePercent =
                    (double) (deltaScore - FLOAT_TEXT_SCORE_MIN) /
                    (double) (FLOAT_TEXT_SCORE_MAX - FLOAT_TEXT_SCORE_MIN);

            fontSize = FLOAT_TEXT_FONT_SIZE_MIN + 
                    (double) ((FLOAT_TEXT_FONT_SIZE_MAX - FLOAT_TEXT_FONT_SIZE_MIN) *
                    scorePercent);
        }

        return (int) fontSize;
    }

    public void levelChanged(LevelEvent event)
    {
        setLevelScore( event.getNextLevelScore() );
        setTargetLevelScore( event.getNextTargetLevelScore() );
    }

    public void gameStarted(GameEvent event)
    {
        // Ignore it.
    }

    public void gameReset(GameEvent event)
    {
        resetScore();
        setTargetLevelScore( generateTargetLevelScore( event.getLevel() ) );
    }

    public void gameOver(GameEvent event)
    {
        // Ignore it.
    }

    /**
     * Save the state.
     */
    public void saveState()
    {
        managerState.put( Keys.LEVEL_SCORE, levelScore );
        managerState.put( Keys.TOTAL_SCORE, totalScore );
        managerState.put( Keys.LEVEL_TARGET, targetLevelScore );
        managerState.put( Keys.TOTAL_TARGET, targetTotalScore );
        managerState.put( Keys.HIGH_SCORE, highScore );
    }

    /**
     * Load the state.
     */
    public void loadState()
    {
        // See if there is a save state.
        if ( managerState.isEmpty() == true )
        {
            CouchLogger.get().recordWarning( this.getClass(), "No save state exists" );
            return;
        }

        setLevelScore( (Integer) managerState.get( Keys.LEVEL_SCORE ) );
        setTotalScore( (Integer) managerState.get( Keys.TOTAL_SCORE ) );
        setTargetLevelScore( (Integer) managerState.get( Keys.LEVEL_TARGET ) );
        setTargetTotalScore( (Integer) managerState.get( Keys.TOTAL_TARGET ) );
        setHighScore( (Integer) managerState.get( Keys.HIGH_SCORE ) );
    }

    public void resetState()
    {
        setTargetTotalScore( 0 );
        setTotalScore( 0 );
        setTargetLevelScore( 0 );
        setLevelScore( 0 );
    }

}
