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
    private int level;
    
    public GameEvent(Object source, int level)    
    {
        super(source);
        this.level = level;
    }

    public int getLevel()
    {
        return level;
    }
        
}
