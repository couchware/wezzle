/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.IBuilder;
import ca.couchware.wezzle2d.IGameWindow;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.event.IMouseListener;
import ca.couchware.wezzle2d.event.MouseEvent;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import ca.couchware.wezzle2d.util.ImmutableRectangle;
import java.awt.Color;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class Window extends AbstractEntity implements IMouseListener
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
     * The reference to the game window.
     */
    protected IGameWindow window;     
    
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
    private Window(Builder builder)
    {
        // Set window reference.
        this.window = ResourceFactory.get().getGameWindow();
        
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
    
    public static class Builder implements IBuilder<Window>
    {
        // Required values.       
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
        
        public Builder(int x, int y)
        {            
            this.x = x;
            this.y = y;
        }
        
        public Builder(Window window)
        {            
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
        
        public Window end()
        {
            Window win = new Window(this);
            
            if (visible == true && disabled == false)
                win.window.addMouseListener(win); 
            
            return win;
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
     * Draw the window to the screen.
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
        window.setColor(color);
        window.fillRect(
                x + cornerSprite.getWidth() + offsetX, 
                y + cornerSprite.getHeight() + offsetY, 
                width - cornerSprite.getWidth() * 2, 
                height - cornerSprite.getWidth() * 2);
        
        // Draw the corners.
        
        // Top left.
        cornerSprite.draw(
                x + offsetX, 
                y + offsetY, 
                cornerSprite.getWidth(), cornerSprite.getHeight(), 
                0, opacity);
        
        // Top right.
        cornerSprite.draw(
                x + offsetX + width - cornerSprite.getWidth(), 
                y + offsetY, 
                cornerSprite.getWidth(), cornerSprite.getHeight(), 
                Math.toRadians(90), opacity);
        
        // Bottom left.
        cornerSprite.draw(
                x + offsetX, 
                y + offsetY + height - cornerSprite.getWidth(), 
                cornerSprite.getWidth(), cornerSprite.getHeight(), 
                Math.toRadians(-90), opacity);
        
        // Bottom right.
        cornerSprite.draw(
                x + offsetX + width - cornerSprite.getWidth(), 
                y + offsetY + height - cornerSprite.getHeight(), 
                cornerSprite.getWidth(), cornerSprite.getHeight(), 
                Math.toRadians(180), opacity);  
        
        // Draw the horizontal sides.
        horizontalSprite.draw(
                x + offsetX + cornerSprite.getWidth(), 
                y + offsetY,
                width - cornerSprite.getWidth() * 2, 
                horizontalSprite.getHeight(),
                0, opacity);        
        
        // Top.
//        final int topLimit = (int) Math.ceil((double) width / (double) horizontalSprite.getWidth());              
//        for (int i = 0; i < topLimit; i++)
//        {
//            // See if we're the last part, and if we are, chop it a bit.
//            if (i == topLimit - 1)
//            {   
//                int finalWidth = width 
//                        - cornerSprite.getWidth() * 2 
//                        - (topLimit - 1) * horizontalSprite.getWidth();
//                
//                horizontalSprite.drawRegion(
//                    x + offsetX + cornerSprite.getWidth() + horizontalSprite.getWidth() * i, 
//                    y + offsetY,        
//                    horizontalSprite.getWidth(),
//                    horizontalSprite.getHeight(),
//                    0, 0,
//                    finalWidth, horizontalSprite.getHeight(),
//                    0, opacity);
//            }
//            else
//            {                   
//                horizontalSprite.draw(
//                    x + offsetX + cornerSprite.getWidth() + horizontalSprite.getWidth() * i, 
//                    y + offsetY,        
//                    horizontalSprite.getWidth(),
//                    horizontalSprite.getHeight(),                    
//                    0, opacity);   
//            }                               
//        }
                   
        // Bottom.
        horizontalSprite.draw(
                x + offsetX + cornerSprite.getWidth(), 
                y + offsetY + height - cornerSprite.getHeight(),
                width - cornerSprite.getWidth() * 2, 
                horizontalSprite.getHeight(),
                Math.toRadians(180), opacity);
        
        // Draw the vertical sides.
        
        // Left.
        verticalSprite.draw(
                x + offsetX, 
                y + offsetY + cornerSprite.getHeight(),
                verticalSprite.getWidth(), 
                height - cornerSprite.getHeight() * 2,
                0, opacity);
        
        // Right.
        verticalSprite.draw(
                x + offsetX + width - cornerSprite.getWidth(), 
                y + offsetY + cornerSprite.getHeight(),
                verticalSprite.getWidth(), 
                height - cornerSprite.getHeight() * 2,
                Math.toRadians(180), opacity);
        
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
            handleMoved(window.getMouseImmutablePosition());
            
            window.addMouseListener(this);            
        }
        // If we're removing it.
        else
        {          
            // Clear the last mouse position.
            handleMoved(ImmutablePosition.ORIGIN);
            
            //window.setCursor(Cursor.DEFAULT_CURSOR);            
            window.removeMouseListener(this);            
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
    
}
