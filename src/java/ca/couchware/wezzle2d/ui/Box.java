/*
 *  Wezzle
 *  Copyright (c) 2007-2010 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.util.IBuilder;
import ca.couchware.wezzle2d.IGraphics;
import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.event.IMouseListener;
import ca.couchware.wezzle2d.event.MouseEvent;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import ca.couchware.wezzle2d.util.ImmutableRectangle;
import java.awt.Color;
import java.util.EnumSet;

/**
 * Creates a graphical box.
 * 
 * @author cdmckay
 */
public class Box extends AbstractEntity implements IMouseListener
{           
    
    /**
     * The border style of the window.
     */
    public enum Border
    {
        MEDIUM,
        THICK;
              
        @Override
        public String toString()
        {
            String s = super.toString();
            return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        }  
    }
    
    /**
     * The state of the window.
     */
    protected enum State
    {
        HOVERED
    }
    
    /**
     * The current state of the window.
     */
    protected EnumSet<State> state = EnumSet.noneOf(State.class);
    
    /**
     * The sprite for a corner of the window frame.
     */
    final protected String pathCorner;
    
    /**
     * The sprite for the horizontal (top or bottom) part of the window frame.
     */
    final protected String pathHorizontal;
    
    /**
     * The sprite for the vertical (left or right) part of the window frame.
     */
    final  protected String pathVertical;
    
    /**
     * The reference to the window.
     */
    protected IWindow win;
    
    /**
     * The reference to the graphics instance.
     */
    protected IGraphics graphics;
    
    /**
     * The shape of the window.
     */
    protected ImmutableRectangle shape;
    
    /**
     * The corner sprite.
     */
    protected ISprite cornerSprite;
    
    /**
     * The horizontal sprite.
     */
    protected ISprite horizontalSprite;
    
    /**
     * The vertical sprite.
     */
    protected ISprite verticalSprite;
    
    /**
     * The border style of the window.
     */
    protected Border border;
    
    /**
     * The color of the window background.
     */
    protected Color color;
    
