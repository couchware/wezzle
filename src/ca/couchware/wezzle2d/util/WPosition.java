package ca.couchware.wezzle2d.util;

import java.awt.Point;

/**
 * A class that represents an arbitrary (x, y) position.
 * @author cdmckay
 *
 */

public final class WPosition
{
    public static final WPosition ORIGIN = new WPosition(0, 0);
    
	private final int x;
	private final int y;
	
    /**
     * Creates an XYPosition from two integer coordinates.
     * 
     * @param x
     * @param y
     */
	public WPosition(final int x, final int y)
	{
		this.x = x;
		this.y = y;
	}
    
    /**
     * Creates an XYPosition from an AWT Point.
     * 
     * @param pt
     */
    public WPosition(Point pt)
    {
        this.x = pt.x;
        this.y = pt.y;
    }

	/**
	 * Gets the x.
	 * @return The x.
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Gets the y.
	 * @return The y.
	 */
	public int getY()
	{
		return y;
	}
    
    /**
     * Converts it to a pretty string.
     */
    @Override
    public String toString()
    {
        return "(x,y) = (" + x + "," + y + ")";
    }
}
