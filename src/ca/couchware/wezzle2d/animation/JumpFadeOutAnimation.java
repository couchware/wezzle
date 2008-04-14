package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.util.XYPosition;

/**
 * An animation that starts an explosion in the middle of the entity.
 * 
 * @author cdmckay
 */
public class JumpFadeOutAnimation extends Animation
{            
    /**
     * The period of each frame.
     */
    final private static int FRAME_PERIOD = 60;      
    
    /**
     * The number of frames to stay opaque.
     */
    final private static int OPAQUE_FRAME_MAX = 8;
    
    /**
     * The amount of opacity to reduce each step.
     */
    final private static int OPACITY_STEP = 8;
    
    /**
     * The minimum opacity before the animation ends.
     */
    final private static int OPACITY_MIN = 0;
    
    /**
     * The state where the text is still opaque.
     */
    final private static int STATE_OPAQUE = 0;
    
    /**
     * The state where the text fades out.
     */
    final private static int STATE_FADE = 1;
       
    /**
     * Reference to the layer manager.
     */
    final private LayerManager layerMan;
    
    /**
     * The current pulse state.
     */
    private int state;
    
    /**
     * The entity being float faded.
     */
    final Entity entity;
    
    /**
     * The number of opaque frames that have passed.
     */
    private int opaqueFrameCount;
    
    /**
     * The initial position.
     */
    private XYPosition initialPosition;
    
    /**
     * The x-component of the launch speed.
     */
    private double vX;
    
    /**
     * The y-component of the launch speed.
     */
    private double vY;
    
    /**
     * The launch angle, in degrees.
     */
    private int theta;
    
    /**
     * The gravity.
     */
    private double g;
    
    /**
     * The max time for the animation to run for, in ms.
     */
    private int duration;

   /**
    * Fire an entity with the given launch speed, angle and gravity and 
    * rotation speed.        
    */
    public JumpFadeOutAnimation(
            final double v,
            final int theta,
            final double g,     
            final int duration,
            final LayerManager layerMan,
            final Entity entity)
    {                
        // Invoke super constructor.
        super(FRAME_PERIOD);    
        
        // Record the initial position.
        initialPosition = entity.getXYPosition();
        
        // Determine the components of the launch velocity.
        vX = v * Math.cos(Math.toRadians(theta));
        vY = v * Math.sin(Math.toRadians(theta));
        
        // Record other values.
        this.theta = theta;
        this.g = g;        
        this.duration = duration;
        
        // Set reference to layer manager.
        this.layerMan = layerMan;
        
        // Set a reference to the entity.
        this.entity = entity;                    
                        
        // Set the initial pulse state.
        state = STATE_OPAQUE;
        
        // Initialize opaque frame count.
        opaqueFrameCount = 0;
        
        // Add the floating text to the layer manager.
        layerMan.add(entity, Game.LAYER_EFFECT);
    }       

    public void nextFrame(long delta)
    {
        // Check if we're done, if we are, return.
        if (isDone() == true)
            return;
              
        // Add delta to counter.  This serves as the time variable.
        counter += delta;                               
            
        // Determine the current x and y.
        double t = (double) counter;
        double x = vX * t;
        double y = vY * t - 0.5 * g * t * t;
        
        // Move the entity.
        entity.setX(initialPosition.x + (int) x);
        entity.setY(initialPosition.y - (int) y);
        entity.setOpacity(
                100 - Util.scaleInt(0, duration, 0, 100, (int) counter));
        
        if (counter > duration)   
        {
            done = true;                    
            layerMan.remove(entity, Game.LAYER_EFFECT);
        }
    }
    
    @Override
    public void setVisible(final boolean visible)
    {
        this.visible = visible;        
        if (entity != null)
            entity.setVisible(visible);
    }
    
}
