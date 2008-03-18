package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.*;

/**
 * An animation that zooms the animation out into nothing.
 * 
 * @author cdmckay
 */
public class ZoomOutAnimation extends Animation
{     
    /**
     * The period of each frame.
     */
    final private static int FRAME_PERIOD = 18;
    
    /**
     * The minimum width the entity may become before the animation
     * is complete.
     */
    final private static int MIN_WIDTH = 0;    
    
     /**
     * The amount the entity size is changed by per step (in pixels).
     */
    final private static int ZOOM_STEP = 2;
           
    /**
     * The constructor.
     */
    public ZoomOutAnimation(final Entity entity)
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
            
            // If we're pulsing down, reducing the size and translate slightly.                              
            entity.setWidth(entity.getWidth() - ZOOM_STEP);
            entity.setX(entity.getX() + ZOOM_STEP / 2);

            entity.setHeight(entity.getHeight() - ZOOM_STEP);
            entity.setY(entity.getY() + ZOOM_STEP / 2);  
            
            // If we reach the minimum size, then we're done.
            if (entity.getWidth() == MIN_WIDTH)
                done = true;
        } // end if          
    }
}
