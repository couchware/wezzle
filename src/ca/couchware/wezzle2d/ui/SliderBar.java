/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.*;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RectangularShape;

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
        spriteRail.draw(x, y + 7);
        
        // Draw the handle.
        spriteHandle.draw(x, y);
    }

    public void mouseClicked(MouseEvent e)
    {
        // Intentionally blank.
    }

    public void mousePressed(MouseEvent e)
    {
        // Intentionally blank.
    }

    public void mouseReleased(MouseEvent e)
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

    public void mouseDragged(MouseEvent e)
    {
        // Intentionally blank.
    }

    public void mouseMoved(MouseEvent e)
    {
        // Intentionally blank.
    }
    
}
