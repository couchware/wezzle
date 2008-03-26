package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.util.XYPosition;
import java.awt.Color;
import java.util.Set;

/**
 * An animation that starts an explosion in the middle of the entity.
 * 
 * @author cdmckay
 */
public class FloatTextAnimation extends Animation
{            
    /**
     * The period of each frame.
     */
    final private static int FRAME_PERIOD = 60;

    /**
     * The amount of movement, per frame, in the X-direction.
     */
    final private static int X_STEP = 0;        

    
    /**
     * The amount of movement, per frame, in the Y-direction.
     */
    final private static int Y_STEP = -1;        
    
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
    final Text floatText;
    
    /**
     * The number of opaque frames that have passed.
     */
    private int opaqueFrameCount;

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
    public FloatTextAnimation(final int x, final int y, 
            final LayerManager layerMan,
            final String text, 
            final Color color,
            final float size)
    {                
        // Invoke super constructor.
        super(FRAME_PERIOD);    
        
        // Set reference to layer manager.
        this.layerMan = layerMan;
        
        // Determine the left-mo
        
        // Load the explosion and centre it over the entity.
        floatText = ResourceFactory.get().getText();
        floatText.setXYPosition(x, y);
        floatText.setAlignment(Text.VCENTER | Text.HCENTER);
        floatText.setColor(color);
        floatText.setSize(size);
        floatText.setText(text);        
        
        Util.handleMessage("Font size is " + size + ".", Thread.currentThread());
                        
        // Set the initial pulse state.
        state = STATE_OPAQUE;
        
        // Initialize opaque frame count.
        opaqueFrameCount = 0;
        
        // Add the floating text to the layer manager.
        layerMan.add(floatText, Game.LAYER_EFFECT);
    }
    
    public FloatTextAnimation(final XYPosition p,
            final LayerManager layerMan,
            final String text, 
            final Color color,
            final float size)
    {
        this(p.x, p.y, layerMan, text, color, size);
    }

    public void nextFrame(long delta)
    {
        // Check if we're done, if we are, return.
        if (isDone() == true)
            return;
        
        // Add to counter.
        counter += delta;
        
        // See if enough time has elapsed to advance the frame.
        if (counter >= period)
        {
            // Increase the frame.
            frames++;            
            
            // Remove the period time so the counter will work for ensuing
            // frames.
            counter -= period;
            
            // Move text.
            floatText.setX(floatText.getX() + X_STEP);
            floatText.setY(floatText.getY() + Y_STEP);
            
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
                    floatText.setOpacity(floatText.getOpacity() - OPACITY_STEP);
                    
                    // If the opacity reaches the minimum, stop the animation.
                    if (floatText.getOpacity() == OPACITY_MIN)
                    {
                        // Remove explosion from layer manager.
                        layerMan.remove(floatText, 1);
                        
                        // Set done flag.
                        done = true;
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
        if (floatText != null)
            floatText.setVisible(visible);
    }
    
}
