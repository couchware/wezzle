/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.group;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.animation.FinishedAnimation;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.audio.Sound;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.RadioGroup;
import ca.couchware.wezzle2d.ui.Scroller;
import ca.couchware.wezzle2d.ui.SliderBar;
import java.util.ArrayList;
import java.util.List;

/**
 * The group class is a way for controlling a bunch of entities at once.
 *
 * @author cdmckay
 */
public abstract class AbstractGroup extends AbstractEntity implements IGroup
{        
    //--------------------------------------------------------------------------
    // Protected Members
    //--------------------------------------------------------------------------
    
    /**
     * The parent of the group.  Set to null if there is no parent.
     */
    protected IGroup parent = null;
    
    /**
     * Is the screen activated?
     */
    protected boolean activated = false;

    /**
     * A reference to the game window.
     */
    final protected IWindow window;

    /**
     * An linked list of all the entities in this screen.
     */
    final protected List<IEntity> entityList;

    //--------------------------------------------------------------------------
    // Constructor
    //--------------------------------------------------------------------------
    
    /**
     * The constructa'.
     *
     * @param window
     * @param layerMan
     */
    public AbstractGroup(IGroup parent)
    {       
        // Store the references.
        this.parent = parent;
        this.window = ResourceFactory.get().getWindow();        

        // Make all groups start invisible.
        super.setVisible(false);
        
        // Create the entity list.
        this.entityList = new ArrayList<IEntity>();
    }
    
    public AbstractGroup()
    {
        this(null);
    }
    
    //--------------------------------------------------------------------------
    // Instance Members
    //--------------------------------------------------------------------------

    /**
     * Updates the logic for the group.
     *
     * @param game
     * @param hub
     */
    public void updateLogic(Game game, ManagerHub hub)
    {
        for (IEntity entity : entityList)
        {
            boolean isClickable =
                    (entity instanceof IButton && ((IButton) entity).clicked( true )) ||
                    (entity instanceof RadioGroup && ((RadioGroup) entity).changed( true )) ||
                    (entity instanceof Scroller && ((Scroller) entity).changed( true ));

            if (isClickable) hub.soundMan.play( Sound.CLICK_LIGHT );
        }
    }

    /**
     * A convenience method for determining if any of the controls in the
     * group have been changed.
     *
     * @return True if a button has been pressed, false otherwise.
     */
    public boolean controlChanged()
    {
        boolean changed = false;

        for (IEntity e : entityList)
            if (e instanceof IButton)
                changed = changed || ((IButton) e).clicked(true);
            else if (e instanceof SliderBar)
                changed = changed || ((SliderBar) e).changed(true);
            else if (e instanceof RadioGroup)
                changed = changed || ((RadioGroup) e).changed(true);

        return changed;
    }

    /**
     * A convenience method to clear all change notifications on all controls
     * in the group.
     */
    public void clearChanged()
    {
        //CouchLogger.get().recordMessage(this.getClass(), "Cleared by a group.");

        for (IEntity e : entityList)
        {
            if (false) { }
            else if (e instanceof IButton)    ((IButton)    e).clicked();
            else if (e instanceof SliderBar)  ((SliderBar)  e).changed();
            else if (e instanceof RadioGroup) ((RadioGroup) e).changed();
        }
    }    
    
    @Override
    public boolean draw()
    {
        throw new UnsupportedOperationException(
                "This method is not supported for groups.");
    }
    
    /**
     * Returns an animation that gradually shows the group's visual elements.
     * 
     * @return An animation that can be tested for doneness.
     */
    public IAnimation animateShow()
    {
        return FinishedAnimation.get();
    }
    
    /**
     * Returns an animation that gradually hides the group's visual elements.
     * 
     * @return An animation that can be tested for doneness.
     */
    public IAnimation animateHide()
    {
        return FinishedAnimation.get();
    }

    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------
    
    @Override
    public void setVisible(final boolean visible)
    {
        // This is more important than you think.  Basically, since we might
        // be adding or removing listeners, we want to make sure we only add
        // a listener once, and that we only remove it once.  This ensures that.
        if (isVisible() == visible)
            return;

        // Set the variable.
        super.setVisible(visible);

        // Adjust all the member entities.
        for (IEntity e : entityList)
            e.setVisible(visible);
    }

    /**
     * Is this group activated? The specific meaning of activated differs
     * from group to group. Refer to the specific groups documentation.
     *
     * @return True if activated, false otherwise.
     */
    public boolean isActivated()
    {
        return activated;
    }

    /**
     * Sets the activated property of the group.
     *
     * @param activated
     */
    public void setActivated(boolean activated)
    {
        this.activated = activated;
    }    
    
    public void resetControls()
    {
        // Optionally overridden.
    }
    
    @Override
    public void dispose()
    {
        for (IEntity e : entityList)
            e.dispose();
    }

}