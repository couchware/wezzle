/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

/**
 * An interface for listening to Wezzle tile events.
 * 
 * @author cdmckay
 */
public interface IWezzleListener 
{

    /**
     * Occurs whenever the wezzle timer is changed.  The most common
     * occurance of this is when the timer counts down.
     * 
     * @param event
     */
    public void wezzleTimerChanged(WezzleEvent event);
        
}
