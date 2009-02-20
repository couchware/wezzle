/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.util.ImmutableRectangle;

/**
 * A tall button.
 * 
 * @author cdmckay
 */
public class TallButton extends Button
{

    /** The left sprite. */
    final private static String LEFT_SPRITE_PATH = Settings.getSpriteResourcesPath() 
            + "/Button_Tall_Left" 
            + FILE_EXT;
    
    /** The middle sprite. */
    final private static String MIDDLE_SPRITE_PATH = Settings.getSpriteResourcesPath() 
            + "/Button_Tall_Middle" 
            + FILE_EXT;
    
    /** The right sprite. */
    final private static String RIGHT_SPRITE_PATH = Settings.getSpriteResourcesPath() 
            + "/Button_Tall_Right" 
            + FILE_EXT;
    
    private TallButton(Builder builder)
    {
        super(builder);
        
        // Load the sprites.
        leftSprite   = ResourceFactory.get().getSprite(LEFT_SPRITE_PATH);      
        middleSprite = ResourceFactory.get().getSprite(MIDDLE_SPRITE_PATH);      
        rightSprite  = ResourceFactory.get().getSprite(RIGHT_SPRITE_PATH);  
        
        // Set the height.
        this.height = middleSprite.getHeight();
                               
        // Validate the width.
        if (!validateWidth())
            throw new RuntimeException("The button width is too narrow.");
        
        // Recalulate offests.
        this.offsetX = determineOffsetX(alignment, width);
        this.offsetY = determineOffsetY(alignment, height);
        
        // Set the shape.
        this.shape = new ImmutableRectangle(x + offsetX, y + offsetY, width, height);
    }        
    
    public static class Builder extends Button.Builder
    {
        public Builder(int x, int y)
        {
            super(x, y);            
        }
        
        @Override
        public Button end()
        {
            TallButton button = new TallButton(this);
            
            if (visible == true && disabled == false)
                button.window.addMouseListener(button);           
            
            return button;
        }
    }
    
}
