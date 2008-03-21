package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.*;

/**
 * An animation that zooms the animation into nothing.
 * 
 * @author cdmckay
 */
public class JiggleFadeAnimation extends Animation
{     
    /**
     * The period of each frame.
     */
    final private static int FRAME_PERIOD = 65;
    
    /**
     * The jiggle factor, in degrees.
     */
    final private static int JIGGLE_FACTOR = 50;
    
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
     * The constructor.
     */
    public JiggleFadeAnimation(final Entity entity)
    {                
        // Invoke super constructor.
        super(entity, FRAME_PERIOD);
    }

    public void nextFrame(long delta)
    {
        // Check if we're done, if we are, return.
        if (isDone() == true)
            return;
        
        // Add to counter.
        counter += delta;
        
        // See if enough time has elapsed to advance the frame.
        if (counter >= period)
        {
            // Increase the frame.
            frame++;            
            
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
                done = true;
        } // end if          
    }
}
