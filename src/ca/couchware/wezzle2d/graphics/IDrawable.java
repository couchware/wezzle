package ca.couchware.wezzle2d.graphics;

import java.awt.Rectangle;

public interface IDrawable
{
    /**
     * Draw the drawable.
     * 
     * @return True if it was drawn (i.e. it was visible and dirty) 
     * or false otherwise.
     */
	public boolean draw();
    
    /**
     * Get a rectangle that contains the area of drawable.
     * 
     * @return
     */
    public Rectangle getDrawRect();
    
    /**
     * Force a recalculation of the draw rectangle.
     */
    public void resetDrawRect();
    
    /**
     * Sets the visibility of the drawable.
     * 
     * @param visible
     */
    public void setVisible(boolean visible);
    
    /**
     * Gets the visibility of the drawable.
     * 
     * @return
     */
    public boolean isVisible();
    
    /**
     * Sets the dirtiness of the drawable.  A drawable will only be redrawn
     * to the screen if it is dirty.
     * 
     * @param dirty
     */
    public void setDirty(boolean dirty);
    
    /**
     * Gets the dirtiness of the drawable.
     * 
     * @return
     */
    public boolean isDirty();
}
