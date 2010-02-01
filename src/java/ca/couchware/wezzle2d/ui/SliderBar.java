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
    private final IWindow window;
    
    /**
     * The graphics instance.
     */
    private final IGraphics gfx;
    
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
    private int virtualLower;
    
    /**
     * The upper part of the virutal range.
     */
    private int virtualUpper;
    
    /**
     * The current virtual value.
     */
    private int virtualValue;
    
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
    private SliderBar(Builder builder)
    {
        // Check the arguments.
        if (builder == null)
            throw new NullPointerException("Builder cannot be null");

        // Set to visible.
        this.visible = builder.visible;
        
        // Store the window reference.
        this.window = ResourceFactory.get().getWindow();
        this.gfx = ResourceFactory.get().getGraphics();
        
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
        this.opacity = builder.opacity;
        
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
                
        // Initially it is not changed.
        this.changed = false;
        
        // Set the virtual stuff.
        this.virtualLower = builder.virtualLower;
        this.virtualUpper = builder.virtualUpper;
        this.virtualValue = builder.virtualValue;                
        
        // Default the slider to the left/top.
        this.slideOffset = (int) ((double) maxOffset * getVirtualPercent());
        
        // Set dirty so it will be drawn.        
        this.dirty = true;
    }
    
    public static class Builder implements IBuilder<SliderBar>
    {
        // Required values.         
        private int x;
        private int y;     
        
        // Optional values.
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);              
        private int opacity = 100;       
        private int width  = 200;
        private int height = 200;
        private Orientation orientation = Orientation.HORIZONTAL;
        private boolean visible = true;
        private int virtualLower = 0;
        private int virtualUpper = 100;
        private int virtualValue = 0;
        
        public Builder(int x, int y)
        {                      
            this.x = x;
            this.y = y;
        }
        
        public Builder(SliderBar sliderBar)
        {                        
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
        
        public Builder virtualRange(int l, int u)
        { virtualLower = l; virtualUpper = u; return this; }   
        
        public Builder virtualValue(int val)        
        { virtualValue = val; return this; }           
        
        public SliderBar build()
        {
            SliderBar bar = new SliderBar(this);
            
            if (visible)
                bar.window.addMouseListener(bar);        
            
            return bar;
        }                
    }
    
    // -------------------------------------------------------------------------
    // Instance Methods
    // -------------------------------------------------------------------------
    
    private void drawRail(int x, int y, int width, int height, int border)
    {
        gfx.setColor(CouchColor.newInstance(
                Color.BLACK, 
                CouchColor.scaleOpacity(opacity)));
        gfx.fillRect(x, y, width + border * 2, height + border * 2);
        
        gfx.setColor(CouchColor.newInstance(
                Color.DARK_GRAY, 
                CouchColor.scaleOpacity(opacity)));
        gfx.fillRect(x + border, y + border, width, height);
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
                fireSliderBarPressedEvent( this.virtualValue );

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

        synchronize();
    }

    public void mouseReleased(MouseEvent e)
    {
        //Util.handleMessage("Released.", Thread.currentThread());

        // Snap the slider bar to the virtual value.
        setVirtualValue(this.virtualValue);

        fireSliderBarReleasedEvent( this.virtualValue );
        
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
            
            synchronize();
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
    
    public void mouseWheel(MouseEvent e)
    {
        // Intentionally left blank.
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
    
    private void synchronize()
    {        
        final float offset =
                (float) ((virtualUpper - virtualLower) * slideOffset) /
                (float) maxOffset;
        
        this.virtualValue = this.virtualLower + Math.round( offset );
    }
    
    /**
     * Set the slider offset.  Automatically ensures that the value is within
     * the correct range.
     * @param slideOffset
     */
    final protected void setSlideOffset(final int slideOffset)
    {
        if (slideOffset == this.slideOffset)
            return;

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
              
        // Fire change event.
        fireSliderBarChangedEvent(this.virtualValue);
    }           
       
    public int getVirtualValue()
    {       
        return virtualValue;
    }
    
    /**
     * Set the slider offset as a value between a range of two numbers.
     *
     * @param value
     */
    final public void setVirtualValue(int value)
    {
        if(value < virtualLower || value > virtualUpper)
            throw new IllegalArgumentException("Value is out of range");
       
        this.virtualValue = value;
        
        setSlideOffset(((virtualValue - virtualLower) * maxOffset) / (virtualUpper - virtualLower));
    }   

    public int getVirtualLower()
    {                
        // Return the value.
        return virtualLower;
    }  

    public int getVirtualUpper()
    {
        return virtualUpper;
    }   
    
    final public double getVirtualPercent()
    {
        return (double) (virtualValue - virtualLower) / (double) (virtualUpper - virtualLower);
    }
    
    /** An interface for listening to SliderBar changes. */
    public static interface ISliderBarListener 
    {
        public void sliderBarChanged(int virtualValue);
        public void sliderBarPressed(int virtualValue);
        public void sliderBarReleased(int virtualValue);
    }
    
    /** The change listener list. */
    private List<ISliderBarListener> sliderBarListenerList = new ArrayList<ISliderBarListener>();
    
    private void fireSliderBarChangedEvent(int virtualValue)
    {
        for (ISliderBarListener listener : sliderBarListenerList)
            listener.sliderBarChanged(virtualValue);
    }

    private void fireSliderBarPressedEvent(int virtualValue)
    {
        for (ISliderBarListener listener : sliderBarListenerList)
            listener.sliderBarPressed(virtualValue);
    }

    private void fireSliderBarReleasedEvent(int virtualValue)
    {
        for (ISliderBarListener listener : sliderBarListenerList)
            listener.sliderBarReleased(virtualValue);
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
    
    @Override
    public void dispose()
    {
        // Stop listening to the mouse events.
        if (this.visible)
            window.removeMouseListener(this);
    }
    
}
