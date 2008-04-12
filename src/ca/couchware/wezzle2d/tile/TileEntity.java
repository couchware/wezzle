package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.*;
import java.awt.Rectangle;

/**
 * A class representing a game tile.
 * @author cdmckay
 *
 */

public class TileEntity extends GraphicEntity implements Movable
{
    
    final public static int COLOR_BLUE = 0;
	final public static int COLOR_GREEN = 1;
	final public static int COLOR_PURPLE = 2;
	final public static int COLOR_RED = 3;		
	final public static int COLOR_YELLOW = 4;
	
	final public static int NUMBER_OF_COLORS = 5;        
    
    /**
     * The square root of 2.
     */
    final private static double SQRT_2 = Math.sqrt(2);
	
	/**
	 * The associated board manager.
	 */
	protected final BoardManager boardMan;
	
	/**
	 * The colour of the tile.
	 */
	protected final int color;
	
	/**
	 * The current bottom bound.
	 */
	protected int bottomBound;
	
	/**
	 * The current left bound.
	 */
	protected int leftBound;
    
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
	public TileEntity(BoardManager boardMan, int color, int x, int y) 
	{
		// Invoke super.		
		super(Game.SPRITES_PATH + "/Tile" + toColorString(color) + ".png", 
                x, y);                
						
        // Set the position.
        this.x2 = x;
        this.y2 = y;
        
        this.x2_ = x;
        this.y2_ = y;
        
		// Set board manager and color reference.
		this.boardMan = boardMan;	
		this.color = color;
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
			if (y2 > bottomBound)
			{
				y2 = bottomBound;
				dy = 0;
			}
		}	
		
		if (dx != 0)
		{						
			// Move the tile.
			x2 += (delta * dx) / 1000;
			
			// Make sure we haven't exceeded our bound.
			// If we have, stop moving.
			if (x2 < leftBound)
			{
				x2 = leftBound;
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
	public int getX()
	{
		return (int) x2;
	}
    
	/**
	 * @param x The x to set.
	 */
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
	public int getY()
	{               
		return (int) y2;
	}
    
    /**
	 * @param y The y to set.
	 */
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
	public int getColor()
	{
		return color;
	}

	/**
	 * Sets the bottomBound.
	 */
	public void calculateBottomBound(int tilesInColumn)
	{
		this.bottomBound = boardMan.getY() + boardMan.getHeight() 
                - (tilesInColumn * boardMan.getCellHeight());
		this.bottomBound -= boardMan.getCellHeight();
	}
	
	/**
	 * Sets the leftBound.
	 */
	public void calculateLeftBound(int tilesInRow)
	{
		this.leftBound = boardMan.getX() + (tilesInRow * boardMan.getCellWidth());
	}
	
	public static int randomColor()
	{
		return Util.random.nextInt(NUMBER_OF_COLORS);
	}
	
	private static String toColorString(int color)
	{
		switch (color)
		{
			case COLOR_BLUE:
				return "Blue";
				
			case COLOR_GREEN:
				return "Green";
				
			case COLOR_PURPLE:
				return "Purple";
				
			case COLOR_RED:
				return "Red";
				
			case COLOR_YELLOW:
				return "Yellow";
				
			default:
				Util.handleWarning("Unknown color number. Defaulting to 'Red'.", 
                        Thread.currentThread());
				return "Red";
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
        
        return rect1;
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
