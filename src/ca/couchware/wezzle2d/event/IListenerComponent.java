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

    public void registerScoreListener(IScoreListener listener);
    public void registerLevelListener(ILevelListener listener);
    public void registerMoveListener(IMoveListener listener);
    
    public void notifyScoreListener(ScoreEvent e);
    public void notifyLevelListener(LevelEvent e);
    public void notifyMoveListener(MoveEvent e);
    
}
