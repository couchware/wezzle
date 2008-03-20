package ca.couchware.wezzle2d.button;

import java.awt.Shape;

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
        
    public BooleanButton(final int x, final int y, final Shape shape)
    {
        // Invoke super.
        super(x, y, shape);
        
        // Set the button to be initially deactivated.
        this.activated = false;
    }      
    
    public void handleReleased()
    {
        if (activated == true)
        {
            activated = false;            
        }
        else
        {
            activated = true;            
        }
        
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
    
}
