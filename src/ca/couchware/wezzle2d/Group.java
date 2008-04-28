package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.button.Button;
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
    protected boolean activated;
    
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
    public void setVisible(boolean visible)
    {
        // Set the variable.
        super.setVisible(visible);
        
        // Adjust all the member entities.
        for (Entity e : entityList)
            e.setVisible(visible);
    }

    public boolean isActivated()
    {
        return activated;
    }

    public void setActivated(boolean activated)
    {
        this.activated = activated;
    }   
    
    public boolean buttonClicked()
    {
        boolean clicked = false;
        
        for (Entity e : entityList)
            if (e instanceof Button)
                clicked = clicked || ((Button) e).clicked();
        
        return clicked;
    }
   
}
