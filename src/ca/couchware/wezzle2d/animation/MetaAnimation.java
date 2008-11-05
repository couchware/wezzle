/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.IBuilder;
import ca.couchware.wezzle2d.manager.LogManager;
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
            setFinished();        
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
    public void nextFrame()
    {
        long delta = 14;
        
        // Make sure we've set the started flag.
        setStarted();
        
        if (this.finished == true) return;                    
        
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
        // A temporary variable holding the finished status.
        boolean f;
        
        switch (finishRule)
        {
            case FIRST:
         
                    // Assume that we are not finished.
                    f = false;
                
                    // Cycle through the animation list.  If we find that
                    // one animation is done, then the meta animation is done.
                    for (IAnimation a : animationList)
                    {
                        a.nextFrame();
                        if (a.isFinished() == true)
                            f = true;              
                    }
                
                break;
                
            case ALL:
                
                    // Assume we are finished.
                    f = true;
                
                    // Cycle through the animation list.  If we find at least
                    // one animation that is not done, then we keep going.
                    for (IAnimation a : animationList)
                    {
                        a.nextFrame();
                        if (a.isFinished() == false)
                            f = false;
                    }                                        
                
                break;
                                
            default: throw new AssertionError();
        }            
        
        if (f == true)
            setFinished();
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
        a.nextFrame();
        if (a.isFinished() == true) animationList.remove(0);
        
         // If there are no animations left, then we are done.
        if (animationList.isEmpty() == true)
        {
            setFinished();
            return;
        }
    }        

}
