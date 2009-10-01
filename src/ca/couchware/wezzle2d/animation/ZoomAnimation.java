package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.util.IBuilder;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.util.ImmutableDimensions;

/**
 * An animation that can zoom an entity in, out, or in and out in a loop.
 * 
 * @author cdmckay
 */
public class ZoomAnimation extends AbstractAnimation
{  
   
    /**
     * The zoom types.
     */
    public static enum Type
    {
        /**
         * The entity will start at the max size and reduce itself to its
         * min size.  At that point the animation will report as done.
         */
        IN, 
        
        /**
         * The entity will start at the min size and increase to its max
         * size.  At that point it will report done.
         */
        OUT, 
        
        /**
         * The entity will act that same as the case of IN, except will
         * not end once min size has been reached.  Instead, it will
         * switch its type to LOOP_OUT and grow to max size.  This will
         * continue indefinitely until the animation is manually stopped
         * or cleaned up.
         */
        LOOP_IN, 
        
        /**
         * Same as LOOP_IN, but starts in the min size and expands to
         * the max size.
         */
        LOOP_OUT
    }        
    
    /**
     * The entity being animated.
     */
    private IEntity entity;
    
    /**
     * The counter.
     */
    private int ticks = 0;
    
    /**
     * The total number of ticks.  For non-looping zooms, this is the same
     * as ticks.  For looping zooms, this is the total number of ticks (as
     * the ticks variable gets reset each loop).
     */
    private int totalTicks = 0;
    
    private int speedIn;      
    private int speedOut;      
          
    final private int minWidth;
    final private int maxWidth;
    
    private Type type;
    
    private ImmutableDimensions dimensions;
    private ImmutablePosition position;
    
    final private int wait;        
    final private int duration;
    
    private ZoomAnimation(Builder builder)
    {                        
        // Save a reference to the entity.
        this.type = builder.type;
        this.entity = builder.entity;   
        this.speedIn = builder.speedIn;
        this.speedOut = builder.speedOut;
        this.wait = builder.wait;
        this.duration = builder.duration;                
        
        // Round min width to nearest even number.
        this.minWidth = builder.minWidth < 0 ? 0 : builder.minWidth;
                
        // Round max width to nearest even number.
        this.maxWidth = builder.maxWidth < 0 ? entity.getWidth() : builder.maxWidth;
               
        // Remember initial dimensions.
        this.dimensions = new ImmutableDimensions(entity.getWidth(), entity.getHeight());                
    }

    private void determinePosition()
    {
        // Remember initial position.
        this.position = new ImmutablePosition(entity.getX(), entity.getY());

        // Set the entity up based on what type of zoom we're doing.
        int dw = 0;
        switch (type)
        {
            case IN:
            case LOOP_IN:

                entity.setWidth(maxWidth);
                entity.setHeight(maxWidth);
                dw = maxWidth - dimensions.getWidth();
                entity.setX(position.getX() - dw);
                entity.setY(position.getY() - dw);

                break;

            case OUT:
            case LOOP_OUT:

                entity.setWidth(minWidth);
                entity.setHeight(minWidth);
                dw = dimensions.getWidth() - minWidth;
                entity.setX(position.getX() + dw);
                entity.setY(position.getY() + dw);

                break;

            default: throw new AssertionError();
        }
    }
      
    public static class Builder implements IBuilder<ZoomAnimation>
    {
        private final Type type;
        private final IEntity entity;
        
        private int wait = 0;
        private int duration = -1;
        private int minWidth = -1;
        private int maxWidth = -1;
        private int speedIn = 8;
        private int speedOut = 8;
        
        public Builder(Type type, IEntity entity)
        {
            assert type   != null;
            assert entity != null;
            
            this.type = type;
            this.entity = entity;
            this.minWidth = 0;
            this.maxWidth = entity.getWidth();
        }
        
