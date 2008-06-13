package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.button.*;
import ca.couchware.wezzle2d.util.Util;
import java.util.LinkedList;

/**
 * The group class is a way for controlling a bunch of entities at once.
 * 
 * @author cdmckay
 */
public class Group extends Entity
{
    /**
     * Is the screen activated?
     */
    protected boolean activated = false;
    
    /**
     * A reference to the game window.
     */
    final protected GameWindow window;
    
    /**
     * A reference to the layer manager.
     */
    final protected LayerManager layerMan;
    
    /**
     * An linked list of all the entities in this screen.
     */
    final protected LinkedList<Entity> entityList;
    
    /**
     * The constructa'.
     * 
     * @param window
     * @param layerMan
     */
    public Group(final GameWindow window, final LayerManager layerMan)
    {
        // Invoke super.
        super();
        
        // Make all groups start invisible.
        super.setVisible(false);
        
        // Store the reference.
        this.window = window;
        this.layerMan = layerMan;
        
        // Create the entity list.
        this.entityList = new LinkedList<Entity>();
    }
    
     @Override
    public void draw()
    {
        throw new UnsupportedOperationException(
                "This method is not supported for groups");
    }       
    
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
        for (Entity e : entityList)                   
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
    
    /**
     * A convenience method for determining if any of the buttons in the
     * group have been pressed.
     * 
     * @return True if a button has been pressed, false otherwise.
     */
    public boolean buttonClicked()
    {
        boolean clicked = false;
        
        for (Entity e : entityList)
            if (e instanceof Button)
                clicked = clicked || ((Button) e).clicked(true);
        
        return clicked;
    }
    
    /**
     * A convenience method to clear all click notifications on all buttons
     * in the group.
     */
    public void clearClicked()
    {
        Util.handleMessage("Cleared by a group.", Thread.currentThread());
        
        for (Entity e : entityList)
            if (e instanceof Button)
                ((Button) e).clicked();        
    }
    
    /**
     * A convenience method for deactivating all boolean buttons in the
     * group.
     */
    public void resetButtons()
    {
       for (Entity e : entityList)
            if (e instanceof BooleanButton)
                ((BooleanButton) e).setActivated(false);
    }
   
}