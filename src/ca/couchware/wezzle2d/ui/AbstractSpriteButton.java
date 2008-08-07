package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.*;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RectangularShape;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class representing a clickable button.
 * 
 * @author cdmckay
 */
public abstract class AbstractSpriteButton extends AbstractEntity implements        
        IButton,
        MouseListener, 
        MouseMotionListener
{
    
    /**
     * The button's current state.
     */
    public static enum State
    {
        NORMAL, HOVER, ACTIVE, PRESSED
    }      
    
    /**
     * The current state of the button.
     */
    protected State state;
    
    // -------------------------------------------------------------------------
    // Instance Attributes
    // -------------------------------------------------------------------------               
    
    /**
     * The window that button is in.  This is for adding and removing
     * the mouse listeners.
     */
    final protected GameWindow window;    
    
    /**
     * The shape of the button.
     */
    protected RectangularShape shape;  
    
    /**
	 * The current location of the mouse pointer.
	 */
	private WPosition mousePosition;             
    
    /**
     * Was the button just clicked?  This flag is cleared once it
     * is read.
     */
    private AtomicBoolean clicked = new AtomicBoolean(false);  
    
    /**
     * Was the button just clicked?  This flag is cleared once it
     * is read.
     */
    private AtomicBoolean activated = new AtomicBoolean(false);  
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    /**
     * The constructor.
     * @param shape The shape of the button.
     */
    public AbstractSpriteButton(final GameWindow window, final int x, final int y)
    {
        // Set to visible.
        visible = true;
        window.addMouseListener(this);
        window.addMouseMotionListener(this);        
     
        // Store the window reference.
        this.window = window;
        
        // Set the initial state.
        this.state = State.NORMAL;
        
        // Set the shape.
        shape = new Rectangle();
        
        // Set the position.
        this.x = x;
        this.y = y;
        
        this.x_ = x;
        this.y_ = y;                
                
        // Set dirty so it will be drawn.        
        dirty = true;
    }
    
    // -------------------------------------------------------------------------
    // Instance Methods
    // -------------------------------------------------------------------------                                      
    
    private void handleReleased()
    {
        // Retrieve the mouse position.        
        final WPosition p = getMousePosition(); 
                
        if (shape.contains(p.getX(), p.getY()) == true)
        {
            // If the mouse is released over a button that is depressed, then
            // that's a click.
            if (state == State.PRESSED)
            {
                clicked.set(true);        
                activated.set(isActivated() == true ? false : true);
            }
            
            state = State.HOVER;                        
        }        
        else
        {
            if (isActivated() == true)
                state = State.ACTIVE;
            else
                state = State.NORMAL;
        }
                            
        setDirty(true);
    }
    
    private void handlePressed()
    {        
        state = State.PRESSED;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }    
    
    /**
     * Handles the updating the button when the mouse has been moved.  This 
     * method should rarely have to be overridden.
     * 
     * @param pos
     */
    public void handleMoved(WPosition pos)
    {
        // Get the last position.
        WPosition lastPos = getMousePosition(); 
        if (lastPos == null) lastPos = WPosition.ORIGIN;
        
		// Set the new mouse position.
		setMousePosition(pos.getX(), pos.getY());          
        
        // Handle case where there is no last position.
        if (lastPos == null) lastPos = pos;
        
        // Ignore click if we're outside the button.
        if (shape.contains(lastPos.getX(), lastPos.getY()) == false 
                && shape.contains(pos.getX(), pos.getY()) == true)
        {
            handleMouseOn();                
        }
        else if (shape.contains(lastPos.getX(), lastPos.getY()) == true
                && shape.contains(pos.getX(), pos.getY()) == false)
        {
            handleMouseOff();
        }
    }
       
    public void handleMouseOn()
    {
        //Util.handleWarning("Hand", Thread.currentThread());
        window.setCursor(Cursor.HAND_CURSOR);
        
        if (state != State.PRESSED && state != State.HOVER)
        {
            state = State.HOVER;
            setDirty(true);
        }                
    }           
    
    public void handleMouseOff()
    {   
        //Util.handleWarning("Default", Thread.currentThread());
        window.setCursor(Cursor.DEFAULT_CURSOR);
        
        if (isActivated() == true && state != State.ACTIVE)
        {
            state = State.ACTIVE;
            setDirty(true);
        }
        else if (isActivated() == false && state != State.NORMAL)
        {
            state = State.NORMAL;
            setDirty(true);
        }                
    }
    
    public boolean clicked()
    {
        return clicked.getAndSet(false);                
    }    
    
    /**
     * A special version of clicked() that does not automatically reset the
     * flag if <pre>preserve</pre> is true.
     * 
     * @param preserve
     * @return
     */
    public boolean clicked(boolean preserve)
    {
        if (preserve == true)
            return clicked.get();
        else
            return clicked();
    }        
        
    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------
    
	/**
	 * Gets the mousePosition.
	 * @return The mousePosition.
	 */
	public synchronized WPosition getMousePosition()
	{
//        if (mousePosition == null)
//            Util.handleWarning("Mouse position is null!", 
//                    Thread.currentThread());
        
		return mousePosition;
	}

	/**
	 * Sets the mousePosition.
	 * @param mousePosition The mousePosition to set.
	 */
	public synchronized void setMousePosition(final int x, final int y)
	{
		this.mousePosition = new WPosition(x, y);
	}                    
    
    @Override
    public void setVisible(boolean visible)
    {
        // Ignore if visibility not changed.
        if (isVisible() == visible)
            return;
        
        // Invoke super.
        super.setVisible(visible);
        
        // Add or remove listener based on visibility.
        if (visible == true)
        {
            // Pretend like we just moved the mouse.
            handleMoved(window.getMouseWPosition());
            
            window.addMouseListener(this);
            window.addMouseMotionListener(this);
        }
        else
        {          
            // Clear the last mouse position.
            handleMoved(new WPosition(0, 0));
            
            window.setCursor(Cursor.DEFAULT_CURSOR);
            
            window.removeMouseListener(this);
            window.removeMouseMotionListener(this);
        }        
    }
    
    public boolean isActivated()
    {
        return activated.get();
    }

    public void setActivated(boolean activated)
    {
        this.activated.set(activated);
        
        if (isActivated() == true)
        {
            state = State.ACTIVE;            
        }
        else
        {            
            state = State.NORMAL;           
            setMousePosition(0, 0);
            handleMoved(window.getMouseWPosition());
        }
        
        setDirty(true);
    }
    
    //--------------------------------------------------------------------------
    // Events
    //--------------------------------------------------------------------------
    
    public void mouseClicked(MouseEvent e)
	{
		// Intentionally blank.
        
	}

	public void mouseEntered(MouseEvent e)
	{
		// Intentionally blank.
		
	}

	public void mouseExited(MouseEvent e)
	{
		// Intentionally blank.
		
	}

	public void mousePressed(MouseEvent e)
	{
		// Retrieve the mouse position.
        setMousePosition(e.getX(), e.getY());
        final WPosition p = getMousePosition();                
        
        // Ignore click if we're outside the button.
        if (shape.contains(p.getX(), p.getY()) == false)
            return;                    
            
		// Check which button.
        switch (e.getButton())
        {
            // Left mouse clicked.
            case MouseEvent.BUTTON1:
                handlePressed();
                break;                        
                
            default:
                // Intentionally left blank.
        }
	}

	public void mouseReleased(MouseEvent e)
	{      
        setMousePosition(e.getX(), e.getY());
        
        // Check which button.
        switch (e.getButton())
        {
            // Left mouse clicked.
            case MouseEvent.BUTTON1:
                handleReleased();
                break;                        

            default:
                // Intentionally left blank.
        }                
	}

	public void mouseDragged(MouseEvent e)
	{
        // Intentionally left blank.		  
	}

	/**
	 * Called automatically when the mouse is moved.
	 */
	public void mouseMoved(MouseEvent e)
	{	
        handleMoved(new WPosition(e.getX(), e.getY()));
    }        
            
}
