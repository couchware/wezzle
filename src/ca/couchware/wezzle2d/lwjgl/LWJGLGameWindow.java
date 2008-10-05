/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.lwjgl;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.IGameWindow;
import ca.couchware.wezzle2d.IGameWindowCallback;
import ca.couchware.wezzle2d.SystemTimer;
import ca.couchware.wezzle2d.event.IMouseListener;
import ca.couchware.wezzle2d.event.MouseEvent;
import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

/**
 * An implementation of the the Wezzle game window using LWJGL.  It is 
 * responsible for taking care of graphic rendering and keyboard input.
 * 
 * @author Kevin Glass
 * @author Brian Matzon
 * @author Cameron McKay
 */
public class LWJGLGameWindow implements IGameWindow 
{
  
	/** 
     * The callback which should be notified of window events 
     */
	private IGameWindowCallback callback;
  
	/** 
     * True if the game is currently "running", i.e. the game loop is looping .
     */
	private boolean gameRunning = true;
  
	/**
     * The width of the game display area.
     */
	private int width;
  
	/** 
     * The height of the game display area.
     */
	private int height;

	/** 
     * The loader responsible for converting images into OpenGL textures.
     */
	private TextureLoader textureLoader;
  
	/** 
     * Title of window, we get it before our window is ready, so store it 
     * until needed.
     */
	private String title;
	
	/**
	 * Create a new game window that will use OpenGL to 
	 * render our game.
	 */
	public LWJGLGameWindow() 
    { }
	
	/**
	 * Retrieve access to the texture loader that converts images
	 * into OpenGL textures. Note, this has been made package level
	 * since only other parts of the JOGL implementations need to access
	 * it.
	 * 
	 * @return The texture loader that can be used to load images into
	 * OpenGL textures.
	 */
	TextureLoader getTextureLoader() 
    {
		return textureLoader;
	}
	
	/**
	 * Set the title of this window.
	 *
	 * @param title The title to set on this window
	 */
	public void setTitle(String title) 
    {	    
        this.title = title;
	    
        // Set the title if the display has been created.
        if (Display.isCreated()) 
        {
	    	Display.setTitle(title);
	    }
	}

	/**
	 * Set the resolution of the game display area.
	 *
	 * @param x The width of the game display area
	 * @param y The height of the game display area
	 */
	public void setResolution(int x, int y) 
    {
		width = x;
		height = y;
	}

	/**
   	 * Sets the display mode for fullscreen mode.
	 */
	private boolean setDisplayMode()
    {
    	try
        {
            // Get display modes.
            DisplayMode[] dm = org.lwjgl.util.Display.getAvailableDisplayModes(
                    width, height, 
                    -1, -1, -1, -1, 60, 60);
            
            org.lwjgl.util.Display.setDisplayMode(dm, new String[]
            {
                "width=" + width, "height=" + height,
                "freq=" + 60,
                "bpp=" + Display.getDisplayMode().getBitsPerPixel()
            });
            
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Unable to enter fullscreen, continuing in windowed mode.");
        }

        return false;
	}

	/**
	 * Start the rendering process. This method will cause the 
	 * display to redraw as fast as possible.
	 */
	public void start() 
    {
		try
        {
            setDisplayMode();
            Display.setVSyncEnabled(true);            
            Display.create(new PixelFormat(0, 16, 1));
            Display.setTitle(this.title);

            // Enable textures since we're going to use these for our sprites.
            GL11.glEnable(GL11.GL_TEXTURE_2D);

            // Disable the OpenGL depth test since we're rendering 2D graphics.
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();

            GL11.glOrtho(0, width, height, 0, -1, 1);
            
            // Enable transparency.
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);            

            textureLoader = new TextureLoader();

            if (callback != null)
            {
                callback.initialize();
            }
        }
        catch (LWJGLException le)
        {
            callback.windowClosed();
        }

