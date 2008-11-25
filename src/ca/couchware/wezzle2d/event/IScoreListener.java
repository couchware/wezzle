/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

/**
 *
 * @author kgrad
 */
public interface IScoreListener extends IListener 
{
    /**
     * Fired when the score is reset to 0.
     * 
     * @param event
     */
    public void scoreReset(ScoreEvent event);
    
    /**
     * Fired each time the player's score changes.
     * 
     * @param event The associated event.
     * @param gameType The game type, either a normal GAME or a TUTORIAL.
     */
    public void scoreChanged(ScoreEvent event);
    
    /**
     * Fired every time the target score in the score manager is changed.
     * 
     * @param event
     */
    public void targetScoreChanged(ScoreEvent event);
}
