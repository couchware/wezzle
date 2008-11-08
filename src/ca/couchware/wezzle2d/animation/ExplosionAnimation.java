package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.util.ImmutableDimension;
import ca.couchware.wezzle2d.util.ImmutablePosition;

/**
 * An animation that starts an explosion in the middle of the entity.
 * 
 * @author cdmckay
 */
public class ExplosionAnimation extends AbstractAnimation
{    
    
    /**
     * The counter.
     */
    private long counter;
    
    /**
     * Path to the explosion sprite.
     */
    final private static String PATH = Settings.getSpriteResourcesPath() + "/Explosion.png";             
    
    /**
     * The entity being animated.
     */
    final private IEntity entity;
    
    /**
     * Reference to the layer manager.
     */
    final private LayerManager layerMan;        
    
    /**
     * The explosion entity.
     */
    final private IEntity explosion;   
    
    /**
     * The initial position.
     */
    final private ImmutablePosition initialPosition;
    
    /**
     * The initial dimensions.
     */
    final private ImmutableDimension initialDimensions;
    
    /**
     * Explode out/in duration, in ms.
     */
    final private int duration;
    
    /**
     * The speed of the explosion.
     */
    final private double v;
    
    /**
     * The constructor.
     */
    public ExplosionAnimation(final IEntity entity, final LayerManager layerMan)
    {                       
        // Save a reference to the entity.
        this.entity = entity;

        // Set reference to layer manager.
        this.layerMan = layerMan;                
        
        // Set the speed.
        this.v = 0.15;
        
        // Load the explosion and centre it over the entity.
        explosion = new GraphicEntity.Builder(0, 0, PATH).opacity(50).end();        
        
        // Set the explosion durations.
        duration = (int) ((double) (explosion.getHeight() / 2) / v);        
        
        // Set the initial dimensions to 2x2.
        explosion.setWidth(2);
        explosion.setHeight(2);
        this.initialDimensions = new ImmutableDimension(2, 2);
        
        // Move it to the centre of the entity.
        explosion.setX(entity.getX() + (entity.getWidth() / 2) - 1);
        explosion.setY(entity.getY() + (entity.getHeight() / 2) - 1);                                      
        this.initialPosition = explosion.getXYPosition();                                                            
        
        // Reset the draw rectangle.
        explosion.resetDrawRect();
        explosion.setDirty(true);
        
        // Add explosion to the layer manager.
        layerMan.add(explosion, Layer.EFFECT);
    }

    public void nextFrame()
    {
        long delta = 14;
        
        // Make sure we've set the started flag.
        setStarted();
        
        // Check if we're done, if we are, return.
        if (this.finished == true)
            return;               
        
        // Add to counter.
        counter += delta;                    
        
        // See if we're exploding out.
        if (counter < duration)
        {                        
            double t = (double) counter;
            int d = (int) (v * t);
            explosion.setWidth(initialDimensions.getWidth() + d * 2);
            explosion.setHeight(initialDimensions.getHeight() + d * 2);
            explosion.setX(initialPosition.getX() - d);
            explosion.setY(initialPosition.getY() - d);
        }
        // See if we're exploding in.
        else if (counter >= duration && counter < duration * 2)
        {
            double t = (double) (duration * 2 - counter);
            int d = (int) (v * t);
            explosion.setWidth(initialDimensions.getWidth() + d * 2);
            explosion.setHeight(initialDimensions.getHeight() + d * 2);
            explosion.setX(initialPosition.getX() - d);
            explosion.setY(initialPosition.getY() - d);
        }
        // See if we're done.
        else if (counter >= duration * 2)
        {
            setFinished();
            layerMan.remove(explosion, Layer.EFFECT);
        }                        
    }
    
    @Override
    public void setVisible(final boolean visible)
    {
        super.setVisible(visible);
        
        if (explosion != null)
            explosion.setVisible(visible);
    }
}
