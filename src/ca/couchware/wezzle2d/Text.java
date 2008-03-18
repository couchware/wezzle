package ca.couchware.wezzle2d;
import java.awt.Color;


/**
 * The Text interface is used to draw text to the screen in a particular font.
 * The font is hard coded as bubbleboy2 but maybe be changed in the future.
 * 
 * @author Kevin
 *
 */
public interface Text extends Drawable
{
	
	public static int TOP = 1;
	public static int VCENTER = 2;
	public static int BOTTOM = 4;
	public static int LEFT = 8;
	public static int HCENTER = 16;
	public static int RIGHT = 32;
	
    /**
     * Get the X-coordinate.
     * @return The X-coordinate.
     */
    public int getX();
    
    /**
     * Set the X-coordinate.
     * @param x
     */
    public void setX(int x);

    /**
     * Get the Y-coordinate.
     * @return The Y-coordinate.
     */
    public int getY();

    /**
     * Set the Y-coordinate.
     * @param y
     */
    public void setY(int y);
    
    /**
     * Set the XY-coordinates.
     * @param x
     * @param y
     */
    public void setXYPosition(final int x, final int y);   
    
	/**
	 * Set the text.
	 * 
	 * @param t The text.
	 */
	public void setText(String text);
	
	/**
	 * Set the color of the text.
	 * The initial size should be set to 14.
	 * 
	 * @param s The size.
	 */
	public void setSize(float size);
	
	/**
	 * Set the text color.
	 * The initial color should be black.
	 * 
	 * @param c The color to set to.
	 */
	public void setColor(Color color);
	
	/**
	 * Set the anchor of the text box. The anchor is initially set to the top left. 
	 * 
	 * @param x The x anchor coordinate with respect to the top left corner of the text box.
	 * @param y The y anchor coordinate with respect to the top left corner of the text box.
	 */
	public void setAnchor(int anchor);
		
}