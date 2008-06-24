package ca.couchware.wezzle2d.util;

import java.awt.Point;

/**
 * A class that represents an arbitrary (x, y) position.
 * @author cdmckay
 *
 */

public class XYPosition
{
	public final int x;
	public final int y;
	
    /**
     * Creates an XYPosition from two integer coordinates.
     * 
     * @param x
     * @param y
     */
	public XYPosition(final int x, final int y)
	{
		this.x = x;
		this.y = y;
	}
    
    /**
     * Creates an XYPosition from an AWT Point.
     * 
     * @param pt
     */
    public XYPosition(Point pt)
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
    public String toString()
    {
        return "(" + x + "," + y + ")";
    }
}
