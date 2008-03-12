package ca.couchware.wezzle2d.java2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
	/** The URL. */
	private URL url;
	
	/** The font. */
	private Font font;
	
	/** The size of the font */
	private float size;
	
	/** The color of the text */
	private Color color;
	
	/** The text */
	private String text;
	
	/** The game window to which this text is going to be drawn */
	private Java2DGameWindow window;	

	/** The x offset for anchor. */
	int anchorx;
	
	/** The y offset for anchor. */
	int anchory;

	
	
	/**
	 * The constructor loads the default text settings.
	 * The default settings are:
	 * 
	 * size: 14pt.
	 * Color: Black.
	 * Text: "".
	 * 
	 * @param window The game window to be drawn to.
	 */
	public Java2DText(Java2DGameWindow window)
	{
		this.url = this.getClass().getClassLoader().getResource("resources/bubbleboy2.ttf");
		this.size = 24.0f;
		this.color = Color.black;
		this.text = "";
		this.window = window;
		
		// Set the default anchors.
		this.anchorx = 0;
		this.anchory = 0;
		
		// Setup the font bubbleboy2.
		try
		{
			this.font = Font.createFont(Font.TRUETYPE_FONT, url.openStream());	
			this.font = font.deriveFont(this.size);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}	

	/**
	 * Gets the text.
	 * @return The text.
	 */
	public String getText()
	{
		return text;
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
	 * Gets the size.
	 * @return The size.
	 */
	public float getSize()
	{
		return size;
	}
	
	/**
	 * Set the color of the text.
	 * The initial size is set to 14.
	 * 
	 * @param s The size.
	 */
	public void setSize(float s)
	{
		this.size = s;
	}
	
//	/**
//	 * Gets the font.
//	 * @return The font.
//	 */
//	public Font getFont()
//	{
//		return font;
//	}
//
//	/**
//	 * Sets the font.
//	 * @param font The font to set.
//	 */
//	public void setFont(Font font)
//	{
//		this.font = font;
//	}

	/**
	 * Gets the color.
	 * @return The color.
	 */
	public Color getColor()
	{
		return color;
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
	public void setAnchor(int anchor)
	{
		// Get the width and height of the font.
		int strWidth = window.getFontMetrics(this.font).stringWidth(this.text);
		int strHeight = this.font.getSize();
		
		// They y's.
		if((anchor & Text.BOTTOM) == Text.BOTTOM)
		{
			this.anchory = strHeight;
		}
		else if((anchor & Text.VCENTER) == Text.VCENTER)
		{
			this.anchory = strHeight / 2;
		}
		else if((anchor & Text.TOP) == Text.TOP)
		{
			this.anchory = 0;
		}
		else
		{
			// The default y value is top.
			this.anchory = 0;
		}
		
		
		// The X's. 
		if((anchor & Text.LEFT) == Text.LEFT)
		{
			this.anchorx = 0;
		}
		else if(((anchor & Text.HCENTER) == Text.HCENTER))
		{
			this.anchorx = strWidth / 2;
		}
		else if((anchor & Text.RIGHT) == Text.RIGHT)
		{
			this.anchorx = strWidth;
		}
		else
		{
			// The default x value is Left.
			this.anchorx = 0;	
		}
		
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
			Graphics2D g = window.getDrawGraphics();			
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			
			// Test url.
			assert (url != null);
						
			// Test font.
			assert (font != null);
			
			g.setFont(font);
			g.setColor(this.color);			
			
			
			g.drawString(this.text, x + this.anchorx, y + this.anchory);			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
}
