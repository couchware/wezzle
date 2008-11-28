/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

/**
 * Interface for listening to score events.  Here's how score events are fired:
 * 
 * 1. New Game Started
 * 
 *    In this scenario, the user starts a new game at level 1.
 * 
 *    i.  targetScoreChanged - Target score was changed to 1200.
 *    ii. scoreChanged       - Score was changed to 0.
 * 
 * 2. Got a Line
 * 
 *    In this scenario, the user had 400 points and got a line worth 
 *    300 points. 
 * 
 *    i.  scoreIncreased - Player's score increased by 300.
 *    ii. scoreChanged   - Player's score changed to 700.
 * 
 * 3. Level Up
 * 
 *    In this scenario, the user beat level one with a score of 1600/1200.
 * 
 *    i.  targetScoreChanged - Target score was changed to 2400.
 *    ii. scoreChanged       - Score was changed to 400.
 * 
 * 4. Game Over
 * 
 *    In this scenario, the user just lost a game and restarted.
 * 
 *    i.  targetScoreChanged - Target score was set to 1200.
 *    ii. scoreChanged       - Score was changed to 0.
 * 
 * @author Cameron
 * @author kgrad
 */
public interface IScoreListener extends IListener 
{    
    
    /**
     * Fired each time the score is increased by some amount.
     * @param event
     */
    public void scoreIncreased(ScoreEvent event);
    
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
