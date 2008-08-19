/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

import java.util.EventObject;

/**
 *  A custom line event.
 * 
 * @author kgrad
 */
public class LineEvent extends EventObject
{
    private int lines;
    
    public LineEvent(int deltaLines, Object eventSource)
    {
        super(eventSource);
        this.lines = deltaLines;
    }
    
    public int getLineCount(){ return this.lines; }
}