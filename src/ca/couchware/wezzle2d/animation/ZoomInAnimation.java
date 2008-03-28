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
    final private static int FRAME_PERIOD = 36;
    
    /**
     * The minimum width the entity will be resized to by the constructor.
     */
    final private static int MIN_WIDTH = 0;
    
    /**
     * The amount the entity size is changed by per step (in pixels).
     */
    final private static int ZOOM_STEP = 4;
    
    /**
     * The entity being animated.
     */
    protected Entity entity;
    
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
        super(FRAME_PERIOD);
        
        // Save a reference to the entity.
        this.entity = entity;
        
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
        
        // Set the number of frames that have passed to 0.
        frames = 0;
        
        // See how many frames have passed.
        while (counter >= period)
        {
            frames++;
            counter -= period;
        }                
        
        // Advance the number of frames.
        for (int i = 0; i < frames; i++)
        {                          
            // Make it visible if it's not already.
            entity.setVisible(true);
            
            // If we're pulsing down, reducing the size and translate slightly.                              
            entity.setWidth(entity.getWidth() + ZOOM_STEP);
            entity.setX(entity.getX() - ZOOM_STEP / 2);

            entity.setHeight(entity.getHeight() + ZOOM_STEP);
            entity.setY(entity.getY() - ZOOM_STEP / 2);  
            
            // If we reach the minimum size, then we're done.
            if (entity.getWidth() == maxWidth)
            {
                done = true;
                break;
            }
        } // end if          
    }
}