package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.*;

/**
 * An animation that:
 * 1) Resizes the entity very small (approximately 0 pixels).
 * 2) Zooms that entity in until it becomes its original size.
 * 
 * Currently, the entity is assumed to be square.
 * 
 * @author cdmckay
 */
public class ZoomInAnimation extends Animation
{     
    /**
     * The period of each frame.
     */
    final private static int FRAME_PERIOD = 18;
    
    /**
     * The minimum width the entity will be resized to by the constructor.
     */
    final private static int MIN_WIDTH = 0;
    
    /**
     * The amount the entity size is changed by per step (in pixels).
     */
    final private static int ZOOM_STEP = 2;
    
    /**
     * The maximum width of the entity.  This will be the size it
     * zooms in to.
     */
    final private int maxWidth;
           
    /**
     * The constructor.
     */
    public ZoomInAnimation(final Entity entity)
    {                
        // Invoke super constructor.
        super(entity, FRAME_PERIOD);
        
        // Remember the width.
        maxWidth = entity.getWidth();
        
        // Make the entity small and invisible.
        entity.setWidth(MIN_WIDTH);
        entity.setX(entity.getX() + maxWidth / 2 - MIN_WIDTH);
        
        entity.setHeight(MIN_WIDTH);
        entity.setY(entity.getY() + maxWidth / 2 - MIN_WIDTH);
        
        entity.setVisible(false);
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
            
            // Make it visible if it's not already.
            entity.setVisible(true);
            
            // If we're pulsing down, reducing the size and translate slightly.                              
            entity.setWidth(entity.getWidth() + ZOOM_STEP);
            entity.setX(entity.getX() - ZOOM_STEP / 2);

            entity.setHeight(entity.getHeight() + ZOOM_STEP);
            entity.setY(entity.getY() - ZOOM_STEP / 2);  
            
            // If we reach the minimum size, then we're done.
            if (entity.getWidth() == maxWidth)
                done = true;
        } // end if          
    }
}
