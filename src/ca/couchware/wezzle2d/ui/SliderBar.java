/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

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
 * This is a class for creating a slider bar.  A slider bar is a common
 * UI element used to set a value between a range.  It is commonly used
 * to adjust sound volume.
 * 
 * Consider a slider bar:
 * 
 * Off setting:    *--------
 * Medium setting: ----*----
 * High setting:   --------*
 * 
 * The "-" part is called the "rail".  The "*" part is called the handle.
 * 
 * @author cdmckay
 */
public class SliderBar extends Entity implements        
        MouseListener, 
        MouseMotionListener
{
    /**
     * The width of the clickable slider bar area.
     */
    final private static int WIDTH = 208;
    
    /**
     * The height of the clickable slider bar area.
     */
    final private static int HEIGHT = 15;     
    
    /**
     * The normal state.
     */
    final private static int STATE_NORMAL = 0;
    
    /**
     * The pressed state.
     */
    final private static int STATE_PRESSED = 1;

    // -------------------------------------------------------------------------
    // Instance Attributes
    // -------------------------------------------------------------------------               
    
    /**
     * The window that button is in.  This is for adding and removing
     * the mouse listeners.
     */
    protected final GameWindow window;
    
    /**
     * The shape of the button.
     */
    protected final RectangularShape shape;       
    
    /**
     * The current state of the button.
     */
    protected int state;
    
    /**
	 * The current location of the mouse pointer.
	 */
	protected XYPosition mousePosition;
    
    /**
     * The sprite for the rail.
     */
    protected Sprite spriteRail;
    
    /**
     * The sprite for the handle.
     */
    protected Sprite spriteHandle;
    
    /**
     * The slide offset, starting from the left side.
     */
    protected int slideOffset;
    
    /**
     * The maximum value for the slide offset.
     */
    final protected int maxOffset;
            
    /**
     * The lower part of the virtual range.
     */
    protected double virtualLower;
    
    /**
     * The upper part of the virutal range.
     */
    protected double virtualUpper;
    
    /**
     * The current virtual value.
     */
    protected double virtualValue;
    
    /**
     * Whether or not the slider value has changed.
     */
    protected AtomicBoolean changed;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    /**
     * The constructor.
     * @param shape The shape of the button.
     */
    public SliderBar(final GameWindow window, final int x, final int y)
    {
        // Set to visible.
        visible = true;
        window.addMouseListener(this);
        window.addMouseMotionListener(this);
     
        // Store the window reference.
        this.window = window;
        
        // Set the position.
        this.x = x;
        this.y = y;
        
        this.x_ = x;
        this.y_ = y;
        
        // Set the dimensions.
        this.width = WIDTH;
        this.height = HEIGHT;
        
        // Set default anchor.
        this.alignment = TOP | LEFT;
        
        // Save shape reference.
        this.shape = new Rectangle(x, y, WIDTH, HEIGHT);                      
        
        // Load in the sprites.
        spriteRail = ResourceFactory.get()
                .getSprite(Game.SPRITES_PATH + "/SliderBarRail.png");
        
        spriteHandle = ResourceFactory.get()
                .getSprite(Game.SPRITES_PATH + "/SliderBarHandle.png");
        
        // Start in normal state.
        state = STATE_NORMAL;
        
        // Default the slider to the far left.
        slideOffset = 0;
        
        // Determine the maximum slider offset.
        maxOffset = width - spriteHandle.getWidth();
        
        // Initially it is not changed.
        changed = new AtomicBoolean(false);
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }
    
    // -------------------------------------------------------------------------
    // Instance Methods
    // -------------------------------------------------------------------------
    
    @Override
    public void draw()
    {
        // Remember last coordinate.
        x_ = x;
        y_ = y;
        
        // Don't draw if not visible.
        if (isVisible() == false)
            return;
        
        // Draw the rail.
        spriteRail.draw(x + offsetX, y + offsetY + 7);
        
        // Draw the handle.
        spriteHandle.draw(x + offsetX + slideOffset, y + offsetY);
    }

    public void mouseClicked(MouseEvent e)
    {
        // Intentionally blank.        
    }

    public void mousePressed(MouseEvent e)
	{
		// Retrieve the mouse position.
        setMousePosition(e.getX(), e.getY());
        final XYPosition p = getMousePosition();                
        
        // Ignore click if we're outside the button.
        if (shape.contains(p.x, p.y) == false)
            return;                    
                            
        //Util.handleMessage("Pressed.", Thread.currentThread());
        
		// Check which button.
        switch (e.getButton())
        {
            // Left mouse clicked.
            case MouseEvent.BUTTON1:
                state = STATE_PRESSED; 
                setSlideOffset(p.x - x - offsetX - spriteHandle.getWidth() / 2);
                
            default:                
                break;   
        }
	}

    public void mouseReleased(MouseEvent e)
    {
        //Util.handleMessage("Released.", Thread.currentThread());
        
        // Reset the state.
        state = STATE_NORMAL;
    }

    public void mouseEntered(MouseEvent e)
    {
        // Intentionally blank.
    }

    public void mouseExited(MouseEvent e)
    {
        // Intentionally blank.
    }

    public void mouseDragged(MouseEvent e)
    {   
        if (state == STATE_PRESSED)
        {
             // Retrieve the mouse position.
            setMousePosition(e.getX(), e.getY());
            final XYPosition p = getMousePosition();

            // If the state is pressed, then move the slider around.        
            setSlideOffset(p.x - x - offsetX - spriteHandle.getWidth() / 2);
        }       
    }

    public void mouseMoved(MouseEvent e)
    {
        // Get the last position.
        XYPosition lp = getMousePosition(); 
        
		// Set the mouse position.
		setMousePosition(e.getX(), e.getY());
        
        // Retrieve the mouse position.
        final XYPosition p = getMousePosition();  
        
        // Handle case where there is no last position.
        if (lp == null) lp = p;
        
        // Mouse over.
        if (shape.contains(lp.x, lp.y) == false 
                && shape.contains(p.x, p.y) == true)
        {
            window.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));            
        }
        // Mouse out.
        else if (shape.contains(lp.x, lp.y) == true
                && shape.contains(p.x, p.y) == false)
        {
            window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    } 
    
    public boolean changed()
    {
        return changed.getAndSet(false);                
    }    
    
    /**
     * A special version of changed() that does not automatically reset the
     * flag if <pre>preserve</pre> is true.
     * 
     * @param preserve
     * @return
     */
    public boolean changed(boolean preserve)
    {
        if (preserve == true)
            return changed.get();
        else
            return changed();
    }
    
    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------
    
    /**
	 * Set the alignment of the button. 
     * The alignment is initially set to the top left. 
	 * 
	 * @param x The x alignment coordinate with respect 
     * to the top left corner of the button.
	 * @param y The y alignment coordinate with respect 
     * to the top left corner of the button.
	 */
    @Override
	public void setAlignment(final int alignment)
	{
        // Invoke super.
        super.setAlignment(alignment);	                
        
        // Move the shape.        
        shape.setFrame(x + offsetX, y + offsetY,
                shape.getWidth(), shape.getHeight());                
	}    
    
    /**
	 * Gets the mousePosition.
	 * @return The mousePosition.
	 */
	public synchronized XYPosition getMousePosition()
	{
        if (mousePosition == null)
            Util.handleWarning("Mouse position is null!", 
                    Thread.currentThread());
        
		return mousePosition;
	}

	/**
	 * Sets the mousePosition.
	 * @param mousePosition The mousePosition to set.
	 */
	public synchronized void setMousePosition(final int x, final int y)
	{
		this.mousePosition = new XYPosition(x, y);
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
            window.addMouseListener(this);
            window.addMouseMotionListener(this);
        }
        else
        {
            window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            window.removeMouseListener(this);
            window.removeMouseMotionListener(this);
        }        
    }
    
    /**
     * Sets the virtual range for the slider.
     * 
     * @param lower
     * @param upper
     */
    public void setVirtualRange(double lower, double upper)
    {
        assert(upper > lower);
        
        this.virtualLower = lower;
        this.virtualUpper = upper;
    }
    
    /**
     * Get the slider offset.
     * @return
     */
    public int getSlideOffset()
    {
        return slideOffset;
    }
    
    /**
     * Get the slider offset as a value between 0.0 and 1.0.
     * @return
     */
    public double getSlideOffsetPercent()
    {
        return (double) slideOffset / (double) maxOffset;
    }

    /**
     * Set the slider offset.  Automatically ensures that the value is within
     * the correct range.
     * @param slideOffset
     */
    protected void setSlideOffset(final int slideOffset)
    {
        // Make sure the slider stays on the rail.
        if (slideOffset < 0)
            this.slideOffset = 0;
        else if (slideOffset > maxOffset)
            this.slideOffset = maxOffset;
        else
            this.slideOffset = slideOffset;                
        
        // Make it dirty so we get a redraw.
        setDirty(true);
        
        // Set changed.
        changed.set(true);
    }
    
    /**
     * Set the slider offset as value between 0.0 and 1.0.  Automatically 
     * ensures that the passed value is within the range.
     * @param slideOffsetPercent
     */
    public void setSlideOffsetPercent(final double slideOffsetPercent)
    {
        setSlideOffset((int) ((double) maxOffset * slideOffsetPercent));
    }      
    
    public double getVirtualValue()
    {
        return virtualValue;
    }
    
    /**
     * Set the slider offset as a value between a range of two numbers.
     *
     * @param value
     */
    public void setVirtualValue(double value)
    {
        assert(value >= virtualLower);
        assert(value <= virtualUpper);
        
        double percent = (value - virtualLower) / (virtualUpper - virtualLower);        
        setSlideOffsetPercent(percent);
    }   

    public double getVirtualLower()
    {
        // Determine the virtual value.                
        virtualValue = (virtualUpper - virtualLower) * getSlideOffsetPercent();
        
        // Return the value.
        return virtualLower;
    }  

    public double getVirtualUpper()
    {
        return virtualUpper;
    }   
            
}
