package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.LayerManager;
import ca.couchware.wezzle2d.graphics.Entity;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.util.XYPosition;

/**
 * An animation that starts an explosion in the middle of the entity.
 * 
 * @author cdmckay
 */
public class JumpAnimation extends ReferenceAnimation
{    
    
    /**
     * The counter.
     */
    private long counter;
    
    /**
     * The entity being float faded.
     */
    final Entity entity;
    
    /**
     * The initial position.
     */
    private XYPosition initialPosition;
    
    /**
     * The x-component of the launch speed.
     */
    private double vX;
    
    /**
     * The y-component of the launch speed.
     */
    private double vY;
    
    /**
     * The launch angle, in degrees.
     */
    private int theta;
    
    /**
     * The gravity.
     */
    private double g;
    
    /**
     * The max time for the animation to run for, in ms.
     */
    private int duration;

   /**
    * Fire an entity with the given launch speed, angle and gravity and 
    * rotation speed.        
    */
    public JumpAnimation(
            final double v,
            final int theta,
            final double g,     
            final int duration,            
            final Entity entity)
    {                
        // Invoke super constructor.
        super();    
        
        // Record the initial position.
        initialPosition = entity.getXYPosition();
        
        // Determine the components of the launch velocity.
        vX = v * Math.cos(Math.toRadians(theta));
        vY = v * Math.sin(Math.toRadians(theta));
        
        // Record other values.
        this.theta = theta;
        this.g = g;        
        this.duration = duration;              
        
        // Set a reference to the entity.
        this.entity = entity;            
    }       

    public void nextFrame(long delta)
    {
        // Check if we're done, if we are, return.
        if (isDone() == true)
            return;
              
        // Add delta to counter.  This serves as the time variable.
        counter += delta;                               
            
        // Determine the current x and y.
        double t = (double) counter;
        double x = -vX * t;
        double y = vY * t - 0.5 * g * t * t;
        
        // Move the entity.
        entity.setX(initialPosition.x + (int) x);
        entity.setY(initialPosition.y - (int) y);        
        
        if (counter > duration)   
        {
            setDone(true);                                            
        }
    }
    
    @Override
    public void setVisible(final boolean visible)
    {
        super.setVisible(visible);
        
        if (entity != null)
            entity.setVisible(visible);
    }
    
}
