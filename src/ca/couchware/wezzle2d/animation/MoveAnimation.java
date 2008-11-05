package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.util.ImmutablePosition;

/**
 * An animation that starts an explosion in the middle of the entity.
 * 
 * @author cdmckay
 */
public class MoveAnimation extends AbstractAnimation
{    
    
    /**
     * The finish rules.
     */
    public enum FinishRule
    {
        /**
         * Finish the animation when the first coordinate is met (default).
         */
        FIRST,        
        
        /**
         * Finish the animation only when both coordinates are met.
         */
        BOTH
    }
    
    /**
     * Is skip on?  This is engaged when both x and y boundaries are met to
     * tell the animation to skip calculations until done.
     */
    private boolean skip = false;
    
    /**
     * Counts the number of ticks.
     */
    private int ticks;
    
    /**
     * The entity that is being moved.
     */
    final IEntity entity;
    
    /**
     * The initial position.
     */
    private ImmutablePosition initialPosition;
    
    /**
     * The x-component of the launch speed, in pixels / tick.
     */
    private int vX;
    
    /**
     * The y-component of the launch speed, in pixels / tick.
     */
    private int vY;
    
    /**
     * The speed, p-part of a rational number.
     */
    private int vp;
    
    /**
     * The number of pixels to skip each tick, q-part of a rational number.
     */
    private int vq;
    
    /**
     * The number of frames left to skip.
     */
    private int Q;
    
    /**
     * The minimum x that the animation may move to.
     */
    private int minX;
    
    /**
     * The minimum y that the animation may move to.
     */    
    private int minY;
    
    /**
     * The maximum x that the animation may move to.
     */
    private int maxX;
    
    /**
     * The maximum y that the animation may move to.
     */
    private int maxY;
    
    /**
     * The launch angle, in degrees / tick.
     */
    private int theta;
    
    /**
     * The anglar velocity, in degrees / tick.
     */
    private int omega;
    
    /**
     * The gravity, in pixels / tick / tick.
     */
    private int g;
    
    /**
     * The amount of time to wait before starting.
     */
    private int wait;
    
    /**
     * Whether or not wait period is finished.
     */
    private boolean waitFinished = false;
    
    /**
     * The max time for the animation to run for, in ticks.
     */
    private int duration;
    
    /**
     * The finish rule for the animation.
     */
    private final FinishRule finishRule;

   /**
    * Fire an entity with the given launch speed, angle and gravity and 
    * rotation speed.        
    */
    private MoveAnimation(Builder builder)
    {                
        // Set a reference to the entity.
        this.entity = builder.entity;                           
        
        // Record other values.
        this.theta = builder.theta;
        this.g = builder.g;        
        this.omega = builder.omega;
        this.wait = builder.wait;
        this.duration = builder.duration;  
        
        this.minX = builder.minX;
        this.minY = builder.minY;
        this.maxX = builder.maxX;
        this.maxY = builder.maxY;
        this.finishRule = builder.finishRule;
        
        // Determine the components of the launch velocity.
        this.vX = (int) ((double) builder.vp * Math.cos(Math.toRadians(theta)));
        this.vY = (int) ((double) builder.vp * Math.sin(Math.toRadians(theta)));
        
        // Set the number of frames to skip.
        this.vq = builder.vq;       
    }    
    
    public static class Builder implements IBuilder<MoveAnimation>
    {      
        private final IEntity entity;
        
        private int wait = 0;
        private int duration = -1;
        private int theta = 0;
        private int g = 0;   
        private int vp = 0; // p/q
        private int vq = 0;
        private int omega = 0;
        private int minX = Integer.MIN_VALUE;
        private int minY = Integer.MIN_VALUE;
        private int maxX = Integer.MAX_VALUE;
        private int maxY = Integer.MAX_VALUE;
        private FinishRule finishRule = FinishRule.FIRST;
        
        public Builder(IEntity entity)
        {            
            this.entity = entity;
        }
        
        public Builder wait(int val) { wait = val; return this; }
        public Builder duration(int val) { duration = val; return this; }
        public Builder theta(int val) { theta = val; return this; }
        public Builder speed(int p) { vp = p; vq = 1; return this; } 
        public Builder speed(int p, int q) { vp = p; vq = q; return this; }        
        public Builder gravity(int val) { g = val; return this; }     
        public Builder omega(int val) { omega = val; return this; }
        public Builder minX(int val) { minX = val; return this; }
        public Builder minY(int val) { minY = val; return this; }
        public Builder maxX(int val) { maxX = val; return this; }
        public Builder maxY(int val) { maxY = val; return this; }
        public Builder finishRule(FinishRule val) { finishRule = val; return this; }
                
        public MoveAnimation end()
        {
            return new MoveAnimation(this);
        }                
    }

    public void nextFrame()
    {                   
        // Make sure we've set the started flag.
        if (this.started == false)
        {
            // Record the initial position.
            initialPosition = entity.getXYPosition();            
            setStarted();
        }
        
        // Check if we're done, if we are, return.
        if (this.finished == true)
        {
            //LogManager.recordMessage("Move finished!");
            return;
        }
              
        // Increment counter.  This serves as the time variable.
        ticks++;
        
        // Skip if necessary.
        if (skip == true)
        {
            if (ticks > wait + duration)
                setFinished();
            
            return;
        }
        
        if (waitFinished == false && ticks > wait)
        {
            waitFinished = true;
            ticks = 1;
        }                
       
        if (waitFinished == true)
        {
            // Determine the current x and y.            
            int x = (vX * ticks) / vq;
            int y = (vY * ticks) / vq - (g * Util.sq(ticks)) / 20;

            // Move the entity.
            int newX = initialPosition.getX() + x;
            int newY = initialPosition.getY() - y;
            
            boolean doneX = false;
            boolean doneY = false;
            
            if (newX <= minX) 
            { 
                newX = minX; doneX = true;
            }
            else if (newX >= maxX) 
            {
                newX = maxX; doneX = true;
            }            
            
            if (newY <= minY) 
            {
                newY = minY; doneY = true;
            }
            else if (newY >= maxY)
            {
                newY = maxY; doneY = true;
            }           
            
            entity.setX(newX);                        
            entity.setY(newY);                        
            entity.setRotation(ticks * omega);
                    
            if ((finishRule == FinishRule.FIRST && (doneX == true || doneY == true))
                || (finishRule == FinishRule.BOTH && (doneX == true && doneY == true)))
                
            {
                if (duration == -1) setFinished();
                else skip = true;
            }
            
            if (duration != -1 && ticks > wait + duration)   
                setFinished();                                                        
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
