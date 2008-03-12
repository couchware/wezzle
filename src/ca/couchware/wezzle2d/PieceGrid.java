package ca.couchware.wezzle2d;

import java.util.Arrays;

import ca.couchware.wezzle2d.piece.Piece;

/**
 * The piece grid is a 3x3 matrix that has each cell selectively
 * activated depending on what piece is currently loaded into it.
 * 
 * @author cdmckay
 *
 */

public class PieceGrid implements Drawable
{
	/**
	 * The current structure of the piece, representing by a 2D 
	 * boolean array.  Where the array is true, there is a selector.
	 * Where it is false, there is not.
	 */
	private Boolean[][] structure;
	
	/**
	 * A 2D array of sprites.  Each cell corresponds to a cell in the
	 * structure.  Each cell is only drawn if the corresponding structure
	 * cell is true.
	 */
	private Sprite[][] sprites;
	
	/**
	 * The constructor.  Initializes the structure and sprites arrays.
	 * @param path
	 * @param x
	 * @param y
	 */
	public PieceGrid(String path, int x, int y)
	{
		// Create an blank out the structure.
		structure = new Boolean[Piece.MAX_COLUMNS][Piece.MAX_ROWS];
		
		for (int i = 0; i < structure.length; i++)
			Arrays.fill(structure[i], new Boolean(false));
		
		// Load in all the sprites.
		sprites = new Sprite[Piece.MAX_COLUMNS][Piece.MAX_ROWS];
		
		for (int i = 0; i < sprites.length; i++)
			for (int j = 0; j < sprites[0].length; j++)
				sprites[i][j] = ResourceFactory.get().getSprite(path);
	}	
	
	public void setStructure(final Boolean[][] structure)
	{
		// Save the new array.
		this.structure = structure;
	}

	public void draw()
	{
		// TODO Auto-generated method stub
		
	}
}
