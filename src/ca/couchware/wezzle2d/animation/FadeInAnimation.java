package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.*;

/**
 * An animation that zooms the animation out into nothing.
 * 
 * @author cdmckay
 */
public class FadeInAnimation extends Animation
{     
    /**
     * The period of each frame.
     */
    final private static int FRAME_PERIOD = 65;
    
    /**
     * The minimum width the entity may become before the animation
     * is complete.
     */
    final private static int OPACITY_MAX = 100;    
    
     /**
     * The amount the entity size is changed by per step (in pixels).
     */
    final private static int OPACITY_STEP = 12;

    /**
     * The entity being animated.
     */
    protected Entity entity;
    
    /**
     * The constructor.
     */
    public FadeInAnimation(final Entity entity)
    {                
        // Invoke super constructor.
        super(FRAME_PERIOD);
        
        // Save a reference to the entity.
        this.entity = entity;
        this.entity.setOpacity(0);
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
        
        // See how many frames have passed.
        frames = (int) counter / period;
        counter = counter % period;
        
        // Advance the number of frames.
        for (int i = 0; i < frames; i++)
        {
            // If we're pulsing down, reducing the size and translate slightly.                              
            entity.setOpacity(entity.getOpacity() + OPACITY_STEP);
            
            // If we reach the minimum size, then we're done.
            if (entity.getOpacity() == OPACITY_MAX)
            {
                done = true;
                break;
            }
        } // end if          
    }
}
