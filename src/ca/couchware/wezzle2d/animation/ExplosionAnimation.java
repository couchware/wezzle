package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
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
    private int ticks;
    
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
    final private int speed;
    
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
        this.speed = SettingsManager.get().getInt(Key.ANIMATION_EXPLOSION_SPEED);
        
        // Load the explosion and centre it over the entity.
        explosion = new GraphicEntity.Builder(0, 0, PATH)
                .opacity(SettingsManager.get().getInt(Key.ANIMATION_EXPLOSION_OPACITY))
                .end();        
        
        // Set the explosion durations.
        duration = ((explosion.getHeight() / 2) * 1000) / speed;        
        
        // Set the initial dimensions to 2x2.
        explosion.setWidth(SettingsManager.get().getInt(Key.ANIMATION_EXPLOSION_INITIAL_WIDTH));
        explosion.setHeight(SettingsManager.get().getInt(Key.ANIMATION_EXPLOSION_INITIAL_HEIGHT));
        this.initialDimensions = new ImmutableDimension(2, 2);
        
        // Move it to the centre of the entity.
        explosion.setX(entity.getX() + (entity.getWidth()  / 2) - 1);
        explosion.setY(entity.getY() + (entity.getHeight() / 2) - 1);                                      
        this.initialPosition = explosion.getPosition();                                                            
        
        // Reset the draw rectangle.
        explosion.resetDrawRect();
        explosion.setDirty(true);
        
        // Add explosion to the layer manager.
        layerMan.add(explosion, Layer.EFFECT);
    }

    public void nextFrame()
    {        
        // Make sure we've set the started flag.
        setStarted();
        
        // Check if we're done, if we are, return.
        if (this.finished == true)
            return;               
        
        // Add to counter.
        ticks++;
        int ms = ticks * Settings.getMillisecondsPerTick();
        
        // See if we're exploding out.
        if (ms < duration)
        {                                   
            int d = (speed * ms) / 1000;
            explosion.setWidth(  initialDimensions.getWidth()  + d * 2 );
            explosion.setHeight( initialDimensions.getHeight() + d * 2 );
            explosion.setX(initialPosition.getX() - d);
            explosion.setY(initialPosition.getY() - d);
        }
        // See if we're exploding in.
        else if (ms >= duration && ms < duration * 2)
        {
            int t = duration * 2 - ms;
            int d = (speed * t) / 1000;
            explosion.setWidth(  initialDimensions.getWidth()  + d * 2 );
            explosion.setHeight( initialDimensions.getHeight() + d * 2 );
            explosion.setX(initialPosition.getX() - d);
            explosion.setY(initialPosition.getY() - d);
        }
        // See if we're done.
        else if (ms >= duration * 2)
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
