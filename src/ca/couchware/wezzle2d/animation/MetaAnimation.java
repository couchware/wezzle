/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.IBuilder;
import ca.couchware.wezzle2d.LogManager;
import java.util.ArrayList;
import java.util.List;

/**
 * An animation that may contain 1 or more normal animations.  Can be configured
 * to signal that it is done based on a variety of criteria.
 * 
 * @author cdmckay
 */
public class MetaAnimation extends AbstractAnimation
{

    public enum RunRule
    {
        /**
         * Run all the animations at the same time.
         */
        SIMULTANEOUS,
        
        /**
         * Run the animations in the order they were added, which each ensuing
         * animation running after the previous one is finished.
         */
        SEQUENCE
    }
    
    public enum FinishRule
    {
        /**
         * The meta animation will return done as soon as the first animation
         * it encapsulates is done.
         */
        FIRST,
        
        /**
         * The meta animation will return done as soon as all of the animations
         * it encapsulates are done.
         */
        ALL
    }
    
    private final RunRule runRule;
    private final FinishRule finishRule;
    
    private final List<IAnimation> animationList;
    
    private MetaAnimation(Builder builder)
    {
        this.runRule = builder.runRule;
        this.finishRule = builder.finishRule;
        this.animationList = builder.animationList;
        
        if (this.animationList.isEmpty() == true)        
            setFinished(true);        
    }
    
    public static class Builder implements IBuilder<MetaAnimation>
    {
        private final List<IAnimation> animationList;        
        
        private RunRule runRule = RunRule.SIMULTANEOUS;
        private FinishRule finishRule = FinishRule.FIRST;        
        
        public Builder()
        {
            this.animationList = new ArrayList<IAnimation>();
        }
        
        public Builder add(IAnimation val) 
        { animationList.add(val); return this; }
        
        public Builder runRule(RunRule val)
        { runRule = val; return this; }
        
        public Builder finishRule(FinishRule val) 
        { finishRule = val; return this; }       

        public MetaAnimation end()
        {
            return new MetaAnimation(this);
        }                
    }
    
    @Override
    public void nextFrame(long delta)
    {
        if (isFinished() == true) return;                    
        
        switch (runRule)
        {            
            case SIMULTANEOUS:
                
                handleSimultaneousRule(delta);
                break;
                
            case SEQUENCE:
                
                handleSequenceRule(delta);
                break;
                
            default: throw new AssertionError();            
        }                
    }
    
    /**
     * This method handles the behavoiur of the meta animation if the
     * simultaneous run rule.
     * 
     * @param delta
     */
    private void handleSimultaneousRule(long delta)
    {
        boolean finished;
        
        switch (finishRule)
        {
            case FIRST:
         
                    // Assume that we are not finished.
                    finished = false;
                
                    // Cycle through the animation list.  If we find that
                    // one animation is done, then the meta animation is done.
                    for (IAnimation a : animationList)
                    {
                        a.nextFrame(delta);
                        if (a.isFinished() == true)
                            finished = true;              
                    }
                
                break;
                
            case ALL:
                
                    // Assume we are finished.
                    finished = true;
                
                    // Cycle through the animation list.  If we find at least
                    // one animation that is not done, then we keep going.
                    for (IAnimation a : animationList)
                    {
                        a.nextFrame(delta);
                        if (a.isFinished() == false)
                            finished = false;
                    }                                        
                
                break;
                                
            default: throw new AssertionError();
        }            
        
        setFinished(finished);
    }
    
     /**
     * This method handles the behavoiur of the meta animation if the
     * sequence run rule.
     * 
     * @param delta
     */
    private void handleSequenceRule(long delta)
    {               
        // The animation list must not be empty if we got to this point.
        assert animationList.isEmpty() == false;
        
        // If there are animations left, then we run the first one until it is
        // done, then remove it and do the next one and so on.
        IAnimation a = animationList.get(0);
        a.nextFrame(delta);
        if (a.isFinished() == true) animationList.remove(0);
        
         // If there are no animations left, then we are done.
        if (animationList.isEmpty() == true)
        {
            setFinished(true);
            return;
        }
    }
    
    /**
     * This method is run when the animation is first loaded by the
     * animation manager.
     */
    @Override
    public void onStart()
    {
        for (IAnimation a : animationList)
            a.onStart();
    }
    
    /**
     * This method is run when the animation is finished running.
     */
    @Override
    public void onFinish()
    {
        for (IAnimation a : animationList)
            a.onFinish();
    }

}
