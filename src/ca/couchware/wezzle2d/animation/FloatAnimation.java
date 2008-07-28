package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.LayerManager;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.util.WPosition;

/**
 * An animation that starts an explosion in the middle of the entity.
 * 
 * @author cdmckay
 */
public class FloatAnimation extends AbstractAnimation
{  
    
    /**
     * The default duration.
     * Timed to match the default fade wait/duration so they can be
     * combined easily.
     */
    final static private int DEFAULT_DURATION = 1150;
    
    /**
     * The speed of the float.
     */
    final static private double SPEED = 0.03;       
    
    /**
     * The counter.
     */
    private long counter;
    
    /**
     * Reference to the layer manager.
     */
    final private LayerManager layerMan;
    
    /**
     * The entity being float faded.
     */
    final private IEntity entity;       
    
     /**
     * The initial position.
     */
    private WPosition initialPosition;
    
    /**
     * The speed of the float in the x direction.
     */
    final private double vX;
    
    /**
     * The speed of the float in the y direction.
     */
    final private double vY;      
    
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
    public FloatAnimation(            
            final int duration,
            final double dirX, final double dirY,                    
            final LayerManager layerMan,
            final IEntity entity)
    {                
        // Invoke super constructor.
        super();    
        
        // Set the wait and duration.
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
    
    public FloatAnimation(
            final double dirX, final double dirY,                    
            final LayerManager layerMan,
            final IEntity entity)            
    {
        this(DEFAULT_DURATION, dirX, dirY, layerMan, entity);
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
        
        entity.setX(initialPosition.getX() + (int) dX);
        entity.setY(initialPosition.getY() + (int) dY);                            
        
        // See if we're done.
        if (counter > duration)   
        {
            setDone(true);   
            layerMan.remove(entity, Game.LAYER_EFFECT);
        }
    }
    
    @Override
    public void setVisible(final boolean visible)
    {
        super.setVisible(visible);
        
        if (entity != null)
            entity.setVisible(visible);
    }
    
}