        loop();
	}

	/**
	 * Register a callback that will be notified of game window
	 * events.
	 *
	 * @param callback The callback that should be notified of game
	 * window events. 
	 */
	public void setGameWindowCallback(IGameWindowCallback callback) 
    {
		this.callback = callback;
	}
	
	/**
	 * Check if a particular key is current pressed.
	 *
	 * @param keyCode The code associated with the key to check 
	 * @return True if the specified key is pressed
	 */
	public boolean isKeyPressed(int keyCode) 
    {		
		switch(keyCode) 
        {
            case KeyEvent.VK_SPACE:
                keyCode = Keyboard.KEY_SPACE;
                break;
            case KeyEvent.VK_LEFT:
                keyCode = Keyboard.KEY_LEFT;
                break;
            case KeyEvent.VK_RIGHT:
                keyCode = Keyboard.KEY_RIGHT;
                break;
		}    
		
		return org.lwjgl.input.Keyboard.isKeyDown(keyCode);
	}
       
    /**
     * Get the accurate system time.
     *
     * @return The system time in milliseconds
     */
    public static long getTime() 
    {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }
  
	/**
	 * Run the main game loop. This method keeps rendering the scene
	 * and requesting that the callback update its screen.
	 */
	private void loop() 
    {       
        final int TICKS_PER_SECOND = 60;
        final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
                
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT 
                | GL11.GL_DEPTH_BUFFER_BIT 
                | GL11.GL_STENCIL_BUFFER_BIT);
        
		while (gameRunning == true)
        {
            // Clear screen.            
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();

            // Always call Window.update(), all the time - it does some behind the
            // scenes work, and also displays the rendered output
            Display.update();

            if (Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
            {
                gameRunning = false;
                Display.destroy();
                callback.windowClosed();
            }            
            // The window is in the foreground, so we should play the game
            else if (Display.isActive())
            {
                callback.update(SKIP_TICKS);
                callback.render();
                Display.sync(TICKS_PER_SECOND);
            }
            // The window is not in the foreground, so we can allow other stuff to run and
            // infrequently update.
            else
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                { }
                
                callback.update(SKIP_TICKS);

                // Only bother rendering if the window is visible or dirty
                if (Display.isVisible() || Display.isDirty())
                {
                    callback.render();
                }
            } // end if               
        } // end while
	}
    
    //--------------------------------------------------------------------------
    // Color & clip code from GTGE.
    //--------------------------------------------------------------------------

	/** OpenGL matrix. */
	private static FloatBuffer glMatrix = BufferUtils.createFloatBuffer(16);

	/** Display dimension. */
	private static IntBuffer display = BufferUtils.createIntBuffer(16);
    
    /** A null rectangle. **/
    final private static Rectangle NULL_RECTANGLE = new Rectangle();
     
    /** The clip area. */
    private Rectangle clipArea;
    
    /** The current colour. */
    private Color color = Color.BLACK;
    
    public void setColor(Color color)
    {
        this.color = color;
    }
    
    public Color getColor()
    {
        return color;
    }    

    public void drawRect(int x, int y, int width, int height)
    {
        drawRect(x, y, width, height, GL11.GL_LINE_LOOP, color);
    }

    public void fillRect(int x, int y, int width, int height)
    {
        drawRect(x, y, width, height, GL11.GL_QUADS, color);
    }
    
    private void drawRect(
            int x, int y, int width, int height,
            int type, 
            Color color) 
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glColor4f((float) color.getRed() / 255f,
                       (float) color.getGreen() / 255f,
                       (float) color.getBlue() / 255f,
                       (float) color.getAlpha() / 255f);
        GL11.glLineWidth(1.0f);

        GL11.glBegin(type);
            GL11.glVertex2f(x, 		   y);
            GL11.glVertex2f(x + width, y);
            GL11.glVertex2f(x + width, y + height);
            GL11.glVertex2f(x, 		   y + height);
        GL11.glEnd();

        GL11.glColor3f(1.0f, 1.0f, 1.0f);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

    public void setClip(Shape shape)
    {                
        // See if the shape is null, if it is, then disable the clip.
        if (shape == null)
        {            
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glDisable(GL11.GL_STENCIL_TEST);            
            return;
        }
        
        // If the shape is a rectangle, use glScissor.
        if (shape instanceof Rectangle)
        {
            Rectangle rect = (Rectangle) shape;
            scissorClip(rect);
        }
        // For any other shape, use the stencil buffer.
        else
        {
            // TODO Currently, the stencil isn't fully implemented, so we'll use
            // a box for now.
            Rectangle rect = shape.getBounds();
            scissorClip(rect);            
            //stencilClip(shape);
        }                
    }
    
    private void scissorClip(Rectangle rect)
    {
        GL11.glGetInteger(GL11.GL_VIEWPORT, display);

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(
                rect.x, display.get(3) - rect.y - rect.height, 
                rect.width, rect.height);
    }
    
    private void stencilClip(Shape shape)
    {
        // Convert the shape to rectangle.
        Rectangle rect = shape.getBounds();
        
        // Disable colour modification.
        GL11.glColorMask(false, false, false, false);
        
        // Clear the stencil buffer.
        GL11.glClearStencil(0);
        
        // Enable the stencil buffer.
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        
        // Make it so we can set the stencil buffer to whatever
        // we draw.
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);        
        
        // Carve a rectangle into the stencil buffer.
        GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(rect.x, rect.y);
            GL11.glVertex2f(rect.x + rect.width, rect.y);
            GL11.glVertex2f(rect.x + rect.width, rect.y + rect.height);
            GL11.glVertex2f(rect.x, rect.y + rect.height);
        GL11.glEnd();
        
        // Re-enable colours.
        GL11.glColorMask(true, true, true, true);
        
        // Now change the stencil buffer so we can use it as a clip.
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 1);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);        
    }        

    public Shape getClip()
    {
       return null;
    }

    public void setCursor(int type)
    {
        // Intentionally left blank.
    }

    public ImmutablePosition getMouseImmutablePosition()
    {
        return new ImmutablePosition(Mouse.getX(), height - Mouse.getY());
    }

    //--------------------------------------------------------------------------
    // IMouseListener Fields
    //--------------------------------------------------------------------------
    
    List<IMouseListener> mouseListenerList = new ArrayList<IMouseListener>();      
    private ImmutablePosition mousePosition = new ImmutablePosition(
            Mouse.getX(), height - Mouse.getY());
    
    //--------------------------------------------------------------------------
    // IMouseListener Methods
    //--------------------------------------------------------------------------        
    
    public void fireMouseEvents()
    {
        // Poll mouse events.
        while (Mouse.next())
        {           
            int mouseX = Mouse.getEventX();
            int mouseY = height - Mouse.getEventY();
            
            // Check for mouse movement.            
            if (mouseX != mousePosition.getX() 
                    || mouseY != mousePosition.getY())
            {
                // Update internal position.                
                mousePosition = new ImmutablePosition(mouseX, mouseY);
                
                // Set an event.
                for (IMouseListener l : mouseListenerList)
                    l.mouseMoved(new MouseEvent(this, 
                            MouseEvent.Button.NONE,
                            EnumSet.noneOf(MouseEvent.Modifier.class),
                            mousePosition,
                            MouseEvent.Type.MOUSE_MOVED));
            }            
            
            if (Mouse.getEventButton() != -1)
            {
                int eventButton = Mouse.getEventButton();
                MouseEvent.Button button = MouseEvent.Button.NONE;                
                    
                // Left-click.
                if (eventButton == 0)
                {
                    button = MouseEvent.Button.LEFT;
                }
                // Right-click.
                else if (eventButton == 1)
                {
                    button = MouseEvent.Button.RIGHT;
                }
                // Middle-click.
                else if (eventButton == 2)
                {
                    button = MouseEvent.Button.MIDDLE;
                }

                MouseEvent.Type type = MouseEvent.Type.MOUSE_RELEASED;
                
                // Pressed.
                if (Mouse.getEventButtonState())
                {
                    type = MouseEvent.Type.MOUSE_PRESSED;
                }      
                
                for (IMouseListener l : mouseListenerList)
                {
                    switch (type)
                    {
                        case MOUSE_PRESSED:
                            l.mousePressed(new MouseEvent(this,
                                    button,
                                    EnumSet.noneOf(MouseEvent.Modifier.class),
                                    mousePosition,
                                    type));
                            break;
                            
                        case MOUSE_RELEASED:
                            l.mouseReleased(new MouseEvent(this,
                                    button,
                                    EnumSet.noneOf(MouseEvent.Modifier.class),
                                    mousePosition,
                                    type));
                            break;
                            
                        default: throw new AssertionError();
                    } // end switch
                } // end for
            } // if
        } // end while
    }
    
    /**
     * Clears the mouse events.
     */
    public void clearMouseEvents()           
    {
        // Empty the mouse events.
        while (Mouse.next());
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

}
