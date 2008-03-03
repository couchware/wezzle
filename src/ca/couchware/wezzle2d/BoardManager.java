package ca.couchware.wezzle2d;

/**
 * Manages the game board.  A replacement for the GameBoard class from
 * the SVG-based Wezzle. * @author cdmckay
 *
 */

public class BoardManager
{		
	/**
	 * The x-coordiante of the top left corner of the board.
	 */
	final private int x;
	
	/**
	 * The y-coordinate of the top left corner of the board.
	 */
	final private int y;
	
	/**
	 * The number of columns in the game board.
	 */
	final private int columns;
	
	/**
	 * The number of rows in the game board.
	 */
	final private int rows;
	
	/**
	 * The width of a grid cell.
	 */
	final private int cellWidth;
	
	/**
	 * The height of a grid cell.
	 */
	final private int cellHeight;
	
	/**
	 * The array representing the game board.
	 */
	final private TileEntity[] board;
	
	/**
	 * The constructor.
	 */
	public BoardManager(final int x, final int y, final int columns, final int rows)
	{
		// Set the cell width and height. Hard-coded to 32x32 for now.
		this.cellWidth = 32;
		this.cellHeight = 32;
		
		// Set the x and y coordinates.
		this.x = x;
		this.y = y;
		
		// Set columns and rows.
		this.columns = columns;
		this.rows = rows;
		
		// Initialize board.
		board = new TileEntity[columns * rows];
	}
	
	public void createTile(final int index, final String color)
	{
		// Sanity check.
		assert(index < columns * rows);
		
		TileEntity t = new TileEntity(color, x + (index % columns) * cellWidth,
				y + (index / columns) * cellHeight);
		
		setTile(index, t);
	}
	
	public void setTile(int index, TileEntity tile)
	{
		// Sanity check.
		assert(index < columns * rows);
		
		// Set the tile.
		board[index] = tile;
	}
	
	public void setTile(int column, int row, TileEntity tile)
	{
		// Make sure we're within parameters.
		assert(column < columns && row < rows);
		
		// Forward.
		setTile(column + (row * columns), tile);
	}

	/**
	 * @return The cellWidth.
	 */
	public int getCellWidth()
	{
		return cellWidth;
	}

	/**
	 * @return The cellHeight.
	 */
	public int getCellHeight()
	{
		return cellHeight;
	}	
	
	/**
	 * Draw the board to the screen.
	 */
	public void draw()
	{
		for (int i = 0; i < board.length; i++)
			if (board[i] != null)
				board[i].draw();
	}
}
