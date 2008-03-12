package ca.couchware.wezzle2d;

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

public class PieceManager implements Drawable, MouseListener, MouseMotionListener
{	
	/**
	 * The current location of the mouse pointer.
	 */
	private Position mousePosition;
	
	/**
	 * A piece entity.  This will be a grid.
	 */
	private PieceEntity piece;
	
	/**
	 * The board manager the piece manager to attached to.
	 */
	private BoardManager boardMan;

	/**
	 * The constructor.
	 * 
	 * @param bm The board manager.
	 */
	public PieceManager(BoardManager bm)
	{
		// Set the reference.
		this.boardMan = bm;
		
		// Create initial mouse position.
		mousePosition = new Position(boardMan.getX(), boardMan.getY());
		
		// Create new piece entity at the origin of the board.
		piece = new PieceEntity(mousePosition.getX(), mousePosition.getY());
	}
	
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
		
		// Set the mouse position.
		setMousePosition(e.getX(), e.getY());
	}

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

	/**
	 * Draws the piece to the screen at the current cursor
	 * location.
	 */
	public void draw()
	{
		// Retrieve the current position.
		Position p = getMousePosition();	
		
		// Draw the piece there.
		piece.setX(p.getX());
		piece.setY(p.getY());
		
		// Draw the piece.
		piece.draw();
	}
}
