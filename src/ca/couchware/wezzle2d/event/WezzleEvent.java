/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

import java.util.EventObject;

/**
 * A wezzle event.
 * 
 * @author cdmckay
 */
public class WezzleEvent extends EventObject
{

    final private int timerValue;
    
    public WezzleEvent(Object source, int timerValue)
    {
        super(source);
        this.timerValue = timerValue;
    }

    public int getTimerValue()
    {
        return timerValue;
    }        
    
}
