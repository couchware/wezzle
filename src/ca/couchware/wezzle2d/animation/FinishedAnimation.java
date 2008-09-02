/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.animation;

/**
 * An animation meant to be used as a placeholder where an animation is required
 * by the code by not needed by the programmer.
 * 
 * @author cdmckay
 */
public class FinishedAnimation extends AbstractAnimation
{

    final private static FinishedAnimation single = new FinishedAnimation();
    
    private FinishedAnimation()
    {
        // The animation starts finished.
        setStarted();
        setFinished();
    }
    
    public static FinishedAnimation get()
    { return single; }
    
    @Override
    public void nextFrame(long delta)
    {
        // Do nothing.
    }

}
