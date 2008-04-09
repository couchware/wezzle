package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.util.XYPosition;
import java.awt.Color;

/**
 * An animation that starts an explosion in the middle of the entity.
 * 
 * @author cdmckay
 */
public class FloatLabelAnimation extends Animation
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
     * The explosion entity.
     */
    final Label floatLabel;
    
    /**
     * The number of opaque frames that have passed.
     */
    private int opaqueFrameCount;
    
    /**
     * The x step.
     */
    final int stepX;
    
    /**
     * The y step.
     */
    final int stepY;

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
    public FloatLabelAnimation(final int x, final int y, 
            final int stepX, final int stepY,            
            final LayerManager layerMan,
            final String text, 
            final int alignment,
            final Color color,
            final float size)
    {                
        // Invoke super constructor.
        super(FRAME_PERIOD);    
        
        // Set the steps.
        this.stepX = stepX;
        this.stepY = stepY;
        
        // Set reference to layer manager.
        this.layerMan = layerMan;
              
        // Load the explosion and centre it over the entity.
        floatLabel = ResourceFactory.get().getText();
        floatLabel.setXYPosition(x, y);
        floatLabel.setAlignment(alignment);
        floatLabel.setColor(color);
        floatLabel.setSize(size);
        floatLabel.setText(text);   
        
        // Reset the draw rectangle.
        floatLabel.resetDrawRect();               
                        
        // Set the initial pulse state.
        state = STATE_OPAQUE;
        
        // Initialize opaque frame count.
        opaqueFrameCount = 0;
        
        // Add the floating text to the layer manager.
        layerMan.add(floatLabel, Game.LAYER_EFFECT);
    }
    
    public FloatLabelAnimation(final XYPosition p,
            final int stepX, final int stepY,            
            final LayerManager layerMan,            
            final String text, 
            final int alignment,
            final Color color,
            final float size)
    {
        this(p.x, p.y, stepX, stepY, layerMan, text, alignment, color, size);
    }

    public void nextFrame(long delta)
    {
        // Check if we're done, if we are, return.
        if (isDone() == true)
            return;
        
        // Add to counter.
        counter += delta;
        
        // See how many frames have passed.
        frames = (int) counter / period;
        counter = counter % period;
        
        // See if enough time has elapsed to advance the frame.
        animation:
        for (int i = 0; i < frames; i++)
        {                                   
            // Move text.
            floatLabel.setX(floatLabel.getX() + stepX);
            floatLabel.setY(floatLabel.getY() + stepY);
            
            // If we're pulsing down, reducing the size and translate slightly.
            switch (state)
            {
                case STATE_OPAQUE:
                    
                    // Since we're staying opaque, do nothing to the text.
                    opaqueFrameCount++;
                    
                    // See if we're reached the max number of frames.
                    if (opaqueFrameCount == OPAQUE_FRAME_MAX)
                        state = STATE_FADE;
                    
                    break;
                
                case STATE_FADE:
                                                    
                    // Reduce the opacity.
                    floatLabel.setOpacity(floatLabel.getOpacity() - OPACITY_STEP);
                    
                    // If the opacity reaches the minimum, stop the animation.
                    if (floatLabel.getOpacity() == OPACITY_MIN)
                    {
                        // Remove explosion from layer manager.
                        if (layerMan.remove(floatLabel, Game.LAYER_EFFECT) 
                                == false)
                            throw new IllegalStateException(
                                    "Could not remove label from layer.");
                        
                        // Set done flag.
                        done = true;
                        break animation;                       
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
        if (floatLabel != null)
            floatLabel.setVisible(visible);
    }
    
}
