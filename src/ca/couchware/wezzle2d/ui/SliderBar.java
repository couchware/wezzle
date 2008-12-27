/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.event.IMouseListener;
import ca.couchware.wezzle2d.event.MouseEvent;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.util.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


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
public class SliderBar extends AbstractEntity implements IMouseListener
{
    
    /** The possible orientations of the slider bar. */
    public enum Orientation
    {        
        HORIZONTAL,
        VERTICAL
    }        
    
    /** The slider states. */
    private static enum State
    {
        NORMAL, 
        PRESSED
    }
    
    /**
     * The width of the slider bar.  This is only used when
     * the bar is VERTICAL.
     */
    final private static int WIDTH = 16;
    
    /**
     * The height of the slider bar area.  This is only used when the 
     * bar if HORIZONTAL.
     */
    final private static int HEIGHT = 16;     

    // -------------------------------------------------------------------------
    // Instance Attributes
    // -------------------------------------------------------------------------               
    
    /**
     * The window that button is in.  This is for adding and removing
     * the mouse listeners.
     */
    private final IGameWindow window;
    
    /**
     * The shape of the button.
     */
    private final RectangularShape shape;       
    
    /** The orientation of the slider bar. */
    private final Orientation orientation;
    
    /**
     * The current state of the button.
     */
    private State state;    
    
    /**
	 * The current location of the mouse pointer.
	 */
	private ImmutablePosition mousePosition;      
    
    /**
     * The sprite for the handle.
     */
    private ISprite spriteHandle;
    
    /**
     * The slide offset, starting from the left side.
     */
    private int slideOffset;
    
    /**
     * The maximum value for the slide offset.
     */
    final private int maxOffset;
            
    /**
     * The lower part of the virtual range.
     */
    private double virtualLower;
    
    /**
     * The upper part of the virutal range.
     */
    private double virtualUpper;
    
    /**
     * The current virtual value.
     */
    private double virtualValue;
    
    /**
     * Whether or not the slider value has changed.
     */
    private boolean changed;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    /**
     * The constructor.
     * @param shape The shape of the button.
     */
    public SliderBar(Builder builder)
    {
        // Set to visible.
        this.visible = builder.visible;
        
        // Store the window reference.
        this.window = builder.window;                       
        
        // Set the position.
        this.x = builder.x;
        this.y = builder.y;
        
        this.x_ = x;
        this.y_ = y;
        
        // Load the slider bar handle.
        this.spriteHandle = ResourceFactory.get()
                .getSprite(Settings.getSpriteResourcesPath() + "/SliderBarHandleRounded.png");
        
        // Set the orientation.
        this.orientation = builder.orientation;
        
        // Set the dimensions.
        switch (orientation)
        {
            case HORIZONTAL:
                this.width  = builder.width;
                this.height = HEIGHT;    
                
                // Determine the maximum slider offset.
                this.maxOffset = width - spriteHandle.getWidth();
                
                break;
                
            case VERTICAL:
                this.width  = WIDTH;
                this.height = builder.height;
                
                // Determine the maximum slider offset.
                this.maxOffset = height - spriteHandle.getWidth();
                
                break;
         
            default: throw new AssertionError();
        }
              
        // Set default anchor.
        this.alignment = builder.alignment;
        this.offsetX = determineOffsetX(alignment, width);
        this.offsetY = determineOffsetY(alignment, height);
        
        // Save shape reference.
        this.shape = new Rectangle(x + offsetX, y + offsetY, this.width, this.height);               
        
        // Start in normal state.
        this.state = State.NORMAL;
        
        // Default the slider to the left/top.
        this.slideOffset = 0;
                       
        // Initially it is not changed.
        this.changed = false;
        
        // Set the virtual stuff.
        setVirtualRange(builder.virtualLower, builder.virtualUpper);
        setVirtualValue(builder.virtualValue);
        
        // Set dirty so it will be drawn.        
        this.dirty = true;
    }
    
    public static class Builder implements IBuilder<SliderBar>
    {
        // Required values.  
        private final IGameWindow window;
        private int x;
        private int y;     
        
        // Optional values.
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);              
        private int opacity = 100;
        private int width  = 200;
        private int height = 200;
        private Orientation orientation = Orientation.HORIZONTAL;
        private boolean visible = true;
        private double virtualLower = 0.0;
        private double virtualUpper = 1.0;
        private double virtualValue = 0.5;
        
        public Builder(int x, int y)
        {            
            this.window = ResourceFactory.get().getGameWindow();
            this.x = x;
            this.y = y;
        }
        
