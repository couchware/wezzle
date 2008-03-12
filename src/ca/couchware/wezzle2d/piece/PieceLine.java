package ca.couchware.wezzle2d.piece;

import ca.couchware.wezzle2d.Util;

/**
 * A piece class representing the Line piece.
 * @author cdmckay
 *
 */

public class PieceLine extends Piece
{
	public PieceLine()
	{
		// Run the super's contructor.
		super();
		
		// Set the name.
		this.name = "Line";
		this.structure = new Boolean[][] 
		{ 
				{ false, false, false },
				{ true,  true,  true },				
				{ false, false, false }
		};
		
		// Set the dimensions.
		this.columns = 1;
		this.rows = 3;
		
		// Set the size.
		this.size = 3;
	}
	
	public void rotate()
	{
		// Transpose the structure.		
		Util.transpose(structure);
		
		// Update the dimensions.
		int swap = this.columns;
		this.columns = this.rows;
		this.rows = swap;
	}

}
