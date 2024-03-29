package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.IBuilder;
import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.lwjgl.LWJGLWindow;
import ca.couchware.wezzle2d.lwjgl.LWJGLTextLabel;
import ca.couchware.wezzle2d.lwjgl.LWJGLSprite;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.ITextLabel;
import java.awt.Canvas;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


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
        LWJGL
    }

    private SettingsManager settingsMan;
    private Renderer renderer = null;
    private IWindow win;

    /**
     * The default contructor has been made private to prevent construction of
     * this class anywhere externally. This is used to enforce the singleton
     * pattern that this class attempts to follow
     */
    private ResourceFactory()
    {
        
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

    public void cleanup()
    {        
        renderer = null;
        win = null;
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
        // resources inthe current rendering method, we are not allowed to change rendering        
        if ( win == null )
        {
            this.renderer = renderer;
        }        
    }

    /**
     * Set the settings manager.  This must be set before any of the
     * other methods of the resource factory are used.
     * 
     * @param settingsMan
     */
    public void setSettingsManager(SettingsManager settingsMan)
    {
        if ( settingsMan == null )
        {
            throw new NullPointerException( "Settings Manager is null" );
        }

        this.settingsMan = settingsMan;
    }  

    /**
     * Create the game window that will be used to render the game.
     *
     * @return The game window in which the game should be rendered.
     */
    public IWindow createWindow( Canvas parent )
    {       
        if ( win == null )
        {
            switch ( renderer )
            {
                case LWJGL:
                    win = new LWJGLWindow( parent, this.settingsMan );
                    break;
            }           
        }        

        return win;
    }

    /**
     * Retrieve the game window that should be used to render the game.
     *
     * @return The game window in which the game should be rendered.
     */
    public IWindow getWindow()
    {
        if ( win == null )
        {
            throw new RuntimeException("You need to create a window to get one");
        }

        return win;
    }

    /**
     * Create or get a sprite which displays the image that is pointed to in the
     * classpath by "ref"
     *
     * @param path A reference to the image to load.
     * @return A sprite that can be drawn onto the current graphics context.
     */
    public ISprite getSprite(String path)
    {
        if ( win == null )
        {
            throw new RuntimeException(
                    "Attempt to retrieve sprite before game window was created" );
        }

        switch ( renderer )
        {
            case LWJGL:
                return new LWJGLSprite( (LWJGLWindow) win, path );
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
        if ( win == null )
        {
            throw new RuntimeException(
                    "Attempted to retrieve text before game window was created" );
        }

        switch ( renderer )
        {
            case LWJGL:
                return new LWJGLTextLabel( (LWJGLWindow) win,
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

    /**
     * This method will preload all the sprites in the sprite directory.  It 
     * can only be run once.
     */
    public Collection<Runnable> preloadSprites()
    {                
        // The list of the sprites.
        List<String> spriteList = new ArrayList<String>();        

        InputStream inputStream = ResourceFactory.class
                .getClassLoader()
                .getResourceAsStream(Settings.getSpriteResourcesListPath());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line = null;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                spriteList.add(line);
            }

            reader.close();
        }
        catch (IOException ex)
        {
            CouchLogger.get().recordException(this.getClass(), ex, true /* Fatal */);
        }

        final String spritePath = Settings.getSpriteResourcesPath();
        // Get the contents of the directory.
        List<Runnable> runnableList = new ArrayList<Runnable>();
        for ( final String spriteFilePath : spriteList )
        {
            InputStream in = ResourceFactory.class
                .getClassLoader()
                .getResourceAsStream(spritePath + "/" + spriteFilePath);

            if (in == null)
            {
                CouchLogger.get().recordMessage(this.getClass(), 
                        "Could not find sprite: " + spriteFilePath);
                continue;
            }
            else
            {
                try
                {
                    in.close();
                }
                catch (IOException ex)
                {
                    CouchLogger.get().recordException(this.getClass(), ex);
                }
            }

            runnableList.add( new Runnable()
            {               
                public void run()
                {
                    getSprite( spritePath + "/" + spriteFilePath );
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
