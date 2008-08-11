/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.util;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * An immutable rectangle.
 * 
 * @author cdmckay
 */
public class ImmutableRectangle implements Shape
{
    
    final Rectangle rect;
    
    public ImmutableRectangle()
    {
        rect = new Rectangle();
    }
    
    public ImmutableRectangle(int x, int y, int width, int height)
    {
        rect = new Rectangle(x, y, width, height);
    }
    
    public int getX()
    {
        return rect.x;
    }
    
    public int getMaxX()
    {
        return rect.x + rect.width;
    }        
    
    public int getY()
    {
        return rect.y;
    }
    
    public int getMaxY()
    {
        return rect.y + rect.height;
    }
    
    public int getWidth()
    {
        return rect.width;
    }
    
    public int getHeight()
    {
        return rect.height;
    }
    
    public Rectangle getBounds()
    {
        return rect.getBounds();
    }

    public Rectangle2D getBounds2D()
    {
        return rect.getBounds2D();
    }

    public boolean contains(double x, double y)
    {
        return rect.contains(x, y);
    }

    public boolean contains(Point2D p)
    {
        return rect.contains(p);
    }
    
    public boolean contains(ImmutablePosition p)
    {
        return rect.contains(p.getX(), p.getY());
    }

    public boolean intersects(double x, double y, double w, double h)
    {
        return rect.intersects(x, y, w, h);
    }

    public boolean intersects(Rectangle2D r)
    {
        return rect.intersects(r);
    }

    public boolean contains(double x, double y, double w, double h)
    {
        return rect.contains(x, y, w, h);
    }

    public boolean contains(Rectangle2D r)
    {
        return rect.contains(r);
    }

    public PathIterator getPathIterator(AffineTransform at)
    {
        return rect.getPathIterator(at);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness)
    {
        return rect.getPathIterator(at, flatness);
    }              
    
}
