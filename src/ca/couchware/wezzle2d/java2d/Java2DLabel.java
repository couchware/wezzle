/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.java2d;

import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.util.XYPosition;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.font.FontRenderContext;
import java.util.EnumSet;

/**
 * This class provides a Java2D implementation of the Label interface.
 * The font is hard coded as BubbleBoy2 but may be be changed in the future.
 * 
 * @author Kevin, Cameron
 *
 */

public class Java2DLabel implements ILabel
{    
    
    /** 
     * The game window to which this text is going to be drawn 
     */
	final private Java2DGameWindow window;	
    
    /**
     * The x-coordinate of the label.     
     */
    private int x;
    private int x_;
    
    /**
     * The y-coordinate of the label.     
     */
    private int y;
    private int y_;
    
    /**
     * The previous width.  Used for calculating the draw rectangle.
     */
    private int width_ = 0;
    
    /**
     * The previous height.  Used for calculating the draw rectangle.
     */
    private int height_ = 0;
    
    /**
     * The color of the label.
     */
    final private Color color;
    
    /**
     * The size of the label.
     */
    final private float size;
    
    /**
     * The text of the label.
     */
    final private String text;
    
	/** 
     * The font. 
     */
	final private Font font;  
    
     /** 
     * Is this visible? 
     */
    private boolean visible = true;
    
    /**
     * Is it dirty (i.e. does it need to be redrawn)?
     */
    private boolean dirty = true;           
    
    /**
     * The opacity (in percent).
     */
    private int opacity = 100;
    
    /**
     * The cached draw rectangle.
     */
    private Rectangle drawRect;
        
    /**
     * The alignment of the label.
     */
    final private EnumSet<Alignment> alignment;
    
    /**
     * The x-offset.
     */
    final private int offsetX;
    
    /**
     * The y-offset.
     */
    final private int offsetY;	
    
    /**
     * The text layout instance.
     */
	final private TextLayout textLayout;		        
    
	/**
	 * Create a new Java2D label.	 
	 */
	public Java2DLabel(Java2DGameWindow window,
            int x, int y,
            EnumSet<Alignment> alignment,
            Color color,
            int opacity,
            float size,
            String text,
            boolean visible)
	{                		
        // Setup the values.
		this.window = window;
        this.x  = x;
        this.x_ = x;
        this.y  = y;
        this.y_ = y;        
        this.alignment = alignment;
        this.color = color;
        this.opacity = limitOpacity(opacity);
        this.size = size;
        this.text = text;
        this.visible = visible;
                       
        // Get the font.
        this.font = Java2DFontStore.get().getFont((int) size);
        
        // Create the text layout object.
        this.textLayout = createTextLayout(window.getDrawGraphics(), font);
        
        // Set the old widths.
//        this.width_ = getWidth();
//        this.height_ = getHeight();
        
        // Setup the alignment.  This should be replaced, alignment should
        // be immutable.        
        int[] offsets = determineOffsets(alignment);
        this.offsetX = offsets[0];
        this.offsetY = offsets[1];        
        
        // Set dirty so it will be drawn.        
        setDirty(true);
	}		           		
	
    public EnumSet<Alignment> getAlignment()
    {
        return alignment;
    }
    
    /**
	 * Set the anchor of the text box. The anchor is initially set to the top left. 
	 * 
	 * @param x The x anchor coordinate with respect to the top left corner of the text box.
	 * @param y The y anchor coordinate with respect to the top left corner of the text box.
	 */    
	public void setAlignment(EnumSet<Alignment> alignment)
	{                                     						
        throw new UnsupportedOperationException("Not supported");
	}
    
    private int[] determineOffsets(EnumSet<Alignment> alignment)
    {
        // Get the width and height of the font.
		Rectangle2D bounds = textLayout.getBounds();
        
        // Create two offset variables.
        int offsetX_, offsetY_;
        
		// The Y alignment.
		if (alignment.contains(Alignment.BOTTOM))
		{
            // TODO This may not work.  It has not been tested.
			offsetY_ = 0;
		}        
		else if (alignment.contains(Alignment.MIDDLE))
		{			
            offsetY_ = (int) (-bounds.getY() / 2f + 0.5);                        
		}
		else if (alignment.contains(Alignment.TOP))
		{
			offsetY_ = (int) (-bounds.getY() + 0.5);                                              
		}
		else
		{
			throw new IllegalStateException("No Y alignment set!");
		}
		
		// The X alignment. 
		if (alignment.contains(Alignment.LEFT))
		{
			offsetX_ = (int) -bounds.getMinX();
		}
		else if (alignment.contains(Alignment.CENTER))
		{
			offsetX_ = (int) -(bounds.getMinX() + bounds.getWidth() / 2);			
		}
		else if (alignment.contains(Alignment.RIGHT))
		{
			offsetX_ = (int) -bounds.getMaxX();
		}
		else
		{
			throw new IllegalStateException("No X alignment set!");
		}
        
        return new int[] { offsetX_, offsetY_ };
    }    	
	
