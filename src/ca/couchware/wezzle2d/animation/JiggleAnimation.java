package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.graphics.Entity;
import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.*;

/**
 * An animation that zooms the animation into nothing.
 * 
 * @author cdmckay
 */
public class JiggleAnimation extends Animation
{            
    
    /**
     * The jiggle factor, in degrees.
     */
    final private static int JIGGLE_FACTOR = 40;                
    
    /**
     * The number of jiggles to occur, so far.
     */
    protected int jiggles;
    
    /**
     * The entity being animated.
     */
    protected Entity entity;
    
    /**
     * The duration of the animation.
     */
    final protected int duration;
    
    /**
     * The amount of time per frame.
     */
    protected int period;

    /**
     * Creates a jiggle animation that runs for the passed duration and jiggles
     * every at the end of every period.
     * 
     * @param duration
     * @param period
     * @param entity
     */
    public JiggleAnimation(
            final int duration, 
            final int period, 
            final Entity entity)
    {                        
        // Set values.
        this.jiggles = 0;
        this.duration = duration;
        this.period = period;
        
        // Save a reference to the entity.
        this.entity = entity;
    }

    public void nextFrame(long delta)
    {
        // Check if we're done, if we are, return.
        if (isDone() == true)
            return;
        
        // Add to counter.
        counter += delta;                

        // Jiggle if period is up.
        if (counter / period > jiggles)
        {
            jiggles++;
            
            entity.setRotation(Math.toRadians(
                Util.random.nextInt(JIGGLE_FACTOR) - JIGGLE_FACTOR / 2));
        }        

        // If we reach the minimum opacity, then we're done.            
        if (counter >= duration)
        {                
            done = true;                
        }         
    }
}
