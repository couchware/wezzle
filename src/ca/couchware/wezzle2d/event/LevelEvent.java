/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

import java.util.EventObject;

/**
 *  A custom level event.
 * 
 * @author kgrad
 */
public class LevelEvent extends EventObject
{
    public LevelEvent(Object eventSource)
    {
        super(eventSource);
    }
}

