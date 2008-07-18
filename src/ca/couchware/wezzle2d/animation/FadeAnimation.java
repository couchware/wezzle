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
     * The default wait.
     */
    final static protected int DEFAULT_WAIT = 400;
    
    /**
     * The default duration.
     */
    final static protected int DEFAULT_DURATION = 750;
    
    /**
     * The fade possibilities.
     */
    public static enum FadeType
    {
        IN, 
        OUT, 
        LOOP_IN, 
        LOOP_OUT
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
     * The amount of time, in ms, to wait before fading out.
     */
    protected int wait;
    
    /**
     * The max time for the animation to run for, in ms.
     */
    protected int duration;
    
    /**
     * The constructor.
     */
    public FadeAnimation(FadeType type, int wait, int duration, 
            final Entity entity)
    {                
        // Invoke super constructor.
        super();
        
        // Is it fade in?
        this.type = type;
        
        // Save the wait.
        this.wait = wait;
        
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
    
    public FadeAnimation(FadeType type, final Entity entity)
    {
        this(type, DEFAULT_WAIT, DEFAULT_DURATION, entity);
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
        if (counter > wait)
        {
            switch (type)
            {
                case IN:      
                case LOOP_IN:
                    entity.setOpacity(
                            Util.scaleInt(
                            0, duration, 0, 100, (int) counter - wait));
                    
                    break;
                
                case OUT:
                case LOOP_OUT:
                    entity.setOpacity(
                            100 - Util.scaleInt(
                            0, duration, 0, 100, (int) counter - wait));                
                    
                    break;
                   
                default:
                    throw new IllegalStateException("Unknown fade type.");
            }            
        }
                
        // See if we're done.
        if (counter > wait + duration)   
        {
            switch (type)
            {
                case IN:
                case OUT:
                    
                    done = true;   
                    break;
                    
                case LOOP_IN:
                    
                    counter -= duration;
                    type = FadeType.LOOP_OUT;
                    break;
                    
                case LOOP_OUT:
                    
                    counter -= duration;
                    type = FadeType.LOOP_IN;
                    break;
                
                default:
                    throw new IllegalStateException("Unknown fade type.");
            }
        } // end if
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
