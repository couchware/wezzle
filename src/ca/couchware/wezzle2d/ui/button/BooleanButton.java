package ca.couchware.wezzle2d.ui.button;

import ca.couchware.wezzle2d.GameWindow;
import java.awt.geom.RectangularShape;

/**
 *
 * @author cdmckay
 */
public abstract class BooleanButton extends Button
{

    /**
     * Is the button currently activated?
     */
    protected boolean activated;       
    
    /**
     * The constructor.
     * @param x
     * @param y
     * @param width
     * @param height
     * @param shape
     */        
    public BooleanButton(final GameWindow window,
            final int x, final int y, 
            final int width, final int height,
            final RectangularShape shape)
    {
        // Invoke super.
        super(window, x, y, width, height, shape);
                
        // Set the button to be initially deactivated.
        this.activated = false;
    }      
    
    @Override
    public void handleReleased()
    {
        clicked = true;
        
        if (activated == true)        
            activated = false;                    
        else        
            activated = true;                    
        
        state = STATE_HOVER;
        
        setDirty(true);
    }
    
    @Override
    public void handlePressed()
    {        
        state = STATE_PRESSED;
        
        setDirty(true);
    }            
    
    @Override
    public void handleMouseOn()
    {
        if (state != STATE_PRESSED 
                && state != STATE_HOVER)
        {
            state = STATE_HOVER;
            setDirty(true);
        }                
    }        
    
    @Override
    public void handleMouseOff()
    {     
        if (activated == true
                && state != STATE_ACTIVE)
        {
            state = STATE_ACTIVE;
            setDirty(true);
        }
        else if (activated == false 
                && state != STATE_NORMAL)
        {
            state = STATE_NORMAL;
            setDirty(true);
        }                
    }

    public boolean isActivated()
    {
        return activated;                
    }

    public void setActivated(boolean activated)
    {
        this.activated = activated;
        
        if (activated == true)
            state = STATE_ACTIVE;
        else
            state = STATE_NORMAL;
        
        setDirty(true);
    }
    
}
