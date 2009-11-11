/*
 * Wezzle
 * Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.lwjgl;

import ca.couchware.wezzle2d.IGraphics;
import ca.couchware.wezzle2d.util.CouchColor;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import org.lwjgl.opengl.GL11;

/**
 * An LWJGL implementation of the IGraphics interface.
 * 
 * @author cdmckay
 */
public class LWJGLGraphics implements IGraphics
{
    
    /** The current colour. */
    private CouchColor color = CouchColor.newInstance(Color.WHITE);
    
    /**
     * Sets the current drawing colour.
     * 
     * @param color
     */
    public void setColor(Color color)
    {
        this.color = CouchColor.newInstance(color);
    }
    
    public void setColor(CouchColor color)
    {
        this.color = color;
    }
    
    /**
     * Gets the current drawing colour.
     * 
     * @return
     */
    public CouchColor getColor()
    {
        return color;
    }                

    /** The current clip shape. */
    private Shape clip = null;
    
    public void setClip(Shape shape)
    {                
        // Set the clip.
        this.clip = shape;
        
        // See if the shape is null, if it is, then disable the clip.
        if (shape == null)
        {                        
            GL11.glDisable(GL11.GL_STENCIL_TEST); 
            return;
        }
        
        // Stencil out the shape.
        stencilClip(shape);                        
    }       
    
    /**
     * Clip a shape using the stencil buffer.
     * 
     * @param shape
     */
    private void stencilClip(Shape shape)
    {               
        // Disable colour modification.
        GL11.glColorMask(false, false, false, false);
                
        // Enable the stencil buffer.
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        
        // Make it so we can set the stencil buffer to whatever
        // we draw.
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);        
        
        // Carve a rectangle into the stencil buffer.
        if (shape instanceof Ellipse2D)
        {
            Ellipse2D e = (Ellipse2D) shape;
            drawEllipse(e.getX(), e.getY(), e.getWidth(), e.getHeight(), 50, true);
        }
        else
        {
            Rectangle rect = shape.getBounds();
            fillRect(rect.x, rect.y, rect.width, rect.height);
        }
        
        // Re-enable colours.
        GL11.glColorMask(true, true, true, true);
        
