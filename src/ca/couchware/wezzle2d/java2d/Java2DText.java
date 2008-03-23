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
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.font.FontRenderContext;

/**
 * The Java2DText class provides a java2D implementation of the text interface.
 * The font is hard coded as bubbleboy2 but maybe be changed in the future.
 * 
 * @author Kevin, Cameron
 *
 */

public class Java2DText extends Text
{    
	/** 
     * The URL. 
     */
	private URL url;
	
	/** 
     * The font. 
     */
	private Font font;				
    
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
	private int offsetX;
	
	/** 
     * The y offset for anchor. 
     */
	private int offsetY;	
    
    /**
     * Do we need to update the text layout?
     */
    private boolean updateTextLayoutNextDraw = false;
    
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
        // Invoke super.
        super();
        
		this.url = this.getClass().getClassLoader()
                .getResource("resources/bubbleboy2.ttf");        		
		
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
                        
        // Setup some values.       
		this.window = window;
        setText(text);
        setVisible(true);
        setOpacity(100);
        setSize(24.0f);
		setColor(Color.BLACK);		        
		
		// Set the default anchor.
		this.anchor = TOP | LEFT;
	}		
	
	/**
	 * Set the text.
	 * Recalculate any anchor points.
	 * 
	 * @param t The text.
	 */
	public void setText(final String text)
	{
        // Set the text.
		this.text = text;        
        
        // Get rid of old layout.
        this.textLayout = null;
        
        // Schedule a text layout update.
        this.updateTextLayoutNextDraw = true;
		
		// Recalculate the anchor points.
		// The anchor will be automatically recalculated with the next 
        // text layout update.
	}		
	
	/**
	 * Set the color of the text.
	 * The initial size is set to 14.
	 * 
	 * @param s The size.
	 */
	public void setSize(float size)
	{
        // Update size.
		this.size = size;
        
        // Derive font with updated size.
		this.font = font.deriveFont(this.size);
        
        // Update the text layout.
        this.textLayout = null;
        this.updateTextLayoutNextDraw = true;
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
        
        // Return if there's no text or text layout object.
		if (text == null 
                || text.equals("") == true 
                || textLayout == null)
			return;
		
		// Get the width and height of the font.
		Rectangle2D bounds = textLayout.getBounds();
        
		// The Y anchors.
		if((anchor & Text.BOTTOM) == Text.BOTTOM)
		{
			this.offsetY = 0;
		}
		else if((anchor & Text.VCENTER) == Text.VCENTER)
		{
			this.offsetY = (int) bounds.getCenterY();
		}
		else if((anchor & Text.TOP) == Text.TOP)
		{
			this.offsetY = (int) bounds.getMinY();
		}
		else
		{
			Util.handleWarning("No Y anchor set!", Thread.currentThread());
		}
		
		// The X anchors. 
		if((anchor & Text.LEFT) == Text.LEFT)
		{
			this.offsetX = (int) bounds.getMinX();
		}
		else if((anchor & Text.HCENTER) == Text.HCENTER)
		{
			this.offsetX = (int) bounds.getMaxX() / 2;			
		}
		else if((anchor & Text.RIGHT) == Text.RIGHT)
		{
			this.offsetX = (int) bounds.getMaxX();
		}
		else
		{
			Util.handleWarning("No X anchor set!", Thread.currentThread());
		}					
	}
	
    /**
     * Updates the text layout instance.
     * @param frctx The current font render context.
     */
    private void updateTextLayout(final FontRenderContext frctx)
    {      
        // Create new text layout.
		this.textLayout = new TextLayout(text, font, frctx);
        
        // Update anchor.
        this.setAnchor(this.anchor);
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
			
            // Update the text layout if flagged.
            if (updateTextLayoutNextDraw == true)
            {
                // Clear the flag.
                updateTextLayoutNextDraw = false;
                
                // Update it.
                updateTextLayout(g.getFontRenderContext());
            }
            
			// Test url.
			assert (url != null);
						
			// Test font.
			assert (font != null);
			
			g.setFont(font);
			g.setColor(this.color);		
            
            // Opacity.
            Composite c = null;
            if (opacity != 100)
            {
                c = g.getComposite();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
                        ((float) opacity) / 100.0f));
            }
			
			textLayout.draw(g, x - offsetX, y - offsetY);
            
            // Opacity.
            if (opacity != 100)        
                g.setComposite(c);
		}
		catch(Exception e)
		{
			Util.handleException(e);
		}		
	}   
    
}
