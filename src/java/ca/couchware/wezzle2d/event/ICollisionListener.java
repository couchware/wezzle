/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */


package ca.couchware.wezzle2d.event;


import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.difficulty.GameDifficulty;

/**
 *
 * @author kgrad
 */
public interface ICollisionListener extends IListener
{
    public void collisionOccured(CollisionEvent event, ManagerHub hub, GameDifficulty difficulty);
}
