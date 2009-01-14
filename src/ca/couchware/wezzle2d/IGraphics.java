/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.SuperColor;
import java.awt.Color;
import java.awt.Shape;

/**
 * An interface for drawing shapes to the window.
 * 
 * @author cdmckay
 */
public interface IGraphics 
{
//--------------------------------------------------------------------------
    // Draw
    //--------------------------------------------------------------------------
    
    /**
     * Set the drawing color.
     * 
     * @param c
     */
    public void setColor(Color color);
    
    /**
     * Set the drawing color.
     * 
     * @param c
     */
    public void setColor(SuperColor color);
    
    /**
     * Get the drawing color.
     * 
     * @return The current color.
     */
    public SuperColor getColor();
    
    public void drawLine(int x1, int y1, int x2, int y2);
    
    public void drawEllipse(double x, double y, double width, double height, int points);
    public void fillEllipse(double x, double y, double width, double height, int points);
    
    /**
     * Draws the outline of the specified rectangle.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void drawRect(
            int x, int y, 
            int width, int height);            
    
    /**
     * Fills the specified rectangle.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void fillRect(
            int x, int y, 
            int width, int height);
    
    public void drawArc(
            float x1, float y1, 
            float width, float height,
			int segments, 
            float start, float end);
    
    public void fillArc(
            float x1, float y1, 
            float width, float height,
			int segments, 
            float start, float end);    
    
    public void drawRoundRect(
            int x, int y, 
            int width, int height,
			int cornerRadius, 
            int segments);
    
    public void fillRoundRect(
            int x, int y, 
            int width, int height, 
            int cornerRadius,
            int segments);            
    
    //--------------------------------------------------------------------------
    // Clip
    //--------------------------------------------------------------------------	   
    
    /**
     * Sets the current clip rectangle.  Only drawables within the clip area are
     * drawn to the screen.
     * 
     * @param r
     */
    public void setClip(Shape s);
    
    /**
     * Gets the current clip rectangle.
     * 
     * @return
     */
    public Shape getClip();
}
