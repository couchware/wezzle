package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.util.Util;

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
    private long counter;
    
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
    public static enum FadeType
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
    private FadeType type;
    
    /**
     * The amount of time, in ms, to wait before fading out.
     */
    private int wait;
    
    /**
     * The max time for the animation to run for, in ms.
     */
    private int duration;
    
    /**
     * The constructor.
     */
    private FadeAnimation(Builder builder)
    {                
        // Invoke super constructor.
        super();
        
        // Is it fade in?
        this.type = builder.type;
        
        // Save a reference to the entity.
        this.entity = builder.entity;
        
        // Save the wait.
        this.wait = builder.wait;
        
        // Save the duration.
        this.duration = builder.duration;        
        
        // Set the opacities.
        this.minOpacity = builder.minOpacity;
        this.maxOpacity = builder.maxOpacity;
                
        // Set the initial opacity.
        if (type == FadeType.IN)        
            this.entity.setOpacity(minOpacity);
        else
            this.entity.setOpacity(maxOpacity);
        
        // Make sure the entities are visible.
        this.entity.setVisible(true);
    }
    
    public static class Builder implements IBuilder<FadeAnimation>
    {
        private final FadeType type;
        private final IEntity entity;
        
        private int wait = 400;
        private int duration = 750;
        private int minOpacity = 0;
        private int maxOpacity = 100;
        
        public Builder(FadeType type, IEntity entity)
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

    public void nextFrame(long delta)
    {
        // Check if we're done, if we are, return.
        if (isDone() == true)
            return;              
        
        // Add to counter.
        counter += delta;
        
        // Adjust opacity.
        if (counter > wait)
        {
            switch (type)
            {
                case IN:      
                case LOOP_IN:
                   
                    int i = Util.scaleInt(0, duration, 
                            minOpacity, maxOpacity, (int) counter - wait);
                    entity.setOpacity(i);
                            
                    break;
                
                case OUT:
                case LOOP_OUT:
                    
                    int o = Util.scaleInt(0, duration, 
                            minOpacity, maxOpacity, (int) counter - wait);
                    entity.setOpacity(maxOpacity - o + minOpacity);                         
                    
                    break;
                   
                default:
                    throw new AssertionError();
            }   
            
            // See if we're done.
            if (counter > wait + duration)   
            {
                switch (type)
                {
                    case IN:
                    case OUT:

                        setDone(true);
                        break;

                    case LOOP_IN:

                        counter -= duration;
                        type = FadeType.LOOP_OUT;
                        break;

                    case LOOP_OUT:

                        counter -= duration;
                        type = FadeType.LOOP_IN;
                        break;

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
    public FadeType getType()
    {
        return type;
    }
        
}
