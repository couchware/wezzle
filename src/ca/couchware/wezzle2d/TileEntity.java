package ca.couchware.wezzle2d;

/**
 * A class representing a game tile.
 * @author cdmckay
 *
 */

public class TileEntity extends Entity
{
	final public static String COLOR_BLUE = "Blue";
	final public static String COLOR_GREEN = "Green";
	final public static String COLOR_PURPLE = "Purple";
	final public static String COLOR_RED = "Red";		
	final public static String COLOR_YELLOW = "Yellow";
	
	/**
	 * The associated board manager.
	 */
	protected final BoardManager boardMan;
	
	/**
	 * The colour of the tile.
	 */
	protected final String color;
	
	/**
	 * The current bottom bound.
	 */
	protected int bottomBound;
	
	/**
	 * The current left bound.
	 */
	protected int leftBound;
	
	/**
	 * Creates a tile at (x,y) with the specified color.
	 * @param color
	 * @param x
	 * @param y
	 */
	public TileEntity(BoardManager bm, String color, int x, int y) 
	{
		// Invoke super.		
		super("resources/Tile" + color + ".png", x, y);	
						
		// Set board manager and color reference.
		this.boardMan = bm;	
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
			y += (delta * dy) / 1000;
			
			// Make sure we haven't exceeded our bound.
			// If we have, stop moving.
			if (y > bottomBound)
			{
				y = bottomBound;
				dy = 0;
			}
		}	
		
		if (dx != 0)
		{						
			// Move the tile.
			x += (delta * dx) / 1000;
			
			// Make sure we haven't exceeded our bound.
			// If we have, stop moving.
			if (x < leftBound)
			{
				x = leftBound;
				dx = 0;
			}
		}		
	}
	
	@Override
	public void collidedWith(Entity other)
	{
		// Do nothing.  This method is probably going to get removed.
	}	
	
	/**
	 * Gets the color.
	 * @return The color.
	 */
	public String getColor()
	{
		return color;
	}

	/**
	 * Sets the bottomBound.
	 */
	public void calculateBottomBound(int tilesInColumn)
	{
		this.bottomBound = boardMan.getY() + boardMan.getHeight() - (tilesInColumn * boardMan.getCellHeight());
		this.bottomBound -= boardMan.getCellHeight();
	}
	
	/**
	 * Sets the leftBound.
	 */
	public void calculateLeftBound(int tilesInRow)
	{
		this.leftBound = boardMan.getX() + (tilesInRow * boardMan.getCellWidth());
	}
}
