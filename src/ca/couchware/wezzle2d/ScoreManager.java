package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.tile.*;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Kevin
 */
 public class ScoreManager implements IManager
 {    
     
    /**
     * The different types of scores.
     * 
     * @author cdmckay
     */
    public enum ScoreType
    {
        LINE, BOMB, STAR, ROCKET
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
    
    /**
     * The board manager.
     */
    final private BoardManager boardMan;
    
    /**
     * The Property Manager.
     */
    final private PropertyManager propertyMan;
    
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
    public ScoreManager(BoardManager boardMan, PropertyManager propertyMan,
            HighScoreManager highScoreMan)
    {
        // Create the save state.
        managerState = new EnumMap<Keys, Object>(Keys.class);
        
        // Store reference to board manager.
        this.boardMan = boardMan;
        this.propertyMan = propertyMan;
        
        // Initialize the scores.
        this.totalScore = 0;
        this.levelScore = 0;
        this.highScore = highScoreMan.getHighestScore();
    }

    /**
     * Requires documentation.
     * 
     * @param set
     * @param lineType
     * @param cascadeCount
     * @return The change in score.
     */
    public int calculateLineScore(Set set, ScoreType type, int chainCount)
    {
        // Initialize tile counts.
        int numNormal = 0;
        int numBomb = 0;
        int numStar = 0;
        int numRocket = 0;
        int numMultiply4x = 0;
        int numMultiply3x = 0;
        int numMultiply2x = 0;		
		
        // Cycle through the set counting the pieces.
        for (Iterator it = set.iterator(); it.hasNext();)
        {
            // Grab the tile.
            TileEntity t = boardMan.getTile((Integer) it.next());
            
            // See what kinda tile it is.
            if (t != null)
            {
                if (t.getClass() == TileEntity.class)
                {
                    numNormal++;
                }
                else if (t.getClass() == X2TileEntity.class)                    
                {
                    numMultiply2x++;
                }
                else if (t.getClass() == X3TileEntity.class)                    
                {
                    numMultiply3x++;
                }
                else if (t.getClass() == X4TileEntity.class)                    
                {
                    numMultiply4x++;
                }
                else if (t.getClass() == BombTileEntity.class)
                {
                    numBomb++;
                }
                else if (t.getClass() == StarTileEntity.class)                    
                {
                    numStar++;
                }
                else if (t.getClass() == RocketTileEntity.class)
                {
                    numRocket++;
                }
                else
                {
                    throw new RuntimeException(
                            "Tile type is not accounted for.");
                }
            } // end if
        } // end for

        // Change this when we add bombs and multipliers.
        int deltaScore = (int) (calculateLineTilePoints(
                numNormal 
                + numBomb 
                + numStar
                + numRocket
                + numMultiply2x 
                + numMultiply3x 
                + numMultiply4x, 
                type)              
                * Math.pow(2, numMultiply2x)
                * Math.pow(3, numMultiply3x)
                * Math.pow(4, numMultiply4x)
                * chainCount);

        LogManager.recordMessage("chainCount = " + chainCount, 
                "ScoreManager#calculateLineScore");
        
        // Update progress and text.
        updateScore(deltaScore);
        
        // Return the delta score.
        return deltaScore;
    }

    /**
     * Requires documenation.
     * @param numTotal
     * @param lineType
     * @return
     */
    private int calculateLineTilePoints(int numTotal, ScoreType type)
    {
        // If we have a minimal line, it's just 4 times the points/tile.
        if (numTotal <= 4  
                || type == ScoreType.BOMB
                || type == ScoreType.ROCKET)
        {
            return numTotal * POINTS_PER_LINE_TILE;
        }
        else if (type == ScoreType.STAR)
        {
            return (numTotal * POINTS_PER_LINE_TILE) / 2;
        }

        // If we have more, 4 times the points/tile + 100 + 150 + 200 + ...
        int score = 4 * POINTS_PER_LINE_TILE;

        for (int i = 0; i < numTotal - 4; i++)
            score += (i + 2) * POINTS_PER_LINE_TILE;

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
        assert(indexSet != null);
        
        // Initilize deltaScore variable.
        int deltaScore = 0;

        // Cycle through the set counting the pieces.
        for (Iterator it = indexSet.iterator(); it.hasNext(); )
        {
            if (it.next() != null)
            {
                deltaScore += POINTS_PER_PIECE_TILE;
            }
        }
            
        // Update the score if there is a score update.
        if (deltaScore != 0)
        {			
            // Update scores.
            updateScore(deltaScore);
        }
        else
        {
            // Otherwise do nothing.
        }
        
        // Return the score.
        return deltaScore;
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
    }

    public int getTotalScore()
    {
        return this.totalScore;
    }

    public void setTotalScore(int totalScore)
    {
        this.totalScore = totalScore;
    }
    
    // Resets all the scores to 0.
    public void resetScore()
    {
        setLevelScore(0);
        setTotalScore(0);
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
        this.targetTotalScore = targetTotalScore;
    }  
    
    /**
     * A method to update all the scores.
     * @param deltaScore A change in score.
     */
    private void updateScore(int deltaScore)	
    {	
            // Update the level score.
            levelScore += deltaScore;

            // Update the target score.
            totalScore += deltaScore;

            if(totalScore > highScore)
                    setHighScore(totalScore);
    }
    
    /**
     * A method for calculating the font size for a float text based on
     * the magnitude of the score.
     * 
     * @param deltaScore The score to use.
     * @return The font size appropriate for the passed score.
     */
    public float determineFontSize(final int deltaScore)
    {
        // Determine font size.
        float fontSize = 0;

        if (deltaScore < FLOAT_TEXT_SCORE_MIN)
            fontSize = FLOAT_TEXT_FONT_SIZE_MIN;
        else if (deltaScore > FLOAT_TEXT_SCORE_MAX)
            fontSize = FLOAT_TEXT_FONT_SIZE_MAX;
        else
        {
            double scorePercent = 
                    (double) (deltaScore - FLOAT_TEXT_SCORE_MIN) 
                    / (double) (FLOAT_TEXT_SCORE_MAX - FLOAT_TEXT_SCORE_MIN);
            
            fontSize = FLOAT_TEXT_FONT_SIZE_MIN 
                    + (float) 
                    ((FLOAT_TEXT_FONT_SIZE_MAX - FLOAT_TEXT_FONT_SIZE_MIN) 
                    * scorePercent);
        }
        
        return fontSize;
    }

    /**
     * Save the state.
     */
    public void saveState()
    {
        managerState.put(Keys.LEVEL_SCORE, levelScore);
        managerState.put(Keys.TOTAL_SCORE, totalScore);
        managerState.put(Keys.LEVEL_TARGET, targetLevelScore);
        managerState.put(Keys.TOTAL_TARGET, targetTotalScore);
        managerState.put(Keys.HIGH_SCORE, highScore);       
    }

    /**
     * Load the state.
     */
    public void loadState()
    {                
        // See if there is a save state.
        if (managerState.isEmpty() == true)
        {
            LogManager.recordWarning("No save state exists.", "ScoreManager#load");
            return;
        }
        
        levelScore = (Integer) managerState.get(Keys.LEVEL_SCORE);
        totalScore = (Integer) managerState.get(Keys.TOTAL_SCORE);
        targetLevelScore = (Integer) managerState.get(Keys.LEVEL_TARGET);
        targetTotalScore = (Integer) managerState.get(Keys.TOTAL_TARGET);
        highScore = (Integer) managerState.get(Keys.HIGH_SCORE);
    }
    
}