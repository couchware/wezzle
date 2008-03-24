package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.XYPosition;

/**
 * An interface inplemented by classes that are positionable.
 * 
 * @author cdmckay
 */
public interface Positionable 
{
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
}
