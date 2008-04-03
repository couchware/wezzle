package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.util.XYPosition;
import ca.couchware.wezzle2d.util.Util;
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
     * The pulse down state.
     */
    final private static int PULSE_DOWN = 0;
    
    /**
     * The pulse up state.
     */
    final private static int PULSE_UP = 1;
    
    /**
     * The entity being animated.
     */
    protected Entity entity;
    
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
    public PulseAnimation(final Entity entity, final int period)
    {                
        // Invoke super constructor.
        super(period);       
        
        // Save a reference to the entity.
        this.entity = entity;
        
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
                        frames = 0;
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
        frames = 0;
        counter = 0;
    }

    public int getPeriod()
    {
        return period;
    }

    public void setPeriod(int period)
    {
        this.period = period;
    }        
    
}
