        package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.graphics.Sprite;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.ui.Label;
import ca.couchware.wezzle2d.java2d.Java2DGameWindow;
import ca.couchware.wezzle2d.java2d.Java2DSpriteStore;
import ca.couchware.wezzle2d.java2d.Java2DLabel;
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
    public static enum RenderType
    {
        JAVA2D, LWJGL
    }    	

	/** 
	 * The type of rendering that we are currently using. 
	 */
	private RenderType renderType = RenderType.JAVA2D;
	
	/** 
     * The window the game should use to render.
     */
	private GameWindow window;    

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
	 * @param renderingType
	 *            The type of rendering to use
	 */
	public void setRenderingType(RenderType renderType)
	{
		// If the rendering type is unrecognized tell the caller.
		if (renderType != RenderType.JAVA2D)
		{
			// Note, we could create our own exception to be thrown here but it
			// seems a little bit over the top for a simple message. In general
			// RuntimeException should be sub-classed and thrown, not thrown
			// directly.
			throw new RuntimeException("Unknown rendering type specified: "
					+ renderType);
		}

		// If the window has already been created then we have already created
		// resources in
		// the current rendering method, we are not allowed to change rendering
		// types
		if (window != null)
		{
			throw new RuntimeException("Attempt to change rendering method at game runtime");
		}

		this.renderType = renderType;
	}

	/**
	 * Retrieve the game window that should be used to render the game
	 * 
	 * @return The game window in which the game should be rendered
	 */
	public GameWindow getGameWindow()
	{
		// if we've yet to create the game window, create the appropriate one
		// now
		if (window == null)
		{
			switch (renderType)
			{
				case JAVA2D:
				{
					window = new Java2DGameWindow();
					break;
				}
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
	public Sprite getSprite(String path)
	{
		if (window == null)
		{
			throw new RuntimeException(
					"Attempt to retrieve sprite before game window was created.");
		}

		switch (renderType)
		{
			case JAVA2D:
			{
				return Java2DSpriteStore.get().getSprite(
                        (Java2DGameWindow) window, path);
			}		
		}

		throw new RuntimeException("Unknown rendering type: " + renderType);
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

		switch (renderType)
		{
			case JAVA2D:
			{
				return new Java2DLabel((Java2DGameWindow) window,
                        builder.x,
                        builder.y,
                        builder.alignment,                       
                        builder.color,
                        builder.opacity,
                        builder.size,
                        builder.text,
                        builder.visible);
			}		
		}

		throw new RuntimeException("Unknown rendering type: " + renderType);
	}      
    
    public static class LabelBuilder implements IBuilder<ILabel>
    {        
        private final int x;
        private final int y;        
                
        private EnumSet<Alignment> alignment = 
                EnumSet.of(Alignment.TOP, Alignment.LEFT);
        private Color color = Color.WHITE;
        private int opacity = 100;
        private float size = 14.0f;
        private String text = ""; 
        private boolean visible = true;
        
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
        }
        
        public LabelBuilder alignment(EnumSet<Alignment> val)
        { alignment = val; return this; }
        
        public LabelBuilder color(Color val)        
        { color = val; return this; }
        
        public LabelBuilder opacity(int val)
        { opacity = val; return this; }         
        
        public LabelBuilder size(float val)
        { size = val; return this; }
        
        public LabelBuilder text(String val)
        { text = val; return this; }     
        
        public LabelBuilder visible(boolean val)
        { visible = val; return this; }                

        public ILabel end()
        {
            return get().getLabel(this);
        }                
    }
    	
}