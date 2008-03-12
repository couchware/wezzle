package ca.couchware.wezzle2d;

/**
 * A class representing a block of a piece.
 * 
 * @author cdmckay
 *
 */

public class PieceEntity extends Entity
{
	public PieceEntity(int x, int y) 
	{
		// Invoke super.		
		super("resources/Selector.png", x, y);		
	}
	
	/**
	 * Request that this entity move itself based on a certain amount of time
	 * passing.
	 * 
	 * @param delta
	 *            The amount of time that has passed in milliseconds.
	 */
	public void move(long delta)
	{
		// Intentionally left blank.  A piece entity (at the moment) moves
		// instantaneously.
	}
}
