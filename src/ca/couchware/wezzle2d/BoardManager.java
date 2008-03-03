package ca.couchware.wezzle2d;

/**
 * Manages the game board.  A replacement for the GameBoard class from
 * the SVG-based Wezzle.  The board manager is a singleton class.
 * @author cdmckay
 *
 */

public class BoardManager
{
	/**
	 * The singleton reference.
	 */
	private static final BoardManager single = new BoardManager();
	
	/**
	 * The number of columns in the game board.
	 */
	final public static int TOTAL_COLUMNS = 8;
	
	/**
	 * The number of rows in the game board.
	 */
	final public static int TOTAL_ROWS = 12;
	
	/**
	 * The array representing the game board.
	 */
	final private static TileEntity[] board = new TileEntity[TOTAL_COLUMNS * TOTAL_ROWS];
	
	/**
	 * The default contructor has been made private to prevent construction of
	 * this class anywhere externally. This is used to enforce the singleton
	 * pattern that this class attempts to follow.
	 */
	public BoardManager()
	{
		// Intentionally left blank.
	}
	
	/**
	 * Retrieve the single instance of this class.
	 * 
	 * @return The single instance of this class.
	 */
	public static BoardManager get()
	{
		return single;
	}
	
	public static void setTile(int index, TileEntity tile)
	{
		// Set the tile.
		board[index] = tile;
	}
	
	public static void setTile(int column, int row, TileEntity tile)
	{
		// Make sure we're within parameters.
		assert(column < TOTAL_COLUMNS && row < TOTAL_ROWS);
		
		// Forward.
		setTile(column + (row * TOTAL_COLUMNS), tile);
	}		
}
