package ca.couchware.wezzle2d;

import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * The window in which the game will be displayed. This interface exposes just
 * enough to allow the game logic to interact with, while still maintaining an
 * abstraction away from any physical implementation of windowing (i.e. AWT,
 * LWJGL)
 * 
 * @author Kevin Glass
 */

public interface GameWindow
{

	/**
	 * Set the title of the game window
	 * 
	 * @param title
	 *            The new title for the game window
	 */
	public void setTitle(String title);

	/**
	 * Set the game display resolution
	 * 
	 * @param x
	 *            The new x resolution of the display
	 * @param y
	 *            The new y resolution of the display
	 */
	public void setResolution(int x, int y);

    public void drawClip(Shape s);
    
    public void setClip(Rectangle r);
    
    public Shape getClip();
    
    public void clearClip();
    
	/**
	 * Start the game window rendering the display
	 */
	public void startRendering();

	/**
	 * Set the callback that should be notified of the window events.
	 * 
	 * @param callback
	 *            The callback that should be notified of game window events.
	 */
	public void setGameWindowCallback(GameWindowCallback callback);

	/**
	 * Check if a particular key is pressed
	 * 
	 * @param keyCode
	 *            The code associate with the key to check
	 * @return True if the particular key is pressed
	 */
	public boolean isKeyPressed(int keyCode);
	
	/**
	 * Registers a mouse listener.
	 */
	public void addMouseListener(MouseListener l);
	
	/**
	 * Registers a mouse motion listener.
	 */
	public void addMouseMotionListener(MouseMotionListener l);
    
    /**
     * Unregisters a mouse listener.
     */
    public void removeMouseListener(MouseListener l);
    
    /**
     * Unregisters a mouse motion listener.
     */
    public void removeMouseMotionListener(MouseMotionListener l); 
    
    /**
     * Changes the cursor.
     */
    public void setCursor(Cursor cursor);
}