/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.GameWindow;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.graphics.Entity;
import ca.couchware.wezzle2d.graphics.Sprite;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class Window extends Entity
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
    protected GameWindow window;
    
    /**
     * The corner sprite.
     */
    protected Sprite cornerSprite;
    
    /**
     * The horizontal sprite.
     */
    protected Sprite horizontalSprite;
    
    /**
     * The vertical sprite.
     */
    protected Sprite verticalSprite;
    
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
    public Window(GameWindow window, int x, int y, int width, int height)
    {
        // Set window reference.
        this.window = window;
        
         // Set the position.
        this.x = x;
        this.y = y;
        
        this.x_ = x;
        this.y_ = y;
        
        // Set the dimensions.
        this.width = width;
        this.height = width;
        
        this.width_ = width;
        this.height_ = height;
        
        // Set default anchor.
        this.alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);
        
        // Create the sprites.
        cornerSprite = ResourceFactory.get().getSprite(PATH_CORNER);
        horizontalSprite = ResourceFactory.get().getSprite(PATH_HORIZONTAL);
        verticalSprite = ResourceFactory.get().getSprite(PATH_VERTICAL);
        
        // Create the background colour.  This colour is changed each time
        // the opacity is changed.
        this.color = new Color(0, 0, 0, ((float) opacity) / 100.0f);
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
    public void draw()
    {
        // Draw the inside of the window.
        window.setColor(color);
        window.fillRect(x + offsetX, y + offsetY, width, height);
        
        // Draw the corners.
        
        // Top left.
        cornerSprite.draw(
                x + offsetX - cornerSprite.getWidth(), 
                y + offsetY - cornerSprite.getHeight(), 
                cornerSprite.getWidth(), cornerSprite.getHeight(), 
                0, opacity);
        
        // Top right.
        cornerSprite.draw(
                x + offsetX + width, 
                y + offsetY - cornerSprite.getHeight(), 
                cornerSprite.getWidth(), cornerSprite.getHeight(), 
                Math.toRadians(90), opacity);
        
        // Bottom left.
        cornerSprite.draw(
                x + offsetX - cornerSprite.getWidth(), 
                y + offsetY + height, 
                cornerSprite.getWidth(), cornerSprite.getHeight(), 
                Math.toRadians(-90), opacity);
        
        // Bottom right.
        cornerSprite.draw(
                x + offsetX + width, 
                y + offsetY + height, 
                cornerSprite.getWidth(), cornerSprite.getHeight(), 
                Math.toRadians(180), opacity);  
        
        // Draw the horizontal sides.
        
        // Top.
        horizontalSprite.draw(
                x + offsetX, 
                y + offsetY - horizontalSprite.getHeight(),
                width, horizontalSprite.getHeight(),
                0, opacity);
        
        // Bottom.
        horizontalSprite.draw(
                x + offsetX, 
                y + offsetY + height,
                width, horizontalSprite.getHeight(),
                Math.toRadians(180), opacity);
        
        // Draw the vertical sides.
        
        // Left.
        verticalSprite.draw(
                x + offsetX - verticalSprite.getWidth(), 
                y + offsetY,
                verticalSprite.getWidth(), height,
                0, opacity);
        
        // Right.
        verticalSprite.draw(
                x + offsetX + width, 
                y + offsetY,
                verticalSprite.getWidth(), height,
                Math.toRadians(180), opacity);
    }
    
    @Override
    public Rectangle getDrawRect()
    {
        // Check if the draw rect is null.  If it is, generate a new one.
        if (drawRect == null)
        { 
            Rectangle r = super.getDrawRect();

            r.add(r.getX() - cornerSprite.getWidth(),
                    r.getY() - cornerSprite.getHeight());

            r.add(r.getMaxX() + cornerSprite.getWidth(),
                    r.getMaxY() + cornerSprite.getHeight());
            
            drawRect = r;
            r = null;
        }
        
        return drawRect;
    }
    
}
