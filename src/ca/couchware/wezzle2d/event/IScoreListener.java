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
    public void scoreChanged(ScoreEvent event, IListenerManager.GameType gameType);
}