        public Builder(SliderBar sliderBar)
        {            
            this.window = sliderBar.window;
            this.x = sliderBar.x;
            this.y = sliderBar.y;
            this.alignment = sliderBar.alignment.clone();     
            this.orientation = sliderBar.orientation;
            this.opacity = sliderBar.opacity;                        
            this.width = sliderBar.width;
            this.visible = sliderBar.visible;
            this.virtualLower = sliderBar.virtualLower;
            this.virtualUpper = sliderBar.virtualUpper;
            this.virtualValue = sliderBar.virtualValue;
        }
        
        public Builder x(int val) { x = val; return this; }        
        public Builder y(int val) { y = val; return this; }
               
        public Builder alignment(EnumSet<Alignment> val) 
        { alignment = val; return this; }
                        
        public Builder opacity(int val)
        { opacity = val; return this; }
        
        public Builder width(int val)
        { width = val; return this; }
        
        public Builder height(int val)
        { height = val; return this; }
        
        public Builder orientation(Orientation val)
        { orientation = val; return this; }
        
        public Builder visible(boolean val) 
        { visible = val; return this; }
        
        public Builder virtualRange(double l, double u)
        { virtualLower = l; virtualUpper = u; return this; }   
        
        public Builder virtualValue(double val)        
        { virtualValue = val; return this; }           
        
