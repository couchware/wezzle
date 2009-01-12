/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.lwjgl;

import ca.couchware.wezzle2d.IGameWindow;
import ca.couchware.wezzle2d.IGameWindowCallback;
import ca.couchware.wezzle2d.event.IKeyListener;
import ca.couchware.wezzle2d.event.IMouseListener;
import ca.couchware.wezzle2d.event.KeyEvent;
import ca.couchware.wezzle2d.event.KeyEvent.Modifier;
import ca.couchware.wezzle2d.event.MouseEvent;
import ca.couchware.wezzle2d.event.MouseEvent.Button;
import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.util.Ascii;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
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
     * The key presses.
     */
    private Set<Character> keyPressSet;
    
    /**
     * The mouse states.
     */
    private Map<MouseEvent.Button, Boolean> mouseStateMap;
    
	/** 
     * True if the game is currently "running", i.e. the game loop is looping .
     */
	private boolean gameRunning = true;
    
    /** True if the window is active. */
    private boolean active = false;
  
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
    {
        this.keyPressSet = new HashSet<Character>();
        
        this.mouseStateMap = 
                new EnumMap<Button, Boolean>(Button.class);
        
        this.mouseStateMap.put(Button.LEFT, false);
        this.mouseStateMap.put(Button.RIGHT, false);
        this.mouseStateMap.put(Button.MIDDLE, false);
    }
	
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
                    -1, -1, -1, -1,
                    Display.getDisplayMode().getFrequency(),
                    Display.getDisplayMode().getFrequency());
            
            org.lwjgl.util.Display.setDisplayMode(dm, new String[]
            {
                "width=" + width, "height=" + height,
                "freq=" + Display.getDisplayMode().getFrequency(),
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
    
    /** The original display mode before we tampered with things. */
	protected DisplayMode originalDisplayMode;

    /** The display mode we're going to try and use. */
	protected DisplayMode targetDisplayMode;
    
    /**
	 * Set the display mode to be used.
     * [Code taken from Slick2D.]
	 * 
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 * @throws SlickException Indicates a failure to initialise the display
	 */
	public void setDisplayMode(int width, int height, boolean fullscreen)
    {        
		try
        {
            this.targetDisplayMode = null;
            if (fullscreen)
            {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;

                for (int i = 0; i < modes.length; i++)
                {
                    DisplayMode current = modes[i];

                    if ((current.getWidth() == width) && (current.getHeight() == height))
                    {
                        if ((targetDisplayMode == null) || (current.getFrequency() >= freq))
                        {
                            if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel()))
                            {
                                targetDisplayMode = current;
                                freq = targetDisplayMode.getFrequency();
                            }
                        }

                        // if we've found a match for bpp and frequence against the 
                        // original display mode then it's probably best to go for this one
                        // since it's most likely compatible with the monitor
                        if ((current.getBitsPerPixel() == originalDisplayMode.getBitsPerPixel()) &&
                                (current.getFrequency() == originalDisplayMode.getFrequency()))
                        {
                            targetDisplayMode = current;
                            break;
                        }
                    }
                }
            }
            else
            {
                targetDisplayMode = new DisplayMode(width, height);
            }

            if (targetDisplayMode == null)
            {
                throw new RuntimeException("Failed to find value mode: " + width + "x" + height + " fullscreen=" + fullscreen + ".");
            }

//            this.width = width;
//            this.height = height;

            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);

//            if (Display.isCreated())
//            {
//                initGL();
//                enterOrtho();
//            }
//
//            if (targetDisplayMode.getBitsPerPixel() == 16)
//            {
//                InternalTextureLoader.get().set16BitMode();
//            }
        }
        catch (LWJGLException e)
        {
            throw new RuntimeException("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + ".", e);
        }				
	}


	/**
	 * Start the rendering process. This method will cause the 
	 * display to redraw as fast as possible.
	 */
	public void start() 
    {
		try
        {
            this.originalDisplayMode = Display.getDisplayMode();
            setDisplayMode(width, height, false);
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
//        final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;   
//        final int MAX_FRAME_SKIP = 10;
        
//        long nextGameTick = getTime();
//        int loopCounter;
        
        // Clear the stencil buffer.        
        GL11.glClearStencil(0);     
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        
        while (gameRunning == true)
        {                       
            // Always call Window.update(), all the time - it does some behind the
            // scenes work, and also displays the rendered output
            Display.update();
                        
            // Clear screen.
            GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();             		
            //GL11.glEnable(GL11.GL_LINE_SMOOTH | GL11.GL_POLYGON_SMOOTH);
                        
            if (Display.isCloseRequested())
            {
                gameRunning = false;
                Display.destroy();
                callback.windowClosed();
            }            
            // The window is in the foreground, so we should play the game
            else if (Display.isActive())
            {
                // Notify of activation.
                if (this.active == false) 
                {
                    callback.windowActivated();
                    this.active = true;
                }
                
//                loopCounter = 0;
//
//                while (getTime() > nextGameTick && loopCounter < MAX_FRAME_SKIP)
//                {
//                    callback.update(this.speed);
//                    nextGameTick += SKIP_TICKS;
//                    loopCounter++;
//                }                       
//
//                callback.render();  
                
                callback.update();                                               
                callback.draw();                                                
                
                GL11.glColor3f(0, 0, 0);                
                Display.sync(TICKS_PER_SECOND);                
            }
            // The window is not in the foreground, so we can allow other stuff to run and
            // infrequently update.
            else
            {
                if (this.active == true)
                {
                    callback.windowDeactivated();
                    this.active = false;
                }
                
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                { }
                
                callback.update();

                // Only bother rendering if the window is visible or dirty
                if (Display.isVisible() || Display.isDirty())
                {
                    callback.draw();
                }
            } // end if               
        } // end while
	}
    
    //--------------------------------------------------------------------------
    // Color & clip code.
    //--------------------------------------------------------------------------
       
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

    private void bindColor()
    {
        GL11.glColor4f((float) color.getRed()   / 255f,
                       (float) color.getGreen() / 255f,
                       (float) color.getBlue()  / 255f,
                       (float) color.getAlpha() / 255f);
        GL11.glLineWidth(1.0f);
    }    
    
    public void drawRect(int x, int y, int width, int height)
    {
        drawRect(x, y, width, height, GL11.GL_LINE_LOOP);
    }

    public void fillRect(int x, int y, int width, int height)
    {
        drawRect(x, y, width, height, GL11.GL_QUADS);
    }
    
    private void drawRect(
            int x, int y, int width, int height,
            int type) 
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        bindColor();

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
            //GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glDisable(GL11.GL_STENCIL_TEST); 
//            copyBuffer();
            return;
        }
        
        // Stencil out the shape.
