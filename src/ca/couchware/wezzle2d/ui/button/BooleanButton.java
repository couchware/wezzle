package ca.couchware.wezzle2d.ui.button;

import ca.couchware.wezzle2d.GameWindow;
import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.util.XYPosition;
import java.awt.Cursor;
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
        // Retrieve the mouse position.        
        final XYPosition p = getMousePosition(); 
                
        if (contains(p.x, p.y) == true)
        {
            // If the mouse is released over a button that is depressed, then
            // that's a click.
            if (state == STATE_PRESSED)
            {
                clicked.set(true);
        
                if (activated == true)    
                {
                    activated = false;                    
                    onDeactivation();
                }
                else        
                {
                    activated = true;                    
                    onActivation();
                }

                
            }
            
            state = STATE_HOVER;                        
        }        
        else
        {
            if (isActivated() == true)
                state = STATE_ACTIVE;
            else
                state = STATE_NORMAL;
        }
            
                
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
        //Util.handleWarning("Hand", Thread.currentThread());
        window.setCursor(Cursor.HAND_CURSOR);
        
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
        //Util.handleWarning("Default", Thread.currentThread());
        window.setCursor(Cursor.DEFAULT_CURSOR);
        
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
        {
            state = STATE_ACTIVE;
            onActivation();
        }
        else
        {            
            state = STATE_NORMAL;
            onDeactivation();
            setMousePosition(0, 0);
            handleMoved(new XYPosition(window.getMousePosition()));
        }
        
        setDirty(true);
    }
    
    /**
     * This method may be called from either the event thread (if activated
     * by a mouse-click) or from the main thread if it's actiavted 
     * programmatically.  Make sure you only use thread-safe methods (in this
     * case, the only thread-safe method is setText) to modify the button.
     */
    public void onActivation()
    {
        // Optionally overridden.
    }
    
     /**
     * This method may be called from either the event thread (if activated
     * by a mouse-click) or from the main thread if it's actiavted 
     * programmatically.  Make sure you only use thread-safe methods (in this
     * case, the only thread-safe method is setText) to modify the button.
     */
    public void onDeactivation()
    {
        // Optionally overridden.
    }
    
}
