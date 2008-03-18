package ca.couchware.wezzle2d;

/**
 * A class representing a game tile.
 * @author cdmckay
 *
 */

public class TileEntity extends Entity
{
    final public static int COLOR_BLUE = 0;
	final public static int COLOR_GREEN = 1;
	final public static int COLOR_PURPLE = 2;
	final public static int COLOR_RED = 3;		
	final public static int COLOR_YELLOW = 4;
	
	final public static int NUMBER_OF_COLORS = 5;        
	
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
	 * Creates a tile at (x,y) with the specified color.
	 * @param color
	 * @param x
	 * @param y
	 */
	public TileEntity(BoardManager boardMan, int color, int x, int y) 
	{
		// Invoke super.		
		super("resources/Tile" + toColorString(color) + ".png", x, y);                
						
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
				Util.handleWarning("Unknown color number. Defaulting to 'Red'.", Thread.currentThread());
				return "Red";
		}
    }
}
