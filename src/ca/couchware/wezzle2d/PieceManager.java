package ca.couchware.wezzle2d;

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
import java.util.Set;

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
	 * The current location of the mouse pointer.
	 */
	private Position mousePosition;
	
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
		mousePosition = new Position(boardMan.getX(), boardMan.getY());
        
        // Adjust the position.
        Position ap = adjustPosition(mousePosition);
        
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
    public Position adjustPosition(Position p)
	{
		int column = (p.getX() - boardMan.getX()) / boardMan.getCellWidth();
		int row = (p.getY() - boardMan.getY()) / boardMan.getCellWidth();
		
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
		
		return new Position(
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
    public Set commitPiece(Position p)
    {
        // Create a set.
        HashSet set = new HashSet();
        
        // Convert to rows and columns.
        Position ap = adjustPosition(p);
        int column = (ap.getX() - boardMan.getX()) / boardMan.getCellWidth();
		int row = (ap.getY() - boardMan.getY()) / boardMan.getCellWidth();
        
        // Get the piece struture.
        Boolean[][] structure = piece.getStructure();
        
        // Remove all tiles covered by this piece.
        for (int j = 0; j < structure[0].length; j++)
        {
            for (int i = 0; i < structure.length; i++)
            {	
                if (structure[i][j] == true 
                        && boardMan.getTile(column - 1 + i, row - 1 + j) != null)
                {
                    // The piece is over a tile.
//                    pieceOverTile = true;

                    // Record the tile.
//                    tiles[tilePointer++] = board.getTile(correctColumn - 1 + i, correctRow - 1 + j);

                    // Remove the tile.
                   set.add(boardMan.getTile(column - 1 + i, row - 1 + j));
                   boardMan.removeTile(column - 1 + i, row - 1 + j);
                }		
            } // end for				
        } // end for
        
        // Return the set.
        return set;
    }
    
    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------
    
	/**
	 * Gets the mousePosition.
	 * @return The mousePosition.
	 */
	public synchronized Position getMousePosition()
	{
		return mousePosition;
	}

	/**
	 * Sets the mousePosition.
	 * @param mousePosition The mousePosition to set.
	 */
	public synchronized void setMousePosition(int x, int y)
	{
		this.mousePosition = new Position(x, y);
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

    //--------------------------------------------------------------------------
    // Logic
    //--------------------------------------------------------------------------
    
    public void logic(final Game game)
    { 
        // Ignore logic if not visible.
        if (isVisible() == false)
            return;
        
        if (isMouseLeftReleased() == true)
        {
            // Remove the piece, getting the tiles in return.
            Set set = commitPiece(getMousePosition());                    
            
            // Score the piece.
            game.scoreMan.calculatePieceScore(set);
            
            // Play the sound.
            game.soundMan.play(SoundManager.CLICK);
            
            // Refactor the board.
            game.runRefactor();
            
            // Reset flag.
            setMouseLeftReleased(false);
        }
        
        if (isMouseRightReleased() == true)
        {
            // Rotate the piece.            
            piece.rotate();
            
            // Reset flag.
            setMouseRightReleased(false);
        }
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
        
        // Retrieve the current mouse position.
        Position p = getMousePosition();
        
        // If mouse is inside the board then update the piece grid location.
        if (p.getX() > boardMan.getX()
                && p.getX() <= boardMan.getX() + boardMan.getWidth()
                && p.getY() > boardMan.getY()
                && p.getY() <= boardMan.getY() + boardMan.getHeight())
        {
            // Filter the current position.
            Position ap = adjustPosition(p);

            // Draw the piece there.
            pieceGrid.setX(ap.getX());
            pieceGrid.setY(ap.getY());  
        }        		            
		
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
        Position p = getMousePosition();
        
        // Ignore click if we're outside the board.
        if (p.getX() < boardMan.getX()
                || p.getX() >= boardMan.getX() + boardMan.getWidth()
                || p.getY() < boardMan.getY()
                || p.getY() >= boardMan.getY() + boardMan.getHeight())
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
		// Debug.
//		Util.handleMessage("Mouse moved to: " + e.getX() + "," + e.getY() + ".", 
//                Thread.currentThread());		       
        
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
