package ca.couchware.wezzle2d;

/**
 * A class that represents an arbitrary (x, y) position.
 * @author cdmckay
 *
 */

public class XYPosition
{
	public final int x;
	public final int y;
	
	public XYPosition(final int x, final int y)
	{
		this.x = x;
		this.y = y;
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
}
