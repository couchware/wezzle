package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.IBuilder;
import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.lwjgl.LWJGLWindow;
import ca.couchware.wezzle2d.lwjgl.LWJGLGraphics;
import ca.couchware.wezzle2d.lwjgl.LWJGLTextLabel;
import ca.couchware.wezzle2d.lwjgl.LWJGLSprite;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.ITextLabel;
import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * A central reference point for creating resources for use in the game. The
 * resources return may be implemented in several different rendering contexts
 * but will also work within the GameWindow supplied from this class. For
 * instance, a Sprite retrieved as a resource will draw happily in the
 * GameWindow supplied from this factory
 * 
 * @author Kevin Glass
 */
public class ResourceFactory
{
    /** The default label color. */
    private static Color defaultLabelColor = Color.RED;

    /**
     * Change the default color for all sprite buttons.
     * 
     * @param The new color.
     */
    public static void setDefaultLabelColor(Color color)
    {
        defaultLabelColor = color;
    }

    /** The single instance of this class to ever exist. */
    private static final ResourceFactory SINGLE = new ResourceFactory();

    /**
     * The choice of rendering engines.
     */
    public static enum Renderer
    {
        /** Use the Java2D rendering engine. */
        JAVA2D,
        /** Use the LWJGL OpenGL engine. */
        LWJGL

    }
    /**
     * The type of rendering that we are currently using.
     */
    private Renderer renderer = Renderer.LWJGL;

    /**
     * The settings manager.
     */
    private SettingsManager settingsMan;

    /**
     * The window the game should use to render.
     */
    private IWindow window;

    /**
     * The graphics the game should use to draw.
     */
    private IGraphics gfx;

    /**
     * The default contructor has been made private to prevent construction of
     * this class anywhere externally. This is used to enforce the singleton
     * pattern that this class attempts to follow
     */
    private ResourceFactory()
    {
        // Intentionally blank.
    }

    /**
     * Retrieve the single instance of this class.
     *
     * @return The single instance of this class.
     */
    public static ResourceFactory get()
    {
        return SINGLE;
    }

    /**
     * Set the rendering method that should be used. Note: This can only be done
     * before the first resource is accessed.
     *
     * @param renderer
     *            The type of render to use.
     */
    public void setRenderer(Renderer renderer)
    {
        // If the window has already been created then we have already created
        // resources in
        // the current rendering method, we are not allowed to change rendering
        // types
        if ( window != null )
        {
            throw new RuntimeException( "You may not change the rendering type during runtime" );
        }

        this.renderer = renderer;
    }

    public Renderer getRenderer()
    {
        return renderer;
    }

    public void setSettingsManager(SettingsManager settingsMan)
    {
        if ( settingsMan == null )
        {
            throw new NullPointerException( "Settings Manager is null" );
        }

        this.settingsMan = settingsMan;
    }

    public SettingsManager getSettingsManager()
    {
        return this.settingsMan;
    }

    /**
     * Retrieve the game window that should be used to render the game
     *
     * @return The game window in which the game should be rendered
     */
    public IWindow getWindow()
    {
        // if we've yet to create the game window, create the appropriate one
        // now
        if ( window == null )
        {
            switch ( renderer )
            {
                case LWJGL:
                    window = new LWJGLWindow( this.settingsMan );
                    break;
            }
        }

        return window;
    }

    public IGraphics getGraphics()
    {
        // if we've yet to create the game window, create the appropriate one
        // now
        if ( gfx == null )
        {
            switch ( renderer )
            {
                case LWJGL:
                    gfx = new LWJGLGraphics();
                    break;
            }
        }

        return gfx;
    }

    /**
     * Create or get a sprite which displays the image that is pointed to in the
     * classpath by "ref"
     *
     * @param path
     *            A reference to the image to load
     * @return A sprite that can be drawn onto the current graphics context.
     */
    public ISprite getSprite(String path)
    {
        if ( window == null )
        {
            throw new RuntimeException(
                    "Attempt to retrieve sprite before game window was created" );
        }

        switch ( renderer )
        {
            case LWJGL:
                return new LWJGLSprite(
                        (LWJGLWindow) window,
                        (LWJGLGraphics) gfx,
                        path );
        }

        throw new RuntimeException( "Unknown rendering type: " + renderer );
    }

