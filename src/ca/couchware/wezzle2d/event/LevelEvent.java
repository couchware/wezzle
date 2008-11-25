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
    final private int levelScore;
    
    /** The target level score. */
    final private int targetLevelScore;
    
    public LevelEvent(
            Object source, 
            int oldLevel,            
            int newLevel,
            int levelScore,
            int targetLevelScore)
    {
        super(source);
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.levelScore = levelScore;
        this.targetLevelScore = targetLevelScore;
    }   

    public int getOldLevel()
    {
        return oldLevel;
    }
    
    public int getNewLevel()
    {
        return newLevel;
    }  
    
    public int getLevelScore()
    {
        return levelScore;
    }

    public int getTargetLevelScore()
    {
        return targetLevelScore;
    }   
        
}

