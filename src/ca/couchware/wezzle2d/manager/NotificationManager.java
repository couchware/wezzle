/*
 *  Wezzle
 *  Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.animation.AnimationAdapter;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.animation.FinishedAnimation;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation.RunRule;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ui.AchievementNotification;
import ca.couchware.wezzle2d.ui.INotification;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * A class for managing in-game notifications.
 * @author Cameron McKay
 */
public class NotificationManager implements IManager
{
    /** The list of notifications to be shown. */
    private Queue<INotification> notificationQueue = new LinkedList<INotification>();

    /** The current notification animation. */
    private IAnimation notificationAnimation = FinishedAnimation.get();

    private NotificationManager() {}

    public static NotificationManager newInstance()
    {
        return new NotificationManager();
    }

    public void offer(INotification notif)
    {
        if (notif == null)
            throw new NullPointerException("Notification must not be null");

        this.notificationQueue.offer(notif);
    }

    public void updateLogic(final Game game, final ManagerHub hub)
    {       

        // Check to see if there are any notifications to show.
        if (!this.notificationQueue.isEmpty() && this.notificationAnimation.isFinished())
        {
            final INotification notif = this.notificationQueue.remove();

            int x = Game.SCREEN_WIDTH + 10 + notif.getWidth() / 2;
            notif.setPosition(x, 490);

            IAnimation slideIn = new MoveAnimation.Builder(notif)
                    .speed(375).minX(670).duration(4000).theta(180).build();

            IAnimation fadeOut = new FadeAnimation.Builder(FadeAnimation.Type.OUT, notif)
                    .duration(500).build();

            IAnimation meta = new MetaAnimation.Builder()
                    .runRule(RunRule.SEQUENCE)
                    .add(slideIn)
                    .add(fadeOut)
                    .build();

            meta.addAnimationListener(new AnimationAdapter()
            {
                @Override
                public void animationStarted()
                { hub.layerMan.add(notif, Layer.UI); }

                @Override
                public void animationFinished()
                { hub.layerMan.remove(notif, Layer.UI); }
            });

            this.notificationAnimation = meta;
            hub.animationMan.add(meta);
        }
    }
}
