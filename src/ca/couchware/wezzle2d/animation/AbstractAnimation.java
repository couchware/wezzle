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

    public void onStart()
    {
        // Override.
    }

    public void onFinish()
    {
        // Override.
    }        
    
}
