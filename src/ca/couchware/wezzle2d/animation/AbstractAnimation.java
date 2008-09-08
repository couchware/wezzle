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
    protected Runnable startAction;
    
    /**
     * The finish action.
     */
    protected Runnable finishAction;       
    
    /**
     * Advance the frame.
     * 
     * @param delta
     */
    public abstract void nextFrame(long delta);

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
    public void setStartAction(Runnable startAction)
    {
        this.startAction = startAction;
    }
    
    /**
     * Get the start action.
     */
    public Runnable getStartAction()
    {
        return startAction;
    }
    
    /**
     * Set the finish action.
     */
    public void setFinishAction(Runnable finishAction)
    {
        this.finishAction = finishAction;
    }
    
    /**
     * Get the finish action.
     */
    public Runnable getFinishAction()
    {
        return finishAction;
    }

    final public void onStart()
    {
        if (startAction != null) startAction.run();
    }

    final public void onFinish()
    {
        if (finishAction != null) finishAction.run();
    }        
    
}
