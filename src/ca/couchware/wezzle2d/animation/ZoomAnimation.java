package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.*;
import java.awt.Dimension;

/**
 * An animation that zooms the animation into nothing.
 * 
 * @author cdmckay
 */
public class ZoomAnimation extends Animation
{     
    /**
     * The period of each frame.
     */
    final private static int FRAME_PERIOD = 18;
    
    /**
     * The minimum width the entity may become before switching 
     * pulse states.
     */
    final private static int MIN_WIDTH = 0;    
           
    /**
     * The constructor.
     */
    public ZoomAnimation(final Entity entity)
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
            entity.setWidth(entity.getWidth() - 2);
            entity.setX(entity.getX() + 1);

            entity.setHeight(entity.getHeight() - 2);
            entity.setY(entity.getY() + 1);  
            
            // If we reach the minimum size, then we're done.
            if (entity.getWidth() == MIN_WIDTH)
                done = true;
        } // end if          
    }
}
