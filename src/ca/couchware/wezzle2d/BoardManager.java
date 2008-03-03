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
	 * The total number of cells.
	 */
	final private int cells;
	
	/**
	 * The width of the board.
	 */
	final private int width;
	
	/**
	 * The height of the board.
	 */
	final private int height;
	
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
		this.cells = columns * rows;
		
		// Set the board width and height.
		this.width = columns * cellWidth;
		this.height = rows * cellHeight;
		
		// Initialize board.
		board = new TileEntity[columns * rows];
	}
	
	public void startShiftDown()
	{
		// Start them moving down.
		for (int i = 0; i < cells; i++)
		{
			if (board[i] != null)
			{
				board[i].setYMovement(150);
				board[i].calculateBottomBound(countTilesBelowCell(i));
			}
		}
	}
	
	/**
	 * Moves all currently moving tiles.
	 * @returns True if there is still more moving to happen.
	 */
	public boolean moveAll(long delta)
	{
		for (int i = 0; i < cells; i++)		
			if (board[i] != null)
				board[i].move(delta);
		
		return true;
	}
	
	public int countTilesBelowCell(int index)
	{
		// Sanity check.
		assert(index >= 0 && index < cells);
		
		// The current column and row.
		int column = index % columns;
		int row = index / columns;
		
		// If we're at the bottom row, return 0.
		if (row == rows - 1)
			return 0;
		
		// The tile count.
		int count = 0;
		
		// Cycle through the column rows, counting tiles.
		for (int j = row + 1; j < rows; j++)
			if (getTile(column, j) != null)
				count++;
		
		// Return the count.
		return count;
	}
	
	public void createTile(final int index, final String color)
	{
		// Sanity check.
		assert(index < columns * rows);
		
		TileEntity t = new TileEntity(this, color, x + (index % columns) * cellWidth,
				y + (index / columns) * cellHeight);
		
		setTile(index, t);
	}
	
	public TileEntity getTile(int index)
	{
		// Sanity check.
		assert(index >= 0 && index < columns * rows);
		
		// Set the tile.
		return board[index];
	}
	
	public TileEntity getTile(int column, int row)
	{
		// Make sure we're within parameters.
		assert(column >= 0 && row >= 0 && column < columns && row < rows);
		
		return getTile(column + (row * columns));
	}
	
	public void setTile(int index, TileEntity tile)
	{
		// Sanity check.
		assert(index >= 0 && index < columns * rows);
		
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
	 * Gets the width.
	 * @return The width.
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * Gets the height.
	 * @return The height.
	 */
	public int getHeight()
	{
		return height;
	}

	/**
	 * Gets the x.
	 * @return The x.
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Gets the y.
	 * @return The y.
	 */
	public int getY()
	{
		return y;
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
