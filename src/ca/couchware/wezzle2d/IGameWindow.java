package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.event.IMouseListener;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 * The window in which the game will be displayed. This interface exposes just
 * enough to allow the game logic to interact with, while still maintaining an
 * abstraction away from any physical implementation of windowing (i.e. AWT,
 * LWJGL)
 * 
 * @author Kevin Glass
 */

public interface IGameWindow
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
	public void setGameWindowCallback(IGameWindowCallback callback);

	/**
	 * Check if a particular key is pressed
	 * 
	 * @param keyCode
	 *            The code associate with the key to check
	 * @return True if the particular key is pressed
	 */
	public boolean isKeyPressed(int keyCode);
    
    //--------------------------------------------------------------------------
    // Draw
    //--------------------------------------------------------------------------
    
    /**
     * Set the drawing color.
     * 
     * @param c
     */
    public void setColor(Color c);
    
    /**
     * Draws the outline of the specified rectangle.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void drawRect(int x, int y, int width, int height);            
    
    /**
     * Fills the specified rectangle.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void fillRect(int x, int y, int width, int height);
    
    //--------------------------------------------------------------------------
    // Clip
    //--------------------------------------------------------------------------
	
    /**
     * Outlines the passed shape with the color white.  Principally used
     * for debugging the clip algorithm.
     * 
     * @param s
     */
    public void drawClip(Shape s);
    
    /**
     * Sets the current clip rectangle.  Only drawables within the clip area are
     * drawn to the screen.
     * 
     * @param r
     */
    public void setClip(Rectangle r);
    
    /**
     * Gets the current clip rectangle.
     * 
     * @return
     */
    public Shape getClip();
    
    /**
     * Clears the clip rectangle, meaning the whole screen will be drawn.
     */        
    public void clearClip();
    
    //--------------------------------------------------------------------------
    // Mouse
    //--------------------------------------------------------------------------
    
	/**
	 * Registers a mouse listener.
     * 
     * @param l
	 */
	public void addMouseListener(IMouseListener l);		
    
    /**
     * Unregisters a mouse listener.
     * 
     * @param l
     */
    public void removeMouseListener(IMouseListener l);        
    
    /**
     * Changes the cursor.
     * 
     * @param type
     */
    public void setCursor(int type);
    
    /**
     * Get the current mouse position.
     * 
     * @return The current mouse position.
     */
    public ImmutablePosition getMouseImmutablePosition();   
    
    /**
     * Fires all the queued up mouse events.
     */
    public void fireMouseEvents();
    
    /**
     * Clears all the queued up mouse events.  Both this method and 
     * <pre>fireMouseEvents()</pre> clear the queue.  However, this method
     * does not fire the events in the queue, it just deletes them. 
     */
    public void clearMouseEvents();
    
}