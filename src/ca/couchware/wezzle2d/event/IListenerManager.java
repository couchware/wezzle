/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.event;

/**
 * An abstract class for state components.
 * 
 * @author kgrad
 */
public interface IListenerManager 
{
    /** The different game types. */
    public enum GameType
    {
        /** A normal game. */
        GAME,        
        
        /** A tuorial game. */
        TUTORIAL
    }  
    
    /** The different classes of listener. */
    public enum Listener
    {
        SCORE,
        LEVEL,
        MOVE,
        LINE,
        GAME
    }
    
    public void registerListener(Listener listenerType, IListener listener);
    
    public void notifyScoreChanged(ScoreEvent e, GameType gameType);
    public void notifyLevelChanged(LevelEvent e);
    public void notifyMoveCommitted(MoveEvent e, GameType gameType);
    public void notifyLineConsumed(LineEvent e, GameType gameType);
    public void notifyGameStarted(GameEvent e);    
    public void notifyGameReset(GameEvent e);
    public void notifyGameCompleted(GameEvent e);
}
