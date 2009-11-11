/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

import java.util.EventObject;

/**
 * A custom score event.
 * 
 * @author kgrad
 */
public class ScoreEvent extends EventObject
{
    /** 
     * The score value changes depending on the event type being called.
     * The values are as follows:
     * 
     * scoreIncreased     - The change in score.
     * scoreChanged       - The new score.
     * targetScoreChanged - The new target score.
     */
    private int score;    
    
    public ScoreEvent(Object source, int score)
    {
        super(source);
        
        this.score = score;      
    }

    public int getScore()
    {
        return score;
    }    
    
}