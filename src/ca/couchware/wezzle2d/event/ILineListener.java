/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */


package ca.couchware.wezzle2d.event;

/**
 *
 * @author kgrad
 */
public interface ILineListener extends IListener
{
    public void lineConsumed(LineEvent event, IListenerManager.GameType gameType);
}
