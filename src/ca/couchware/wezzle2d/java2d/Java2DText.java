package ca.couchware.wezzle2d.java2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import ca.couchware.wezzle2d.Text;

/**
 * The Java2DText class provides a java2D implementation of the text interface.
 * The font is hard coded as bubbleboy2 but maybe be changed in the future.
 * 
 * @author Kevin
 *
 */

public class Java2DText implements Text
{
	/** The size of the font */
	private float size;
	
	/** The color of the text */
	private Color color;
	
	/** The text */
	private String text;
	
	/** The game window to which this text is going to be drawn */
	private Java2DGameWindow window;
	
	/** The x and y offsets used when a new anchor is defined. */
	private int xOffset, yOffset;


	/**
	 * The constructor loads the default text settings.
	 * The default settings are:
	 * 
	 * size: 14pt.
	 * Color: Black.
	 * Text: "".
	 * (x,y): (0,0);
	 * 
	 * @param window The game window to be drawn to.
	 */
	public Java2DText(Java2DGameWindow window)
	{
		this.size = 14f;
		this.color = Color.black;
		this.text = "";
		this.window = window;
		this.xOffset = 0;
		this.yOffset = 0;
	}
	

	/**
	 * Set the text.
	 * 
	 * @param t The text.
	 */
	public void setText(String t)
	{
		this.text = t;
	}
	
	
	
	/**
	 * Set the color of the text.
	 * The initial size is set to 14.
	 * 
	 * @param s The size.
	 */
	public void setSize(int s)
	{
		this.size = s;
	}
	
	
	
	/**
	 * Set the text color.
	 * The initial color is black.
	 * 
	 * @param c The color to set to.
	 */
	public void setColor(Color c)
	{
		this.color = c;
	}
	
	
	
	/**
	 * Set the anchor of the text box. The anchor is initially set to the top left. 
	 * 
	 * @param x The x anchor coordinate with respect to the top left corner of the text box.
	 * @param y The y anchor coordinate with respect to the top left corner of the text box.
	 */
	public void setAnchor(int x, int y)
	{
		this.xOffset = x;
		this.yOffset = y;
	}
	
		
	
	/**
	 * Draw the text onto the graphics context provided.
	 * 
	 * @param x The x location at which to draw the text.         
	 * @param y The y location at which to draw the text.       
	 */
	public void draw(int x, int y)
	{
		
		
		try
		{
			// Get the graphics.
			Graphics2D g2d = (Graphics2D)this.window.getGraphics();
			URL url = this.getClass().getClassLoader().getResource("resources/bubbleboy2.ttf");
			
			// Test url.
			assert (url != null);
			
			
			// Create the font, size it and display the text.
			Font font = Font.createFont(Font.TRUETYPE_FONT, url.openStream());
			font.deriveFont(this.size);
		
			// Test font.
			assert (font != null);
			
			g2d.setFont(font);
			g2d.setColor(this.color);
			
			g2d.drawString(this.text, x+this.xOffset, y+this.yOffset);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	

}
