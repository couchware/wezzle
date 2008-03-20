package ca.couchware.wezzle2d.button;

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
     * Was the button just pushed?  This flag is cleared once it
     * is read.
     */
    protected boolean pushed;
    
    /**
     * The constructor.
     * @param x
     * @param y
     * @param width
     * @param height
     * @param shape
     */        
    public BooleanButton(final int x, final int y, 
            final int width, final int height,
            final RectangularShape shape)
    {
        // Invoke super.
        super(x, y, width, height, shape);
        
        // Set the button to be initially deactivated.
        this.activated = false;
    }      
    
    public void handleReleased()
    {
        pushed = true;
        
        if (activated == true)        
            activated = false;                    
        else        
            activated = true;                    
        
        state = STATE_HOVER;
    }
    
    public void handlePressed()
    {        
        state = STATE_PRESSED;
    }            
    
    public void handleMouseOn()
    {
        if (state != STATE_PRESSED)
            state = STATE_HOVER;
    }        
    
    public void handleMouseOff()
    {     
        if (activated == true)
            state = STATE_ACTIVE;
        else
            state = STATE_NORMAL;
    }

    public boolean isActivated()
    {
        return activated;
    }

    public void setActivated(boolean activated)
    {
        this.activated = activated;
    }

    public boolean wasPushed()
    {
        boolean value = pushed;
        pushed = false;
        return value;
    }    
    
}
