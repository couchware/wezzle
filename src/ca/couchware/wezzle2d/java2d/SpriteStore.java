package ca.couchware.wezzle2d.java2d;

import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.manager.LogManager;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 * A resource manager for sprites in the game. Its often quite important how and
 * where you get your game resources from. In most cases it makes sense to have
 * a central resource loader that goes away, gets your resources and caches them
 * for future use.
 * <p>
 * [singleton]
 * <p>
 * 
 * @author Kevin Glass
 */
public class SpriteStore
{
	/**
	 * The single instance of this class
	 */
	private static SpriteStore single = new SpriteStore();

	/**
	 * Get the single instance of this class .
	 * 
	 * @return The single instance of this class
	 */
	public static SpriteStore get()
	{
		return single;
	}

	/**
	 * The cached sprite map, from reference to sprite instance
	 */
	private HashMap<String, ISprite> sprites = new HashMap<String, ISprite>();

	/**
	 * Retrieve a sprite from the store
	 * 
	 * @param window
	 *            The window to which the sprite will be drawn
	 * @param ref
	 *            The reference to the image to use for the sprite
	 * @return A sprite instance containing an accelerate image of the request
	 *         reference
	 */
	public ISprite getSprite(Java2DGameWindow window, String path)
	{
		// if we've already got the sprite in the cache
		// then just return the existing version
		if (sprites.get(path) != null)
		{
			return (ISprite) sprites.get(path);
		}

		// otherwise, go away and grab the sprite from the resource
		// loader
		BufferedImage sourceImage = null;

		try
		{
			// The ClassLoader.getResource() ensures we get the sprite
			// from the appropriate place, this helps with deploying the game
			// with things like webstart. You could equally do a file look
			// up here.
			URL url = this.getClass().getClassLoader().getResource(path);

			if (url == null)
			{
				LogManager.recordMessage("Can't find path: " + path, 
                        "Java2DSpriteStore#getSprite");
			}

			// use ImageIO to read the image in
			sourceImage = ImageIO.read(url);
		}
		catch (IOException e)
		{
			LogManager.recordMessage("Failed to load: " + path,
                    "Java2DSpriteStore#getSprite");
		}

		// Create an accelerated image of the right size to store our sprite in.
//		GraphicsConfiguration gc = GraphicsEnvironment
//				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
//				.getDefaultConfiguration();
//        
//		Image image = gc.createCompatibleImage(sourceImage.getWidth(),
//				sourceImage.getHeight(), Transparency.TRANSLUCENT);
//
//		// draw our source image into the accelerated image
//		image.getGraphics().drawImage(sourceImage, 0, 0, null);

		// create a sprite, add it the cache then return it
		ISprite sprite = new Java2DSprite(window, sourceImage);
		sprites.put(path, sprite);

		return sprite;
	}                
    
}