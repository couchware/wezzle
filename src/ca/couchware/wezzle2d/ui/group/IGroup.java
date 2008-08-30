/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui.group;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.animation.IAnimation;

/**
 *
 * @author cdmckay
 */
public interface IGroup {

    /**
     * Returns an animation that gradually hides the group's visual elements.
     *
     * @return An animation that can be tested for doneness.
     */
    IAnimation animateHide();

    /**
     * Returns an animation that gradually shows the group's visual elements.
     *
     * @return An animation that can be tested for doneness.
     */
    IAnimation animateShow();

    /**
     * A convenience method to clear all change notifications on all controls
     * in the group.
     */
    void clearChanged();

    /**
     * A convenience method for determining if any of the controls in the
     * group have been changed.
     *
     * @return True if a button has been pressed, false otherwise.
     */
    boolean controlChanged();
   
    boolean draw();

    /**
     * Is this group activated? The specific meaning of activated differs
     * from group to group. Refer to the specific groups documentation.
     *
     * @return True if activated, false otherwise.
     */
    boolean isActivated();

    /**
     * Sets the activated property of the group.
     *
     * @param activated
     */
    void setActivated(boolean activated);
   
    void setVisible(final boolean visible);

    /**
     * This method is called when the group is activated and detects a click.
     *
     * @param game The game state.
     */
    void updateLogic(Game game);

}
