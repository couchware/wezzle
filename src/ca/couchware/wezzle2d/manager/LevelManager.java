package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.event.LevelEvent;

/**
 * Manages the level.
 * 
 * @author cdmckay
 * @author kgrad
 */

public class LevelManager implements IManager      
{       
	//--------------------------------------------------------------------------
	// Instance Members
	//--------------------------------------------------------------------------	   
    
    /** The listener manager. */
    private ListenerManager listenerMan;
    
    /** The score manager. */
    private ScoreManager scoreMan;
    
	/**
	 * The current level. 
	 */
	private int level = 1;    	       
    
    /**
     * The percentage of tiles to maintain.
     */
    private int tileRatio = 80;
    
    /**
     * The minimum drop.
     */
    private int minimumDrop = 1;
    
    /**
     * The number of pieces to drop in concurrently.
     */
    private int parallelDropInAmount = 4;       
        
    /**
     * The level at which the difficulty begins to increase.
     */
    private int difficultyIncreaseLevel = 3;
    
    /**
     * The number of levels before the difficulty level increases.
     */
    private int levelDifficultySpeed = 2;
    
    /** 
     * The maximum number of tiles to drop in.
     */
    private int maximumDropAmount = 8;
           
    //--------------------------------------------------------------------------
	// Constructor
	//--------------------------------------------------------------------------
    
	/**
	 * The constructor.
	 * @param fragment
	 * @param board
	 * @param scoreManager
	 */
	private LevelManager(ListenerManager listenerMan, ScoreManager scoreMan)
	{				
        // Remember the listener manager.
        this.listenerMan = listenerMan;
        this.scoreMan    = scoreMan;                
	}
	        
    /**
     * Returns a new world manager instance.
     * 
     * @param settingsMan
     * @return
     */       
    public static LevelManager newInstance(
            ListenerManager listenerMan,
            ScoreManager    scoreMan)
    {
        return new LevelManager(listenerMan, scoreMan);
    }   
    
    //--------------------------------------------------------------------------
	// Instance Methods
	//--------------------------------------------------------------------------               
    
    /**
     * Calculates the number of tiles to drop.
     * 
     * @param game The game.
     * @param pieceSize The size of the piece consumed.
     * @return The number of tiles do drop.
     */
    public int calculateDropNumber(final Game game, int pieceSize)
    {
        float tiles = game.boardMan.getNumberOfTiles();
        float totalSpots = game.boardMan.getColumns() * game.boardMan.getRows();
        
        // The number of tiles for the current level.
        int levelDrop = (this.level / this.levelDifficultySpeed);
        
        // Check for difficulty ramp up.
        if (this.level > this.difficultyIncreaseLevel)
            levelDrop = (this.difficultyIncreaseLevel / this.levelDifficultySpeed);
        
        // The percent of the board to readd.
        int  boardPercentage = (int)((totalSpots - tiles) * 0.1f); 
        
        // We are low. drop in a percentage of the tiles, increasing if there 
        // are fewer tiles.
        if ((tiles / totalSpots) * 100 < this.tileRatio)
        {
            // If we are past the level ramp up point, drop in more.
            if (this.level > this.difficultyIncreaseLevel)
            {
                  int dropAmt = pieceSize +  levelDrop 
                    + (this.level - this.difficultyIncreaseLevel) 
                    + boardPercentage + this.minimumDrop;
                  
                    if(dropAmt > this.maximumDropAmount + pieceSize)
                    dropAmt = this.maximumDropAmount + pieceSize;
                
                return dropAmt;
                  
            }
            else
            {
                int dropAmt = pieceSize + levelDrop + boardPercentage 
                        + this.minimumDrop;
                
                  if(dropAmt > this.maximumDropAmount + pieceSize)
                    dropAmt = this.maximumDropAmount + pieceSize;
                
                return dropAmt;
            }
        }
        else
        {
            // If we are past the level ramp up point, drop in more.
            if (this.level > this.difficultyIncreaseLevel)
            {
                int dropAmt = pieceSize + levelDrop
                    + (this.level - this.difficultyIncreaseLevel) 
                    + this.minimumDrop;
                
                if(dropAmt > this.maximumDropAmount + pieceSize)
                    dropAmt = this.maximumDropAmount + pieceSize;
                
                return dropAmt;
            }
            else
            {
                int dropAmt = pieceSize + levelDrop + this.minimumDrop;
                
                  if(dropAmt > this.maximumDropAmount + pieceSize)
                    dropAmt = this.maximumDropAmount + pieceSize;
                
                return dropAmt;
            }
        }
    }                  
   
    //--------------------------------------------------------------------------
	// Getters and Setters
	//--------------------------------------------------------------------------         
    
	/**
     * Get the current level.
     * 
	 * @return the currentLevel
	 */
	public int getLevel()
	{
		return level;
	}	
	
	/**
	 * @param currentLevel the currentLevel to set
	 */
	public void setLevel(int newLevel, boolean levelUp)
	{               
        int oldLevel = this.level;
        int excessLevelScore = scoreMan.getLevelScore() - scoreMan.getTargetLevelScore();       
        int nextLevelScore = 0;
        int nextTargetLevelScore  = scoreMan.generateTargetLevelScore(newLevel);

        if (excessLevelScore > nextTargetLevelScore/ 2)
        {
            nextLevelScore = nextTargetLevelScore / 2;
        }       
        else
        {
            nextLevelScore = excessLevelScore;
        }
                        
        // Set the level.
        this.level = newLevel;
                        
        listenerMan.notifyLevelChanged(new LevelEvent(this, 
                oldLevel, 
                newLevel,
                levelUp,
                nextLevelScore,
                nextTargetLevelScore));
	}	
	
   
	/**
	 * Increment the level.
	 */
	public void incrementLevel()
        {	
		// Increment the level.
		setLevel(level + 1, true);
	}	
	            
    /**
     * Get the parallel drop in amount.
     * @return The amount.
     */
    public int getParallelTileDropInAmount()
    {
        return this.parallelDropInAmount;
    }    	
	               
    public void saveState()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void loadState()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
	
    /**
     * Resets the board manager to appropriate settings for the first level.
     */
    public void resetState()
    {
        // Reset to level 1.
        this.setLevel(1, false);
    }
    
}
