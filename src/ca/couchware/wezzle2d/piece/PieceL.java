package ca.couchware.wezzle2d.piece;

import ca.couchware.wezzle2d.util.Util;

/**
 * A piece class representing the Diagonal piece.
 * @author cdmckay
 *
 */

public class PieceL extends Piece
{
	public PieceL()
	{
		// Run the super's contructor.
		super();
		
		// Set the name.
		this.name = "L";
		this.structure = new Boolean[][] 
		{ 
				{ false, false, false },
				{ false, true,  true },
				{ false, true,  false }
		};
		
		// Set the dimensions.
		this.columns = 2;
		this.rows = 2;
		
		// Set the size.
		this.size = 3;
	}
	
    @Override
	public void rotate()
	{
		Util.swap2d(structure, 1, 0, 2, 1);
		Util.swap2d(structure, 1, 0, 1, 2);
		Util.swap2d(structure, 1, 0, 0, 1);
	}

}
