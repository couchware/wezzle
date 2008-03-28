package ca.couchware.wezzle2d;

/**
 * A sprite to be displayed on the screen. Note that a sprite contains no state
 * information, i.e. its just the image and not the location. This allows us to
 * use a single sprite in lots of different places without having to store
 * multiple copies of the image.
 * 
 * @author Kevin Glass
 */
public interface Sprite
{
	/**
	 * Get the width of the drawn sprite
	 * 
	 * @return The width in pixels of this sprite.
	 */
	public int getWidth();

	/**
	 * Get the height of the drawn sprite
	 * 
	 * @return The height in pixels of this sprite.
	 */
	public int getHeight();  

	/**
	 * Draw the sprite onto the graphics context provided.
	 * 
	 * @param x
	 *            The x location at which to draw the sprite.
	 * @param y
	 *            The y location at which to draw the sprite.
	 */
	public void draw(int x, int y);    
    
    /**
	 * Draw the sprite, scaled to the width and height provided, rotated by
     * theta, with the given opacity percentage (as an integer).
	 * 
	 * @param x
	 *            The x location at which to draw the sprite.
	 * @param y
	 *            The y location at which to draw the sprite.
     * @param width The width.
     * @param height The height.
	 */
    public void draw(int x, int y, 
            int width, int height, 
            double theta, int opacity);
    
    /**
     * Draw the the region of the sprite to the the coordinate provided with
     * the given opacity.
     * 
     * @param x
     * @param y
     * @param rx
     * @param ry
     * @param rwidth
     * @param rheight
     * @param opacity
     */
    public void drawClipped(int x, int y, 
            int width, int height,
            int rx, int ry, 
            int rwidth, int rheight, 
            double theta, int opacity);
}