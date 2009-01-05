package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.event.LevelEvent;

/**
 * Manages the level.
 * 
 * @author cdmckay
 * @author kgrad
 */

public class LevelManager implements IResettable
{       
	//--------------------------------------------------------------------------
	// Instance Members
	//--------------------------------------------------------------------------	   
    
    /** The listener manager. */
    private ListenerManager listenerMan;
    
    /** The score manager. */
    private ScoreManager scoreMan;
    
	/** The current level. */
	private int level = 1;     
           
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
     * Resets the board manager to appropriate settings for the first level.
     */
    public void resetState()
    {
        // Reset to level 1.
        this.level = 1;
    }
    
}
