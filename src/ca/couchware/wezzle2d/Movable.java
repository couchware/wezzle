package ca.couchware.wezzle2d;

/**
 * An interface for things in Wezzle which need to move related to frame-rate.
 * 
 * @author cdmckay
 */
public interface Movable 
{
    /**
     * Move the movable by an amount related to delta.
     * 
     * @param delta
     */
    public void move(long delta);
    
    /**
     * Get the X-movement rate in pixels/sec.
     * 
     * @return
     */
    public double getXMovement();
    
    /**
     * Set the X-movement rate in pixels/sec.
     * 
     * @param dx
     */
    public void setXMovement(double dx);
    
    /**
     * Get the Y-movement rate in pixels/sec.
     * 
     * @return
     */    
	public double getYMovement();
    
    /**
     * Set the Y-movement rate in pixels/sec.
     * 
     * @param dy
     */
	public void setYMovement(double dy);
}