    /**
     * Updates the text layout instance.
     * @param frctx The current font render context.
     */
    private TextLayout createTextLayout(Graphics2D g, Font font)
    {             
        // Set the font.
        g.setFont(font);  
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);        
        
        // Get the render context.
        FontRenderContext frctx = g.getFontRenderContext();
        
        // Create new text layout.        
        return new TextLayout(text, font, frctx);                     
    }    
    
    private int getLetterHeight()
    {        
        // Return the height.
        return (int) -textLayout.getBounds().getY();            
    }
    
	/**
	 * Draw the text onto the graphics context provided.	   
	 */
	public void draw()
	{
        x_ = x + offsetX;
        y_ = y + offsetY;
        
        width_ = getWidth();
        height_ = getHeight();
        
		// Return if the label is invisible.
		if (isVisible() == false)
			return;                					
            
        // Get the graphics context.
        Graphics2D g = window.getDrawGraphics();
        
        // Deal with opacity.
        Composite c = null;        
        if (opacity != 100)
        {
            c = g.getComposite();
            g.setComposite(
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
                    ((float) opacity) / 100.0f));
        }

        g.setColor(Color.BLACK);

        textLayout.draw(g, x + offsetX + 1, y + offsetY + 1);            

        g.setColor(this.color);		

        textLayout.draw(g, x + offsetX, y + offsetY);            

        // Opacity.
        if (opacity != 100)        
            g.setComposite(c);            					
	}        

    /**
     * Returns the width of the label.
     * 
     * @return The width of the label.
     */    
    public int getWidth()
    {        
        return (int) textLayout.getBounds().getMaxX();         
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
                "Not supported.");
    }

    /**
     * Returns the height of the label.
     * 
     * @return The height of the label.
     */    
    public int getHeight()
    {        
        return (int) textLayout.getBounds().getHeight();
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
    
    
    
    public Rectangle getDrawRect()
    {
        // If the draw rect is null, generate it.
        if (drawRect == null)
        {            
            // Draw a rectangle that can fully cover the text.
            Rectangle rect = new Rectangle(x, y, getWidth() + 2, getHeight() + 2);      
            
            // Move it so it covers the text.
            rect.translate(offsetX, offsetY);
            
            // Add the old rectangle.
            rect.add(new Rectangle(x_, y_, width_ + 2, height_ + 2));                             
                                     
            // Move the point up to the top left corner.
            rect.translate(0, -(getLetterHeight() + 1));

//            if (rect.getMinX() < 0 || rect.getMinY() < 0)
//            {
//                Util.handleWarning("Label drawn outside of screen.", 
//                        Thread.currentThread());
//                
//                Rectangle r = new Rectangle(x, y, getWidth() + 2, getHeight() + 2);
//                Util.handleWarning("r1 = " + r, Thread.currentThread());
//                r.translate(offsetX, offsetY);
//                Util.handleWarning("r2 = " + r, Thread.currentThread());
//                r.add(new Rectangle(x_, y_, width_ + 2, height_ + 2));
//                Util.handleWarning("r3 = " + r, Thread.currentThread());                
//                r.translate(0, -(getLetterHeight() + 1));
//                Util.handleWarning("r4 = " + r, Thread.currentThread());
//                Util.handleWarning("Offending text is " + text,
//                        Thread.currentThread());
//            }
        
            drawRect = rect;
        }
               
        return drawRect;
    }
       
    public void resetDrawRect()
    {
        x_ = x;
        y_ = y;
        
        width_ = getWidth();
        height_ = getHeight();
    }

    public Color getColor()
    {
        return color;
    }

    public float getSize()
    {
        return size;
    }

    public String getText()
    {
        return text;
    }

    public void setOpacity(int opacity)
    {        
        this.opacity = limitOpacity(opacity);
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }

    public int getOpacity()
    {
        return opacity;
    }
    
    private int limitOpacity(int opacity)
    {
        if (opacity < 0) return 0;
        if (opacity > 100) return 100;
        return opacity;
    }

    public void setRotation(double theta)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getRotation()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }  

    public void setVisible(boolean visible)
    {
        this.visible = visible;
        
        // Set dirty so it will be drawn.
        setDirty(true);
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setDirty(boolean dirty)
    {
        this.dirty = dirty;
        
        // Set draw rect to null so it'll be regenerated.
        this.drawRect = null;
    }

    public boolean isDirty()
    {
        return dirty;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {        
        this.x = x;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }

    public XYPosition getXYPosition()
    {
        return new XYPosition(getX(), getY());
    }

    public void setXYPosition(int x, int y)
    {
        setX(x);
        setY(y);
    }

    public void setXYPosition(XYPosition p)
    {
        setX(p.getX());
        setY(p.getY());
    }

    public void translate(int dx, int dy)
    {
        setX(getX() + dx);
        setY(getY() + dy);
    }    
    
}