        // Now change the stencil buffer so we can use it as a clip.
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 1);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);        
    }        

    public Shape getClip()
    {
       return this.clip;
    }               
    
    /**
     * Draws a rectangle outline.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void drawRect(int x, int y, int width, int height)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        this.color.bind();
                       
        GL11.glBegin(GL11.GL_LINE_LOOP);
            GL11.glVertex2f(x, 		   y);
            GL11.glVertex2f(x + width, y);
            GL11.glVertex2f(x + width, y + height);
            GL11.glVertex2f(x, 		   y + height);
        GL11.glEnd();
        
        // This is to fix a bug that exists on some graphics cards where
        // the top-right corner of a rectangle is not rendered.
        GL11.glBegin(GL11.GL_POINTS);
            GL11.glVertex2i(x + width, y);
        GL11.glEnd();

        GL11.glColor3f(1.0f, 1.0f, 1.0f);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public void fillRect(int x, int y, int width, int height)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        this.color.bind();
                
        GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(x, 		   y);
            GL11.glVertex2f(x + width, y);
            GL11.glVertex2f(x + width, y + height);
            GL11.glVertex2f(x, 		   y + height);
        GL11.glEnd();

        GL11.glColor3f(1.0f, 1.0f, 1.0f);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }            
    
    /**
     * Draws a line.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void drawLine(int x1, int y1, int x2, int y2) 
    {        
		if (x1 == x2) 
        {
            // Swap them if necessary.
			if (y1 > y2) 
            {
				int temp = y2;
				y2 = y1;
				y1 = temp;
			}
            
			int step = 1;
			fillRect(x1, y1, step, (y2 - y1) + step);
			return;
		} 
        else if (y1 == y2) 
        {
			if (x1 > x2) 
            {
				int temp = x2;
				x2 = x1;
				x1 = temp;
			}
            
			int step = 1;			
			fillRect(x1, y1, (x2 - x1) + step, step);
			return;
		}
		
		this.color.bind();		

		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2f(x1, y1);
		GL11.glVertex2f(x2, y2);
		GL11.glEnd();		
	}        
    
    /** The value of two times PI. */
    final private static double TWO_PI = 2.0 * Math.PI;
      
    /**
     * Draws an ellipse outline.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @param points
     * @param filled
     */
    private void drawEllipse(double x, double y, 
            double width, double height, 
            int points, boolean filled)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        this.color.bind();

        float xf = (float) (x + width  / 2);
        float yf = (float) (y + height / 2);
        float wf = (float) width  / 2;
        float hf = (float) height / 2;
        
        int mode = filled ? GL11.GL_POLYGON : GL11.GL_LINE_LOOP;

        GL11.glBegin(mode);
            for (double t = 0.0; t <= TWO_PI; t += TWO_PI / points)
                GL11.glVertex2f(
                        wf * (float) Math.cos(t) + xf, 
                        hf * (float) Math.sin(t) + yf);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }    

    /** The default number of ellipse points used. */
    final int DEFAULT_ELLIPSE_POINTS = 100;

    public void drawEllipse(double x, double y, double width, double height, int points)
    {
        drawEllipse(x, y, width, height, points, false);
    }

    public void drawEllipse(double x, double y, double width, double height)
    {
        drawEllipse(x, y, width, height, DEFAULT_ELLIPSE_POINTS);
    }
    
    public void fillEllipse(double x, double y, double width, double height, int points)
    {
        drawEllipse(x, y, width, height, points, true);
    }

    public void fillEllipse(double x, double y, double width, double height)
    {
        fillEllipse(x, y, width, height, DEFAULT_ELLIPSE_POINTS);
    }
       
    /**
     * Draws an arc.
     * 
     * Code adapted from Slick2D.
     * 
     * @param x1
     * @param y1
     * @param width
     * @param height
     * @param segments
     * @param start
     * @param end
     */
    public void drawArc(
            float x1, float y1, 
            float width, float height,
			int segments, 
            float start, float end) 
    {			
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        
		this.color.bind();

		while (end < start) end += 360;		

		float cx = x1 + (width  / 2.0f);
		float cy = y1 + (height / 2.0f);

        int step = 360 / segments;
        
		GL11.glBegin(GL11.GL_LINE_STRIP);		

		for (int a = (int) start; a < (int) (end + step); a += step) 
        {
			float angle = a;
			if (angle > end) angle = end;
            
			float x = (float) (cx + (Math.cos(Math.toRadians(angle)) * width  / 2.0f));
			float y = (float) (cy + (Math.sin(Math.toRadians(angle)) * height / 2.0f));

			GL11.glVertex2f(x, y);
		}
		
        GL11.glEnd();       
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
    
    /**
	 * Fill an arc.
	 * 
	 * @param x1
	 *            The x coordinate of the top left corner of a box containing
	 *            the arc
	 * @param y1
	 *            The y coordinate of the top left corner of a box containing
	 *            the arc
	 * @param width
	 *            The width of the arc
	 * @param height
	 *            The height of the arc
	 * @param segments
	 *            The number of line segments to use when filling the arc
	 * @param start
	 *            The angle the arc starts at
	 * @param end
	 *            The angle the arc ends at
	 */
	public void fillArc(
            float x1, float y1, 
            float width, float height,
			int segments, 
            float start, float end) 
    {		

		while (end < start) end += 360;		

		float cx = x1 + (width / 2.0f);
		float cy = y1 + (height / 2.0f);

        int step = 360 / segments;
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
        
        this.color.bind();
        
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);		        
		GL11.glVertex2f(cx, cy);

		for (int a = (int) start; a < (int) (end + step); a += step) 
        {			
            float angle = a;			
            if (angle > end) 
            {
				angle = end;
			}

			float x = (float) (cx + (Math.cos(Math.toRadians(angle)) * width / 2.0f));
			float y = (float) (cy + (Math.sin(Math.toRadians(angle)) * height / 2.0f));

			GL11.glVertex2f(x, y);
		}
		GL11.glEnd();
        
        // Anti-alias code.
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(cx, cy);
        if (end != 360) { end -= 10; }

        for (int a = (int) start; a < (int) (end + step); a += step) 
        {
            float angle = a;
            if (angle > end) 
            {
                angle = end;
            }

            float x = (float) (cx + (Math.cos(Math.toRadians(angle + 10))
                    * width  / 2.0f));
            float y = (float) (cy + (Math.sin(Math.toRadians(angle + 10))
                    * height / 2.0f));

            GL11.glVertex2f(x, y);
        }
        GL11.glEnd();			
        
        GL11.glEnable(GL11.GL_TEXTURE_2D);	
	}
    
    /**
     * Draws a rectangle with rounded corners.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @param cornerRadius
     * @param segments
     */
    public void drawRoundRect(
            int x, int y, 
            int width, int height,
			int cornerRadius, 
            int segments) 
    {
		if (cornerRadius < 0)
        {
			throw new IllegalArgumentException("Corner radius must be > 0.");
        }
        
		if (cornerRadius == 0) 
        {
			drawRect(x, y, width, height);
			return;
		}

		int mr = (int) Math.min(width, height) / 2;
		
        // Make sure that w & h are larger than 2 * cornerRadius.
		if (cornerRadius > mr) cornerRadius = mr;		

		drawLine(x + cornerRadius, y, x + width + 1 - cornerRadius, y);
		drawLine(x, y + cornerRadius, x, y + height + 1 - cornerRadius);
		drawLine(x + width, y + cornerRadius, x + width, y + height - cornerRadius);
		drawLine(x + cornerRadius, y + height, x + width + 1 - cornerRadius, y + height);

		int d = cornerRadius * 2;
		// bottom right - 0, 90
		drawArc(x + width + 1 - d, y + height + 1 - d, d, d, segments, 0, 90);
		// bottom left - 90, 180
		drawArc(x, y + height + 1 - d, d, d, segments, 90, 180);
		// top right - 270, 360
		drawArc(x + width + 1 - d, y, d, d, segments, 270, 360);
		// top left - 180, 270
		drawArc(x, y, d, d, segments, 180, 270);
	}            

    /**
	 * Draws a filled rounded rectangle.
	 * 
	 * @param x
	 *            The x coordinate of the top left corner of the rectangle
	 * @param y
	 *            The y coordinate of the top left corner of the rectangle
	 * @param width
	 *            The width of the rectangle
	 * @param height
	 *            The height of the rectangle
	 * @param cornerRadius
	 *            The radius of the rounded edges on the corners
	 * @param segments
	 *            The number of segments to make the corners out of
	 */
	public void fillRoundRect(
            int x, int y, 
            int width, int height,
			int cornerRadius, int segments) 
    {
		if (cornerRadius < 0)
        {
			throw new IllegalArgumentException("Corner radius must be > 0.");
        }
        
		if (cornerRadius == 0) 
        {
			fillRect(x, y, width, height);
			return;
		}

		int minRadius = (int) Math.min(width, height) / 2;
		
        // Make sure that w & h are larger than 2 * cornerRadius.
		if (cornerRadius > minRadius) 
        {
			cornerRadius = minRadius;
		}

		int d = cornerRadius * 2;

		fillRect(x + cornerRadius, y, width - d, cornerRadius);
		fillRect(x, y + cornerRadius, cornerRadius, height - d);
		fillRect(x + width - cornerRadius, y + cornerRadius, cornerRadius, height - d);
		fillRect(x + cornerRadius, y + height - cornerRadius, width - d, cornerRadius);
		fillRect(x + cornerRadius, y + cornerRadius, width - d, height - d);

		// Bottom right - 0, 90.
		fillArc(x + width - d, y + height - d, d, d, segments, 0, 90);
		// Bottom left - 90, 180.
		fillArc(x, y + height - d, d, d, segments, 90, 180);
		// Top right - 270, 360.
		fillArc(x + width - d, y, d, d, segments, 270, 360);
		// Top left - 180, 270.
		fillArc(x, y, d, d, segments, 180, 270);
	}
    
}
