/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.util;

/**
 * An immutable class for representing a 2D dimension.
 * 
 * @author cdmckay
 */
public final class WDimension 
{
    private final int width;
    private final int height;
    
    public WDimension(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }    
    
    @Override
    public String toString()
    {
        return "(w,h) = (" + width + "," + height +")";
    }
}
