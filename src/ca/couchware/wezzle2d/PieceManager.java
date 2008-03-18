package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.XYPosition;
import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.tile.TileEntity;
import ca.couchware.wezzle2d.animation.PulseAnimation;
import ca.couchware.wezzle2d.animation.Animation;
import ca.couchware.wezzle2d.piece.Piece;
import ca.couchware.wezzle2d.piece.PieceDash;
import ca.couchware.wezzle2d.piece.PieceDiagonal;
import ca.couchware.wezzle2d.piece.PieceDot;
import ca.couchware.wezzle2d.piece.PieceL;
import ca.couchware.wezzle2d.piece.PieceLine;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * The piece manager keeps track of where the mouse pointer is on the board
 * and places the piece selector accordingly when its draw method is called.
 * 
 * @author cdmckay
 *
 */

public class PieceManager implements 
        Drawable, MouseListener, MouseMotionListener
{	
    /**
     * Whether or not this is visible.
     */
    private boolean visible;
    
    /**
     * Was the board recently refactored?
     */
    private boolean refactored = false;
    
    /**
     * Is the board Dropping in a tile?
     */
    private boolean isTileDropActive = false;
    
    /**
     * The tile drop count.
     */
    private int tileDropCount = 0;
    
	/**
	 * The current location of the mouse pointer.
	 */
	private XYPosition mousePosition;
	
    /**
     * Was the left mouse button clicked?
     */
    private boolean mouseLeftReleased;
    
    /**
     * Was the right mouse button clicked?
     */
    private boolean mouseRightReleased;
    
    /**
     * The current piece.
     */
    private Piece piece;
    
	/**
	 * The piece grid.  The graphical representation of the piece.
	 */
	private PieceGrid pieceGrid;
    
    /**
     * The current piece column.
     */
    private int pieceColumn;
    
    /**
     * The current piece row.
     */
    private int pieceRow;
	
	/**
	 * The board manager the piece manager to attached to.
	 */
	private BoardManager boardMan;

    //--------------------------------------------------------------------------
    // Constructor
    //--------------------------------------------------------------------------
    
	/**
	 * The constructor.
	 * 
	 * @param boardMan The board manager.
	 */
	public PieceManager(BoardManager boardMan)
	{
        // Set visible.
        this.visible = true;
        
		// Set the reference.
		this.boardMan = boardMan;
        
        // Default the mouse buttons to not clicked.
        mouseLeftReleased = false;
        mouseRightReleased = false;
        
        // Create new piece entity at the origin of the board.
		pieceGrid = new PieceGrid(boardMan, 0, 0);
        
        // Load a random piece.
        loadRandomPiece();
		
		// Create initial mouse position.
		mousePosition = new XYPosition(boardMan.getX(), boardMan.getY());
        
        // Adjust the position.
        XYPosition ap = adjustPosition(mousePosition);
        
        // Move the piece there.
        pieceGrid.setX(ap.getX());
        pieceGrid.setY(ap.getY());
	}	
    
    //--------------------------------------------------------------------------
    // Instance Methods
    //--------------------------------------------------------------------------    
  
    /**
     * Load a piece into the piece grid.
	 * @param piece The piece to set.
	 */
	public void loadPiece(final Piece piece)
	{
        // Remember which piece it is for rotating.
        this.piece = piece;
        
        // Load the piece into the piece grid.
		pieceGrid.loadStructure(piece.getStructure());
	}	
    
    /**
     * Loads a random piece into the piece grid.
     */
    public void loadRandomPiece()
    {
		switch(Util.random.nextInt(5))		
		{
			case Piece.PIECE_DOT:
				loadPiece(new PieceDot());
				break;
				
			case Piece.PIECE_DASH:
				loadPiece(new PieceDash());
				break;
				
			case Piece.PIECE_LINE:
				loadPiece(new PieceLine());
				break;
				
			case Piece.PIECE_DIAGONAL:
				loadPiece(new PieceDiagonal());
				break;
				
			case Piece.PIECE_L:
				loadPiece(new PieceL());
				break;
				
			default:
				Util.handleError("Unknown piece.", Thread.currentThread());
				break;
		}
	}
    
    /**
     * Adjusts the position of the piece grid so that it is within the board's
     * boundaries.
     * 
     * @param p The position to adjust.
     * @return The adjusted position.
     */
    public XYPosition adjustPosition(XYPosition p)
	{
		int column = convertXToColumn(p.getX());
		int row = convertYToRow(p.getY());
		
		if (column >= boardMan.getColumns())
			column = boardMan.getColumns() - 1;
		
		if (row >= boardMan.getRows())
			row = boardMan.getRows() - 1;
		
		// Get the piece structure.
		Boolean[][] structure = piece.getStructure();
		
		// Cycle through the structure.
		for (int j = 0; j < structure[0].length; j++)
		{
			for (int i = 0; i < structure.length; i++)
			{	
				if (structure[i][j] == true)
				{					
					if (column - 1 + i < 0)
						column++;					
					else if (column - 1 + i >= boardMan.getColumns())
						column--;
						
					if (row - 1 + j < 0)
						row++;
					else if (row - 1 + j >= boardMan.getRows())
						row--;										
				}										
			} // end for				
		} // end for
		
		return new XYPosition(
                boardMan.getX() + (column * boardMan.getCellWidth()), 
                boardMan.getY() + (row * boardMan.getCellHeight()));
	}
    
    /**
     * Commits the current piece to the current location.  This will result
     * in all tiles covered by the piece to be removed and a refactor will
     * be initiated.
     * 
     * @param p The position of the piece.
     */
    public Set commitPiece(XYPosition p)
    {
        // Get the set of selected tiles.
        Set indexSet = getSelectedIndexSet(p);
        
        // Remove the tiles in the set.        
        boardMan.removeTiles(getSelectedIndexSet(p));
        
        // Return the set.
        return indexSet;
    }
    
    public Set getSelectedIndexSet(XYPosition p)
    {
         // Create a set.
        HashSet indexSet = new HashSet();
        
        // Convert to rows and columns.
        XYPosition ap = adjustPosition(p);
        int column = convertXToColumn(ap.getX());
		int row = convertYToRow(ap.getY());
        
        // Get the piece struture.
        Boolean[][] structure = piece.getStructure();
        
        for (int j = 0; j < structure[0].length; j++)
        {
            for (int i = 0; i < structure.length; i++)
            {	
                if (structure[i][j] == true 
                        && boardMan.getTile(column - 1 + i, row - 1 + j) != null)
                {
                    // Remove the tile.
                   indexSet.add(new Integer((column - 1 + i) 
                           + (row - 1 + j) * boardMan.getColumns()));                   
                }		
            } // end for				
        } // end for
        
        // Return the set.
        return indexSet;
    }
    
    public void notifyRefactored()
    {
        refactored = true;
    }
    
    private boolean isOnBoard(final XYPosition p)
    {
        if (p.getX() > boardMan.getX()
                && p.getX() <= boardMan.getX() + boardMan.getWidth()
                && p.getY() > boardMan.getY()
                && p.getY() <= boardMan.getY() + boardMan.getHeight())
            return true;
        else
            return false;
    }
    
    private int convertXToColumn(final int x)
    {
        return (x - boardMan.getX()) / boardMan.getCellWidth();
    }    
    
    private int convertYToRow(final int y)
    {
       return (y - boardMan.getY()) / boardMan.getCellHeight(); 
    }
    
    private void startAnimationAt(final XYPosition p)
    {
        // Add new animations.
        Set indexSet = getSelectedIndexSet(p);
        for (Iterator it = indexSet.iterator(); it.hasNext(); )
        {
            final TileEntity t = boardMan.getTile((Integer) it.next());

            // Skip if this is not a tile.
            if (t == null)
                continue;

            // Make sure they have a pulse animation.                   
            t.setAnimation(new PulseAnimation(t));           
        }
    }
    
    private void stopAnimationAt(final XYPosition p)
    {
        // Remove old animations.
        Set indexSet = getSelectedIndexSet(p);
        for (Iterator it = indexSet.iterator(); it.hasNext(); )
        {                    
            final TileEntity t = boardMan.getTile((Integer) it.next());

            // Skip if this is not a tile.
            if (t == null)
                continue;

            Animation a = t.getAnimation();

            if (a != null)
                a.cleanUp();

            t.setAnimation(null);
        }
    }
    
    //--------------------------------------------------------------------------
    // Logic
    //--------------------------------------------------------------------------
    
    public void updateLogic(final Game game)
    { 
       
       // If the board is refactoring, do not logicify.
       if(game.isRefactoring() == true)
             return;
        
        // Grab the current mouse position.
        final XYPosition p = getMousePosition();                             
            
        // Drop in any tiles that need to be dropped, one at a time. 
        //
        // The if statement encompasses the entire function in order to ensure
        // that the board is locked while tiles are dropping.
        if(isTileDropActive == true)
        {                               
            // Find a random empty column and drop in a tile.
            // The algorithm to find the empty column is as follows:
            // Whenever there is a blockage, it is always contiguous (XX---)
            // so we find the first open column, generate a random number
            // between 0 and maxColumns - openColumnIndex, add the open 
            // column index to it, and voila! random column.
            
            int openColumnIndex = -1;
            
            // Find the first open column.
            for(int i = 0; i < boardMan.getColumns(); i++)
            {
                if(boardMan.getTile(i) == null)
                {
                    //We have found an open column.
                    openColumnIndex = i;
                    break;
                }
                else
                {
                    // A tile exists here.. continue with the loop.
                }
            }
            
            // Make sure we have found a column.
            assert(openColumnIndex >= 0);
            
            int index = Util.random.nextInt(boardMan.getColumns() - openColumnIndex) 
                    + openColumnIndex;
            
            // Sanity check.
            assert (index >= 0 && index < boardMan.getColumns());
            
            boardMan.createTile(index, TileEntity.class, 
                    TileEntity.randomColor());
             
            // Run a refactor.
            game.runRefactor(300);
             
            // Check to see if we have more tiles to drop. 
            // If not stop tile dropping.
            if(--this.tileDropCount <= 0 )
            {
                this.isTileDropActive = false;  
            }
        }
        else
        {
            if (isMouseLeftReleased() == true)
            {            
                // Remove and score the piece.
                game.scoreMan.calculatePieceScore(commitPiece(p));

                // Set the count to the piece size.
                this.tileDropCount = this.piece.getSize();
                        
                // Increment the moves.
                game.moveMan.incrementMoveCount();

                // Play the sound.
                game.soundMan.play(SoundManager.CLICK);

                // Start a tile drop.
                this.isTileDropActive = true;
                
                // Make visible.
                this.setVisible(false);

                // Run a refactor.
                game.runRefactor(200);
  
                // Reset flag.
                setMouseLeftReleased(false);
                setMouseRightReleased(false);
            }

            if (isMouseRightReleased() == true)
            {
                // Rotate the piece.            
                stopAnimationAt(pieceGrid.getXYPosition());
                piece.rotate();
                startAnimationAt(pieceGrid.getXYPosition());

                // Reset flag.
                setMouseRightReleased(false);
            }

            // Animate selected pieces.
            if (isOnBoard(p) == true)
            {
                // Filter the current position.
                XYPosition ap = adjustPosition(p);

                // If the position changed, or the board was refactored.
                if (ap.getX() != pieceGrid.getX()
                        || ap.getY() != pieceGrid.getY()
                        || refactored == true)                    
                {
                    // Clear refactored flag.
                    refactored = false;

                    // Stop the old animations.
                    stopAnimationAt(pieceGrid.getXYPosition());

                    // Update piece grid position.
                    pieceGrid.setX(ap.getX());
                    pieceGrid.setY(ap.getY()); 

                    // Start new animation.
                    startAnimationAt(ap);
                } // end if           
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------
    
	/**
	 * Gets the mousePosition.
	 * @return The mousePosition.
	 */
	public synchronized XYPosition getMousePosition()
	{
		return mousePosition;
	}

	/**
	 * Sets the mousePosition.
	 * @param mousePosition The mousePosition to set.
	 */
	public synchronized void setMousePosition(int x, int y)
	{
		this.mousePosition = new XYPosition(x, y);
	}

    public synchronized boolean isMouseLeftReleased()
    {
        return mouseLeftReleased;
    }

    public synchronized void setMouseLeftReleased(boolean mouseLeftReleased)
    {
        this.mouseLeftReleased = mouseLeftReleased;
    }

    public synchronized boolean isMouseRightReleased()
    {
        return mouseRightReleased;
    }

    public synchronized void setMouseRightReleased(boolean mouseRightReleased)
    {
        this.mouseRightReleased = mouseRightReleased;
    }
    
    public synchronized boolean isTileDropping()
    {
        return this.isTileDropActive;
    }
    
    //--------------------------------------------------------------------------
    // Draw
    //--------------------------------------------------------------------------
    
	/**
	 * Draws the piece to the screen at the current cursor
	 * location.
	 */
	public void draw()
	{
        // Don't draw if invisible.
        if (isVisible() == false)
            return;                 
		
		// Draw the piece.
		pieceGrid.draw();
	}
    
    //--------------------------------------------------------------------------
    // Events
    //--------------------------------------------------------------------------
    
    public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e)
	{
        // Debug.
        //Util.handleMessage("Button clicked.", Thread.currentThread());                
        
        // Retrieve the mouse position.
        final XYPosition p = getMousePosition();
        
        // Ignore click if we're outside the board.
        if (isOnBoard(p) == false)
            return;            
            
		// Check which button.
        switch (e.getButton())
        {
            // Left mouse clicked.
            case MouseEvent.BUTTON1:
                this.setMouseLeftReleased(true);
                break;
              
            // Right mouse clicked.
            case MouseEvent.BUTTON3:
                this.setMouseRightReleased(true);
                break;
                
            default:
                Util.handleMessage("No recognized button pressed.", 
                        Thread.currentThread());
        }
	}

	public void mouseDragged(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * Called automatically when the mouse is moved.
	 */
	public void mouseMoved(MouseEvent e)
	{	    
		// Set the mouse position.
		setMousePosition(e.getX(), e.getY());
    }

    public void setVisible(boolean visible)
    {
         this.visible = visible;
    }

    public boolean isVisible()
    {
        return visible;
    }
}
