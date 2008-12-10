/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

import java.util.EventObject;

/**
 * The level event.
 * 
 * @author kgrad
 */
public class LevelEvent extends EventObject
{
    
    /** The old level. */
    final private int oldLevel;        
    
    /** The new level. */
    final private int newLevel;
    
    /** The level score. */
    final private int nextLevelScore;
    
    /** The target level score. */
    final private int nextTargetLevelScore;
    
    /** 
     * Is the event a level-up?  That is, did it result from the player 
     * completing a level.
     */
    final private boolean levelUp;
    
    public LevelEvent(
            Object source, 
            int oldLevel,            
            int newLevel,
            boolean levelUp,
            int nextLevelScore,
            int nextTargetLevelScore)
    {
        super(source);
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.levelUp = levelUp;
        this.nextLevelScore = nextLevelScore;
        this.nextTargetLevelScore = nextTargetLevelScore;
    }   

    public int getOldLevel()
    {
        return oldLevel;
    }
    
    public int getNewLevel()
    {
        return newLevel;
    }

    public boolean isLevelUp()
    {
        return levelUp;
    }        
    
    public int getNextLevelScore()
    {
        return nextLevelScore;
    }

    public int getNextTargetLevelScore()
    {
        return nextTargetLevelScore;
    }   
        
}