        public Builder wait(int val) { wait = val; return this; }
        public Builder duration(int val) { duration = val; return this; }        
        public Builder minWidth(int val) { minWidth = val; return this; }
        public Builder maxWidth(int val) { maxWidth = val; return this; }
        public Builder speedIn(int val) { speedIn = val; return this; }
        public Builder speedOut(int val) { speedOut = val; return this; }
        public Builder speed(int val) { speedIn = val; speedOut = val; return this; }

        public ZoomAnimation build()
        {
            return new ZoomAnimation(this);
        }                
    }

    public void nextFrame()
    {     
        // Make sure we've set the started flag.
        if (!this.started)
        {            
            // Record the initial position.
            determinePosition();
            setStarted();                       
        }
        
        // Check if we're done, if we are, return.
        if (this.finished)
        {
            //LogManager.recordMessage("Move finished!");
            return;
        }
       
        // Add to counter.
        ticks++;
        totalTicks++;
        
        // Convert to ms.
        int ms      = ticks      * Settings.getMillisecondsPerTick();
        int totalMs = totalTicks * Settings.getMillisecondsPerTick();
              
        // See if the wait has expired.
        int dx = 0;
        int w  = 0;      
        
        // See if the duration has expired.
        if (duration > 0 && totalMs > wait + duration)   
                setFinished();   
        
        if (totalMs > wait)
        {
            switch (type)
            {                
                case IN:
                case LOOP_IN:                                                           
                    
                    dx = (ms * speedIn) / 1000;
                    w = maxWidth - (dx * 2);                    
                    
                    w = (w <= minWidth) ? minWidth : w;
                    entity.setWidth(w);
                    entity.setHeight(w);
                    entity.setX(position.getX() + dx);
                    entity.setY(position.getY() + dx);
                    
                    if (entity.getWidth() == minWidth)                    
                    {                                               
                        switch (type)
                        {
                            case IN:
                                setFinished();
                                break;
                                
                            case LOOP_IN:
                                ticks = 0;                                
                                type = Type.LOOP_OUT;
                                break;
                                
                            default: throw new AssertionError();
                        }                                                                                                        
                    } // end if
                                       
                    break;
                    
                case OUT:
                case LOOP_OUT:                                        
                    
                    dx = (ms * speedOut) / 1000;                    
                    w = minWidth + (dx * 2);                    
                    
                    w = (w >= maxWidth) ? maxWidth : w;
                    entity.setWidth(w);
                    entity.setHeight(w);
                    entity.setX(position.getX() + (dimensions.getWidth()  - minWidth) / 2 - dx);
                    entity.setY(position.getY() + (dimensions.getHeight() - minWidth) / 2 - dx);
                    
                    if (entity.getWidth() == maxWidth)                    
                    {         
                        // Make sure they're at the right spot.
                        entity.setX(position.getX());
                        entity.setY(position.getY());
                        
                        switch (type)
                        {
                            case OUT:
                                setFinished();
                                break;
                                
                            case LOOP_OUT:
                                ticks = 0;
                                type = Type.LOOP_IN;
                                break;
                                
                            default: throw new AssertionError();
                        }                                                                                                        
                    } // end if
                    
                    break;
                                                        
                default: throw new AssertionError();
            }                       
        } // end if            
    }

    @Override
    public void cleanUp()
    {
        switch (type)
        {
            case LOOP_IN:
            case LOOP_OUT:
                
                // Resize entity to original dimensions.
                entity.setWidth(dimensions.getWidth());
                entity.setHeight(dimensions.getHeight());

                // Move back to original position.
                entity.setX(position.getX());
                entity.setY(position.getY());

                // Mark the animation as done.
                setFinished();
                
                break;
                
            case IN:
            case OUT:
                
                break;
                
            default: throw new AssertionError();
        }
    }    

    public int getSpeedIn()
    {
        return speedIn;
    }

    public void setSpeedIn(int speedIn)
    {
        this.speedIn = speedIn;
    }

    public int getSpeedOut()
    {
        return speedOut;
    }

    public void setSpeedOut(int speedOut)
    {
        this.speedOut = speedOut;
    }        
    
    public void speed(int val)
    {
        setSpeedIn(val);
        setSpeedOut(val);
    }

}
