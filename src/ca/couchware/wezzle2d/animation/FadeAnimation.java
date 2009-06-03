package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.util.IBuilder;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.ui.AchievementNotification;
import ca.couchware.wezzle2d.util.NumUtil;

/**
 * An animation that slowly changes the opacity of an entity.
 * 
 * @author cdmckay
 */
public class FadeAnimation extends AbstractAnimation
{       
    
    /**
     * The counter.
     */
    private int ticks = 0;
    
    /**
     * The minimum opacity the fade should go to.
     */
    final private int minOpacity;
    
    /**
     * The maximum opacity the fade should go to.
     */
    final private int maxOpacity;
    
    /**
     * The fade possibilities.
     */
    public static enum Type
    {
        IN, 
        OUT, 
        LOOP_IN, 
        LOOP_OUT
    }
    
    /**
     * The entity being animated.
     */
    private IEntity entity;

    /**
     * Is the animation fade in?
     */
    private Type type;
    
    /**
     * The amount of time, in ticks, to wait before fading out.
     */
    private int wait;
    
    /**
     * The max time for the animation to run for, in ticks.
     */
    private int duration;  
    
    /**
     * The constructor.
     */
    private FadeAnimation(Builder builder)
    {                        
        // Is it fade in?
        this.type = builder.type;
        
        // Save a reference to the entity.
        this.entity = builder.entity;
        
        // Save the wait.
        this.wait     = builder.wait;        
        this.duration = builder.duration;        
        
        // If duration is 0, then it's already finished.
        if (this.duration == 0)
        {
            setFinished();
        }
        
        // Set the opacities.
        this.minOpacity = builder.minOpacity;
        this.maxOpacity = builder.maxOpacity;                        
    }
    
    public static class Builder implements IBuilder<FadeAnimation>
    {
        private final Type type;
        private final IEntity entity;
        
        private int wait = 0;
        private int duration = 500;
        private int minOpacity = 0;
        private int maxOpacity = 100;
        
        public Builder(Type type, IEntity entity)
        {
            this.type = type;
            this.entity = entity;
        }
        
        public Builder wait(int val) { wait = val; return this; }
        public Builder duration(int val) { duration = val; return this; }
        public Builder minOpacity(int val) { minOpacity = val; return this; }
        public Builder maxOpacity(int val) { maxOpacity = val; return this; }

        public FadeAnimation end()
        {
            return new FadeAnimation(this);
        }                
    }

    public void nextFrame()
    {        
        // Make sure we've set the started flag.
        if (this.started == false)
        {
            // Set the initial opacity.
            if (type == Type.IN)        
                this.entity.setOpacity(minOpacity);
            else
                this.entity.setOpacity(maxOpacity);

            // Make sure the entities are visible.
            this.entity.setVisible(true);
            
            // Set the started flag.
            setStarted();
        }
        
        // Check if we're done, if we are, return.
        if (this.finished == true)
        {
            //LogManager.recordMessage("Fade finished!");
            return;              
        }
        
        // Add to counter.
        ticks++;
        int ms = ticks * Settings.getMillisecondsPerTick();
        
        // Adjust opacity.
        if (ms > wait)
        {                        
            switch (type)
            {
                case IN:      
                case LOOP_IN:
                   
                    int i = NumUtil.scaleInt(0, duration, 
                            minOpacity, maxOpacity, ms - wait);
                    entity.setOpacity(i);
                            
                    break;
                
                case OUT:
                case LOOP_OUT:
                    
                    int o = NumUtil.scaleInt(0, duration, 
                            minOpacity, maxOpacity, ms - wait);
                    entity.setOpacity(maxOpacity - o + minOpacity);                        
                    
                    break;
                   
                default:
                    throw new AssertionError();
            }   
            
            // See if we're done.
            if (ms > wait + duration)   
            {
                switch (type)
                {                     
                    case IN:                   
                    case OUT:

                        setFinished();
                        break;

//                    case LOOP_IN:
//
//                        ticks -= duration;
//                        type = Type.LOOP_OUT;
//                        break;
//
//                    case LOOP_OUT:
//
//                        ticks -= duration;
//                        type = Type.LOOP_IN;
//                        break;

                    default:
                        throw new AssertionError();
                }
            } // end if
        }                        
    }

    /**
     * Get the type of fade.
     * 
     * @return
     */
    public Type getType()
    {
        return type;
    }
        
}
