package ca.couchware.wezzle2d;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * Manages the game board.  A replacement for the GameBoard class from
 * the SVG-based Wezzle.
 *  
 * @author cdmckay
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
	 * The minimum number of tiles in a match.
	 */
	final private int minimumMatch;
	
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
	private TileEntity[] board;
	
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
		
		// Set the minimum match length.
		this.minimumMatch = 3;
		
		// Set the board width and height.
		this.width = columns * cellWidth;
		this.height = rows * cellHeight;
		
		// Initialize board.
		board = new TileEntity[columns * rows];
	}
	
	/**
	 * Generates a random game board with the given parameters.
	 * @param tileMax
	 */
	public void generateBoard(int tileMax)
	{
		for (int i = 0; i < tileMax; i++)
			this.createTile(i, TileEntity.randomColor());
		
		shuffleBoard();
		refactorBoard();
		
		HashSet set = new HashSet();		

		while (true)
		{
			findXMatch(set);
			findYMatch(set);
			
			if (set.size() > 0)
			{
				for (Iterator it = set.iterator(); it.hasNext(); )
				{
					Integer n = (Integer) it.next();
					this.createTile(n.intValue(), TileEntity.randomColor());
				}
				
				set.clear();
			}
			else
				break;
		}
				
	}
	
	/**
	 * Shuffles the board randomly.
	 */
	private void shuffleBoard()
	{
		for (int i = 0; i < cells; i++)
			swapTile(i, Util.random.nextInt(cells));
	}
	
	/**
	 * An instant refactor used for generating boards.
	 */
	private void refactorBoard()
	{
		startShiftDown();
		for (int i = 0; i < cells; i++)
			if (board[i] != null)
				board[i].setYMovement(rows * cellHeight);
		moveAll(1000);
		
		synchronize();
		
		startShiftLeft();
		for (int i = 0; i < cells; i++)
			if (board[i] != null)
				board[i].setXMovement(-columns * cellWidth);
		moveAll(1000);
		
		synchronize();
	}
	
	/**
	 * Searches for all matches in the X-direction and returns a linked list
	 * with the indices of the matches.
	 * @param set The linked list that will be filled with indices.
	 */
	public void findXMatch(Set set)
	{
		// Cycle through the board looking for a match in the X-direction.
		for (int i = 0; i < cells; i++)
		{
			// Check to see if there's even enough room for an X-match.
			if (columns - (i % columns) < minimumMatch)
				continue;
			
			// Make sure there's a tile here.
			if (board[i] == null)
				continue;
			
			// Get the color of this tile.
			int color = board[i].getColor();
			
			// See how long we have a match for.
			int j;
			for (j = 1; j < (columns - (i % columns)); j++)
			{
				if (board[i + j] == null || board[i + j].getColor() != color)
					break;
			}
			
			// Check if we have a match.
			if (j >= minimumMatch)
			{
				Util.handleMessage("XMatch of length " + j + " found.", Thread.currentThread());
				
				// Copy all matched locations to the linked list.
				for (int k = i; k < i + j; k++)				
					set.add(new Integer(k));				
				
				i += j - 1;
			}
		}
	}
	
	/**
	 * Searches for all matches in the Y-direction and returns a set
	 * with the indices of the matches.
	 * @param set The linked list that will be filled with indices.
	 */
	public void findYMatch(Set set)
	{
		// Cycle through the board looking for a match in the Y-direction.
		for (int i = 0; i < cells; i++)
		{
			// Transpose i.
			int ti = Util.pseudoTranspose(i, columns, rows);
			
			// Check to see if there's even enough room for an Y-match.
			if (rows - (ti / columns) < minimumMatch)
				continue;
			
			// Make sure there's a tile here.
			if (board[ti] == null)
				continue;
			
			// Get the color of this tile.
			int color = board[ti].getColor();
			
			// See how long we have a match for.
			int j;
			for (j = 1; j < (rows - (ti / columns)); j++)
			{
				// Transpose i + j.
				int tij = Util.pseudoTranspose(i + j, columns, rows);
				
				if (board[tij] == null 
						|| board[tij].getColor() != color)
					break;
			}
			
			// Check if we have a match.
			if (j >= minimumMatch)
			{
				Util.handleMessage("YMatch of length " + j + " found.", Thread.currentThread());
				
				// Copy all matched locations to the linked list.
				for (int k = i; k < i + j; k++)				
					set.add(new Integer(Util.pseudoTranspose(k, columns, rows)));				
				
				i += j - 1;
			}
		}
	}
	
	/**
	 * Synchronizes the current board array with where the tiles are current
	 * are on the board.  Usually called after a refactor so that the board
	 * array will accurately reflect the board.
	 */
	public void synchronize()
	{				
		TileEntity[] newBoard = new TileEntity[columns * rows];
		
		for (int i = 0; i < cells; i++)
		{			
			if (board[i] != null)
			{
				TileEntity t = board[i];
//				Util.handleMessage("" + (t.getX() - x), null);
				int column = (t.getX() - x) / cellWidth;
				int row = (t.getY() - y) / cellHeight;
												
//				if (column + (row * columns) != i)
//				{
					newBoard[column + (row * columns)] = board[i];
//					board[i] = null;
//				}
			}
		}
		
		board = newBoard;
	}
	
	public void startShiftDown()
	{
		// Start them moving down.
		for (int i = 0; i < cells; i++)
		{
			if (board[i] != null)
			{
				board[i].setYMovement(200);
				board[i].calculateBottomBound(countTilesBelowCell(i));
			}
		}
	}
	
	public void startShiftLeft()
	{
		// Start them moving left.
		for (int i = 0; i < cells; i++)
		{
			if (board[i] != null)
			{
				board[i].setXMovement(-200);
				board[i].calculateLeftBound(countTilesLeftOfCell(i));
			}
		}
	}
	
	/**
	 * Moves all currently moving tiles.
	 * @returns True if there is still more moving to happen.
	 */
	public boolean moveAll(long delta)
	{
		// Set to true if there are more movement to happen.
		boolean moreMovement = false;
		
		for (int i = 0; i < cells; i++)		
			if (board[i] != null)
			{
				board[i].move(delta);
				if (board[i].getXMovement() != 0 
						|| board[i].getYMovement() != 0)
					moreMovement = true;
			}
		
		return moreMovement;
	}
	
	/**
	 * Counts all the tiles that are under the tile at the specified
	 * index.
	 * 
	 * For example, if we had a 3x3 board like this:
	 * 
	 * 012 .X.
	 * 345 XX.
	 * 678 .XX
	 * 
	 * where "X" is a tile and "." is an empty space, then calling
	 * this method on index 1 would return 2.
	 * 
	 * @param index
	 * @return
	 */
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
	
	/**
	 * See <pre>countTilesBelowCell</pre>.
	 * @param index
	 * @return
	 */
	public int countTilesLeftOfCell(int index)
	{
		// Sanity check.
		assert(index >= 0 && index < cells);
		
		// The current column and row.
		int column = index % columns;
		int row = index / columns;
		
		// If we're at the bottom row, return 0.
		if (column == 0)
			return 0;
		
		// The tile count.
		int count = 0;
		
		// Cycle through the column rows, counting tiles.
		for (int i = column - 1; i >= 0; i--)
			if (getTile(i, row) != null)
				count++;
		
		// Return the count.
		return count;
	}
	
