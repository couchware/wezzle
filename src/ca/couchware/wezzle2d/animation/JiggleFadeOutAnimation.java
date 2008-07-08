package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.graphics.Entity;
import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.*;

/**
 * An animation that zooms the animation into nothing.
 * 
 * @author cdmckay
 */
public class JiggleFadeOutAnimation extends Animation
{         
    /**
     * The period of each frame.
     */
    final private static int FRAME_PERIOD = 30;
    
    /**
     * The jiggle factor, in degrees.
     */
    final private static int JIGGLE_FACTOR = 40;
    
    /**
     * The amount of opacity to reduce each step.
     */
    final private static int OPACITY_STEP = 12;
    
    /**
     * The minimum opacity the entity may become before switching 
     * pulse states.
     */
    final private static int OPACITY_MIN = 0;
        
    /**
     * The entity being animated.
     */
    protected Entity entity;
    
    /**
     * The constructor.
     */
    public JiggleFadeOutAnimation(final Entity entity)
    {                
        // Invoke super constructor.
        super(FRAME_PERIOD);
        
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
        
        // See how many frames have passed.
        frames = (int) counter / period;
        counter = counter % period;
        
        // See if enough time has elapsed to advance the frame.        
        for (int i = 0; i < frames; i++)
        {                  
            // Remove the period time so the counter will work for ensuing
            // frames.
            counter -= period;

            // Reduce the opacity.
            entity.setOpacity(entity.getOpacity() - OPACITY_STEP);

            // Jiggle.
            entity.setRotation(Math.toRadians(
                    Util.random.nextInt(JIGGLE_FACTOR) - JIGGLE_FACTOR / 2));

            // If we reach the minimum opacity, then we're done.            
            if (entity.getOpacity() == OPACITY_MIN)
            {                
                done = true;
                break;
            }
        } // end if          
    }
}
