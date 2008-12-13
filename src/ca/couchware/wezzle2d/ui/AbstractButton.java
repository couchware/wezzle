/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.event.IMouseListener;
import ca.couchware.wezzle2d.event.MouseEvent;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import ca.couchware.wezzle2d.util.ImmutableRectangle;
import java.awt.Cursor;
import java.util.EnumSet;

/**
 * A class representing a clickable button.
 * 
 * @author cdmckay
 */
public abstract class AbstractButton extends AbstractEntity implements        
        IButton, IMouseListener       
{
    
    /**
     * Is the mouse on or off the button.
     */
    public static enum State
    {
        PRESSED,
        HOVERED,
        ACTIVATED
    }
    
    // -------------------------------------------------------------------------
    // Instance Attributes
    // -------------------------------------------------------------------------               
    
    /**
     * The window that button is in.  This is for adding and removing
     * the mouse listeners.
     */
    final protected IGameWindow window;    
    
    /**
     * The current state of the button.
     */
    protected EnumSet<State> state;
    
    /**
     * The shape of the button.
     */
    protected ImmutableRectangle shape;                
    
    /**
     * Was the button just clicked?  This flag is cleared once it
     * is read.
     */
    private boolean clicked = false;        
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    /**
     * The constructor.
     * @param shape The shape of the button.
     */
    protected AbstractButton(final int x, final int y)
    {      
        // Grab the window reference.
        this.window = ResourceFactory.get().getGameWindow();
        
        // Set the initial state.
        this.state = EnumSet.noneOf(State.class);
        
        // Set the shape.
        this.shape = new ImmutableRectangle();
        
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
    
    @Override
    public void setX(int x)
    {
        super.setX(x);
        shape = new ImmutableRectangle(x + offsetX, y + offsetY, width, height);
        
        // Pretend like we just moved the mouse.
        handleMoved(window.getMouseImmutablePosition());
    }
    
    @Override
    public void setY(int y)
    {
        super.setY(y);
        shape = new ImmutableRectangle(x + offsetX, y + offsetY, width, height);    
        
        // Pretend like we just moved the mouse.
        handleMoved(window.getMouseImmutablePosition());
    }
    
    protected void handleReleased()
    {                                             
        if (state.containsAll(EnumSet.of(State.PRESSED, State.HOVERED)))
        {
           clicked = true;                      
           
           if (state.remove(State.ACTIVATED) == false)
           {               
               state.add(State.ACTIVATED);
           }
        }   
        
        state.remove(State.PRESSED);
                            
        setDirty(true);
    }
    
    protected void handlePressed()
    {        
        state.add(State.PRESSED);
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }    
    
    /**
     * Handles the updating the button when the mouse has been moved.  This 
     * method should rarely have to be overridden.
     * 
     * @param pos
     */
    protected void handleMoved(ImmutablePosition pos)
    {        
        // See if we moved on to the button.
        if (state.contains(State.HOVERED) == false)
        {      
            if (shape.contains(pos.getX(), pos.getY()) == true)
            {
                state.add(State.HOVERED);
                handleMouseOn();                
            }           
        }
        // See if we moved off the button.
        else if (state.contains(State.HOVERED) == true)                
        {            
            if (shape.contains(pos.getX(), pos.getY()) == false)
            {
                state.remove(State.HOVERED);
                handleMouseOff();
            }
            else
            {
                //LogManager.recordMessage("Hand");
                window.setCursor(Cursor.HAND_CURSOR);
            }
        }        
    }
    
    /**
     * The mouse on runnable.
     */
    protected Runnable mouseOnRunnable = null;

    public void setMouseOnRunnable(Runnable mouseOnRunnable)
    {
        this.mouseOnRunnable = mouseOnRunnable;
    }        
    
    protected void handleMouseOn()
    {   
        if (mouseOnRunnable != null)
            mouseOnRunnable.run();
        
        //LogManager.recordMessage(this + " on called");
        // Set the cursor appropriately.
        window.setCursor(Cursor.HAND_CURSOR);
                
        setDirty(true);                        
    }                
    
    /**
     * The mouse off runnable.
     */
    protected Runnable mouseOffRunnable = null;

    public void setMouseOffRunnable(Runnable mouseOffRunnable)
    {
        this.mouseOffRunnable = mouseOffRunnable;
    }            
               
    protected void handleMouseOff()
    {   
        if (mouseOffRunnable != null)
            mouseOffRunnable.run(); 
        
        //LogManager.recordMessage(this + " off called");
        // Set the cursor appropriately.
        window.setCursor(Cursor.DEFAULT_CURSOR);
                
        setDirty(true);                        
    }
    
    public boolean clicked()
    {
        boolean val = clicked;
        clicked = false;
        return val;
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
            return clicked;
        else
            return clicked();
    }        
        
    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------    	
        
    @Override
    public void setVisible(boolean visible)
    {
        // Ignore if visibility not changed.
        if (this.visible == visible || this.disabled == true)
        {
            this.visible = visible;
            return;
        }
        
        // Invoke super.
        super.setVisible(visible);
        
        // Add or remove listener based on visibility.
        swapMouseListener(visible);     
    }
    
    @Override
    public void setDisabled(boolean disabled)
    {
        //LogManager.recordMessage("Disabled");
        
        // Ignore if disabled or invisible.
        if (this.disabled == disabled || this.visible == false)            
        {
            this.disabled = disabled;
            return;        
        }
        
        // Invoke super class.
        super.setDisabled(disabled);
        
        // Add or remove listener based on disabledness..
        swapMouseListener(!disabled);
    }        
    
    /**
     * Adds or removes this instance from the mouse listener list.
     * 
     * @param add
     */
    private void swapMouseListener(boolean add)
    {
        // If we're adding the listener.
        if (add == true)
        {
            // Pretend like we just moved the mouse.
            handleMoved(window.getMouseImmutablePosition());
            
            window.addMouseListener(this);            
        }
        // If we're removing it.
        else
        {          
            // Clear the last mouse position.
            handleMoved(ImmutablePosition.ORIGIN);
            
            window.setCursor(Cursor.DEFAULT_CURSOR);            
            window.removeMouseListener(this);            
        }  
    }
    
    public boolean isActivated()
    {
        return state.contains(State.ACTIVATED);
    }

    public void setActivated(boolean activated)
    {
        if (activated == true)
            state.add(State.ACTIVATED);
        else
            state.remove(State.ACTIVATED);
            
        setDirty(true);
    }
    
    public ImmutableRectangle getShape()
    {
        return shape;
    }
    
    //--------------------------------------------------------------------------
    // Clickable
    //--------------------------------------------------------------------------
    
    /**
     * The stored click action.
     */
    Runnable clickRunnable = null;
    
    /**
     * Sets the click runnable.
     */
    public void setClickRunnable(Runnable clickRunnable)
    { 
        this.clickRunnable = clickRunnable;
    }
    
    /**
     * Gets the click runnable.
     */
    public Runnable getClickRunnable()
    {
        return clickRunnable;
    }
    
    /**
     * This method is called when the tile is clicked.
     */
    public void onClick()
    {
        if (clickRunnable != null) clickRunnable.run();
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
        // Ignore click if we're outside the button.
        if (state.contains(State.HOVERED) == false)
            return;                    
            
		// Check which button.
        switch (e.getButton())
        {
            // Left mouse clicked.
            case LEFT:
                handlePressed();
                break;                        
                
            default:
                // Intentionally left blank.
        }
	}

	public void mouseReleased(MouseEvent e)
	{             
        // Check which button.
        switch (e.getButton())
        {
            // Left mouse clicked.
            case LEFT:
                handleReleased();
                break;                        

            default:
                // Intentionally left blank.
        }                
	}

	public void mouseDragged(MouseEvent e)
	{
        handleMoved(e.getPosition());		  
	}

	/**
	 * Called automatically when the mouse is moved.
	 */
	public void mouseMoved(MouseEvent e)
	{	
        handleMoved(e.getPosition());
    }        
            
}
