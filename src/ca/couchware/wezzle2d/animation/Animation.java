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
     * The entity being animated.
     */
    protected Entity entity;
    
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
    public Animation(final Entity entity, final int period)
    {
        // Entity cannot be null.
        assert(entity != null);
        
        this.entity = entity;
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
}
