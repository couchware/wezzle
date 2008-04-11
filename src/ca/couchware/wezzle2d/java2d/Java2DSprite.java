package ca.couchware.wezzle2d.java2d;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import ca.couchware.wezzle2d.Sprite;
import ca.couchware.wezzle2d.util.Util;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

/**
 * A sprite to be displayed on the screen. Note that a sprite contains no state
 * information, i.e. its just the image and not the location. This allows us to
 * use a single sprite in lots of different places without having to store
 * multiple copies of the image.
 * 
 * @author Cameron McKay (based on code by Kevin Glass)
 */
public class Java2DSprite implements Sprite
{    
	/** 
     * The image to be drawn for this sprite.
     */
	private BufferedImage image;
    
    /**
     * The volatile image for this sprite.
     */
    private VolatileImage vimage;
	
	/** 
     * The game window to which this sprite is going to be drawn 
     */
	private Java2DGameWindow window;
    
    /**
     * The last alpha composite.
     */
    private AlphaComposite alpha;

	/**
	 * Create a new sprite based on an image
	 * 
	 * @param window
	 *            The game window to which this sprite is going to be drawn
	 * @param image
	 *            The image that is this sprite
	 */
	public Java2DSprite(Java2DGameWindow window, BufferedImage image)
	{
		this.image = image;
	    this.window = window;
        this.alpha = null;
        
        vimage = createVolatileImage(image.getWidth(), 
                    image.getHeight(), Transparency.TRANSLUCENT);        
        render();
	}

	/**
	 * Get the width of the drawn sprite
	 * 
	 * @return The width in pixels of this sprite
	 */
	public int getWidth()
	{
		return image.getWidth(null);
	}

	/**
	 * Get the height of the drawn sprite
	 * 
	 * @return The height in pixels of this sprite
	 */
	public int getHeight()
	{
		return image.getHeight(null);
	}
    
        
    private AlphaComposite getComposite(int opacity)
    {   
        // If we have a cached composite.
        if (alpha != null)
        {
            // Determine the cached opacity integer.
            int cachedOpacity  = (int) (alpha.getAlpha() * 100);
            
            // If they're equal, then return the cache.
            if (cachedOpacity == opacity)
            {
//                Util.handleMessage("Using cached opacity.", 
//                        Thread.currentThread());
                return alpha;            
            }
        }                
        
        // Create new alpha composite and return it.
        alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
                    ((float) opacity) / 100.0f);
        
