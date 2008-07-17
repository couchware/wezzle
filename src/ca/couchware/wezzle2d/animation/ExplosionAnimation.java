package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.LayerManager;
import ca.couchware.wezzle2d.graphics.Entity;
import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.XYPosition;
import java.awt.Dimension;

/**
 * An animation that starts an explosion in the middle of the entity.
 * 
 * @author cdmckay
 */
public class ExplosionAnimation extends Animation
{    
    /**
     * Path to the explosion sprite.
     */
    final private static String PATH = Game.SPRITES_PATH + "/Explosion.png";       
    
    /**
     * The period of each frame.
     */
    final private static int FRAME_PERIOD = 15;
    
    /**
     * The amount the explosion should expand per step.
     */
    final private static int EXPAND_STEP = 4;
    
    /**
     * The amount the explosion should contract per step.
     */
    final private static int CONTRACT_STEP = 4;
    
    /**
     * The entity being animated.
     */
    final protected Entity entity;
    
    /**
     * Reference to the layer manager.
     */
    final protected LayerManager layerMan;        
    
    /**
     * The explosion entity.
     */
    final protected Entity explosion;   
    
    /**
     * The initial position.
     */
    final protected XYPosition initialPosition;
    
    /**
     * The initial dimensions.
     */
    final protected Dimension initialDimensions;
    
    /**
     * Explode out/in duration, in ms.
     */
    final protected int duration;
    
    /**
     * The speed of the explosion.
     */
    final protected double v;
    
    /**
     * The constructor.
     */
    public ExplosionAnimation(final Entity entity, final LayerManager layerMan)
    {                
        // Invoke super constructor.
        super();    
        
        // Save a reference to the entity.
        this.entity = entity;

        // Set reference to layer manager.
        this.layerMan = layerMan;                
        
        // Set the speed.
        this.v = 0.18;
        
        // Load the explosion and centre it over the entity.
        explosion = new GraphicEntity(0, 0, PATH);    
        explosion.setOpacity(50);
        
        // Set the initial dimensions to 2x2.
        explosion.setWidth(2);
        explosion.setHeight(2);
        this.initialDimensions = new Dimension(2, 2);
        
        // Move it to the centre of the entity.
        explosion.setX(entity.getX() + (entity.getWidth() / 2) - 1);
        explosion.setY(entity.getY() + (entity.getHeight() / 2) - 1);                                      
        this.initialPosition = explosion.getXYPosition();                
                        
        // Set the explosion durations.
        duration = 270;           
        
        // Resize the explosion to be 2x2.
        explosion.setWidth(2);
        explosion.setHeight(2);                
        
        // Reset the draw rectangle.
        explosion.resetDrawRect();
        explosion.setDirty(true);
        
        // Add explosion to the layer manager.
        layerMan.add(explosion, Game.LAYER_EFFECT);
    }

    public void nextFrame(long delta)
    {
        // Check if we're done, if we are, return.
        if (isDone() == true)
            return;
        
        // Is there any delay left?
        if (delay > 0)
        {
            // See if this delta will eliminate the delay.
            if (delta > delay)
            {
                // If it will, subtract the remaing delay from the delta.
                delay = 0;
                delta -= delay;
            }
            // Otherwise, subtract delta from delay and we're done.
            else
            {
                delay -= delta;
                return;
            }
        }
        
        // Add to counter.
        counter += delta;                    
        
        // See if we're exploding out.
        if (counter < duration)
        {                        
            double t = (double) counter;
            int d = (int) (v * t);
            explosion.setWidth(initialDimensions.width + d * 2);
            explosion.setHeight(initialDimensions.height + d * 2);
            explosion.setX(initialPosition.x - d);
            explosion.setY(initialPosition.y - d);
        }
        // See if we're exploding in.
        else if (counter >= duration && counter < duration * 2)
        {
            double t = (double) (duration * 2 - counter);
            int d = (int) (v * t);
            explosion.setWidth(initialDimensions.width + d * 2);
            explosion.setHeight(initialDimensions.height + d * 2);
            explosion.setX(initialPosition.x - d);
            explosion.setY(initialPosition.y - d);
        }
        // See if we're done.
        else if (counter >= duration * 2)
        {
            done = true;
            layerMan.remove(explosion, Game.LAYER_EFFECT);
        }                        
    }
    
    @Override
    public void setVisible(final boolean visible)
    {
        this.visible = visible;
        
        if (explosion != null)
            explosion.setVisible(visible);
    }
}
