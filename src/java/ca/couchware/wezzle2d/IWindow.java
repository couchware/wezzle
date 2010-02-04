package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.event.IKeyListener;
import ca.couchware.wezzle2d.event.IMouseListener;
import ca.couchware.wezzle2d.util.ImmutablePosition;

/**
 * The window in which the game will be displayed. This interface exposes just
 * enough to allow the game logic to interact with, while still maintaining an
 * abstraction away from any physical implementation of windowing (i.e. AWT,
 * LWJGL)
 * 
 * @author Kevin Glass
 * @author Cameron McKay
 */
public interface IWindow
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
     * Set the fullscreen setting.
     *
     * @param fullscreen
     */
    public void setFullscreen(boolean fullscreen);

    /**
     * Get the fullscreen settings.
     * 
     * @return
     */
    public boolean isFullscreen();

    /**
     * Start the game window rendering the display
     */
    public void start();
    
    /**
     * Pops up an alert with the given title and message.
     * 
     * @param title
     * @param message
     */
    public void alert(String title, String message);

    /**
     * Set the callback that should be notified of the window events.
     *
     * @param callback
     *            The callback that should be notified of game window events.
     */
    public void setGameWindowCallback(IWindowCallback callback);    

    //--------------------------------------------------------------------------
    // Keyboard
    //--------------------------------------------------------------------------

    /**
     * Update the key presses.
     */
    public void updateKeyPresses();

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
     * 
     * @param l
     */
    public void addKeyListener(IKeyListener l);

    /**
     * Unregisters a mouse listener.
     * 
     * @param l
     */
    public void removeKeyListener(IKeyListener l);

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
