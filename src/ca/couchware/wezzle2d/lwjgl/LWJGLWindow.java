/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */
package ca.couchware.wezzle2d.lwjgl;

import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.IWindowCallback;
import ca.couchware.wezzle2d.event.IKeyListener;
import ca.couchware.wezzle2d.event.IMouseListener;
import ca.couchware.wezzle2d.event.KeyEvent;
import ca.couchware.wezzle2d.event.KeyEvent.Arrow;
import ca.couchware.wezzle2d.event.KeyEvent.Modifier;
import ca.couchware.wezzle2d.event.MouseEvent;
import ca.couchware.wezzle2d.event.MouseEvent.Button;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class LWJGLWindow implements IWindow
{
    private SettingsManager settingsMan;
    private IWindowCallback callback;

    /** The key presses. */
    private Set<Character> keyPressSet;

    /** The mouse states. */
    private Map<MouseEvent.Button, Boolean> mouseStateMap;

    /** True if the game is currently "running", i.e. the game loop is looping. */
    private boolean gameRunning = true;

    /** True if the window is active. */
    private boolean active = false;

    /** The width of the game display area. */

    private int width;
    /** The height of the game display area. */

    private int height;

    /** The loader responsible for converting images into OpenGL textures. */
    private TextureLoader textureLoader;

    /**
     * Title of window, we get it before our window is ready, so store it 
     * until needed.
     */
    private String title;
    private Set<Modifier> modifiers;

    /**
     * Create a new game window that will use OpenGL to
     * render our game.
     */
    public LWJGLWindow(SettingsManager settingsMan)
    {
        this.settingsMan = settingsMan;
        this.keyPressSet = new HashSet<Character>();

        this.mouseStateMap =
                new EnumMap<Button, Boolean>(Button.class);

        this.mouseStateMap.put(Button.LEFT, false);
        this.mouseStateMap.put(Button.RIGHT, false);
        this.mouseStateMap.put(Button.MIDDLE, false);

        modifiers = EnumSet.noneOf(Modifier.class);

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
            System.out.println(
                    "Unable to enter fullscreen, continuing in windowed mode.");
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
                            if ((targetDisplayMode == null)
                                    || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel()))
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
                throw new RuntimeException(
                        "Failed to find value mode: " + width + "x" + height + " fullscreen=" + fullscreen + ".");
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
            throw new RuntimeException(
                    "Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + ".",
                                       e);
        }
    }

    /**
     * Start the rendering process. This method will cause the
     * display to redraw as fast as possible.
     */
    public void start()
    {
        // Get the number of samples and make sure they're
        // with in the correct range.
        int samples = settingsMan.getInt(
                Key.USER_GRAPHICS_ANTIALIASING_SAMPLES);
        if (samples < 0)
        {
            throw new IndexOutOfBoundsException(
                    "Number of samples must be 0 or more");
        }

        // Try to set the pixel format.
        PixelFormat pixelFormat = new PixelFormat()
                .withAlphaBits(0)
                .withDepthBits(16)
                .withStencilBits(1)
                .withSamples(samples);

        this.originalDisplayMode = Display.getDisplayMode();
        setDisplayMode(width, height, false);
        Display.setVSyncEnabled(true);
        Display.setTitle(this.title);

        try
        {
            // Try setting the pixel format.
            Display.create(pixelFormat);
        }
        catch (LWJGLException sampleException)
        {
            // If we failed, try using 0 samples.
            CouchLogger.get().recordWarning(this.getClass(),
                    "Could not set samples to: " + pixelFormat.getSamples());
            
            pixelFormat = new PixelFormat()
                .withAlphaBits(0)
                .withDepthBits(16)
                .withStencilBits(1)
                .withSamples(0);

            try
            {
                Display.create(pixelFormat);
            }
            catch (LWJGLException le)
            {
                throw new RuntimeException("LWJGL exception", le);
            }
        }

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

        // Antialises, but has terrible visual artifacts.
        //GL11.glEnable(GL11.GL_LINE_SMOOTH);
        //GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
        //GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);

        textureLoader = new TextureLoader();

        if (callback != null)
        {
            callback.initialize();
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
    public void setGameWindowCallback(IWindowCallback callback)
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

        // Clear the stencil buffer.        
        GL11.glClearStencil(0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

        while (gameRunning)
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
            else // if (Display.isActive())
            {
                // Notify of activation.
                if (!this.active)
                {
                    callback.windowActivated();
                    this.active = true;
                }

                callback.update();
                //callback.draw();

                // Only bother rendering if the window is visible or dirty
                if (Display.isVisible() || Display.isDirty())
                {
                    callback.draw();
                }

                GL11.glColor3f(0, 0, 0);
                Display.sync(TICKS_PER_SECOND);
            }
        } // end while
    }

    //--------------------------------------------------------------------------
    // Color & clip code.
    //--------------------------------------------------------------------------
    public void setCursor(int type)
    {
        // Intentionally left blank.
    }

    public ImmutablePosition getMouseImmutablePosition()
    {
        return new ImmutablePosition(Mouse.getX(), height - Mouse.getY());
    }

    //--------------------------------------------------------------------------
    // IKeyListener Methods
    //--------------------------------------------------------------------------
    List<IKeyListener> keyListenerList = new ArrayList<IKeyListener>();

    public void addKeyListener(IKeyListener l)
    {
        //CouchLogger.get().recordMessage(this.getClass(), "Added key listener for " + l);

        if (l == null)
        {
            throw new NullPointerException();
        }

        if (keyListenerList.contains(l))
        {
            throw new IllegalStateException("Listener already registered!");
        }

        keyListenerList.add(l);
    }

    public void removeKeyListener(IKeyListener l)
    {
        CouchLogger.get().recordMessage(this.getClass(),
                                        "Removed key listener for " + l);

        if (l == null)
        {
            throw new NullPointerException();
        }

        if (!keyListenerList.contains(l))
        {
            throw new IllegalStateException("Listener not registered!");
        }

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

            Arrow arrow;
            switch (i)
            {
                case Keyboard.KEY_UP:
                    arrow = KeyEvent.Arrow.KEY_UP;
                    break;

                case Keyboard.KEY_DOWN:
                    arrow = KeyEvent.Arrow.KEY_DOWN;
                    break;

                case Keyboard.KEY_LEFT:
                    arrow = KeyEvent.Arrow.KEY_LEFT;
                    break;

                case Keyboard.KEY_RIGHT:
                    arrow = KeyEvent.Arrow.KEY_RIGHT;
                    break;

                default:
                    arrow = KeyEvent.Arrow.NONE;

            }                

            // WARNING: the ctrl mask randomly subtracts 96 from the ascii
            // of any character returned from getEventCharacter(). This is because
            // LWJGL is fucked. We had a 30 min convo about this and decided
            // to fuck the ctrl key. Alt is better.
            char ch = Keyboard.getEventCharacter();
            
            //CouchLogger.get().recordMessage( this.getClass(), (int) ch + " " + Keyboard.getKeyName( i ) );

            KeyEvent event = new KeyEvent(this, ch, this.modifiers, arrow);

            for (IKeyListener listener : list)
            {
                // If it equals NUL, then it's actually a key up.
                if (state)
                {
                    modifiers.add(modifier);
                    listener.keyPressed(event);
                }
                else
                {
                    modifiers.remove(modifier);
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
        return this.keyPressSet.contains((char) key);
    }

    //--------------------------------------------------------------------------
    // IMouseListener Methods
    //--------------------------------------------------------------------------

    List<IMouseListener> mouseListenerList = new ArrayList<IMouseListener>();
    private ImmutablePosition mousePosition = new ImmutablePosition(
            Mouse.getX(), height - Mouse.getY());


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
            {
                continue;
            }

            if (mouseStateMap.get(buttonEnum) == true)
            {
                return buttonEnum;
            }
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
        List<IMouseListener> list =
                new ArrayList<IMouseListener>(mouseListenerList);

        // Poll mouse events.
        while (Mouse.next())
        {
            boolean mouseMoved = false;
            int mouseX = Mouse.getEventX();
            int mouseY = height - Mouse.getEventY();
            int deltaWheel = Mouse.getEventDWheel();

            // Check for mouse movement.            
            if (mouseX != mousePosition.getX() || mouseY != mousePosition.getY())
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
                                                  EnumSet.noneOf(
                        MouseEvent.Modifier.class),
                                                  mousePosition,
                                                  type,
                                                  deltaWheel);

                switch (type)
                {
                    case MOUSE_PRESSED:

                        for (IMouseListener l : list)
                        {
                            l.mousePressed(event);
                        }

                        break;

                    case MOUSE_RELEASED:

                        for (IMouseListener l : list)
                        {
                            l.mouseReleased(event);
                        }

                        break;

                    default:
                        throw new AssertionError();
                } // end switch                

            } // if

            if (mouseMoved)
            {
                Button buttonEnum = findPressedButton();
                if (buttonEnum != Button.NONE)
                {
                    MouseEvent event = new MouseEvent(this,
                                                      buttonEnum,
                                                      EnumSet.noneOf(
                            MouseEvent.Modifier.class),
                                                      mousePosition,
                                                      MouseEvent.Type.MOUSE_DRAGGED,
                                                      deltaWheel);

                    for (IMouseListener l : list)
                    {
                        l.mouseDragged(event);
                    }
                }
                else
                {
                    MouseEvent event = new MouseEvent(this,
                                                      MouseEvent.Button.NONE,
                                                      EnumSet.noneOf(
                            MouseEvent.Modifier.class),
                                                      mousePosition,
                                                      MouseEvent.Type.MOUSE_MOVED,
                                                      deltaWheel);

                    for (IMouseListener l : list)
                    {
                        l.mouseMoved(event);
                    }
                }
            } // end if    

            if (deltaWheel != 0)
            {
                MouseEvent event = new MouseEvent(this,
                                                  MouseEvent.Button.NONE,
                                                  EnumSet.noneOf(
                        MouseEvent.Modifier.class),
                                                  mousePosition,
                                                  MouseEvent.Type.MOUSE_WHEEL,
                                                  deltaWheel);

                for (IMouseListener l : list)
                {
                    l.mouseWheel(event);
                }
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
        {
            throw new NullPointerException();
        }

        if (mouseListenerList.contains(l))
        {
            throw new IllegalStateException("Listener already registered!");
        }

        mouseListenerList.add(l);
    }

    public void removeMouseListener(IMouseListener l)
    {
        if (l == null)
        {
            throw new NullPointerException();
        }

        if (!mouseListenerList.contains(l))
        {
            throw new IllegalStateException("Listener not registered!");
        }

        mouseListenerList.remove(l);
    }
}