    /**
     * Create a text object which will then be configured.
     *
     * @return A Text object that can be modified and drawn to screen.
     */
    private ITextLabel getLabel(LabelBuilder builder)
    {
        if ( window == null )
        {
            throw new RuntimeException(
                    "Attempted to retrieve text before game window was created" );
        }

        switch ( renderer )
        {
            case LWJGL:
                return new LWJGLTextLabel( (LWJGLWindow) window,
                        builder.x,
                        builder.y,
                        builder.alignment,
                        builder.color,
                        builder.opacity,
                        builder.size,
                        builder.text,
                        builder.visible );
        }

        throw new RuntimeException( "Unknown rendering type: " + renderer );
    }

    /** The variable that indicates whether the sprites have been prelaoded. */
    private boolean spritesPreloaded = false;

    /**
     * This method will preload all the sprites in the sprite directory.  It 
     * can only be run once.
     */
    public Collection<Runnable> preloadSprites()
    {
        // Check to see if the sprites have been preloaded.
        if ( this.spritesPreloaded )
        {
            CouchLogger.get().recordWarning( this.getClass(), "Attempted to preload sprites twice!" );
            System.exit( 0 );
        }

        // Flag the sprites as preloaded.
        this.spritesPreloaded = true;

        // The list of the sprites.
        List<String> spriteList = new ArrayList<String>();

        // Detect whether or not we're using a JAR file.
        URI jarPathUrl = null;
        try
        {
            jarPathUrl = ResourceFactory.class.getProtectionDomain().
                    getCodeSource().getLocation().toURI();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        try
        {
            // Open the jar.
            JarInputStream in = new JarInputStream( jarPathUrl.toURL().
                    openStream() );

            while ( true )
            {
                JarEntry entry = in.getNextJarEntry();

                if ( entry == null )
                {
                    break;
                }

                if ( entry.isDirectory() )
                {
                    continue;
                }

                if ( entry.getName().startsWith( Settings.getSpriteResourcesPath() ) )
                {
                    spriteList.add( entry.getName() );
                }
            }
        }
        catch ( IOException e )
        {
            CouchLogger.get().recordException( this.getClass(), e, true /* Fatal */ );
        }

        // Get the contents of the directory.
        List<Runnable> runnableList = new ArrayList<Runnable>();
        for ( final String spriteFilePath : spriteList )
        {
            runnableList.add( new Runnable()
            {
                public void run()
                {
                    //CouchLogger.get().recordMessage( ResourceFactory.class,
                    //        "Preloading " + spriteFilePath + "..." );
                    getSprite( spriteFilePath );
                }

            } );
        } // end for

        return runnableList;
    }

    public static class LabelBuilder implements IBuilder<ITextLabel>
    {
        private int x;
        private int y;

        private EnumSet<Alignment> alignment =
                EnumSet.of( Alignment.TOP, Alignment.LEFT );
        private Color color = defaultLabelColor;
        private int opacity = 100;
        private int size = 14;
        private String text = "";
        private boolean visible = true;        

        public LabelBuilder(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        public LabelBuilder(ITextLabel label)
        {
            this.x = label.getX();
            this.y = label.getY();
            this.alignment = label.getAlignment();
            this.color = label.getColor();
            this.opacity = label.getOpacity();
            this.size = label.getSize();
            this.text = label.getText();
            this.visible = label.isVisible();            
        }

        public LabelBuilder x(int val)
        {
            x = val;
            return this;
        }

        public LabelBuilder y(int val)
        {
            y = val;
            return this;
        }

        public LabelBuilder xy(int xval, int yval)
        {
            x( xval );
            y( yval );
            return this;
        }

        public LabelBuilder alignment(EnumSet<Alignment> val)
        {
            alignment = val;
            return this;
        }

        public LabelBuilder color(Color val)
        {
            color = val;
            return this;
        }

        public LabelBuilder opacity(int val)
        {
            opacity = val;
            return this;
        }

        public LabelBuilder size(int val)
        {
            size = val;
            return this;
        }

        public LabelBuilder text(String val)
        {
            text = val;
            return this;
        }

        public LabelBuilder visible(boolean val)
        {
            visible = val;
            return this;
        }      

        public ITextLabel build()
        {
            return get().getLabel( this );
        }

    }
}