        public SliderBar end()
        {
            SliderBar bar = new SliderBar(this);
            
            if (visible == true)
                bar.window.addMouseListener(bar);        
            
            return bar;
        }                
    }
    
    // -------------------------------------------------------------------------
    // Instance Methods
    // -------------------------------------------------------------------------
    
    private void drawRail(int x, int y, int width, int height, int border)
    {
        window.setColor(SuperColor.newInstance( 
                Color.BLACK, 
                SuperColor.scaleOpacity(opacity)));
        window.fillRect(x, y, width + border * 2, height + border * 2);
        
        window.setColor(SuperColor.newInstance(
                Color.DARK_GRAY, 
                SuperColor.scaleOpacity(opacity)));
        window.fillRect(x + border, y + border, width, height);
    }
    
    @Override
    public boolean draw()
    {
        // Remember last coordinate.
        x_ = x;
        y_ = y;
        
        // Don't draw if not visible.
        if (visible == false)
            return false;
        
        // Draw the rail.
        switch (orientation)
        {
            case HORIZONTAL:
                
                drawRail(x + offsetX, y + offsetY + (HEIGHT - 4) / 2, width - 1, 2, 1);                
                spriteHandle.draw(x + offsetX + slideOffset, y + offsetY).end();
                
                break;
                
            case VERTICAL:
                
                drawRail(x + offsetX + (WIDTH - 4) / 2, y + offsetY, 2, height - 2, 1);
                spriteHandle.draw(
                            x + offsetX,
                            y + offsetY + slideOffset - spriteHandle.getWidth() / 2 + 5)                        
                        .theta(Math.toRadians(90), 0, spriteHandle.getHeight())
                        .end();
                
                break;
                
            default: throw new AssertionError();
        }
        
        
        
        return true;
    }

    public void mouseClicked(MouseEvent e)
    {
        // Intentionally blank.        
    }

    public void mousePressed(MouseEvent e)
	{
		// Retrieve the mouse position.
        setMousePosition(e.getX(), e.getY());
        final ImmutablePosition p = getMousePosition();                
        
        // Ignore click if we're outside the button.
        if (shape.contains(p.getX(), p.getY()) == false)
            return;                    
                            
        //Util.handleMessage("Pressed.", Thread.currentThread());
        
		// Check which button.
        switch (e.getButton())
        {
            // Left mouse clicked.
            case LEFT:
                
                state = State.PRESSED; 
                
                switch (orientation)
                {
                    case HORIZONTAL:
                        setSlideOffset(p.getX() - x - offsetX - spriteHandle.getWidth() / 2);
                        break;
                        
                    case VERTICAL:
                        setSlideOffset(p.getY() - y - offsetY - spriteHandle.getWidth() / 2);
                        break;
                        
                    default: throw new AssertionError();
                }
                                             
                break;
                
            default:                
                break;   
        }
	}

    public void mouseReleased(MouseEvent e)
    {
        //Util.handleMessage("Released.", Thread.currentThread());
        
        // Reset the state.
        state = State.NORMAL;
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
        if (state == State.PRESSED)
        {
             // Retrieve the mouse position.
            setMousePosition(e.getX(), e.getY());
            final ImmutablePosition p = getMousePosition();

            // If the state is pressed, then move the slider around.   
            switch (orientation)
            {
                case HORIZONTAL:
                    
                    setSlideOffset(p.getX() - x - offsetX - spriteHandle.getWidth() / 2);
                    break;
                    
                case VERTICAL:
                    
                    setSlideOffset(p.getY() - y - offsetY - spriteHandle.getWidth() / 2);
                    break;
                    
                default: throw new AssertionError();
            }                        
        }       
    }

    public void mouseMoved(MouseEvent e)
    {
        // Get the last position.
        ImmutablePosition lp = getMousePosition(); 
        
		// Set the mouse position.
		setMousePosition(e.getX(), e.getY());
        
        // Retrieve the mouse position.
        final ImmutablePosition p = getMousePosition();  
        
        // Handle case where there is no last position.
        if (lp == null) lp = p;
        
        // Mouse over.
        if (shape.contains(lp.getX(), lp.getY()) == false 
                && shape.contains(p.getX(), p.getY()) == true)
        {
            window.setCursor(Cursor.HAND_CURSOR);            
        }
        // Mouse out.
        else if (shape.contains(lp.getX(), lp.getY()) == true
                && shape.contains(p.getX(), p.getY()) == false)
        {
            window.setCursor(Cursor.DEFAULT_CURSOR);
        }
    } 
    
    public boolean changed()
    {
        boolean val = changed;
        changed = false;
        return val;             
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
            return changed;
        else
            return changed();
    }
    
    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------       
    
    /**
	 * Gets the mousePosition.
     * 
	 * @return The mousePosition.
	 */
	public synchronized ImmutablePosition getMousePosition()
	{        
		return mousePosition;
	}

	/**
	 * Sets the mousePosition.
     * 
	 * @param mousePosition The mousePosition to set.
	 */
	public synchronized void setMousePosition(final int x, final int y)
	{
		this.mousePosition = new ImmutablePosition(x, y);
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
        }
        else
        {
            window.setCursor(Cursor.DEFAULT_CURSOR);
            window.removeMouseListener(this);            
        }        
    }
    
    /**
     * Sets the virtual range for the slider.
     * 
     * @param lower
     * @param upper
     */
    final public void setVirtualRange(double lower, double upper)
    {
        assert upper > lower;
        
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
    final protected void setSlideOffset(final int slideOffset)
    {
        // Make sure the slider stays on the rail.
        if (slideOffset < 0)
            this.slideOffset = 0;
        else if (slideOffset > maxOffset)
            this.slideOffset = maxOffset;
        else
            this.slideOffset = slideOffset;                
        
        // Make it dirty so we get a redraw.  This is used
        // in lieu of setDirty because it is called form the constructor.
        dirty = true;
        
        // Set changed.        
        changed = true;
        
        // Determine the virtual value.                
        calculateVirtualValue();
        
        // Fire change event.
        fireSliderBarChangedEvent(this.virtualValue);
    }
    
    /**
     * Set the slider offset as value between 0.0 and 1.0.  Automatically 
     * ensures that the passed value is within the range.
     * 
     * @param slideOffsetPercent
     */
    final public void setSlideOffsetPercent(final double slideOffsetPercent)
    {
        setSlideOffset((int) ((double) maxOffset * slideOffsetPercent));
    }      
    
    private void calculateVirtualValue()
    {
        virtualValue = virtualLower + (virtualUpper - virtualLower) * getSlideOffsetPercent();
    }
    
    public double getVirtualValue()
    {
        // Determine the virtual value.                
        calculateVirtualValue();
        
        return virtualValue;
    }
    
    /**
     * Set the slider offset as a value between a range of two numbers.
     *
     * @param value
     */
    final public void setVirtualValue(double value)
    {
        assert value >= virtualLower;
        assert value <= virtualUpper;
        
        double percent = (value - virtualLower) / (virtualUpper - virtualLower);        
        setSlideOffsetPercent(percent);
    }   

    public double getVirtualLower()
    {                
        // Return the value.
        return virtualLower;
    }  

    public double getVirtualUpper()
    {
        return virtualUpper;
    }   
    
    /** An interface for listening to SliderBar changes. */
    public static interface ISliderBarListener 
    {
        public void sliderBarChanged(double virtualValue);
    }
    
    /** The change listener list. */
    private List<ISliderBarListener> sliderBarListenerList = new ArrayList<ISliderBarListener>();
    
    private void fireSliderBarChangedEvent(double virtualValue)
    {
        for (ISliderBarListener listener : sliderBarListenerList)
            listener.sliderBarChanged(virtualValue);
    }
    
    public void addSliderBarListener(ISliderBarListener listener)
    {
        if (this.sliderBarListenerList.contains(listener))
            throw new IllegalArgumentException("Listener already registered!");
        
        this.sliderBarListenerList.add(listener);
    }
    
    public void removeSliderBarListener(ISliderBarListener listener)
    {
        if (!this.sliderBarListenerList.contains(listener))
            throw new IllegalArgumentException("Listener is not registered!");
        
        this.sliderBarListenerList.remove(listener);
    }
    
}
