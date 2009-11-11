/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

/**
 * An interface for notifying the implementor of changes in the game state.
 * 
 * @author cdmckay
 */
public interface IGameListener extends IListener
{    
    public void gameStarted(GameEvent event);
    public void gameReset(GameEvent event);
    public void gameOver(GameEvent event);
}
