package ca.couchware.wezzle2d.java2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.net.URL;

import ca.couchware.wezzle2d.Text;
import ca.couchware.wezzle2d.util.Util;

/**
 * The Java2DText class provides a java2D implementation of the text interface.
 * The font is hard coded as bubbleboy2 but maybe be changed in the future.
 * 
 * @author Kevin, Cameron
 *
 */

public class Java2DText implements Text
{
    
    /**
     * The visibility of the text.
     */
    private boolean visible;
    
    /**
     * The X-cooridinate of the text.
     */
    private int x;
    
    /**
     * The Y-coordinate of the text.
     */
    private int y;
    
	/** 
     * The URL. 
     */
	private URL url;
	
	/** 
     * The font. 
     */
	private Font font;
	
	/** 
     * The size of the font.
     */
	private float size;
	
	/** 
     * The color of the text .
     */
	private Color color;
	
	/** 
     * The text.
     */
	private String text;
    
    /**
     * The text layout instance.
     */
	private TextLayout textLayout;
	
	/** 
     * The game window to which this text is going to be drawn 
     */
	private Java2DGameWindow window;	

	/** 
     * The x offset for anchor. 
     */
	private int anchorX;
	
	/** 
     * The y offset for anchor. 
     */
	private int anchorY;
	
	/**
     * The current anchor 
     */
	private int anchor;

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
        // Initially visible.
        this.visible = true;
        
		this.url = this.getClass().getClassLoader()
                .getResource("resources/bubbleboy2.ttf");
        
		this.size = 24.0f;
		this.color = Color.black;
		this.text = "";
		this.window = window;
		
		// Set the default anchors.
		this.anchor = (Text.TOP | Text.LEFT);
		
		// Setup the font.
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
	 * Recalculate any anchor points.
	 * 
	 * @param t The text.
	 */
	public void setText(final String text)
	{
		this.text = text;
		this.textLayout = new TextLayout(text, font, 
                window.getDrawGraphics().getFontRenderContext());
		
		// Recalculate the anchor points.
		this.setAnchor(this.anchor);
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
	public void setSize(float size)
	{
		this.size = size;
		this.font = font.deriveFont(this.size);
	}

    public int getX()
    {
        return x;
    }

    public void setX(final int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(final int y)
    {
        this.y = y;
    }      
    
    public void setXYPosition(final int x, final int y)
    {
        this.x = x;
        this.y = y;
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
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	/**
	 * Set the anchor of the text box. The anchor is initially set to the top left. 
	 * 
	 * @param x The x anchor coordinate with respect to the top left corner of the text box.
	 * @param y The y anchor coordinate with respect to the top left corner of the text box.
	 */
	public void setAnchor(int anchor)
	{
        // Remember the anchor.
		this.anchor = anchor;
        
        // Return if there's no text.
		if (text.equals("") == true)
			return;
		
		// Get the width and height of the font.
		Rectangle2D bounds = textLayout.getBounds();
        
		// The Y achors.
		if((anchor & Text.BOTTOM) == Text.BOTTOM)
		{
			this.anchorY = 0;
		}
		else if((anchor & Text.VCENTER) == Text.VCENTER)
		{
			this.anchorY = (int) bounds.getCenterY();
		}
		else if((anchor & Text.TOP) == Text.TOP)
		{
			this.anchorY = (int) bounds.getMinY();
		}
		else
		{
			Util.handleWarning("No Y anchor set!", Thread.currentThread());
		}
		
		// The X anchors. 
		if((anchor & Text.LEFT) == Text.LEFT)
		{
			this.anchorX = (int) bounds.getMinX();
		}
		else if((anchor & Text.HCENTER) == Text.HCENTER)
		{
			this.anchorX = (int) bounds.getMaxX() / 2;			
		}
		else if((anchor & Text.RIGHT) == Text.RIGHT)
		{
			this.anchorX = (int) bounds.getMaxX();
		}
		else
		{
			Util.handleWarning("No X anchor set!", Thread.currentThread());
		}					
	}
	
	/**
	 * Draw the text onto the graphics context provided.	   
	 */
	public void draw()
	{
		// Return immediately is string is empty.
		if (isVisible() == false || text.equals("") == true)
			return;
		
		try
		{
			// Get the graphics.
			Graphics2D g = window.getDrawGraphics();			
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
					RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			
			// Test url.
			assert (url != null);
						
			// Test font.
			assert (font != null);
			
			g.setFont(font);
			g.setColor(this.color);			
			
			textLayout.draw(g, x - this.anchorX, y - this.anchorY);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public boolean isVisible()
    {
        return visible;
    }
    
}
