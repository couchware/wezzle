package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.*;

/**
 * An abstract class for animating entities.
 * 
 * @author cdmckay
 */
public abstract class Animation 
{
    /**
     * Whether or not the animation is visible.
     */
    protected boolean visible;       
    
    /**
     * The amount of delay before starting.
     */
    protected int delay;
    
    /**
     * The amount of time per frame.
     */
    protected int period;
    
    /**
     * The current frame.
     */
    protected int frame;
    
    /**
     * The internal time counter (in ms).
     */
    protected long counter;
    
    /**
     * A variable holding the done state.
     */
    protected boolean done;
    
    /**
     * The default constructor.
     */
    public Animation(final int period)
    {        
        // Animation is initially visible.
        this.visible = true;
        
        // Set some defaults.
        this.period = period;
        this.frame = 0;
        this.counter = 0;
        this.done = false;
    }
    
    /**
     * Determines how the entity should be changed based on how much time
     * has passed since last update.
     */
    public abstract void nextFrame(long delta);       
    
    /**
     * Performs cleanup to the animation so that it may remain in a consistent
     * state even if it is called half-way through an animation.
     */
    public void cleanUp()
    {
        // Do nothing.
    }
    
    /**
     * Checks whether the animation is done.  Always returns false if the
     * animation is a looping animation.
     */
    public boolean isDone()
    {
        return done;
    }
    
    /**
     * Checks the visibility of the animation.
     * 
     * @return True if visible, false otherwise.
     */
    public boolean isVisible()
    {
        return visible;
    }
    
    /**
     * Sets the visibility of the animation.
     * 
     * @param visible True if visible, false if not.
     */
    public void setVisible(final boolean visible)
    {
        this.visible = visible;
    }

    /**
     * Get the amount of delay.
     * 
     * @return The amount of delay, in ms.
     */
    public int getDelay()
    {
        return delay;
    }

    /**
     * Set the amount of delay.  This should be set before the animation
     * is started.
     * 
     * @param delay
     */
    public void setDelay(int delay)
    {
        this.delay = delay;
    }
        
}
