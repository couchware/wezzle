package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.event.LevelEvent;
import ca.couchware.wezzle2d.manager.Settings.Key;

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
        
    private ListenerManager listenerMan;   
    private ScoreManager scoreMan;    
    private SettingsManager settingsMan;
    
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
    private LevelManager(ListenerManager listenerMan,
        ScoreManager scoreMan, SettingsManager settingsMan)
    {
    // Remember the listener manager.
    this.listenerMan = listenerMan;
    this.scoreMan    = scoreMan;
    this.settingsMan = settingsMan;
    }

    /**
     * Returns a new world manager instance.
     * 
     * @param settingsMan
     * @return
     */       
    public static LevelManager newInstance(
            ListenerManager listenerMan,
            ScoreManager    scoreMan,
            SettingsManager settingsMan)
    {
        return new LevelManager(listenerMan, scoreMan, settingsMan);
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

        int numerator   = settingsMan.getInt(Key.GAME_SCORE_CARRYOVER_NUMERATOR);
        int denominator = settingsMan.getInt(Key.GAME_SCORE_CARRYOVER_DENOMINATOR);

        // The carryover is the amount of excess score the user may keep
        // between levels.
        int carryover = (nextTargetLevelScore * numerator) / denominator;

        if (excessLevelScore > carryover)
        {
            nextLevelScore = carryover;
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
