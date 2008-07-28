package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.*;

/**
 * An animation that zooms the animation out into nothing.
 * 
 * @author cdmckay
 */
public class ZoomOutAnimation extends AbstractAnimation
{    
    
    /**
     * The counter.
     */
    private long counter;
    
    /**
     * The amount of time per frame.
     */
    protected int period;
    
    /**
     * The current frame.
     */
    protected int frames;
    
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
     * The entity being animated.
     */
    protected AbstractEntity entity;
    
    /**
     * The constructor.
     */
    public ZoomOutAnimation(final AbstractEntity entity)
    {                
        // Invoke super constructor.
        this.period = FRAME_PERIOD;
        
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
        
        // Advance the number of frames.
        for (int i = 0; i < frames; i++)
        {
            // If we're pulsing down, reducing the size and translate slightly.                              
            entity.setWidth(entity.getWidth() - ZOOM_STEP);
            entity.setX(entity.getX() + ZOOM_STEP / 2);

            entity.setHeight(entity.getHeight() - ZOOM_STEP);
            entity.setY(entity.getY() + ZOOM_STEP / 2);  
            
            // If we reach the minimum size, then we're done.
            if (entity.getWidth() == MIN_WIDTH)
            {                
                setDone(true);
                break;
            }
        } // end if          
    }
    
}
