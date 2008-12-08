/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.animation;

/**
 * A reference implementation of the IAnimation interface.
 * 
 * @author cdmckay
 */
public abstract class AbstractAnimation implements IAnimation
{

    /** A empty runnable, used as the default hook. */
    protected Runnable EMPTY_HOOK = new Runnable() { public void run() { } };
    
    /** Whether or not the animation is visible. */
    protected boolean visible = true;
    
    /** Whether or not the animation has started. */
    protected boolean started = false;
    
    /** Whether or not the animation is done. */
    protected boolean finished = false;        
    
    /** The start action. */
    protected Runnable startHook = EMPTY_HOOK;
    
    /** The finish action. */
    protected Runnable finishHook = EMPTY_HOOK;
    
    /**
     * Advance the frame.
     * 
     * @param delta
     */
    public abstract void nextFrame();

    public void cleanUp()
    {
        // Override.
    }       
    
    public boolean isStarted()
    {
        return started;
    }
    
    final protected void setStarted()
    {
        if (this.started == false)
        {
            this.started = true;
            
            // Run the on-start runnable.
            runStartHook();
        }
    }
    
    public boolean isFinished()
    {
        return finished;
    }
    
    final public void setFinished()
    {        
        if (this.finished == false)
        {
            this.finished = true;
            
            // Run the on-finish runnable.
            runFinishHook();
        }
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }
    
    /**
     * Set the start action.
     */
    public void setStartHook(Runnable hook)
    {
        assert hook != null;
        this.startHook = hook;
    }       
    
    public Runnable getStartRunnable()
    {
        return this.startHook;
    }
    
    /**
     * Set the finish action.
     */
    public void setFinishHook(Runnable hook)
    {
        assert hook != null;
        this.finishHook = hook;
    }    
    
     public Runnable getFinishRunnable()
    {
        return this.finishHook;
    }

    final public void runStartHook()
    {
        startHook.run();
    }

    final public void runFinishHook()
    {
        finishHook.run();
    }        
    
}
