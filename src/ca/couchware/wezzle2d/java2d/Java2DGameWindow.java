package ca.couchware.wezzle2d.java2d;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ca.couchware.wezzle2d.GameWindow;
import ca.couchware.wezzle2d.GameWindowCallback;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.Sprite;
import ca.couchware.wezzle2d.util.Keyboard;

/**
 * An implementation of GameWindow which uses Java 2D rendering to produce the
 * scene. In addition its responsible for monitoring the keyboard using AWT.
 * 
 * @author Kevin Glass
 */
public class Java2DGameWindow extends Canvas implements GameWindow
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
	private GameWindowCallback callback;
	
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
				frame.setVisible(true);

				// Add a listener to respond to the user closing the window. If they
				// do we'd like to exit the game.
				frame.addWindowListener(new WindowAdapter()
				{
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
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		// If we have a callback registered then notify
		// it that initialization is taking place.
		if (callback != null)
		{
			callback.initialize();
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
	public void setGameWindowCallback(GameWindowCallback callback)
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
		while (gameRunning)
		{						
			// Get hold of a graphics context for the accelerated
			// surface and black it out.
			g = (Graphics2D) strategy.getDrawGraphics();            
//			g.setColor(Color.black);
//			g.fillRect(0, 0, 800, 600);
			
			// Draw the current background.
			Sprite s = ResourceFactory.get()
                    .getSprite("resources/Background1.png");
			s.draw(0, 0);

			if (callback != null)
			{
				callback.frameRendering();
			}

			// Finally, we've completed drawing so clear up the graphics
			// and flip the buffer over.
			g.dispose();
			strategy.show();
		}
	}
}