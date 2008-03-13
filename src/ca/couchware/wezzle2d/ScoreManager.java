

package ca.couchware.wezzle2d;

import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Kevin
 */
 public class ScoreManager
 {
     /**
     * The amount of point per tile in a line.
     */
    private static int POINTS_PER_LINE_TILE = 50;

    /** 
     * The amount of point per tile in a piece.
     */
    private static int POINTS_PER_PIECE_TILE = 10;
 
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
     */
    public ScoreManager()
    {
        // Initialize the score.
        this.totalScore = 0;
        this.levelScore = 0;
    }

    /**
     * Requires documentation.
     * @param tiles
     * @param lineType
     * @param cascadeCount
     */
    public void calculateLineScore(Set set, int lineType, int cascadeCount)
    {
        // Initialize tile counts.
        int numNormal = 0;
        int numBomb = 0;
        int numMult4x = 0;
        int numMult3x = 0;
        int numMult2x = 0;		

		
        // Cycle through the set counting the pieces.
        for (Iterator it = set.iterator(); it.hasNext(); )
        {			
                if (it.next() != null)
                {
                       // if (tiles[i].getClass() == TileNormal.class)
                                numNormal++;
//                        else if (tiles[i].getClass() == TileMultiplier.class)
//                        {
//                                if (((TileMultiplier) tiles[i]).getMultiplier() == 2)
//                                        numMult2x++;
//                                else if (((TileMultiplier) tiles[i]).getMultiplier() == 3)
//                                        numMult3x++;
//                                else if (((TileMultiplier) tiles[i]).getMultiplier() == 4)
//                                        numMult4x++;
//                        }
//                        else if (tiles[i].getClass() == TileBomb.class)
//                        {
//                                numBomb++;	
//                        }
                }
        }					

        // Change this when we add bombs and multipliers.
        int deltaScore = (int) (calculateLineTilePoints(numNormal + numBomb + numMult2x + numMult3x + numMult4x, lineType)
                *  Math.pow(1.5, numBomb)
                *  Math.pow(2, numMult2x)
                *  Math.pow(3, numMult3x)
                *  Math.pow(4, numMult4x)
                * cascadeCount);

        Util.handleMessage("cascadeCount == " + cascadeCount, Thread.currentThread());
        
        // Update progress and text.
        updateScore(deltaScore);
    }

    /**
     * Requires documenation.
     * @param numTotal
     * @param lineType
     * @return
     */
    private int calculateLineTilePoints(int numTotal, int lineType)
    {
            // If we have a minimal line, it's just 4 times the points/tile.
            if (numTotal <= 4 )//|| lineType == LineEvent.TYPE_BOMB )
                    return numTotal * POINTS_PER_LINE_TILE;

            // If we have more, 4 times the points/tile + 100 + 150 + 200 + ...
            int score = 4 * POINTS_PER_LINE_TILE;

            for (int i = 0; i < numTotal - 4; i++)
                    score += (i + 2) * POINTS_PER_LINE_TILE;

            return score;
    }

    /**
     * Requires documentation.
     * @param set A set of tiles.
     */
    public void calculatePieceScore(Set set)
    {
        //Sanity check.
        assert(set != null);
        
        // Initilize deltaScore variable.
        int deltaScore = 0;

        // Cycle through the set counting the pieces.
        for (Iterator it = set.iterator(); it.hasNext(); )
        {
            if(it.next() != null)
                deltaScore += POINTS_PER_PIECE_TILE;
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
     * A method to reset the high score.
     */
    public void resetHighScore()
    {
        try
        {
          //      propertyManager.setProperty(PropertyManager.HIGH_SCORE, "0");
            this.setHighScore(0);
        }
        catch(Exception e)
        {
                e.printStackTrace();
        }
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
}