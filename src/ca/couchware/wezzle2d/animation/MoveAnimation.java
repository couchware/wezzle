package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.util.WPosition;

/**
 * An animation that starts an explosion in the middle of the entity.
 * 
 * @author cdmckay
 */
public class MoveAnimation extends AbstractAnimation
{    
    
    /**
     * The counter.
     */
    private long counter;
    
    /**
     * The entity.
     */
    final IEntity entity;
    
    /**
     * The initial position.
     */
    private WPosition initialPosition;
    
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
     * The amount of time to wait before starting.
     */
    private int wait;
    
    /**
     * The max time for the animation to run for, in ms.
     */
    private int duration;

   /**
    * Fire an entity with the given launch speed, angle and gravity and 
    * rotation speed.        
    */
    public MoveAnimation(Builder builder)
    {                
        // Set a reference to the entity.
        this.entity = builder.entity;            
        
        // Record the initial position.
        initialPosition = entity.getXYPosition();
        
        // Record other values.
        this.theta = builder.theta;
        this.g = builder.g;        
        this.wait = builder.wait;
        this.duration = builder.duration;      
        
        // Determine the components of the launch velocity.
        vX = builder.v * Math.cos(Math.toRadians(theta));
        vY = builder.v * Math.sin(Math.toRadians(theta));                                       
    }    
    
    public static class Builder implements IBuilder<MoveAnimation>
    {      
        private final IEntity entity;
        
        private int wait = 0;
        private int duration = 750;
        private int theta = 0;
        private double g = 0;   
        private double v = 0;
        
        public Builder(IEntity entity)
        {            
            this.entity = entity;
        }
        
        public Builder wait(int val) { wait = val; return this; }
        public Builder duration(int val) { duration = val; return this; }
        public Builder theta(int val) { theta = val; return this; }
        public Builder v(double val) { v = val; return this; }
        public Builder g(double val) { g = val; return this; }        
                
        public MoveAnimation end()
        {
            return new MoveAnimation(this);
        }                
    }

    public void nextFrame(long delta)
    {
        // Check if we're done, if we are, return.
        if (isDone() == true)
            return;
              
        // Add delta to counter.  This serves as the time variable.
        counter += delta;                               
            
        if (counter > wait)
        {
            // Determine the current x and y.
            double t = (double) counter;
            double x = vX * t;
            double y = vY * t - 0.5 * g * t * t;

            // Move the entity.
            entity.setX(initialPosition.getX() + (int) x);
            entity.setY(initialPosition.getY() - (int) y);        
        
            if (counter > wait + duration)   
            {
                setDone(true);                                            
            }
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
