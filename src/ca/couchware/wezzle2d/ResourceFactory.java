package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
//import ca.couchware.wezzle2d.java2d.Java2DGameWindow;
//import ca.couchware.wezzle2d.java2d.Java2DLabel;
//import ca.couchware.wezzle2d.java2d.SpriteStore;
import ca.couchware.wezzle2d.lwjgl.LWJGLGameWindow;
import ca.couchware.wezzle2d.lwjgl.LWJGLTextLabel;
import ca.couchware.wezzle2d.lwjgl.LWJGLSprite;
import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.menu.Loader;
import ca.couchware.wezzle2d.ui.ITextLabel;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
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
    { defaultLabelColor = color; }    
    
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
     * The window the game should use to render.
     */
	private IGameWindow window;    

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
		if (window != null)
		{
			throw new RuntimeException("You may not change the rendering type during runtime.");
		}

		this.renderer = renderer;
	}
    
    public Renderer getRenderer()
    {
        return renderer;
    }

	/**
	 * Retrieve the game window that should be used to render the game
	 * 
	 * @return The game window in which the game should be rendered
	 */
	public IGameWindow getGameWindow()
	{
		// if we've yet to create the game window, create the appropriate one
		// now
		if (window == null)
		{
			switch (renderer)
			{
//				case JAVA2D:				
//					window = new Java2DGameWindow();
//					break;
				
                case LWJGL:
                    window = new LWJGLGameWindow();
                    break;
			}
		}

		return window;
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
		if (window == null)
		{
			throw new RuntimeException(
					"Attempt to retrieve sprite before game window was created.");
		}

		switch (renderer)
		{
//			case JAVA2D:			
//				return SpriteStore.get().getSprite((Java2DGameWindow) window, path);                
                
            case LWJGL:
                return new LWJGLSprite((LWJGLGameWindow) window, path);					
		}

		throw new RuntimeException("Unknown rendering type: " + renderer);
	}

	/**
	 * Create a text object which will then be configured.
	 *
	 * @return A Text object that can be modified and drawn to screen.
	 */
	private ITextLabel getLabel(LabelBuilder builder)
	{
		if (window == null)
		{
			throw new RuntimeException(
					"Attempted to retrieve text before game window was created");
		}

		switch (renderer)
		{
//			case JAVA2D:			
//				return new Java2DLabel((Java2DGameWindow) window,
//                        builder.x,
//                        builder.y,
//                        builder.alignment,                       
//                        builder.color,
//                        builder.opacity,
//                        builder.size,
//                        builder.text,
//                        builder.visible,
//                        builder.cached);
			
            case LWJGL:
                return new LWJGLTextLabel((LWJGLGameWindow) window,
                        builder.x,
                        builder.y,
                        builder.alignment,                       
                        builder.color,
                        builder.opacity,
                        builder.size,
                        builder.text,
                        builder.visible);
		}

		throw new RuntimeException("Unknown rendering type: " + renderer);
	}      
    
    /** The variable that indicates whether the sprites have been prelaoded. */
    private boolean spritesPreloaded = false;
    
    /**
     * This method will preload all the sprites in the sprite directory.  It 
     * can only be run once.
     */
    public void preloadSprites(Loader loader)
    {
        // Check to see if the sprites have been preloaded.
        if (this.spritesPreloaded) 
        {
            LogManager.recordWarning("Attempted to preload sprites twice!");
            System.exit(0);
        }
        
        // Flag the sprites as preloaded.
        this.spritesPreloaded = true;       
        
        // The list of the sprites.
        List<String> spriteList = new ArrayList<String>();
        
        // Detect whether or not we're using a JAR file.
        URL jarPathUrl = ResourceFactory.class.getProtectionDomain().getCodeSource().getLocation();
        boolean isJar = jarPathUrl.toString().endsWith(".jar");
              
        // If we're running from a JAR, then we need to read the JAR entries to
        // figure out all the names of the sprites.
        if (isJar == true)
        {
            try
            {
                // Open the jar.
                JarInputStream in = new JarInputStream(jarPathUrl.openStream());

                while (true)
                {
                    JarEntry entry = in.getNextJarEntry();
                   
                    if (entry == null) 
                        break;
                    
                    if (entry.isDirectory() == true)
                        continue;                    

                    if (entry.getName().startsWith(Settings.getSpriteResourcesPath()))
                        spriteList.add(entry.getName());                    
                }
            }
            catch (IOException e)
            {
                LogManager.recordException(e);
            }        
        }
        // If we're running from the file system, all we need to do is use
        // the File class to get a list of the sprites.
        else
        {
            // The directory of sprites.
            File dir = null;

            try
            {            
                // Get a list of all the sprites in the sprites directory.
                URL url = this.getClass().getClassLoader()
                        .getResource(Settings.getSpriteResourcesPath());                        

                // Convert to file.
                dir = new File(url.toURI());
                
                // Construct the full URL to the sprite.
                for (String spriteName : dir.list())
                    spriteList.add(Settings.getSpriteResourcesPath() + "/" + spriteName);
            }
            catch (URISyntaxException e)
            {            
                LogManager.recordException(e);
                
            } // end try 
        }
                                           
        // Get the contents of the directory.
        for (final String spriteFilePath : spriteList)
        {
            loader.addTask(new Runnable()
            {
                public void run()
                {
                    LogManager.recordMessage("Preloading " + spriteFilePath + "...");
                    getSprite(spriteFilePath);
                }                 
            });                     
        } // end for
    }
    
    public static class LabelBuilder implements IBuilder<ITextLabel>
    {        
        private int x;
        private int y;        
                
        private EnumSet<Alignment> alignment = 
                EnumSet.of(Alignment.TOP, Alignment.LEFT);
        private Color color = defaultLabelColor;
        private int opacity = 100;
        private int size = 14;
        private String text = ""; 
        private boolean visible = true;
        private boolean cached = true;
        
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
            this.cached = label.isCached();
        }
        
        public LabelBuilder x(int val) { x = val; return this; }        
        public LabelBuilder y(int val) { y = val; return this; }
        public LabelBuilder xy(int xval, int yval) 
        { x(xval); y(yval); return this; }
        
        public LabelBuilder alignment(EnumSet<Alignment> val)
        { alignment = val; return this; }
        
        public LabelBuilder color(Color val)        
        { color = val; return this; }
        
        public LabelBuilder opacity(int val)
        { opacity = val; return this; }         
        
        public LabelBuilder size(int val)
        { size = val; return this; }
        
        public LabelBuilder text(String val)
        { text = val; return this; }     
        
        public LabelBuilder visible(boolean val)
        { visible = val; return this; } 
        
        public LabelBuilder cached(boolean val)
        { cached = val; return this; }

        public ITextLabel end()
        {
            return get().getLabel(this);
        }                
    }       
    	
}