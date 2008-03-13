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
	 * The current location of the mouse pointer.
	 */
	private Position mousePosition;
	
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
	 * @param bm The board manager.
	 */
	public PieceManager(BoardManager boardMan)
	{
		// Set the reference.
		this.boardMan = boardMan;
		
		// Create initial mouse position.
		mousePosition = new Position(boardMan.getX(), boardMan.getY());
		
		// Create new piece entity at the origin of the board.
		pieceGrid = new PieceGrid(boardMan, 
                mousePosition.getX(), 
                mousePosition.getY());
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

    //--------------------------------------------------------------------------
    // Draw
    //--------------------------------------------------------------------------
    
	/**
	 * Draws the piece to the screen at the current cursor
	 * location.
	 */
	public void draw()
	{
		// Retrieve the current position.
		Position p = getMousePosition();	
		
		// Draw the piece there.
		pieceGrid.setX(((p.getX() - boardMan.getX()) / 32) 
                * 32 + boardMan.getX());
		pieceGrid.setY(((p.getY() - boardMan.getY()) / 32) 
                * 32 + boardMan.getY());
        
        
		
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
		// TODO Auto-generated method stub
		
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
//		Util.handleMessage("Mouse moved to: " + e.getX() + "," + e.getY() + ".", Thread.currentThread());
		
        // If mouse is outside the board, then ignore the event.
        if (e.getX() < boardMan.getX()
                || e.getX() > boardMan.getX() + boardMan.getWidth()
                || e.getY() < boardMan.getY()
                || e.getY() > boardMan.getY() + boardMan.getHeight())
            return;
        
		// Set the mouse position.
		setMousePosition(e.getX(), e.getY());
    }
}
