package ca.couchware.wezzle2d.piece;

import ca.couchware.wezzle2d.util.ArrayUtil;

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
		super("L", PieceType.L, 3);
		
		// Set the name.		
		this.structure = new Boolean[][] 
		{ 
				{ false, false, false },
				{ false, true,  true },
				{ false, true,  false }
		};
		
		// Set the dimensions.
		this.columns = 2;
		this.rows = 2;				
	}
	
    @Override
	public void rotate()
	{
		ArrayUtil.swap2d(structure, 1, 0, 2, 1);
		ArrayUtil.swap2d(structure, 1, 0, 1, 2);
		ArrayUtil.swap2d(structure, 1, 0, 0, 1);
	}

}
