package ca.couchware.wezzle2d.piece;

/**
 * A piece class representing the Dot piece.
 * @author cdmckay
 *
 */

public class PieceDot extends Piece
{
	public PieceDot()
	{
		// Run the super's contructor.
		super("Dot", PieceType.DOT, 1);
		
		// Set the name.		
		this.structure = new Boolean[][] 
		{ 
				{ false, false, false },
				{ false, true, false },
				{ false, false, false }
		};
		
		// Set the dimensions.
		this.columns = 1;
		this.rows = 1;		
	}
}
