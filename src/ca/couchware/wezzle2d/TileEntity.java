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
	 * Creates a tile at (x,y) with the specified color.
	 * @param color
	 * @param x
	 * @param y
	 */
	public TileEntity(String color, int x, int y) 
	{
		// Invoke super.		
		super("resources/Tile" + color + ".png", x, y);	
	}

	@Override
	public void collidedWith(Entity other)
	{
		// Do nothing.  This method is probably going to get removed.
	}
}
