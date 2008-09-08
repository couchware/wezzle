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
    private ImmutablePosition initialPosition;
    
    /**
     * The x-component of the launch speed.
     */
    private double vX;
    
    /**
     * The y-component of the launch speed.
     */
    private double vY;
    
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
     * Whether or not wait period is finished.
     */
    private boolean waitFinished = false;
    
    /**
     * The max time for the animation to run for, in ms.
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
        this.wait = builder.wait;
        this.duration = builder.duration;  
        
        this.minX = builder.minX;
        this.minY = builder.minY;
        this.maxX = builder.maxX;
        this.maxY = builder.maxY;
        this.finishRule = builder.finishRule;
        
        // Determine the components of the launch velocity.
        vX = builder.v * Math.cos(Math.toRadians(theta));
        vY = builder.v * Math.sin(Math.toRadians(theta));                                       
    }    
    
    public static class Builder implements IBuilder<MoveAnimation>
    {      
        private final IEntity entity;
        
        private int wait = 0;
        private int duration = -1;
        private int theta = 0;
        private double g = 0;   
        private double v = 0;
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
        public Builder v(double val) { v = val; return this; }
        public Builder g(double val) { g = val; return this; }     
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

    public void nextFrame(long delta)
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
              
        // Add delta to counter.  This serves as the time variable.
        counter += delta;                               
        
        // Skip if necessary.
        if (skip == true)
        {
            if (counter > wait + duration)
                setFinished();
            
            return;
        }
        
        if (waitFinished == false && counter > wait)
        {
            waitFinished = true;
            counter -= wait;
        }                
       
        if (waitFinished == true)
        {
            // Determine the current x and y.
            double t = (double) counter;
            double x = vX * t;
            double y = vY * t - 0.5 * g * t * t;

            // Move the entity.
            int newX = initialPosition.getX() + (int) x;
            int newY = initialPosition.getY() - (int) y;
            
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
                    
            if ((finishRule == FinishRule.FIRST && (doneX == true || doneY == true))
                || (finishRule == FinishRule.BOTH && (doneX == true && doneY == true)))
                
            {
                if (duration == -1) setFinished();
                else skip = true;
            }
            
            if (duration != - 1 && counter > wait + duration)   
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
