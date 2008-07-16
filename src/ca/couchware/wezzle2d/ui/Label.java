/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.graphics.Entity;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.Util;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.EnumSet;

/**
 * The Text abstract class is used to draw text to the screen in a particular 
 * font.  The appropriate methods should be updated for each graphics module. 
 * 
 * @author Kevin
 *
 */
public abstract class Label extends Entity
{
    
    /** 
     * The size of the font.
     */
	protected float size;     
	
	/** 
     * The color of the text.
     */
	protected Color color;
    
    /** 
     * The text.
     */
	protected String text;     
       	
    /**
     * The constructor.
     */
    public Label(final int x, final int y)
    {
        this.x = x;
        this.y = y;
        
        this.x_ = x;
        this.y_ = y;
        
        this.width_ = 0;
        this.height_ = 0;    
        
        setVisible(true);
        
        this.color = Color.BLACK;		        
        this.opacity = 100;
        this.size = 24f;
		
		// Set the default anchor.
		this.alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);
    }        
    
    /**
     * Gets the text.
     * 
     * @return The text string.
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
	public void setText(final String text)
    {
        this.text = text;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }
	
    /**
     * Gets the color of the text.
     * 
     * @return The color of text.
     */
    public float getSize()
    {
        return size;
    }
    
	/**
	 * Set the color of the text.
	 * 
	 * @param size The size.
	 */
	public void setSize(final float size)
    {
        this.size = size;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }        
	
    /**
     * Get the text color.
     * 
     * @return The current text color. 
     */
    public Color getColor()
    {
        return color;        
    }
    
	/**
	 * Set the text color.
	 * The initial color should be black.
	 * 
	 * @param color The color to set to.
	 */
	public void setColor(final Color color)
    {
        this.color = color;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }
	    
    public abstract int getLetterHeight();
    
    @Override
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

            if (rect.getMinX() < 0 || rect.getMinY() < 0)
            {
                Util.handleWarning("Label drawn outside of screen.", 
                        Thread.currentThread());
                
                Rectangle r = new Rectangle(x, y, getWidth() + 2, getHeight() + 2);
                Util.handleWarning("r1 = " + r, Thread.currentThread());
                r.translate(offsetX, offsetY);
                Util.handleWarning("r2 = " + r, Thread.currentThread());
                r.add(new Rectangle(x_, y_, width_ + 2, height_ + 2));
                Util.handleWarning("r3 = " + r, Thread.currentThread());                
                r.translate(0, -(getLetterHeight() + 1));
                Util.handleWarning("r4 = " + r, Thread.currentThread());
                Util.handleWarning("Offending text is " + text,
                        Thread.currentThread());
            }
        
            drawRect = rect;
        }
               
        return drawRect;
    }
    
    @Override
    public void resetDrawRect()
    {
        x_ = x;
        y_ = y;
        
        width_ = getWidth();
        height_ = getHeight();
    }       
		
}