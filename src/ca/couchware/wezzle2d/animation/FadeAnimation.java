package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.graphics.Entity;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.Util;

/**
 * An animation that zooms the animation out into nothing.
 * 
 * @author cdmckay
 */
public class FadeAnimation extends Animation
{      
    /**
     * The fade possibilities.
     */
    public static enum FadeType
    {
        IN, OUT
    }
    
    /**
     * The entity being animated.
     */
    protected Entity entity;

    /**
     * Is the animation fade in?
     */
    protected FadeType type;
    
    /**
     * The max time for the animation to run for, in ms.
     */
    protected int duration;
    
    /**
     * The constructor.
     */
    public FadeAnimation(FadeType type, int duration, final Entity entity)
    {                
        // Invoke super constructor.
        super(0);
        
        // Is it fade in?
        this.type = type;
        
        // Save the duration.
        this.duration = duration;
        
        // Save a reference to the entity.
        this.entity = entity;
        
        // Set the initial opacity.
        if (type == FadeType.IN)        
            this.entity.setOpacity(0);
        else
            this.entity.setOpacity(100);
    }

    public void nextFrame(long delta)
    {
        // Check if we're done, if we are, return.
        if (isDone() == true)
            return;
        
       // Is there any delay left?
        if (delay > 0)
        {
            // See if this delta will eliminate the delay.
            if (delta > delay)
            {
                // If it will, subtract the remaing delay from the delta.
                delay = 0;
                delta -= delay;
            }
            // Otherwise, subtract delta from delay and we're done.
            else
            {
                delay -= delta;
                return;
            }
        }
        
        // Add to counter.
        counter += delta;
        
        // Adjust opacity.
        if (type == FadeType.IN)
        {
            entity.setOpacity(
                    Util.scaleInt(0, duration, 0, 100, (int) counter));
        }
        else
        {
            entity.setOpacity(
                    100 - Util.scaleInt(0, duration, 0, 100, (int) counter));
        }
        
        // See if we're done.
        if (counter > duration)   
        {
            done = true;                                
        }
    }

    /**
     * Get the type of fade.
     * 
     * @return
     */
    public FadeType getType()
    {
        return type;
    }        
}
