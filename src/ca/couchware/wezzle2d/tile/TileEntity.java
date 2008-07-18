package ca.couchware.wezzle2d.tile;

import static ca.couchware.wezzle2d.BoardManager.Direction;
import ca.couchware.wezzle2d.tile.TileColor;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.graphics.Movable;
import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.*;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 * A class representing a game tile.
 * @author cdmckay
 *
 */

public class TileEntity extends GraphicEntity implements Movable
{                                 
	
	/**
	 * The associated board manager.
	 */
	protected final BoardManager boardMan;
	
    /**
     * The bounds.
     */
    protected final EnumMap<Direction, Integer> bounds;
    
	/**
	 * The colour of the tile.
	 */
	protected final TileColor color;		
    
    /**
     * Make x a double.
     */
    protected double x2;
    protected double x2_;
    
    /**
     * Make y a double.
     */
    protected double y2;
    protected double y2_;
    
    /** 
     * The current speed of this entity horizontally (pixels/s). 
     */
	protected double dx;
	
	/** 
     * The current speed of this entity vertically (pixels/s). 
     */
	protected double dy;
    
	/**
	 * Creates a tile at (x,y) with the specified color.
	 * @param color
	 * @param x
	 * @param y
	 */
	public TileEntity(BoardManager boardMan, TileColor color, int x, int y) 
	{
		// Invoke super.		
		super(x, y, 
                Game.SPRITES_PATH + "/Tile" + color + ".png");
                				
        // Set the position.
        this.x2 = x;
        this.y2 = y;
        
        this.x2_ = x;
        this.y2_ = y;
        
		// Set board manager and color reference.
		this.boardMan = boardMan;	
		this.color = color;
                        
        // Create the bounds array.  1 entry for each possible direction.       
        bounds = new EnumMap<Direction, Integer>
                (Direction.class);
        
        bounds.put(Direction.UP, Integer.MIN_VALUE);
        bounds.put(Direction.DOWN, Integer.MAX_VALUE);
        bounds.put(Direction.LEFT, Integer.MIN_VALUE);
        bounds.put(Direction.RIGHT, Integer.MAX_VALUE);
	}
	
	/**
	 * Override move.
	 */
	public void move(long delta)
	{        
		if (dy != 0)
		{
			// Move the tile.
			y2 += (delta * dy) / 1000;
			
			// Make sure we haven't exceeded our bound.
			// If we have, stop moving.
			if (y2 > bounds.get(Direction.DOWN))
			{
				y2 = bounds.get(Direction.DOWN);
				dy = 0;
			}
            else if (y2 < bounds.get(Direction.UP))
            {
                y2 = bounds.get(Direction.UP);
                dy = 0;
            }
		}	
		
		if (dx != 0)
		{						
			// Move the tile.
			x2 += (delta * dx) / 1000;
			
			// Make sure we haven't exceeded our bound.
			// If we have, stop moving.
            if (x2 > bounds.get(Direction.RIGHT))
            {
                x2 = bounds.get(Direction.RIGHT);
                dx = 0;
            }
            else if (x2 < bounds.get(Direction.LEFT))
			{
				x2 = bounds.get(Direction.LEFT);
				dx = 0;
			}
		}	                   
        
        setDirty(true);
    }
    
    /**
	 * Get the x location of this entity.
	 * 
	 * @return The x location of this entity.
	 */
    @Override
	public int getX()
	{
		return (int) x2;
	}
    
	/**
	 * @param x The x to set.
	 */
    @Override
	public void setX(final int x)
	{                            		
        this.x2 = x;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
	}

	/**
	 * Get the y location of this entity.
	 * 
	 * @return The y location of this entity.
	 */
    @Override
	public int getY()
	{               
		return (int) y2;
	}
    
    /**
	 * @param y The y to set.
	 */
    @Override
	public void setY(final int y)
	{		
        this.y2 = y;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
	}
	
    /**
	 * Get the horizontal speed of this entity
	 * 
	 * @return The horizontal speed of this entity (pixels/s).
	 */
	public double getXMovement()
	{
		return dx;
	}
    
	/**
	 * Set the horizontal speed of this entity
	 * 
	 * @param dx
	 *            The horizontal speed of this entity (pixels/s).
	 */
	public void setXMovement(double dx)
	{
		this.dx = dx;
	}

    /**
	 * Get the vertical speed of this entity
	 * 
	 * @return The vertical speed of this entity (pixels/ms).
	 */
	public double getYMovement()
	{
		return dy;
	}
    
	/**
	 * Set the vertical speed of this entity
	 * 
	 * @param dy
	 *            The vertical speed of this entity (pixels/s).
	 */
	public void setYMovement(double dy)
	{
		this.dy = dy;
	}
    
	/**
	 * Gets the color.
	 * @return The color.
	 */
	public TileColor getColor()
	{
		return color;
	}

    public void calculateBound(Direction direction, int tileCount)
    {
        switch (direction)
        {
            case UP:
                
                bounds.put(direction, boardMan.getY()
                        + (tileCount * boardMan.getCellHeight()));
                break;
                
            case DOWN:
                
                bounds.put(direction, boardMan.getY() 
                        + boardMan.getHeight() 
                        - ((tileCount + 1) * boardMan.getCellHeight()));
                
                break;
                
            case LEFT:
                
                bounds.put(direction, boardMan.getX() 
                        + (tileCount * boardMan.getCellWidth()));
                
                break;
                
            case RIGHT:
                
                bounds.put(direction, boardMan.getX() 
                        + boardMan.getWidth() 
                        - ((tileCount + 1) * boardMan.getCellWidth()));
                                
                break;
        }
    }
    
	@Override
    public void draw()
	{
        this.x2_ = x2 + offsetX;
        this.y2_ = y2 + offsetY;
        
        this.width_ = width;
        this.height_ = height;
        
        if (isVisible() == false)
            return;
                        
        sprite.draw((int) x2 + offsetX, (int) y2 + offsetY, 
                width, height, theta, opacity);                
	}   
    
    @Override
    public Rectangle getDrawRect()
    {
        // If the draw rect is null, generate it.
        if (drawRect == null)
        {
            int w2 = ((width * 3) / 2 + 1);        
            int h2 = ((height * 3) / 2 + 1);

            int w2_ = ((width_ * 3) / 2 + 1);        
            int h2_ = ((height_ * 3) / 2 + 1);

            Rectangle rect1 = new Rectangle((int) x2_, (int) y2_, 
                    w2_ + 2, h2_ + 2);                
            rect1.translate(-(w2_ - width_) / 2, -(h2_ - height_) / 2);

            Rectangle rect2 = new Rectangle((int) x2, (int) y2, 
                    w2 + 2, h2 + 2);        
            rect2.translate(-(w2 - width) / 2, -(h2 - height) / 2);

            rect2.translate(offsetX, offsetY);            
            rect1.add(rect2);
            
            // Release reference.
            rect2 = null;
            
            // Set the draw rect.
            drawRect = rect1;
        }
        
        return drawRect;
    }

    @Override
    public void resetDrawRect()
    {
        x2_ = x2;
        y2_ = y2;
        
        width_ = width;
        height_ = height;
    }			     
    
}
