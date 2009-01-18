/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

import java.util.EventObject;

/**
 * A timer event.
 * 
 * @author cdmckay
 */
public class TimerEvent extends EventObject
{
    /** The start time at the time the event was fired. */
    final private int startTime;
    
    /** The current time at the time the event was fired. */
    final private int currentTime;
    
    public TimerEvent(Object source, int startTime, int currentTime)
    {
        super(source);
        this.startTime   = startTime;
        this.currentTime = currentTime;
    }

    public int getStartTime()
    {
        return startTime;
    }
    
    public int getCurrentTime()
    {
        return currentTime;
    }
            
}
