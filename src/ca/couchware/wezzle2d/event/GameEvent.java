/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

import java.util.EventObject;

/**
 * The event object for game events.
 * 
 * @author cdmckay
 */
public class GameEvent extends EventObject
{
    /** The level at the time the event was fired. */
    final private int level;
    
    /** 
     * The total score at the time the event was fired... this is used mostly
     * with game over.
     */
    final private int totalScore;
    
    public GameEvent(Object source, int level, int totalScore)
    {
        super(source);
        this.level = level;
        this.totalScore = totalScore;
    }

    public int getLevel()
    {
        return level;
    }

    public int getTotalScore()
    {
        return totalScore;
    }
        
}
