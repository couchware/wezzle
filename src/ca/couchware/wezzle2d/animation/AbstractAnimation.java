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

    /**
     * Whether or not the animation is visible.
     */
    protected boolean visible = true;
    
    /**
     * Whether or not the animation has started.
     */
    protected boolean started = false;
    
    /**
     * Whether or not the animation is done.
     */
    protected boolean finished = false;        
    
    /**
     * The start action.
     */
    protected Runnable startRunnable;
    
    /**
     * The finish action.
     */
    protected Runnable finishRunnable;       
    
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
            onStart();
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
            onFinish();
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
    public void setStartRunnable(Runnable startRunnable)
    {
        this.startRunnable = startRunnable;
    }       
    
    public Runnable getStartRunnable()
    {
        return this.startRunnable;
    }
    
    /**
     * Set the finish action.
     */
    public void setFinishRunnable(Runnable finishRunnable)
    {
        this.finishRunnable = finishRunnable;
    }    
    
     public Runnable getFinishRunnable()
    {
        return this.finishRunnable;
    }

    final public void onStart()
    {
        if (startRunnable != null) startRunnable.run();
    }

    final public void onFinish()
    {
        if (finishRunnable != null) finishRunnable.run();
    }        
    
}
