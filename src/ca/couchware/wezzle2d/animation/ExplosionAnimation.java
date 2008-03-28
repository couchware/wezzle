package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.*;
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
     * The explode down state.
     */
    final private static int EXPLODE_DOWN = 0;
    
    /**
     * The explode up state.
     */
    final private static int EXPLODE_UP = 1;
    
    /**
     * The entity being animated.
     */
    protected Entity entity;
    
    /**
     * Reference to the layer manager.
     */
    final private LayerManager layerMan;
    
    /**
     * The maximum width the entity may become before switching 
     * explode states.
     */
    final private int maxWidth;
    
    /**
     * The current pulse state.
     */
    private int state;
    
    /**
     * The explosion entity.
     */
    final Entity explosion;   
    
    /**
     * The constructor.
     */
    public ExplosionAnimation(final Entity entity, final LayerManager layerMan)
    {                
        // Invoke super constructor.
        super(FRAME_PERIOD);    
        
        // Save a reference to the entity.
        this.entity = entity;
        
        // Set reference to layer manager.
        this.layerMan = layerMan;
        
        // Load the explosion and centre it over the entity.
        explosion = new Entity(PATH, 0, 0);    
        explosion.setOpacity(50);
        
        // Set the initial pulse state.
        state = EXPLODE_UP;
         
        // Set the maximum width.
        maxWidth = explosion.getWidth();
        
        // Resize the explosion to be 2x2.
        explosion.setWidth(2);
        explosion.setHeight(2);
        
        // Move it to the centre of the entity.
        explosion.setX(entity.getX() + (entity.getWidth() / 2) - 1);
        explosion.setY(entity.getY() + (entity.getHeight() / 2) - 1);
        
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
        
        // Set the number of frames that have passed to 0.
        frames = 0;
        
        // See how many frames have passed.
        while (counter >= period)
        {
            frames++;
            counter -= period;
        }                
        
        // Advance the number of frames.
        for (int i = 0; i < frames; i++)
        {                 
            // If we're pulsing down, reducing the size and translate slightly.
            switch (state)
            {
                case EXPLODE_UP:
                    
                    explosion.setWidth(explosion.getWidth() + EXPAND_STEP);
                    explosion.setX(explosion.getX() - EXPAND_STEP / 2);
                    
                    explosion.setHeight(explosion.getHeight() + EXPAND_STEP);
                    explosion.setY(explosion.getY() - EXPAND_STEP /2);
                    
                    // If the width is equal to the maximum, then
                    // change states.
                    if (explosion.getWidth() == maxWidth)
                        state = EXPLODE_DOWN;
                    
                    break;
                
                case EXPLODE_DOWN:
                    
                    explosion.setWidth(explosion.getWidth() - CONTRACT_STEP);
                    explosion.setX(explosion.getX() + CONTRACT_STEP / 2);
                    
                    explosion.setHeight(explosion.getHeight() - CONTRACT_STEP);
                    explosion.setY(explosion.getY() + CONTRACT_STEP / 2);  
                    
                    // If the width is equal to 2, then we stop.
                    if (explosion.getWidth() <= 2)
                    {
                        // Remove explosion from layer manager.
                        layerMan.remove(explosion, Game.LAYER_EFFECT);
                        
                        // Set done flag.
                        done = true;
                        
                        // Break.
                        break;
                    }
                    
                    break;
                    
                default:
                    Util.handleMessage("Unrecognized state.", 
                            Thread.currentThread());
            }
        } // end if          
    }
    
    public void setVisible(final boolean visible)
    {
        this.visible = visible;
        
        if (explosion != null)
            explosion.setVisible(visible);
    }
}