//        if (shape instanceof Rectangle)
//            scissorClip((Rectangle) shape);
//        else
//            scissorClip(shape.getBounds());
        stencilClip(shape);                        
    }       
    
//    private static IntBuffer viewport = BufferUtils.createIntBuffer(16);
//    private static FloatBuffer rasterPosition = BufferUtils.createFloatBuffer(16);     
//    
//    /**
//     * Copies front buffer to the back buffer.
//     * Taken from:
//     *   http://anirudhs.chaosnet.org/blog/2006.03.04.html
//     */
//    private void copyBuffer()
//    {
//        /* Get the viewport. */
//        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
//        
//        /* Set source buffer. */
//        GL11.glReadBuffer(GL11.GL_FRONT);
//
//        /* Set projection matrix. */
//        GL11.glMatrixMode(GL11.GL_PROJECTION);
//        GL11.glPushMatrix();
//        GL11.glLoadIdentity() ;
//        GL11.glOrtho(0, viewport.get(2), viewport.get(3), 0, -1, 1);
//
//        /* Set modelview matrix. */
//        GL11.glMatrixMode(GL11.GL_MODELVIEW);
//        GL11.glPushMatrix();
//        GL11.glLoadIdentity();
//
//        /* Save old raster position. */
//        GL11.glGetFloat(GL11.GL_CURRENT_RASTER_POSITION, rasterPosition);
//
//        /* Set raster position. */
//        GL11.glRasterPos4f(0f, 0f, 0f, 1f);
//
//        /* Copy buffer. */
//        GL11.glCopyPixels(0, 0, viewport.get(2), viewport.get(3), GL11.GL_COLOR);
//
//        /* Restore old raster position. */        
//        GL11.glRasterPos4f(
//                rasterPosition.get(0), 
//                rasterPosition.get(1),
//                rasterPosition.get(2),
//                rasterPosition.get(3));
//
//        /* Restore old matrices. */
//        GL11.glPopMatrix();
//        GL11.glMatrixMode(GL11.GL_PROJECTION);
//        GL11.glPopMatrix();
//        GL11.glMatrixMode(GL11.GL_MODELVIEW);
//
//        /* Restore source buffer. */
//        GL11.glReadBuffer(GL11.GL_BACK); 
//    }
    
    private void scissorClip(Rectangle rect)
    {       
        //copyBuffer();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor(
                rect.x, this.height - rect.y - rect.height, 
                rect.width, rect.height);
        //GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        
    }
    
    private void stencilClip(Shape shape)
    {               
        // Disable colour modification.
        GL11.glColorMask(false, false, false, false);
                
        // Enable the stencil buffer.
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        
        // Make it so we can set the stencil buffer to whatever
        // we draw.
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);        
        
        // Carve a rectangle into the stencil buffer.
        if (shape instanceof Ellipse2D)
        {
            Ellipse2D e = (Ellipse2D) shape;
            drawEllipse(e.getX(), e.getY(), e.getWidth(), e.getHeight(), true);
        }
        else
        {
            Rectangle rect = shape.getBounds();
            drawRect(rect.x, rect.y, rect.width, rect.height, true);
        }
        
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
    
    private void drawRect(int x, int y, int width, int height, boolean filled)
    {
        int mode = filled ? GL11.GL_POLYGON : GL11.GL_LINE_LOOP;
        
        GL11.glBegin(mode);
            GL11.glVertex2f(x, y);
            GL11.glVertex2f(x + width, y);
            GL11.glVertex2f(x + width, y + height);
            GL11.glVertex2f(x, y + height);
        GL11.glEnd();        
    }
    
    /** The value of two times PI. */
    final private static double TWO_PI = 2.0 * Math.PI;
    
    /** The number of points in an ellipse. */
    final private static double ELLIPSE_POINTS = 50;
    
    private void drawEllipse(double x, double y, double width, double height, boolean filled)
    {        
        float xf = (float) (x + width  / 2);
        float yf = (float) (y + height / 2);
        float wf = (float) width;
        float hf = (float) height;                
        
        int mode = filled ? GL11.GL_POLYGON : GL11.GL_LINE_LOOP;
                
        GL11.glBegin(mode);
            for (double t = 0; t <= TWO_PI; t += TWO_PI / ELLIPSE_POINTS)
                GL11.glVertex2f(
                        wf * (float) Math.cos(t) + xf, 
                        hf * (float) Math.sin(t) + yf);
        GL11.glEnd();        
    }      
       
    /**
	 * Fill an arc.
	 * 
	 * @param x1
	 *            The x coordinate of the top left corner of a box containing
	 *            the arc
	 * @param y1
	 *            The y coordinate of the top left corner of a box containing
	 *            the arc
	 * @param width
	 *            The width of the arc
	 * @param height
	 *            The height of the arc
	 * @param segments
	 *            The number of line segments to use when filling the arc
	 * @param start
	 *            The angle the arc starts at
	 * @param end
	 *            The angle the arc ends at
	 */
	public void fillArc(
            float x1, float y1, 
            float width, float height,
			int segments, 
            float start, float end) 
    {		

		while (end < start) 
        {
			end += 360;
		}

		float cx = x1 + (width / 2.0f);
		float cy = y1 + (height / 2.0f);

        int step = 360 / segments;
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
        
        bindColor();
        
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);		        
		GL11.glVertex2f(cx, cy);

		for (int a = (int) start; a < (int) (end + step); a += step) 
        {			
            float angle = a;			
            if (angle > end) 
            {
				angle = end;
			}

			float x = (float) (cx + (Math.cos(Math.toRadians(angle)) * width / 2.0f));
			float y = (float) (cy + (Math.sin(Math.toRadians(angle)) * height / 2.0f));

			GL11.glVertex2f(x, y);
		}
		GL11.glEnd();
        
        // Anti-alias code.
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(cx, cy);
        if (end != 360) {
            end -= 10;
        }

        for (int a = (int) start; a < (int) (end + step); a += step) 
        {
            float angle = a;
            if (angle > end) 
            {
                angle = end;
            }

            float x = (float) (cx + (Math.cos(Math.toRadians(angle + 10))
                    * width  / 2.0f));
            float y = (float) (cy + (Math.sin(Math.toRadians(angle + 10))
                    * height / 2.0f));

            GL11.glVertex2f(x, y);
        }
        GL11.glEnd();			
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);	
	}
        
    public void fillRoundRect(int x, int y, int width, int height, int cornerRadius)
    {
        fillRoundRect(x, y, width, height, cornerRadius, 30);
    }

    /**
	 * Fill a rounded rectangle
	 * 
	 * @param x
	 *            The x coordinate of the top left corner of the rectangle
	 * @param y
	 *            The y coordinate of the top left corner of the rectangle
	 * @param width
	 *            The width of the rectangle
	 * @param height
	 *            The height of the rectangle
	 * @param cornerRadius
	 *            The radius of the rounded edges on the corners
	 * @param segments
	 *            The number of segments to make the corners out of
	 */
	public void fillRoundRect(
            int x, int y, 
            int width, int height,
			int cornerRadius, int segments) 
    {
		if (cornerRadius < 0)
        {
			throw new IllegalArgumentException("corner radius must be > 0");
        }
        
		if (cornerRadius == 0) 
        {
			fillRect(x, y, width, height);
			return;
		}

		int minRadius = (int) Math.min(width, height) / 2;
		
        // Make sure that w & h are larger than 2 * cornerRadius.
		if (cornerRadius > minRadius) 
        {
			cornerRadius = minRadius;
		}

		int d = cornerRadius * 2;

		fillRect(x + cornerRadius, y, width - d, cornerRadius);
		fillRect(x, y + cornerRadius, cornerRadius, height - d);
		fillRect(x + width - cornerRadius, y + cornerRadius, cornerRadius, height - d);
		fillRect(x + cornerRadius, y + height - cornerRadius, width - d, cornerRadius);
		fillRect(x + cornerRadius, y + cornerRadius, width - d, height - d);

		// Bottom right - 0, 90.
		fillArc(x + width - d, y + height - d, d, d, segments, 0, 90);
		// Bottom left - 90, 180.
		fillArc(x, y + height - d, d, d, segments, 90, 180);
		// Top right - 270, 360.
		fillArc(x + width - d, y, d, d, segments, 270, 360);
		// Top left - 180, 270.
		fillArc(x, y, d, d, segments, 180, 270);
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
    // IKeyListener Attributes
    //--------------------------------------------------------------------------
    
//    Map<IKeyListener, Object> keyListenerMap = new WeakHashMap<IKeyListener, Object>();
//    final static Object KEY_FILLER = new Object();
    List<IKeyListener> keyListenerList = new ArrayList<IKeyListener>();
    
    //--------------------------------------------------------------------------
    // IKeyListener Methods
    //-------------------------------------------------------------------------- 
    
    public void addKeyListener(IKeyListener l)
    {
        LogManager.recordMessage("Added key listener for " + l);
        
        if (l == null)
            throw new NullPointerException();
        
        if (keyListenerList.contains(l))
            throw new IllegalStateException("Listener already registered!");
                
        keyListenerList.add(l);
    }
        
    public void removeKeyListener(IKeyListener l)
    {
        LogManager.recordMessage("Removed key listener for " + l);        
        
        if (l == null)
            throw new NullPointerException();
        
        if (!keyListenerList.contains(l))
            throw new IllegalStateException("Listener not registered!");
        
        keyListenerList.remove(l);
    }
    
    public void updateKeyPresses()
    {    
        // Before we start, make a copy of the listener list in case
        // one of the listeners modifies their listener status.
        List<IKeyListener> list = new ArrayList<IKeyListener>(keyListenerList);   
        
        // If non-empty, clear.
        if (this.keyPressSet.isEmpty() == false)
        {
            this.keyPressSet.clear();
        }

        while (org.lwjgl.input.Keyboard.next())
        {
            boolean state = Keyboard.getEventKeyState();
            int i = Keyboard.getEventKey();
            
            Modifier modifier;           
            switch (i)
            {
                case Keyboard.KEY_LSHIFT:
                    modifier = KeyEvent.Modifier.LEFT_SHIFT;
                    break;                                    
                    
                case Keyboard.KEY_LCONTROL:
                    modifier = KeyEvent.Modifier.LEFT_CTRL;
                    break;                                    
                    
                case Keyboard.KEY_LMETA:
                    modifier = KeyEvent.Modifier.LEFT_META;
                    break;
                    
                case Keyboard.KEY_LMENU:
                    modifier = KeyEvent.Modifier.LEFT_ALT;
                    break;
                    
                case Keyboard.KEY_RMENU:
                    modifier = KeyEvent.Modifier.RIGHT_ALT;
                    break;
                
                case Keyboard.KEY_RMETA:
                    modifier = KeyEvent.Modifier.RIGHT_META;
                    break;
                    
                case Keyboard.KEY_APPS:
                    modifier = KeyEvent.Modifier.APPLICATION;
                    break;
                
                case Keyboard.KEY_RCONTROL:
                    modifier = KeyEvent.Modifier.RIGHT_CTRL;
                    break;
                    
                case Keyboard.KEY_RSHIFT:
                    modifier = KeyEvent.Modifier.RIGHT_SHIFT;
                    break;
                    
                default:
                    modifier = KeyEvent.Modifier.NONE;
            }
            
            
//            LogManager.recordMessage(Keyboard.getKeyName(i) + " was pressed.");
//            LogManager.recordMessage("State is " + state + ".");
            
            char ch = Keyboard.getEventCharacter();                                                  
            
            KeyEvent event = new KeyEvent(this, ch, modifier);
            
            for (IKeyListener listener : list)
            {                                                        
                // If it equals NUL, then it's actually a key up.
                if (state)               
                {                    
                    listener.keyPressed(event);    
                }                
                else
                {
                    listener.keyReleased(event);
                }
            }
            
            this.keyPressSet.add(ch);
        }
    }
    
	/**
	 * Check if a particular key is current pressed.
	 *
	 * @param keyCode The code associated with the key to check 
	 * @return True if the specified key is pressed
	 */
	public boolean isKeyPressed(int key) 
    {			
            return this.keyPressSet.contains((char)key);
	}
    
    //--------------------------------------------------------------------------
    // IMouseListener Attributes
    //--------------------------------------------------------------------------
    
//    Map<IMouseListener, Object> mouseListenerMap = new WeakHashMap<IMouseListener, Object>();
//    final static Object MOUSE_FILLER = new Object();
    
    List<IMouseListener> mouseListenerList = new ArrayList<IMouseListener>();      
    private ImmutablePosition mousePosition = new ImmutablePosition(
            Mouse.getX(), height - Mouse.getY());
        
    //--------------------------------------------------------------------------
    // IMouseListener Methods
    //--------------------------------------------------------------------------        
    
    /**
     * Converts the LWJGL button number into a MouseEvent Button enum.
     * 
     * @param button
     * @return
     */
    public MouseEvent.Button toButtonEnum(int button)
    {
        // Left-click.
        if (button == 0)
        {
            return MouseEvent.Button.LEFT;
        }
        // Right-click.
        else if (button == 1)
        {
            return MouseEvent.Button.RIGHT;
        }
        // Middle-click.
        else if (button == 2)
        {
            return MouseEvent.Button.MIDDLE;
        }
        else
        {
            return MouseEvent.Button.NONE;
        }
    }
    
    /**
     * Checks to see if any mouse button is down and returns the first
     * one it finds that is down.
     * 
     * @return True if any mouse button is down, false otherwise.
     */
    public Button findPressedButton()
    {
        for (Button buttonEnum : Button.values())
        {
            if (buttonEnum == Button.NONE)
                continue;
            
            if (mouseStateMap.get(buttonEnum) == true)                
                return buttonEnum;
        }
        
        return Button.NONE;
    }
    
    /**
     * Fires all the queued up mouse events.
     */
    public void fireMouseEvents()
    {           
        // Before we start, make a copy of the listener list in case
        // one of the listeners modifies their listener status.
        List<IMouseListener> list = new ArrayList<IMouseListener>(mouseListenerList);        
        
        // Poll mouse events.
        while (Mouse.next())
        {    
            boolean mouseMoved = false;                        
            int mouseX = Mouse.getEventX();
            int mouseY = height - Mouse.getEventY();
            int deltaWheel = Mouse.getEventDWheel();
            
            // Check for mouse movement.            
            if (mouseX != mousePosition.getX() 
                    || mouseY != mousePosition.getY())
            {
                // Update internal position.                
                mousePosition = new ImmutablePosition(mouseX, mouseY);
                
                // Set an event.
                mouseMoved = true;                
            }            
            
            if (Mouse.getEventButton() != -1)
            {
                int button = Mouse.getEventButton();
                MouseEvent.Button buttonEnum = MouseEvent.Button.NONE;                                                    
                buttonEnum = toButtonEnum(button);

                MouseEvent.Type type;
                
                // Pressed.
                if (Mouse.getEventButtonState())
                {
                    type = MouseEvent.Type.MOUSE_PRESSED;
                    mouseStateMap.put(buttonEnum, true);
                }     
                // Released.
                else
                {
                    type = MouseEvent.Type.MOUSE_RELEASED;
                    mouseStateMap.put(buttonEnum, false);
                }
                    
                MouseEvent event = new MouseEvent(this,
                        buttonEnum,
                        EnumSet.noneOf(MouseEvent.Modifier.class),
                        mousePosition,
                        type,
                        deltaWheel);
                
                switch (type)
                {
                    case MOUSE_PRESSED:
                        
                        for (IMouseListener l : list)
                            l.mousePressed(event);
                        
                        break;

                    case MOUSE_RELEASED:                                               
                        
                        for (IMouseListener l : list)
                            l.mouseReleased(event);
                        
                        break;

                    default: throw new AssertionError();                    
                } // end switch                
                
            } // if
            
            if (mouseMoved == true)
            {
                Button buttonEnum = findPressedButton();
                if (buttonEnum != Button.NONE)
                {
                    MouseEvent event = new MouseEvent(this, 
                            buttonEnum,
                            EnumSet.noneOf(MouseEvent.Modifier.class),
                            mousePosition,
                            MouseEvent.Type.MOUSE_DRAGGED,
                            deltaWheel);
                
                    for (IMouseListener l : list)
                        l.mouseDragged(event);
                }
                else
                {
                    MouseEvent event = new MouseEvent(this, 
                            MouseEvent.Button.NONE,
                            EnumSet.noneOf(MouseEvent.Modifier.class),
                            mousePosition,
                            MouseEvent.Type.MOUSE_MOVED,
                            deltaWheel);
                
                    for (IMouseListener l : list)
                        l.mouseMoved(event);
                }                
            } // end if    
            
            if (deltaWheel != 0)
            {
                MouseEvent event = new MouseEvent(this, 
                        MouseEvent.Button.NONE,
                        EnumSet.noneOf(MouseEvent.Modifier.class),
                        mousePosition,
                        MouseEvent.Type.MOUSE_WHEEL,
                        deltaWheel);
                
                for (IMouseListener l : list)
                    l.mouseWheel(event);
            }
            
        } // end while
    }
    
    /**
     * Clears the mouse events instead of firing them.
     */
    public void clearMouseEvents()           
    {
        // Empty the mouse events.
        while (Mouse.next());
        
        // Update the mouse state map.
        for (int i = 0; i < 3; i++)
        {
            mouseStateMap.put(toButtonEnum(i), Mouse.isButtonDown(i));
        }
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
        
        mouseListenerList.remove(l);
    }
   
}
