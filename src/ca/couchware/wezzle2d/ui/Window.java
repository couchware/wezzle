/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.IBuilder;
import ca.couchware.wezzle2d.IGameWindow;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.graphics.ISprite;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class Window extends AbstractEntity
{

    /**
     * The sprite for a corner of the window frame.
     */
    final protected String PATH_CORNER = 
            Game.SPRITES_PATH + "/WindowCorner.png";
    
    /**
     * The sprite for the horizontal (top or bottom) part of the window frame.
     */
    final protected String PATH_HORIZONTAL =
            Game.SPRITES_PATH + "/WindowHorizontal.png";
    
    /**
     * The sprite for the vertical (left or right) part of the window frame.
     */
    final protected String PATH_VERTICAL =
            Game.SPRITES_PATH + "/WindowVertical.png";
    
    /**
     * The reference to the game window.
     */
    protected IGameWindow window;
    
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
        
        // Create the sprites.
        cornerSprite = ResourceFactory.get().getSprite(PATH_CORNER);
        horizontalSprite = ResourceFactory.get().getSprite(PATH_HORIZONTAL);
        verticalSprite = ResourceFactory.get().getSprite(PATH_VERTICAL);
        
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
        
        public Window end()
        {
            return new Window(this);
        }                
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
        
        // Top.
        horizontalSprite.draw(
                x + offsetX + cornerSprite.getWidth(), 
                y + offsetY,
                width - cornerSprite.getWidth() * 2, 
                horizontalSprite.getHeight(),
                0, opacity);
        
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
    
}
