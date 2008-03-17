package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.*;
import java.awt.Dimension;

/**
 * An animation that pulses the entity in and out.
 * 
 * @author cdmckay
 */
public class PulseAnimation extends Animation
{    
    /**
     * The period of each frame.
     */
    final private static int FRAME_PERIOD = 120;
    
    /**
     * The pulse down state.
     */
    final private static int PULSE_DOWN = 0;
    
    /**
     * The pulse up state.
     */
    final private static int PULSE_UP = 1;
    
    /**
     * The minimum width the entity may become before switching 
     * pulse states.
     */
    final private int minWidth;
    
    /**
     * The current pulse state.
     */
    private int state;
    
    /**
     * The initial dimensions of the entity.
     */
    final private Dimension d;
    
    /**
     * The initial position of the entity.
     */
    final private XYPosition p;
    
    /**
     * The constructor.
     */
    public PulseAnimation(final Entity entity)
    {                
        // Invoke super constructor.
        super(entity, FRAME_PERIOD);       
        
        // Set the initial pulse state.
        state = PULSE_DOWN;
        
        // Set the minimum width.
        minWidth = entity.getWidth() - 8;
        
        // Remember initial dimensions.
        d = new Dimension(entity.getWidth(), entity.getHeight());
        
        // Remember initial position.
        p = new XYPosition(entity.getX(), entity.getY());        
    }

    public void nextFrame(long delta)
    {
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
            switch (state)
            {
                case PULSE_DOWN:
                    
                    entity.setWidth(entity.getWidth() - 2);
                    entity.setX(entity.getX() + 1);
                    
                    entity.setHeight(entity.getHeight() - 2);
                    entity.setY(entity.getY() + 1);
                    
                    // If the width is equal to the minimum, then
                    // change states.
                    if (entity.getWidth() == minWidth)
                        state = PULSE_UP;
                    
                    break;
                
                case PULSE_UP:
                    
                    entity.setWidth(entity.getWidth() + 2);
                    entity.setX(entity.getX() - 1);
                    
                    entity.setHeight(entity.getHeight() + 2);
                    entity.setY(entity.getY() - 1);
                    
                    // If we're back to the original size, swap states.
                    if (entity.getWidth() == d.width)
                    {                        
                        state = PULSE_DOWN;
                        
                        // Reset frame count.
                        frame = 0;
                    }
                    
                    break;
                    
                default:
                    Util.handleMessage("Unrecognized state.", 
                            Thread.currentThread());
            }
        } // end if          
    }

    public void cleanUp()
    {
        // Resize entity to original dimensions.
        entity.setWidth(d.width);
        entity.setHeight(d.height);
        
        // Move back to original position.
        entity.setX(p.getX());
        entity.setY(p.getY());
        
        // Reset frame and counter.
        frame = 0;
        counter = 0;
    }
}
