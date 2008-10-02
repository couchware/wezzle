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
public interface IListenerComponent 
{
       // Game type
    public static enum GameType
    {
        GAME,        
        TUTORIAL
    }  
    
    public void registerScoreListener(IScoreListener listener);
    public void registerLevelListener(ILevelListener listener);
    public void registerLineListener(ILineListener listener);
    public void registerMoveListener(IMoveListener listener);
    
    public void notifyScoreListener(ScoreEvent e, GameType gameType);
    public void notifyLevelListener(LevelEvent e);
    public void notifyMoveListener(MoveEvent e, GameType gameType);
    public void notifyLineListener(LineEvent e, GameType gameType);
    
}
