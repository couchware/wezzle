/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

/**
 * A listener for listening for timer events.
 * 
 * @author cdmckay
 */
public interface ITimerListener 
{
    public void tickOccurred(TimerEvent event);     
    public void currentTimeReset(TimerEvent event);
    public void startTimeChanged(TimerEvent event);
}
