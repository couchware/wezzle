package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.LayerManager;
import ca.couchware.wezzle2d.graphics.Entity;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.util.XYPosition;

/**
 * An animation that starts an explosion in the middle of the entity.
 * 
 * @author cdmckay
 */
public class FloatFadeOutAnimation extends Animation
{  
    /**
     * The speed of the float.
     */
    final static protected double SPEED = 0.03;
    
    /**
     * The default wait.
     */
    final static protected int DEFAULT_WAIT = 400;
    
    /**
     * The default duration.
     */
    final static protected int DEFAULT_DURATION = 750;
    
    /**
     * Reference to the layer manager.
     */
    final protected LayerManager layerMan;
    
    /**
     * The entity being float faded.
     */
    final protected Entity entity;       
    
     /**
     * The initial position.
     */
    private XYPosition initialPosition;
    
    /**
     * The speed of the float in the x direction.
     */
    final protected double vX;
    
    /**
     * The speed of the float in the y direction.
     */
    final protected double vY;
    
    /**
     * The amount of time, in ms, to wait before fading out.
     */
    protected int wait;
    
    /**
     * The amount of time the entity should spend fading out.
     */
    protected int duration;

    /**
     * Creates a floating text animation centered at (x,y) with the specified
     * text and size.
     * 
     * @param x
     * @param y
     * @param layerMan
     * @param text
     * @param size
     */
    public FloatFadeOutAnimation(
            final int wait,
            final int duration,
            final double dirX, final double dirY,                    
            final LayerManager layerMan,
            final Entity entity)
    {                
        // Invoke super constructor.
        super(0);    
        
        // Set the wait and duration.
        this.wait = wait;
        this.duration = duration;
        
        // Set the speed.
        this.vX = SPEED * dirX;
        this.vY = SPEED * dirY;               
        
        // Set reference to layer manager.
        this.layerMan = layerMan;
        
        // Set a reference to the entity.
        this.entity = entity;                                                     
        
        // Record the initial position.
        initialPosition = entity.getXYPosition();
        
        // Add the floating text to the layer manager.
        layerMan.add(entity, Game.LAYER_EFFECT);
    }   
    
    public FloatFadeOutAnimation(
            final double dirX, final double dirY,                    
            final LayerManager layerMan,
            final Entity entity)            
    {
        this(DEFAULT_WAIT, DEFAULT_DURATION, dirX, dirY, layerMan, entity);
    }

    public void nextFrame(long delta)
    {
        // Check if we're done, if we are, return.
        if (isDone() == true)
            return;
        
        // Add to counter.
        counter += delta;
                                                  
        // Move text.
        double t = (double) counter;
        double dX = vX * t;
        double dY = vY * t;
        
        entity.setX(initialPosition.x + (int) dX);
        entity.setY(initialPosition.y + (int) dY);
        
        // Adjust opacity.
        if (counter > wait)
        {
            entity.setOpacity(
                    100 - Util.scaleInt(0, duration, 0, 100, (int) counter - wait));
        }    
        
        // See if we're done.
        if (counter > wait + duration)   
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