        return alpha;                               
    }

	/**
	 * Draw the sprite onto the graphics context provided.
	 * 
	 * @param x
	 *            The x location at which to draw the sprite
	 * @param y
	 *            The y location at which to draw the sprite
	 */
	public void draw(int x, int y)
	{
		Graphics2D g = window.getDrawGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, 
				RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		
		g.drawImage(image, x, y, null);
	}

    public void draw(final int x, final int y, int width, int height, 
            double theta, int opacity)
    {
        Graphics2D g = window.getDrawGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, 
				RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        GraphicsEnvironment ge = 
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        
        GraphicsConfiguration gc = 
                ge.getDefaultScreenDevice().getDefaultConfiguration();
	
        // Since we're copying from the VolatileImage, we need it in a good state.
        if (vimage.validate(gc) != VolatileImage.IMAGE_OK) 
        {
            vimage = createVolatileImage(vimage.getWidth(), 
                    vimage.getHeight(), vimage.getTransparency());
            render(); // This is coming up in Code Example 4.
        }    
        
        // Opacity.
        Composite c = null;
        
        if (opacity != 100)
        {
            c = g.getComposite();
            g.setComposite(getComposite(opacity));
        }
                
        // Rotate the sprite.
        if (theta != 0.0)
            g.rotate(theta, x + width / 2, y + height / 2);       
        
        // Draw the sprite.
		g.drawImage(vimage, x, y, width, height, null);                      
        
        // Rotate back.
        if (theta != 0.0)
            g.rotate(-theta, x + width / 2, y + height / 2);
        
        // Opacity.
        if (opacity != 100)        
            g.setComposite(c);            
    }

    /**
     * TODO Needs documentation. Rotation has not been tested for this method.
     * 
     * @param x
     * @param y
     * @param clipX
     * @param clipY
     * @param clipWidth
     * @param clipHeight
     * @param theta
     * @param opacity
     */
    public void drawRegion(int x, int y, 
            int width, int height,
            int rx, int ry, 
            int rwidth, int rheight, 
            double theta, int opacity)
    {
        Graphics2D g = window.getDrawGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, 
				RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        GraphicsEnvironment ge = 
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        
        GraphicsConfiguration gc = 
                ge.getDefaultScreenDevice().getDefaultConfiguration();
	
        // Since we're copying from the VolatileImage, we need it in a good state.
        if (vimage.validate(gc) != VolatileImage.IMAGE_OK) 
        {
            vimage = createVolatileImage(vimage.getWidth(), 
                    vimage.getHeight(), vimage.getTransparency());
            render();
        }       
        
        // Set the clip.
        Rectangle r = g.getClipBounds();
        g.setClip(x, y, rwidth, rheight);
        
        // Opacity.
        Composite c = null;
        if (opacity != 100)
        {
            c = g.getComposite();
            g.setComposite(getComposite(opacity));
        }                       
        
        // Rotate the sprite.
        if (theta != 0.0)
            g.rotate(theta, x + width / 2, y + height / 2);    
        
        // Draw the sprite.
		g.drawImage(vimage, x - rx, y - ry, width, height, null);                                     
        
        // Rotate back.
        if (theta != 0.0)
            g.rotate(-theta, x + width / 2, y + height / 2);
        
        // Opacity.
        if (opacity != 100)        
            g.setComposite(c); 
        
        // Clear the clip.
        g.setClip(r);
    }        
    
    /**
     * Creates a volatile image with the given width, height and transparency.
     * 
     * Based on code found at:
     *     http://gpwiki.org/index.php/Java:Tutorials:VolatileImage.
     * 
     * @param width
     * @param height
     * @param transparency
     * @return A new volatile image.
     */
    private VolatileImage createVolatileImage(int width, int height, 
            int transparency)
    {
        // The graphics environment/configuration.
        GraphicsEnvironment ge = 
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        
        GraphicsConfiguration gc = 
                ge.getDefaultScreenDevice().getDefaultConfiguration();
        
        // The volatile image.
        VolatileImage newImage = null;
        newImage = 
                gc.createCompatibleVolatileImage(width, height, transparency);

        // Check to make sure the image is valid.
        int valid = newImage.validate(gc);

        // If it's not, try again until we get an image.
        if (valid == VolatileImage.IMAGE_INCOMPATIBLE)
        {
            newImage = this.createVolatileImage(width, height, transparency);
            return newImage;
        }

        // Return the volatile image.
        return newImage;
    }
    
    /**
     * Renders the volatile image.
     */
    public void render()
    {
        GraphicsEnvironment ge = 
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        
        GraphicsConfiguration gc =                 
                ge.getDefaultScreenDevice().getDefaultConfiguration();

        Graphics2D g = null;

        do
        {
            int valid = vimage.validate(gc);

            if (valid == VolatileImage.IMAGE_INCOMPATIBLE)
            {
                vimage = createVolatileImage(
                        image.getWidth(), 
                        image.getHeight(), 
                        Transparency.TRANSLUCENT);
            }

            try
            {
                g = vimage.createGraphics();        
                
                // These commands cause the Graphics2D object to clear to 
                // (0,0,0,0).
                g.setComposite(AlphaComposite.Src);
                
                // Sets the color to black.
                g.setColor(Color.BLACK);
                
                // Clears the image.
                g.clearRect(0, 0, vimage.getWidth(), vimage.getHeight()); 
                
                // Draw the image to the volatile image.
                g.drawImage(image, null, 0, 0);
            }
            finally
            {
                // It's always best to dispose of your Graphics objects.
                g.dispose();
            }
        }
        while (vimage.contentsLost() == true);
    }
    
}