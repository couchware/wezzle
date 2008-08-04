package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.*;

/**
 * A class representing a game tile.
 * @author cdmckay
 *
 */

public class TileEntity extends GraphicEntity
{                                 
	
	/**
	 * The associated board manager.
	 */
	protected final BoardManager boardMan;	    
    
	/**
	 * The colour of the tile.
	 */
	protected final TileColor color;		
    
    /**
     * The click runnable.
     */
    protected Runnable clickAction;    
    
	/**
	 * Creates a tile at (x,y) with the specified color.
     * 
	 * @param color
	 * @param x
	 * @param y
	 */
	public TileEntity(BoardManager boardMan, TileColor color, int x, int y) 
	{
		// Invoke super.		
		super(new Builder(x, y, Game.SPRITES_PATH + "/Tile" + color + ".png"));                
        
        // Set the position.
        this.x = x;
        this.y = y;
        this.x_ = x;
        this.y_ = y;
        
		// Set board manager and color reference.
		this.boardMan = boardMan;	
		this.color = color;                               
	}
    
    /**
     * Used to make a new tile entity that is roughly the same as the one
     * passed.
     * 
     * @param tile
     */
    public TileEntity(TileEntity tile)
    {
        this(tile.boardMan, tile.color, tile.x, tile.y);
    }    	
    
	/**
	 * Gets the color.
	 * @return The color.
	 */
	public TileColor getColor()
	{
		return color;
	}   
    
	@Override
    public void draw()
	{          
        this.x_ = x + offsetX;
        this.y_ = y + offsetY;
        
        this.width_ = width;
        this.height_ = height;
        
        if (isVisible() == false)
            return;
                        
        sprite.draw((int) x + offsetX, (int) y + offsetY, 
                width, height, theta, opacity);                
	}   
    
    /**
     * Sets the click runnable.
     */
    public void setClickAction(Runnable clickAction)
    { 
        this.clickAction = clickAction;
    }
    
    /**
     * Gets the click runnable.
     */
    public Runnable getClickAction()
    {
        return clickAction;
    }
    
    /**
     * This method is called when the tile is clicked.
     */
    public void onClick()
    {
        if (clickAction != null) clickAction.run();
    }
    
}
