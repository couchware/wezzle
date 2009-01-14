package ca.couchware.wezzle2d.piece;

import ca.couchware.wezzle2d.util.Util;

/**
 * A piece class representing the Diagonal piece.
 * @author cdmckay
 *
 */

public class PieceDiagonal extends Piece
{
	public PieceDiagonal()
	{
		// Run the super's contructor.
		super("Diagonal", PieceType.DIAGONAL, 2);
		
		// Set the name.		
		this.structure = new Boolean[][] 
		{ 
				{ false, false, false },
				{ false, true,  false },
				{ false, false, true  }
		};
		
		// Set the dimensions.
		this.columns = 2;
		this.rows = 2;				
	}
	
    @Override
	public void rotate()
	{
		// Transpose the structure.
		Util.swap2d(structure, 2, 2, 2, 0);
//		Util.swap2d(structure, 0, 0, 2, 0);
//		Util.swap2d(structure, 0, 0, 2, 2);
//		Util.swap2d(structure, 0, 0, 0, 2);
	}

}
