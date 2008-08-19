/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

import ca.couchware.wezzle2d.Game;
import java.util.EventObject;

/**
 *  A custom level event.
 * 
 * @author kgrad
 */
public class LevelEvent extends EventObject
{
    private int deltaLevel;
    private Game game;
    
    public LevelEvent(int deltaLevel, Game g, Object eventSource)
    {
        super(eventSource);
        this.game = g;
        this.deltaLevel = deltaLevel;
    }
    
    public int getLevelChange() { return this.deltaLevel; };
    public Game getGame(){ return this.game; };
}

