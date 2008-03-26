package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.XYPosition;

/**
 * An interface inplemented by classes that are positionable.  A positionable
 * class has an x,y coordinate, a width and height, and is capable
 * of being aligned on any of the given alignment points.
 * 
 * @author cdmckay
 */
public interface Positionable 
{
    /**
     * Align at the vertical top.
     */
    final public static int TOP = 1;
    
    /**
     * Align at the vertical center.
     */
	final public static int VCENTER = 2;
    
    /**
     * Align at the vertical bottom.
     */
	final public static int BOTTOM = 4;
    
    /**
     * Align at the horizontal left.
     */
	final public static int LEFT = 8;
    
    /**
     * Align at the horizontal centre.
     */
	final public static int HCENTER = 16;
    
    /**
     * Align at the horizontal right.
     */
	final public static int RIGHT = 32;
    
    /**
     * Get the x-coordinate.
     * 
     * @return The x-coordinate.
     */
    public int getX();
    
    /**
     * Sets the x-coordinate.
     * 
     * @param x The x-coordinate.
     */
    public void setX(final int x);
    
    /**
     * Gets the y-coordinate.
     * 
     * @return The y-coordinate.
     */
    public int getY();
    
    /**
     * Sets the y-coordinate.
     * 
     * @param y The y-coordinate.
     */
    public void setY(final int y);
    
    /**
     * Gets the x- and y-coordinates.
     * 
     * @return The x- and y-coordinates.
     */
    public XYPosition getXYPosition();
    
    /**
     * Sets the x- and y-coordiantes.
     * 
     * @param x
     * @param y
     */
    public void setXYPosition(final int x, final int y);
    
    /**
     * Sets the x- and y-coordinates.
     * 
     * @param p
     */
    public void setXYPosition(final XYPosition p);
    
    /**
     * Get the width.
     * 
     * @return The width.
     */
    public int getWidth();
    
    /**
     * Sets the width.
     * 
     * @param width The width.
     */
    public void setWidth(final int width);
    
    /**
     * Gets the height.
     * 
     * @return The height.
     */
    public int getHeight();
    
    /**
     * Sets the height.
     * 
     * @param height The height.
     */
    public void setHeight(final int height);
    
    /**
     * Gets the alignment.
     * 
     * @return An integer bitmask representing the alignment.
     */
    public int getAlignment();
    
    /**
     * Sets the alignment.
     * 
     * @param bitmask An integer bitmask representing the alignment.
     */
    public void setAlignment(final int alignment);
}
