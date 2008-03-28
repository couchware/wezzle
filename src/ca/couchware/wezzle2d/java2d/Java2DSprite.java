package ca.couchware.wezzle2d.java2d;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import ca.couchware.wezzle2d.Sprite;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Rectangle;

/**
 * A sprite to be displayed on the screen. Note that a sprite contains no state
 * information, i.e. its just the image and not the location. This allows us to
 * use a single sprite in lots of different places without having to store
 * multiple copies of the image.
 * 
 * @author Kevin Glass
 */
public class Java2DSprite implements Sprite
{    
	/** 
     * The image to be drawn for this sprite 
     */
	private Image image;
	
	/** 
     * The game window to which this sprite is going to be drawn 
     */
	private Java2DGameWindow window;

	/**
	 * Create a new sprite based on an image
	 * 
	 * @param window
	 *            The game window to which this sprite is going to be drawn
	 * @param image
	 *            The image that is this sprite
	 */
	public Java2DSprite(Java2DGameWindow window, Image image)
	{
		this.image = image;
		this.window = window;
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

        // Opacity.
        Composite c = null;
        if (opacity != 100)
        {
            c = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
                    ((float) opacity) / 100.0f));
        }
                
        // Rotate the sprite.
        if (theta != 0.0)
            g.rotate(theta, x + width / 2, y + height / 2);       
        
        // Draw the sprite.
		g.drawImage(image, x, y, width, height, null);                      
        
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
    public void drawClipped(int x, int y, 
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

        // Set the clip.
        Rectangle r = g.getClipBounds();
        g.setClip(x, y, rwidth, rheight);
        
        // Opacity.
        Composite c = null;
        if (opacity != 100)
        {
            c = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
                    ((float) opacity) / 100.0f));
        }                       
        
        // Rotate the sprite.
        if (theta != 0.0)
            g.rotate(theta, x + width / 2, y + height / 2);    
        
        // Draw the sprite.
		g.drawImage(image, x - rx, y - ry, width, height, null);                                     
        
        // Rotate back.
        if (theta != 0.0)
            g.rotate(-theta, x + width / 2, y + height / 2);
        
        // Opacity.
        if (opacity != 100)        
            g.setComposite(c); 
        
        // Clear the clip.
        g.setClip(r);
    }        
    
}