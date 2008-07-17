/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.animation;

/**
 * An X-animation is a composite animation.  It consists of 1 or more layered
 * animations.
 * 
 * @author cdmckay
 */
public class XAnimation extends Animation
{
    
    /**
     * The array of animations.
     */
    protected Animation[] animations;
    
    /**
     * The constructor.
     * 
     * @param size The number of animations that make up this X-animation.
     */
    public XAnimation(int size)
    {
        // Initialize animations array.
        animations = new Animation[size];
    }
    
    /**
     * Advance the frame of all animations that make up the X-animation.
     * 
     * @param delta
     */    
    @Override
    public void nextFrame(long delta)
    {
        // Apply the animations in order, from 0 to whatever.
        for (int i = 0; i < animations.length; i++)
            animations[i].nextFrame(delta);
    }
    
    @Override
    public boolean isDone()
    {
        for (int i = 0; i < animations.length; i++)
            if (animations[i].isDone() == false)
                return false;
        
        return true;                
    }
    
    /**
     * Set the animation.
     * 
     * @param index
     * @param a
     */
    public void set(int index, Animation a)
    {        
        animations[index] = a;
    }

}
