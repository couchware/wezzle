/*
 * Wezzle
 * Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.audio.Sound;
import ca.couchware.wezzle2d.graphics.IEntity;

/**
 * An interface for implementing game notifications.
 *
 * @author Cameron McKay
 */
public interface INotification extends IEntity
{
    Sound getAssociatedSound();
}
