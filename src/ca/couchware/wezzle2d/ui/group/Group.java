/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui.group;

import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.LayerManager;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.ui.*;
import java.util.LinkedList;

/**
 * The group class is a way for controlling a bunch of entities at once.
 *
 * @author cdmckay
 */
public class Group extends AbstractEntity
{        
    //--------------------------------------------------------------------------
    // Protected Members
    //--------------------------------------------------------------------------
    
    /**
     * The parent of the group.  Set to null if there is no parent.
     */
    protected Group parent = null;
    
    /**
     * Is the screen activated?
     */
    protected boolean activated = false;

    /**
     * A reference to the game window.
     */
    final protected IGameWindow window;

    /**
     * A reference to the layer manager.  This is used by groups to add
     * and remove things like buttons and sliders.
     */
    final protected LayerManager layerMan;
    
    /**
     * A reference to the group manager.  This is used to give the buttons
     * the capability to open other groups in a convenient way.
     */
    final protected GroupManager groupMan;

    /**
     * An linked list of all the entities in this screen.
     */
    final protected LinkedList<IEntity> entityList;

    //--------------------------------------------------------------------------
    // Constructor
    //--------------------------------------------------------------------------
    
    /**
     * The constructa'.
     *
     * @param window
     * @param layerMan
     */
    public Group(final IGameWindow window, 
            final LayerManager layerMan, final GroupManager groupMan)
    {
        // Invoke super.
        super();

        // Make all groups start invisible.
        super.setVisible(false);

        // Store the reference.
        this.window = window;
        this.layerMan = layerMan;
        this.groupMan = groupMan;

        // Create the entity list.
        this.entityList = new LinkedList<IEntity>();
    }
    
    //--------------------------------------------------------------------------
    // Instance Members
    //--------------------------------------------------------------------------

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
        LogManager.recordMessage("Cleared by a group.", "Group#clearChanged");

        for (IEntity e : entityList)
            if (e instanceof IButton)
                ((IButton) e).clicked();
            else if (e instanceof SliderBar)
                ((SliderBar) e).changed();
            else if (e instanceof RadioGroup)
                ((RadioGroup) e).changed();
    }    
    
    /**
     * This method is called when the group is activated and detects a click.
     * 
     * @param game The game state.
     */
    public void updateLogic(Game game)
    {
        // Overridden.
    }

    @Override
    public void draw()
    {
        throw new UnsupportedOperationException(
                "This method is not supported for groups");
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

}