/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */


package ca.couchware.wezzle2d.event;

/**
 *
 * @author kgrad
 */
public interface IMoveListener 
{
    public void handleMoveEvent(MoveEvent evt, IListenerComponent.GameType gameType);
}
