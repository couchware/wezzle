package ca.couchware.wezzle2d.piece;

public abstract class Piece
{
	// ---------------------------------------------------------------------------
	// Constants
	// ---------------------------------------------------------------------------	    
	
	public static final int MAX_COLUMNS = 3;
	public static final int MAX_ROWS = 3;
	
	// ---------------------------------------------------------------------------
	// Instance Attributes
	// ---------------------------------------------------------------------------
	
	/**
	 * The internal name for the piece.
	 */
	protected String name;
	
	/**
	 * The 3x3 array of what the piece fills up.
	 */
	protected Boolean [][] structure;
	
	/**
	 * The number of columns in the piece.
	 */
	protected int columns;
	
	/**
	 * The number of rows in the piece.
	 */
	protected int rows;
	
	/**
	 * The size of the piece, in tiles.
	 */
	protected int size;
	
	// ---------------------------------------------------------------------------
	// Instance Methods
	// ---------------------------------------------------------------------------

	/**
	 * Rotates the piece to the right.
	 */
	public void rotate()
	{
		// Intentionally blank.  Meant to be overridden by subclass.
	}
	
	// ---------------------------------------------------------------------------
	// Getters and Setters
	// ---------------------------------------------------------------------------
	
	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the structure
	 */
	public Boolean[][] getStructure()
	{
		return structure;
	}

	/**
	 * @return the columns
	 */
	public int getColumns()
	{
		return columns;
	}

	/**
	 * @return the rows
	 */
	public int getRows()
	{
		return rows;
	}

	/**
	 * @return the size
	 */
	public int getSize()
	{
		return size;
	}
}
