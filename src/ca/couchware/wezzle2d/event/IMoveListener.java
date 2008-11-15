/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */


package ca.couchware.wezzle2d.event;

/**
 *
 * @author kgrad
 */
public interface IMoveListener extends IListener
{
    public void moveCommitted(MoveEvent event, IListenerManager.GameType gameType);
}