    /**
     * Creates a window with it's top-left corner at (x,y) and the given
     * width and height.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    private Box(Builder builder)
    {
        // Set window reference.
        this.win = builder.win;
        this.graphics = win.getGraphics();
        
        // Set the position.
        this.x = builder.x;
        this.y = builder.y;
        
        this.x_ = x;
        this.y_ = y;
        
        // Set the dimensions.
        this.width = builder.width;
        this.height = builder.height;
        
        this.width_ = width;
        this.height_ = height;                 
        
        // Set the opacity and disabledness.
        this.opacity = builder.opacity;
        this.disabled = builder.disabled;
        this.visible = builder.visible;
        
        // Set default anchor.
        this.alignment = builder.alignment;
        this.offsetX = determineOffsetX(alignment, width);
        this.offsetY = determineOffsetY(alignment, height);
        
        // Set the shape.
        this.shape = new ImmutableRectangle(x + offsetX, y + offsetY, width, height);
        
        // Set the border.
        this.border = builder.border;
        
        // Create the sprites.
        this.pathCorner = Settings.getSpriteResourcesPath() + "/Window" + border + "Corner.png";
        this.pathHorizontal = Settings.getSpriteResourcesPath() + "/Window" + border + "Horizontal.png";
        this.pathVertical = Settings.getSpriteResourcesPath() + "/Window" + border + "Vertical.png";
                
        cornerSprite = ResourceFactory.get().getSprite(pathCorner);
        horizontalSprite = ResourceFactory.get().getSprite(pathHorizontal);                
        verticalSprite = ResourceFactory.get().getSprite(pathVertical);
        
        // Create the background colour.  This colour is changed each time
        // the opacity is changed.
        this.color = new Color(0, 0, 0, ((float) opacity) / 100.0f);
    }
    
    public static class Builder implements IBuilder<Box>
    {
        // Required values.
        private IWindow win;
        private int x;
        private int y;             
        
        // Optional values.        
        private int width = 100;
        private int height = 100;
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);       
        private int opacity = 100;        
        private boolean visible = true;
        private boolean disabled = false;
        private Border border = Border.THICK;
        
        public Builder(IWindow win, int x, int y)
        {
            this.win = win;
            this.x = x;
            this.y = y;
        }
        
        public Builder(Box window)
        {
            this.win = window.win;
            this.x = window.x;
            this.y = window.y;
            this.alignment = window.alignment.clone();
            this.width = window.width;
            this.height = window.height;
            this.visible = window.visible;
            this.opacity = window.opacity;
            this.disabled = window.disabled;
            this.border = window.border;
        }
        
        public Builder x(int val) { x = val; return this; }        
        public Builder y(int val) { y = val; return this; }
        
        public Builder width(int val) { width = val; return this; }        
        public Builder height(int val) { height = val; return this; }        
        
        public Builder alignment(EnumSet<Alignment> val) 
        { alignment = val; return this; }
               
        public Builder opacity(int val) 
        { opacity = val; return this; }
                
        public Builder visible(boolean val) 
        { visible = val; return this; }
        
        public Builder disabled(boolean val)
        { disabled = val; return this; }
        
        public Builder border(Border val)
        { border = val; return this; }
        
        public Box build()
        {
            Box box = new Box(this);
            
            if (visible && !disabled)
            {
                win.addMouseListener(box);
            }
            
            return box;
        }                
    } 
    
    @Override
    public void setX(int x)
    {
        super.setX(x);
        shape = new ImmutableRectangle(x + offsetX, y + offsetY, width, height);
    }
    
    @Override
    public void setY(int y)
    {
        super.setY(y);
        shape = new ImmutableRectangle(x + offsetX, y + offsetY, width, height);     
    }

    @Override
    public void setOpacity(int opacity)
    {
        // Invoke super.
        super.setOpacity(opacity);
        
        // Reset the color.
        this.color = new Color(0, 0, 0, ((float) opacity) / 100.0f);
    }
    
    /**
     * Draw the box to the screen.
     */
    @Override
    public boolean draw()
    {
        x_ = x + offsetX;
        y_ = y + offsetY;
        
        width_ = width;
        height_ = height;
        
        if (visible == false)
            return false;
        
        // Draw the inside of the window.
        graphics.setColor(color);
        graphics.fillRect(
                x + cornerSprite.getWidth() + offsetX, 
                y + cornerSprite.getHeight() + offsetY, 
                width - cornerSprite.getWidth() * 2, 
                height - cornerSprite.getWidth() * 2);
        
        // Draw the corners.
        
        // Top left.
        cornerSprite.draw(x + offsetX, y + offsetY)                
                .opacity(opacity)
                .end();
        
        // Top right.
        cornerSprite.draw(
                x + offsetX + width - cornerSprite.getWidth(), 
                y + offsetY)               
                .opacity(opacity)
                .theta(Math.toRadians(90), cornerSprite.getWidth() / 2, cornerSprite.getHeight() / 2)
                .end();
        
        // Bottom left.
        cornerSprite.draw(
                x + offsetX,  
                y + offsetY + height - cornerSprite.getWidth())               
                .opacity(opacity)
                .theta(Math.toRadians(-90), cornerSprite.getWidth() / 2, cornerSprite.getHeight() / 2)
                .end();      
        
        // Bottom right.
        cornerSprite.draw(
                x + offsetX + width  - cornerSprite.getWidth(), 
                y + offsetY + height - cornerSprite.getHeight())               
                .opacity(opacity)
                .theta(Math.toRadians(180), cornerSprite.getWidth() / 2, cornerSprite.getHeight() / 2)
                .end();               
        
        // Draw the horizontal sides.
        horizontalSprite.draw(
                x + offsetX + cornerSprite.getWidth(), 
                y + offsetY)
                .width(width - cornerSprite.getWidth() * 2)               
                .opacity(opacity)
                .end();
        
        // Bottom.        
        int w = width - cornerSprite.getWidth() * 2;
        horizontalSprite.draw(
                x + offsetX + cornerSprite.getWidth(), 
                y + offsetY + height - cornerSprite.getHeight())
                .width(w)                
                .theta(Math.toRadians(180), w / 2, horizontalSprite.getHeight() / 2)
                .opacity(opacity)
                .end();
               
        // Draw the vertical sides.
        
        // Left.
        verticalSprite.draw(
                x + offsetX, 
                y + offsetY + cornerSprite.getHeight())                
                .height(height - cornerSprite.getHeight() * 2)
                .opacity(opacity)
                .end();
        
        // Right.
        int h = height - cornerSprite.getHeight() * 2;
        verticalSprite.draw(
                x + offsetX + width - cornerSprite.getWidth(), 
                y + offsetY + cornerSprite.getHeight())               
                .height(h)
                .theta(Math.toRadians(180), verticalSprite.getWidth() / 2, h / 2)
                .opacity(opacity)
                .end();
        
        return true;
    }        
    
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
            handleMoved(win.getMouseImmutablePosition());
            
            win.addMouseListener(this);
        }
        // If we're removing it.
        else
        {          
            // Clear the last mouse position.
            handleMoved(ImmutablePosition.ORIGIN);
            
            //window.setCursor(Cursor.DEFAULT_CURSOR);            
            win.removeMouseListener(this);
        }  
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
                //window.setCursor(Cursor.HAND_CURSOR);
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
    }

    public void mouseClicked(MouseEvent e)
    {
        // Intentionally left blank.
    }

    public void mouseEntered(MouseEvent e)
    {
        // Intentionally left blank.
    }

    public void mouseExited(MouseEvent e)
    {
        // Intentionally left blank.
    }

    public void mousePressed(MouseEvent e)
    {
        // Intentionally left blank.
    }

    public void mouseReleased(MouseEvent e)
    {
        // Intentionally left blank.
    }

    public void mouseDragged(MouseEvent e)
    {
        // Intentionally left blank.
    }

    public void mouseMoved(MouseEvent e)
    {
        handleMoved(e.getPosition());
    }

    public void mouseWheel(MouseEvent e)
    {
        // Intentionally left blank.
    }
    
    @Override
    public void dispose()
    {
        // Stop listening to the mouse events.
        if (this.visible && !this.disabled)
            win.removeMouseListener(this);
    }
    
}
