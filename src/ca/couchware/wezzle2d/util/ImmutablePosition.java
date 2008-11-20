package ca.couchware.wezzle2d.util;

import java.awt.Point;

/**
 * A class that represents an arbitrary (x, y) position.
 * @author cdmckay
 *
 */

public final class ImmutablePosition
{
    public static final ImmutablePosition ORIGIN = new ImmutablePosition(0, 0);
    
	private final int x;
	private final int y;
	
    /**
     * Creates an XYPosition from two integer coordinates.
     * 
     * @param x
     * @param y
     */
	public ImmutablePosition(final int x, final int y)
	{
		this.x = x;
		this.y = y;
	}
    
    /**
     * Creates an XYPosition from an AWT Point.
     * 
     * @param pt
     */
    public ImmutablePosition(Point pt)
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
    
    public double distanceTo(ImmutablePosition p)
    {
        return Math.sqrt(Util.sq(p.x - x) + Util.sq(p.y - y));
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
