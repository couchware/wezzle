package ca.couchware.wezzle2d;
import java.awt.Color;


/**
 * The Text interface is used to draw text to the screen in a particular font.
 * The font is hard coded as bubbleboy2 but maybe be changed in the future.
 * 
 * @author Kevin
 *
 */
public interface Text
{
	/**
	 * Set the text.
	 * 
	 * @param t The text.
	 */
	public void setText(String t);
	
	
	
	/**
	 * Set the color of the text.
	 * The initial size is set to 14.
	 * 
	 * @param s The size.
	 */
	public void setSize(float s);
	
	
	
	/**
	 * Set the text color.
	 * The initial color is black.
	 * 
	 * @param c The color to set to.
	 */
	public void setColor(Color c);
	
	
	
	/**
	 * Set the anchor of the text box. The anchor is initially set to the top left. 
	 * 
	 * @param x The x anchor coordinate with respect to the top left corner of the text box.
	 * @param y The y anchor coordinate with respect to the top left corner of the text box.
	 */
	public void setAnchor(int x, int y);
	
	
	
	/**
	 * Draw the text onto the graphics context provided.
	 * 
	 * @param x The x location at which to draw the text.         
	 * @param y The y location at which to draw the text.       
	 */
	public void draw(int x, int y);
}