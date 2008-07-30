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
    private boolean visible = true;
    
    /**
     * Whether or not the animation is done.
     */
    private boolean done = false;
    
    /**
     * The start action.
     */
    private Runnable startAction;
    
    /**
     * The finish action.
     */
    private Runnable finishAction;
    
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
    
    public boolean isDone()
    {
        return done;
    }
    
    protected void setDone(boolean done)
    {
        this.done = done;
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

    public void onStart()
    {
        if (startAction != null) startAction.run();
    }

    public void onFinish()
    {
        if (finishAction != null) finishAction.run();
    }        
    
}
