package ca.couchware.wezzle2d.java2d;

import ca.couchware.wezzle2d.IGameWindow;
import ca.couchware.wezzle2d.IGameWindowCallback;
import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.event.IMouseListener;
import ca.couchware.wezzle2d.event.MouseEvent;
import ca.couchware.wezzle2d.util.Keyboard;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * An implementation of GameWindow which uses Java 2D rendering to produce the
 * scene. In addition its responsible for monitoring the keyboard using AWT.
 * 
 * @author Kevin Glass
 */
public class Java2DGameWindow extends Canvas implements IGameWindow, 
        MouseListener, MouseMotionListener
{
    
	/** The strategy that allows us to use accelerate page flipping. */
	private BufferStrategy strategy;
	
	/** True if the game is currently "running", i.e. the game loop is looping */
	private boolean gameRunning = true;

	/** The frame in which we'll display our canvas */
	private JFrame frame;
	
	/** The width of the display */
	private int width;
	
	/** The height of the display */
	private int height;
	
	/** The callback which should be notified of events caused by this window */
	private IGameWindowCallback callback;
	
	/** The current accelerated graphics context */
	private Graphics2D g;

	/**
	 * Create a new window to render using Java 2D. Note this will *not*
	 * actually cause the window to be shown.
	 */
	public Java2DGameWindow() 
	{
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				frame = new JFrame();
			}
		});
	}

	/**
	 * Set the title that should be displayed on the window
	 * 
	 * @param title
	 *            The title to display on the window
	 */
	public void setTitle(final String title)
	{
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				frame.setTitle(title);
			}
		});		
	}

	/**
	 * Set the resolution of the game window. Note, this will only have effect
	 * before the window has been made visible
	 * 
	 * @param x
	 *            The width of the game display
	 * @param y
	 *            The height of the game display
	 */
	public void setResolution(int x, int y)
	{
		width = x;
		height = y;
	}

	/**
	 * Start the rendering process. This method will not return.
	 */
	public void startRendering()
	{			
		final Runnable r = new Runnable()
		{
			public void run()
			{
                // Set it as it's own listener.
                addMouseListener(Java2DGameWindow.this);              
                addMouseMotionListener(Java2DGameWindow.this);
                
				// Get hold the content of the frame and set up the resolution of the
				// game.
				JPanel panel = (JPanel) frame.getContentPane();		
				
				// Set the panel size.				
				panel.setPreferredSize(new Dimension(width, height));			
				panel.setLayout(null);

				// Setup our canvas size and put it into the content of the frame.
				setBounds(0, 0, width, height);				
				panel.add(Java2DGameWindow.this);

				// Tell AWT not to bother repainting our canvas since we're
				// going to do that our self in accelerated mode.
				setIgnoreRepaint(true);

				// Finally make the window visible.				
				frame.setResizable(false);
                
				frame.pack();				
				frame.setLocation(100, 100);				   
                
                // This is code for enabling full screen.  Unfortunately,
                // all the graphics have been made 800x600, so we'd need to
                // change the resolution too.
                // To enable, uncomment these lines and comment the two lines
                // above.
//                frame.setUndecorated(true);
//                GraphicsDevice gd = GraphicsEnvironment
//                        .getLocalGraphicsEnvironment().getDefaultScreenDevice();
//                gd.setFullScreenWindow(frame);                
                
                frame.setVisible(true);

				// Add a listener to respond to the user closing the window. If they
				// do we'd like to exit the game.
               
				frame.addWindowListener(new WindowAdapter()
				{
                    @Override
					public void windowClosing(WindowEvent e)
					{
						if (callback != null)
						{
							callback.windowClosed();
						}
						else
						{
							System.exit(0);
						}
					}
				});
                
                // Add a listener to respond to the user deactivating the window.
				// If they do we'd like to pause the game.
                frame.addWindowListener(new WindowAdapter()
				{
                    @Override
					public void windowDeactivated(WindowEvent e)
					{
						if (callback != null)
						{
							callback.windowDeactivated();
						}
						else
						{
							System.exit(0);
						}
					}
				});
                
                // Add a listener to respond to the user un-minimizing the window.
				// If they do we'd like to pause the game.
                frame.addWindowListener(new WindowAdapter()
				{
                    @Override
					public void windowActivated(WindowEvent e)
					{
						if (callback != null)
						{
							callback.windowActivated();
						}
						else
						{
							System.exit(0);
						}
					}
				});

				// Request the focus so key events come to us.
				requestFocus();

				// Create the buffering strategy which will allow AWT
				// to manage our accelerated graphics.
				createBufferStrategy(2);
				strategy = getBufferStrategy();
								
				// Initialize keyboard.
				Keyboard.init(Java2DGameWindow.this);
			}
		};
		
		try
		{
			javax.swing.SwingUtilities.invokeAndWait(r);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			LogManager.recordException(e);
		}
		catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			LogManager.recordException(e);
		}		

		// If we have a callback registered then notify
		// it that initialization is taking place.
		if (callback != null)
		{
            // Get hold of a graphics context for the accelerated
			// surface and black it out.
			g = (Graphics2D) strategy.getDrawGraphics();  
            
			callback.initialize();
            
            // Finally, we've completed drawing so clear up the graphics
			// and flip the buffer over.
            g.dispose();
            g = null;
		}

		// Start the game loop.
		gameLoop();
	}

	/**
	 * Register the callback that should be notified of game window events.
	 * 
	 * @param callback
	 *            The callback to be notified of display events
	 */
	public void setGameWindowCallback(IGameWindowCallback callback)
	{
		this.callback = callback;
	}

	/**
	 * Check if a particular key is pressed
	 * 
	 * @param keyCode
	 *            The code associated with the key to check
	 * @return True if the specified key is pressed
	 */
	public boolean isKeyPressed(int keyCode)
	{
		return Keyboard.isPressed(keyCode);
	}

	/**
	 * Retrieve the current accelerated graphics context. Note this method has
	 * been made package scope since only the other members of the "java2D"
	 * package need to access it.
	 * 
	 * @return The current accelerated graphics context for this window
	 */
	Graphics2D getDrawGraphics()
	{
		return g;
	}

	/**
	 * Run the main game loop. This method keeps rendering the scene and
	 * requesting that the callback update its screen.
	 */
	private void gameLoop()
	{
        LogManager.recordMessage("Game loop started.", "Java2DGameWindow#gameLoop");
        
        // Did the screen get updated?
        boolean updated = false;
        
		while (gameRunning)
		{						
			// Get hold of a graphics context for the accelerated
			// surface and black it out.
			g = (Graphics2D) strategy.getDrawGraphics();            						
            
			if (callback != null)
			{
				updated = callback.frameRendering();
			}

			// Finally, we've completed drawing so clear up the graphics
			// and flip the buffer over.
            g.dispose();
            g = null;
            
            if (updated == true)                                                     
                strategy.show();            
		}
	}
    
    /**
     * Set the drawing color.
     * 
     * @param c
     */
    public void setColor(Color c)
    {
        if (g == null)
            throw new IllegalStateException("Graphics not yet initialized.");
        
        g.setColor(c);
    }
    
    /**
     * Draws the outline of the specified rectangle.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void drawRect(int x, int y, int width, int height)
    {
        if (g == null)
            throw new IllegalStateException("Graphics not yet initialized.");
        
        g.drawRect(x, y, width, height);
    }
    
    /**
     * Fills the specified rectangle.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void fillRect(int x, int y, int width, int height)
    {
        if (g == null)
            throw new IllegalStateException("Graphics not yet initialized.");
        
        g.fillRect(x, y, width, height);
    }            
    
    /**
     * Outlines the passed shape with the color white.  Principally used
     * for debugging the clip algorithm.
     * 
     * @param s
     */
    public void drawClip(Shape s)
    {
        // Make sure setClip was called corectly.
        if (g == null)
            throw new RuntimeException(
                    "setClip must be called during frameRendering().");
                        
        if (s != null)        
        {
            g.setColor(Color.WHITE);                       
            g.draw(s);
        }
    }    
    
    /**
     * Sets the current clip rectangle.  Only drawables within the clip area are
     * drawn to the screen.
     * 
     * @param r
     */
    public void setClip(Rectangle r)
    {
        // Make sure setClip was called corectly.
        if (g == null)
            throw new RuntimeException(
                    "setClip must be called during frameRendering().");
                        
        g.setClip(r);        
    }
    
    /**
     * Gets the current clip rectangle.
     * 
     * @return
     */
    public Shape getClip()
    {
        // Make sure getClip was called corectly.
        if (g == null)
            throw new RuntimeException(
                    "getClip must be called during frameRendering().");
        
        return g.getClip();
    }
    
    /**
     * Clears the clip rectangle, meaning the whole screen will be drawn.
     */
    public void clearClip()
    {
         // Make sure getClip was called corectly.
        if (g == null)
            throw new RuntimeException(
                    "clearClip must be called during frameRendering().");
        
        g.setClip(null);
    }

    /**
     * Sets the cursor.
     * 
     * @param type
     */
    public void setCursor(int type)
    {                
        Cursor c = Cursor.getPredefinedCursor(type);
        
        if (getCursor().equals(c) == false)
            setCursor(c);
    }
    
    /**
     * The last position of the pointer.
     */    
    private ImmutablePosition lastPosition = ImmutablePosition.ORIGIN;
    
    /**
     * Returns the position of the pointer.
     * 
     * @return
     */
    public synchronized ImmutablePosition getMouseImmutablePosition()
    {                   
        if (getMousePosition() != null)        
            lastPosition = new ImmutablePosition(getMousePosition());                    
        
        return lastPosition;
    }
    
    //--------------------------------------------------------------------------
    // IMouseListener Fields
    //--------------------------------------------------------------------------
    
    List<IMouseListener> mouseListenerList = new ArrayList<IMouseListener>();    
    Queue<MouseEvent> mouseEventQueue = new ConcurrentLinkedQueue<MouseEvent>();              
    
    //--------------------------------------------------------------------------
    // IMouseListener Methods
    //--------------------------------------------------------------------------        
    
    public void fireMouseEvents()
    {
        while (mouseEventQueue.peek() != null)
        {        
            // Grab the first event off the queue.
            MouseEvent event = mouseEventQueue.remove();
            
            // Determine which type of mouse event it is.
            switch (event.getType())
            {
                case MOUSE_CLICKED:
                    for (IMouseListener listener : mouseListenerList)
                        listener.mouseClicked(event);
                    break;
                    
                case MOUSE_PRESSED:
                    for (IMouseListener listener : mouseListenerList)
                        listener.mousePressed(event);
                    break;
                    
                case MOUSE_RELEASED:
                    for (IMouseListener listener : mouseListenerList)
                        listener.mouseReleased(event);
                    break;
                    
                case MOUSE_ENTERED:
                    for (IMouseListener listener : mouseListenerList)
                        listener.mouseEntered(event);
                    break;
                    
                case MOUSE_EXITED:
                    for (IMouseListener listener : mouseListenerList)
                        listener.mouseExited(event);
                    break;
             
                case MOUSE_DRAGGED:
                    for (IMouseListener listener : mouseListenerList)
                        listener.mouseDragged(event);
                    break;
                    
                case MOUSE_MOVED:
                    for (IMouseListener listener : mouseListenerList)
                        listener.mouseMoved(event);
                    break;
                    
                default:
                    throw new IllegalStateException("Unknown event!");
            }
        } // end while
    }
    
    public void clearMouseEvents()           
    {
        mouseEventQueue.clear();
    }
    
    public void addMouseListener(IMouseListener l)
    {
        if (l == null)
            throw new NullPointerException();
        
        if (mouseListenerList.contains(l))
            throw new IllegalStateException("Listener already registered!");
        
        mouseListenerList.add(l);
    }
        
    public void removeMouseListener(IMouseListener l)
    {
        if (l == null)
            throw new NullPointerException();
        
        if (!mouseListenerList.contains(l))
            throw new IllegalStateException("Listener not registered!");
        
        if (mouseListenerList.remove(l) == false)
            throw new IllegalStateException("Failed to remove listener!");
    }
    
    //--------------------------------------------------------------------------
    // MouseListener and MouseMotionListener Methods
    //--------------------------------------------------------------------------

    public void mouseClicked(java.awt.event.MouseEvent e)
    {
        mouseEventQueue.add(new MouseEvent(e, MouseEvent.Type.MOUSE_CLICKED));
    }

    public void mousePressed(java.awt.event.MouseEvent e)
    {
        mouseEventQueue.add(new MouseEvent(e, MouseEvent.Type.MOUSE_PRESSED));
    }

    public void mouseReleased(java.awt.event.MouseEvent e)
    {
        mouseEventQueue.add(new MouseEvent(e, MouseEvent.Type.MOUSE_RELEASED));
    }

    public void mouseEntered(java.awt.event.MouseEvent e)
    {
        mouseEventQueue.add(new MouseEvent(e, MouseEvent.Type.MOUSE_ENTERED));
    }

    public void mouseExited(java.awt.event.MouseEvent e)
    {
        mouseEventQueue.add(new MouseEvent(e, MouseEvent.Type.MOUSE_EXITED));
    }   

    public void mouseDragged(java.awt.event.MouseEvent e)
    {
        mouseEventQueue.add(new MouseEvent(e, MouseEvent.Type.MOUSE_DRAGGED));
    }

    public void mouseMoved(java.awt.event.MouseEvent e)
    {
        mouseEventQueue.add(new MouseEvent(e, MouseEvent.Type.MOUSE_MOVED));
    }
             
}