package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.java2d.Java2DGameWindow;
import ca.couchware.wezzle2d.java2d.Java2DLabel;
import ca.couchware.wezzle2d.java2d.SpriteStore;
import ca.couchware.wezzle2d.lwjgl.LWJGLGameWindow;
import ca.couchware.wezzle2d.lwjgl.LWJGLLabel;
import ca.couchware.wezzle2d.lwjgl.LWJGLSprite;
import ca.couchware.wezzle2d.ui.ILabel;
import java.awt.Color;
import java.util.EnumSet;

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
	/** The single instance of this class to ever exist <singleton> */
	private static final ResourceFactory single = new ResourceFactory();

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
	private Renderer renderer = Renderer.JAVA2D;
	
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
		return single;
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
				case JAVA2D:				
					window = new Java2DGameWindow();
					break;
				
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
			case JAVA2D:			
				return SpriteStore.get().getSprite((Java2DGameWindow) window, path);                
                
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
	private ILabel getLabel(LabelBuilder builder)
	{
		if (window == null)
		{
			throw new RuntimeException(
					"Attempted to retrieve text before game window was created");
		}

		switch (renderer)
		{
			case JAVA2D:			
				return new Java2DLabel((Java2DGameWindow) window,
                        builder.x,
                        builder.y,
                        builder.alignment,                       
                        builder.color,
                        builder.opacity,
                        builder.size,
                        builder.text,
                        builder.visible,
                        builder.cached);
			
            case LWJGL:
                return new LWJGLLabel((LWJGLGameWindow) window,
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
    
    public static class LabelBuilder implements IBuilder<ILabel>
    {        
        private int x;
        private int y;        
                
        private EnumSet<Alignment> alignment = 
                EnumSet.of(Alignment.TOP, Alignment.LEFT);
        private Color color = Color.WHITE;
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
        
        public LabelBuilder(ILabel label)
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

        public ILabel end()
        {
            return get().getLabel(this);
        }                
    }
    	
}