//	/**
//	 * Generates a random board with a specified amount of normal, star and bomb tiles
//	 * 
//	 * @param numNormal     The number of normal tiles on the board.
//	 * @param numMultiplier The number of multiplier tiles on the board.
//	 * @param numBomb 	The number of bombs on the board. Must be < numMultiplier.	 
//	 */			
//	public void generateGameBoard(LinkedList<ItemDescriptor> itemList)
//	{					
//		// Determine the amount of items to add.
//		int itemTotal = 0;
//		for (ItemDescriptor item : itemList)
//			itemTotal += item.getInitialAmount();
//		
//		// Sanity check.
//		assert(itemTotal < cells);
//		
//		// This variable keeps track of where we are in the board.
//		int j = 0;
//		
//		// Cycle through items, adding each.
//		for (ItemDescriptor item : itemList)
//		{
//			for (int i = j; i < j + item.getInitialAmount(); i++)
//			{		
//				try
//				{
//					// Create an instance of it and add it to the board.
//					this.setTile(i, (TileEntity) item.getItemClass().newInstance(), false);																				
//				}
//				catch (Exception e)
//				{
//					Util.handleException(e);
//				}
//			}
//		
//			// Keep track of where we left off on the board.
//			j += item.getInitialAmount();
//		}
//
//		// Shuffle.		
//		this.shuffleGameBoard();
//		
//		// Colour the board.
//		this.colorGameBoard();		
//	}
//	
//	public void shuffleGameBoard()
//	{
//		for (int i = 0; i < cells; i++)
//		{
//			int r = Util.random.nextInt(cells);
//			swapTile(i, r);
//		}		
//	}
//	
//	/**
//	 * A method to colour the private gameBoard based on a probability for tile
//	 * distribution.
//	 * 
//	 * @param prob
//	 *            An array of integer probabilities for tile distribution.
//	 */
//	public void colorGameBoard()
//	{		
//		// The potential colour for the tile.
//		int color = 0;
//		
//		// The tile we are colouring.
//		Tile tile;
//		
//		// First, refactor game board.
//		this.refactorGameBoard(Game.SPEED_INSTANT);	
//		
//		for (int row = this.getRows() - 1; row >= 0; row--)
//		{
//			for (int col = 0; col < this.getColumns(); col++)
//			{
//				// Pick a random colour.
//				color = Math.abs(Util.random.nextInt()) % 5;
//								
//				// If there is no tile here.
//				if (this.getTile(col, row) == null)
//					continue;
//				
//				while (true)
//				{					
//					// Check to see if we can place the tile here.
//					if (col > 2)
//					{
//						// If both pieces to the left are the same, do not
//						// colour.
//						if (this.getTile(col - 1, row).getColor() == color
//								&& this.getTile(col - 2, row).getColor() == color
//								&& this.getTile(col - 3, row).getColor() == color)
//						{
//							color = (color + 1) % 5;
//							continue;
//						}
//					}
//	
//					if (row < (this.getRows() - 3))
//					{
//						if (  this.getTile(col, row + 1).getColor()== color
//								&& this.getTile(col, row + 2).getColor() == color
//								&& this.getTile(col, row + 3).getColor() == color)
//						{
//							color = (color + 1) % 5;
//							continue;
//						}
//					}
//					
//					// Otherwise colour the piece.					
//					tile = this.getTile(col, row);
//					
//					// Set the colour.
//					tile.setColor(color);						
//					
//					// Add it to the board.
//					this.setTile(col, row, tile);									
//					
//					// Make it visible.
//					//Util.handleMessage(tile.getVisibility(), Thread.currentThread());
//					//tile.setVisibility("inherit");
//					//tile.updateUsingComponent();
//					break;
//					
//				} // end while
//			} // end for
//		} // end for	
//		
//		// Print the dist.
//		//board.printTileDist();		
//	}
	
	public void createTile(final int index, final int color)
	{
		// Sanity check.
		assert(index < columns * rows);
		
		TileEntity t = new TileEntity(this, color, 
				x + (index % columns) * cellWidth,
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

	public void swapTile(int index1, int index2)
	{
		// Validate parameters.
		assert(index1 >= 0 && index1 < cells);
		assert(index2 >= 0 && index2 < cells);
		
		TileEntity t = board[index1];
		board[index1] = board[index2];
		board[index2] = t;
		
		if (board[index1] != null)
		{
			board[index1].setX(x + (index1 % columns) * cellWidth);
			board[index1].setY(y + (index1 / columns) * cellHeight);
		}
		
		if (board[index2] != null)
		{
			board[index2].setX(x + (index2 % columns) * cellWidth);
			board[index2].setY(y + (index2 / columns) * cellHeight);
		}
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
	
	/**
	 * Prints board to console (for debugging purposes).
	 */
	public void print()
	{
		for (int i = 0; i < board.length; i++)
		{
			if (board[i] == null)
				System.out.print(".");
			else
				System.out.print("X");
			
			if (i % columns == columns - 1)
				System.out.println();
		}
		
		System.out.println();
	}
}
