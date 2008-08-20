/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.IBuilder;
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
    
    private final FinishRule rule;
    
    private final List<IAnimation> animationList;
    
    private MetaAnimation(Builder builder)
    {
        this.rule = builder.rule;
        this.animationList = builder.animationList;
    }
    
    public static class Builder implements IBuilder<MetaAnimation>
    {
        private final List<IAnimation> animationList;        
        
        private FinishRule rule = FinishRule.FIRST;
        
        public Builder()
        {
            this.animationList = new ArrayList<IAnimation>();
        }
        
        public Builder add(IAnimation val) 
        { animationList.add(val); return this; }
        
        public Builder finishRule(FinishRule val) 
        { rule = val; return this; }       

        public MetaAnimation end()
        {
            return new MetaAnimation(this);
        }                
    }
    
    @Override
    public void nextFrame(long delta)
    {
        if (isFinished() == true)
            return;
        
        boolean finished;
        
        switch (rule)
        {
            case FIRST:
         
                    finished = false;
                
                    for (IAnimation a : animationList)
                    {
                        a.nextFrame(delta);
                        if (a.isFinished() == true)
                            finished = true;              
                    }
                
                break;
                
            case ALL:
                
                    finished = true;
                
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

}
