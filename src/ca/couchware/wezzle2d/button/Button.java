package ca.couchware.wezzle2d.button;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.*;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * A class representing a clickable button.
 * 
 * @author cdmckay
 */
public abstract class Button implements 
        Drawable, 
        MouseListener, 
        MouseMotionListener
{
    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------
       
    /**
     * The normal state.
     */
    final protected static int STATE_NORMAL = 0;

    /**
     * The active state.
     */
    final protected static int STATE_ACTIVE = 1;
    
    /**
     * The hover state.
     */
    final protected static int STATE_HOVER = 2;
    
    /**
     * The pressed state.
     */
    final protected static int STATE_PRESSED = 3;
    
    // -------------------------------------------------------------------------
    // Instance Attributes
    // -------------------------------------------------------------------------        
    
    /**
     * Whether or not the button is visible.
     */
    protected boolean visible;
    
    /**
     * The current x position.
     */
    protected int x;
    
    /**
     * The current y position.
     */
    protected int y;
    
    /**
     * The shape of the button.
     */
    protected final Shape shape;
    
    /**
     * The button text.
     */
    protected String text;
    
    /**
     * The current state of the button.
     */
    protected int state;
    
    /**
	 * The current location of the mouse pointer.
	 */
	protected XYPosition mousePosition;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    /**
     * The constructor.
     * @param shape The shape of the button.
     */
    public Button(final int x, final int y, final Shape shape)
    {
        // Set to visible.
        this.visible = true;
        
        // Set the position.
        this.x = x;
        this.y = y;
        
        // Save shape reference.
        this.shape = shape;               
        
        // Set text to an empty string.
        this.text = "";
        
        // Set the initial state.
        this.state = STATE_NORMAL;               
    }
    
    // -------------------------------------------------------------------------
    // Instance Methods
    // -------------------------------------------------------------------------
       
    /**
     * Draws the button in its normal state.
     */
    public abstract void drawNormal();    
    
    /**
     * Draws the button in its active state.
     */
    public abstract void drawActive();
    
    /**
     * Draws the button in its hover state.
     */
    public abstract void drawHover();
    
    /**
     * Draws the button in its pressed (but not yet released) state.
     */
    public abstract void drawPressed();                 
    
    public void draw()
    {
        // Don't draw if not visible.
        if (isVisible() == false)
            return;

        // See what state we're in.
        switch (state)
        {
            case STATE_NORMAL:
                drawNormal();
                break;
                
            case STATE_ACTIVE:
                drawActive();
                break;
                
            case STATE_HOVER:
                drawHover();
                break;
                
            case STATE_PRESSED:
                drawPressed();
                break;                           
                
            default:
                Util.handleWarning("Unrecognized or unhandled state.", 
                        Thread.currentThread());
        } // end switch
    }
    
    /**
     * Checks to see if the passed position is on top of the button.
     * @param x
     * @param y
     */
    public boolean contains(final int x, final int y)
    {
        return shape.contains(x, y);
    }
    
    public void handleReleased()
    {
        if (state == STATE_PRESSED)
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
        state = STATE_NORMAL;
    }
        
    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------
    
	/**
	 * Gets the mousePosition.
	 * @return The mousePosition.
	 */
	public synchronized XYPosition getMousePosition()
	{
		return mousePosition;
	}

	/**
	 * Sets the mousePosition.
	 * @param mousePosition The mousePosition to set.
	 */
	public synchronized void setMousePosition(int x, int y)
	{
		this.mousePosition = new XYPosition(x, y);
	}
    
    public boolean isVisible()
    {
        return visible;
    }
    
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }    

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
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
        final XYPosition p = getMousePosition();                
        
        // Ignore click if we're outside the button.
        if (contains(p.x, p.y) == false)
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
        // Retrieve the mouse position.
        final XYPosition p = getMousePosition();                
        
        // Ignore click if we're outside the button.
        if (contains(p.x, p.y) == false)
            return;                    
            
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
		// Set the mouse position.
		setMousePosition(e.getX(), e.getY());
        
        // Retrieve the mouse position.
        final XYPosition p = getMousePosition();                
        
        // Ignore click if we're outside the button.
        if (contains(p.x, p.y) == false)
            handleMouseOff();
        else
            handleMouseOn();  
	}

	/**
	 * Called automatically when the mouse is moved.
	 */
	public void mouseMoved(MouseEvent e)
	{	    
		// Set the mouse position.
		setMousePosition(e.getX(), e.getY());
        
        // Retrieve the mouse position.
        final XYPosition p = getMousePosition();                
        
        // Ignore click if we're outside the button.
        if (contains(p.x, p.y) == false)
            handleMouseOff();
        else
            handleMouseOn();        
    }
}
