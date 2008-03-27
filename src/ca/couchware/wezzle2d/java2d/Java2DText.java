package ca.couchware.wezzle2d.java2d;

import ca.couchware.wezzle2d.Game;
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
                .getResource(Game.FONTS_PATH + "/bubbleboy2.ttf");        		
		
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
		this.alignment = TOP | LEFT;
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
        
        // Schedule a text layout update if necessary.        
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
	public void setAlignment(int alignment)
	{
        // Remember the anchor.
		this.alignment = alignment;                
        
        // Return if there's no text or text layout object.
		if (text == null 
                || text.equals("") == true 
                || textLayout == null)
			return;
		
		// Get the width and height of the font.
		Rectangle2D bounds = textLayout.getBounds();
        
		// The Y alignment.
		if((alignment & BOTTOM) == BOTTOM)
		{
			this.offsetY = 0;
		}
		else if((alignment & VCENTER) == VCENTER)
		{
			this.offsetY = (int) bounds.getCenterY();
		}
		else if((alignment & TOP) == TOP)
		{
			this.offsetY = (int) bounds.getMinY();
		}
		else
		{
			Util.handleWarning("No Y alignment set!", Thread.currentThread());
		}
		
		// The X alignment. 
		if((alignment & LEFT) == LEFT)
		{
			this.offsetX = (int) bounds.getMinX();
		}
		else if((alignment & HCENTER) == HCENTER)
		{
			this.offsetX = (int) (bounds.getMinX() + bounds.getWidth() / 2);			
		}
		else if((alignment & RIGHT) == RIGHT)
		{
			this.offsetX = (int) bounds.getMaxX();
		}
		else
		{
			Util.handleWarning("No X alignment set!", Thread.currentThread());
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
        this.setAlignment(alignment);
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
                g.setComposite(
                        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
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

    /**
     * This method will only work when the Graphics2D instance is available.
     * @return The width of the text.
     */
    public int getWidth()
    {
        // See if the graphics are available.
        Graphics2D g = window.getDrawGraphics();
        
        // If we don't, throw an exception.
        if (g == null)
        {
            throw new IllegalStateException("Graphics2D is not availabe yet!");            
        }
        else
        {
            updateTextLayout(g.getFontRenderContext());
            return (int) textLayout.getBounds().getWidth();
        } // end if     
    }

    /**
     * This method is not supported.  Do not use.
     * 
     * @param width
     */
    public void setWidth(int width)
    {
        // This is not supported.        
        throw new UnsupportedOperationException(
                "Width should be changed via the size and text attributes.");
    }

    /**
     * This method will only work when the Graphics2D instance is available.
     * @return The height of the text.
     */
    public int getHeight()
    {
        // See if the graphics are available.
        Graphics2D g = window.getDrawGraphics();
        
        // If we don't, throw an exception.
        if (g == null)
        {
            throw new IllegalStateException("Graphics2D is not availabe yet!");            
        }
        else
        {
            updateTextLayout(g.getFontRenderContext());
            return (int) textLayout.getBounds().getHeight();
        } // end if 
    }

    /**
     * This method is not supported.  Do not use.
     * 
     * @param height
     */
    public void setHeight(int height)
    {
        // This is not supported.        
        throw new UnsupportedOperationException(
                "Height should be changed via the size and text attribute.");
    }
    
}
