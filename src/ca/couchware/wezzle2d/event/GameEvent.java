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
    public GameEvent(Object source)    
    {
        super(source);
    }
}
