package ca.couchware.wezzle2d.animation;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.Util;

/**
 * An animation that starts an explosion in the middle of the entity.
 * 
 * @author cdmckay
 */
public class FloatTextAnimation extends Animation
{    
    /**
     * Reference to the layer manager.
     */
    final private LayerManager layerMan;
    
    /**
     * The period of each frame.
     */
    final private static int FRAME_PERIOD = 15;

    /**
     * The amount of movement, per frame, in the X-direction.
     */
    final private static int X_STEP = 0;        

    
    /**
     * The amount of movement, per frame, in the Y-direction.
     */
    final private static int Y_STEP = -4;        
    
    /**
     * The number of frames to stay opaque.
     */
    final private static int OPAQUE_FRAME_MAX = 4;
    
    /**
     * The amount of opacity to reduce each step.
     */
    final private static int OPACITY_STEP = 12;
    
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
     * The constructor.
     */
    public FloatTextAnimation(final Entity entity, final LayerManager layerMan,
            final String text, final float size)
    {                
        // Invoke super constructor.
        super(entity, FRAME_PERIOD);    
        
        // Set reference to layer manager.
        this.layerMan = layerMan;
        
        // Load the explosion and centre it over the entity.
        floatText = ResourceFactory.get().getText();
        floatText.setX(entity.getX() + entity.getWidth() / 2);
        floatText.setY(entity.getY() + entity.getHeight() / 2);
        floatText.setAnchor(Text.VCENTER | Text.HCENTER);
        floatText.setColor(Game.TEXT_COLOR);
        floatText.setText(text);
        floatText.setSize(size);
                        
        // Set the initial pulse state.
        state = STATE_OPAQUE;
        
        // Initialize opaque frame count.
        opaqueFrameCount = 0;
        
        // Add the floating text to the layer manager.
        layerMan.add(floatText, 1);
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
            frame++;            
            
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
                                                           
                    // If the width is equal to 2, then we stop.
//                    if (floatText <= 2)
//                    {
//                        // Remove explosion from layer manager.
//                        layerMan.remove(explosion, 1);
//                        
//                        // Set done flag.
//                        done = true;
//                    }
                    
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
