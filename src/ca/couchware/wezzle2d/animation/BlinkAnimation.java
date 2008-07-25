/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.graphics.Entity;

/**
 * An animation that will blink an entity for a given duration, or
 * forever.
 * 
 * @author cdmckay
 */
public class BlinkAnimation extends ReferenceAnimation
{

    /**
     * The counter.
     */
    private long counter;
    
    /**
     * The duration types.
     */
    public static enum DurationType
    {
        FIXED, CONTINUOUS
    }
    
    /**
     * The duration type of this animation.
     */
    final private DurationType type;
    
    /**
     * The duration of the animation.
     */
    final private int duration;
    
    /**
     * The period the entity is shown for.
     */
    final private int showPeriod;
    
    /**
     * The period the entity is hidden for.
     */
    final private int hidePeriod;
    
    /**
     * The entity that is being blinked.
     */
    final private Entity entity;
    
    /**
     * The possible states for the blink animation to be in.
     */
    private static enum BlinkState
    {
        SHOW, HIDE
    }
    
    /**
     * The current state of the animation.
     */
    private BlinkState state;
    
    /**
     * The state counter: counts how long we've been
     * in this state.
     */
    private int stateCounter;
    
    /**
     * Create a blink animation on an entity
     * that can run for a set duration or continously
     * and shows it self and hides itself for the given periods.
     * 
     * @param type
     * @param duration
     * @param period
     */    
    public BlinkAnimation(DurationType type, int duration, 
            int showPeriod, int hidePeriod,
            Entity entity) 
    {
        // Assign references.
        this.type = type;
        this.duration = duration;
        this.showPeriod = showPeriod;
        this.hidePeriod = hidePeriod;
        this.entity = entity;       
        
        // Set state to start visible.
        state = BlinkState.SHOW;
        stateCounter = 0;
        entity.setVisible(true);
    }
    
    /**
     * A secondary constructor that is identical to the first except has no
     * duration argument.  Intended for use with the CONTINUOUS duration type.
     * 
     * @param type
     * @param showPeriod
     * @param hidePeriod
     * @param entity
     */
    public BlinkAnimation(DurationType type, int showPeriod, int hidePeriod,
            Entity entity)
    {
        this(type, 0, showPeriod, hidePeriod, entity);
    }
    
    /**
     * Advances the animation by delta ms.
     * 
     * @param delta
     */    
    @Override
    public void nextFrame(long delta)
    {
        // Check if we're done, if we are, return.
        if (isDone() == true)
            return;
              
        // Add delta to counter.  This serves as the time variable.
        counter += delta; 
        
        // See if we're done.
        if (type == DurationType.FIXED && counter > duration)   
        {
            setDone(true);
        }
        
        // Increment state counter.
        stateCounter += delta;
        
        switch (state)
        {
            case SHOW:
                                
                if (stateCounter > showPeriod)
                {
                    state = BlinkState.HIDE;
                    stateCounter -= showPeriod;
                    entity.setVisible(false);
                }
                break;
                
            case HIDE:
                
                if (stateCounter > hidePeriod)
                {
                    state = BlinkState.SHOW;
                    stateCounter -= hidePeriod;
                    entity.setVisible(true);
                }
                break;
                
            default:                
                throw new IllegalStateException("Unknown blink state!");                
        } // end switch                                
    }

    @Override
    public void setVisible(final boolean visible)
    {
        super.setVisible(visible);    
        if (entity != null)
            entity.setVisible(visible);
    }
    
}
