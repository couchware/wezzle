package ca.couchware.wezzle2d.piece;

import ca.couchware.wezzle2d.util.Util;

/**
 * A piece class representing the Dash piece.
 * @author cdmckay
 *
 */

public class PieceDash extends Piece
{
	public PieceDash()
	{
		// Run the super's contructor.
		super("Dash", PieceType.DASH, 2);
		
		// Set the name.		
		this.structure = new Boolean[][]
		{ 
			{ false, false, false },
			{ false, true,  true },
			{ false, false, false }
		};
		
		// Set the dimensions.
		this.columns = 1;
		this.rows = 2;				
	}
	
    @Override
	public void rotate()
	{
		// Transpose the structure.		
		Util.transpose2d(structure);
//		Util.swap2d(structure, 1, 0, 2, 1);
//		Util.swap2d(structure, 1, 0, 1, 2);
//		Util.swap2d(structure, 1, 0, 0, 1);
		
		// Update the dimensions.
		int swap = this.columns;
		this.columns = this.rows;
		this.rows = swap;
	}

}
