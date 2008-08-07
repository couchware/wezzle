/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.GameWindow;
import ca.couchware.wezzle2d.IBuilder;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.graphics.ISprite;
import java.util.EnumSet;

/**
 * A class for creating radio items for use in a RadioGroup.  See RadioGroup 
 * documentation for more details.
 * 
 * @author cdmckay
 */
public class RadioItem extends AbstractEntity
{
    /** The path to the on sprite. */
    final public static String PATH_ON = Game.SPRITES_PATH + "/" + "RadioOn.png";
    
    /** The path to the off sprite. */
    final public static String PATH_OFF = Game.SPRITES_PATH + "/" + "RadioOff.png";
    
    /**
     * The possible states of the radio item.
     */
    public static enum State
    {
        ON, OFF
    }
    
    /**
     * The window the radio item is drawing to.
     */
    private GameWindow window;    
    
    /**
     * The state of the radio button.
     */
    private State state;
    
    /**
     * The sprite for the radio button being off.
     */
    private ISprite spriteOn;
    
    /**
     * The sprite for the radio button being on.
     */
    private ISprite spriteOff;

    /**
     * The label associated with the radio item.
     */
    private ILabel label;
    
    /**
     * The text that will appear in the label.
     */
    private String text;
    
    /**
     * The text size.
     */
    private float textSize;
    
    /**
     * The amount of space between the radio sprite and the label.
     */
    private int pad;
    
    /**
     * Creates a new radio item at the given coordinates.
     * 
     * @param builder The builder object containing all the needed parameters.
     * @throws IllegalStateException if the on and off sprite do not have the
     * same dimensions.
     */
    private RadioItem(Builder builder)
    {
        // Set the game window and coordinates.
        this.window = builder.window;
        this.x = builder.x;
        this.y = builder.y;
        this.x_ = x;
        this.y_ = y;
        
        // Set the text.
        this.text = builder.text;
        this.textSize = builder.textSize;
        
        // Set the state and pad.
        this.state = builder.state;
        this.pad = builder.pad;
        
        // Create the sprite.
        spriteOn = ResourceFactory.get().getSprite(PATH_ON);
        spriteOff = ResourceFactory.get().getSprite(PATH_OFF);
        
        // Make sure that both sprites are the same width and height.
        if ((spriteOn.getWidth() != spriteOff.getWidth())
                || (spriteOn.getHeight() != spriteOff.getHeight()))
        {
            throw new IllegalStateException(
                    "The on and off sprites have different dimensions.");
        }                                
        
        // Create the label.
        label = new ResourceFactory.LabelBuilder(
                x + spriteOn.getWidth() + pad,
                y + spriteOn.getHeight())
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))
                .cached(false).color(Game.TEXT_COLOR).text(text)
                .visible(visible).opacity(opacity).size(textSize).end();                
        
        // Set the width and height.
        this.width = spriteOn.getWidth() + pad + label.getWidth();
        this.height = spriteOn.getHeight();
        this.width_ = width;
        this.height_ = height;
        
        // Determine the offsets.
        this.alignment = builder.alignment;
        this.offsetX = determineOffsetX(alignment);
        this.offsetY = determineOffsetX(alignment);    
        
        // Adjust the label with the offsets.
        label.translate(offsetX, offsetY);
    }
    
    public static class Builder implements IBuilder<RadioItem>
    {
        // Required values.  
        private final GameWindow window;
        private int x;
        private int y;     
        
        // Optional values.
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);        
        private int opacity = 100;
        private boolean visible = true;
        private State state = State.OFF;
        private String text = "";
        private float textSize = 20f;
        private int pad = 10;        
        
        public Builder(GameWindow window, int x, int y)
        {            
            this.window = window;
            this.x = x;
            this.y = y;
        }
        
        public Builder(RadioItem radioItem)
        {            
            this.window = radioItem.window;
            this.x = radioItem.x;
            this.y = radioItem.y;
            this.alignment = radioItem.alignment;            
            this.opacity = radioItem.opacity;                        
            this.visible = radioItem.visible;
            this.state = radioItem.state;
            this.text = radioItem.text;
            this.textSize = radioItem.textSize;
            this.pad = radioItem.pad;
        }
        
        public Builder x(int val) { x = val; return this; }        
        public Builder y(int val) { y = val; return this; }
               
        public Builder alignment(EnumSet<Alignment> val) 
        { alignment = val; return this; }
                        
        public Builder opacity(int val)
        { opacity = val; return this; }
        
        public Builder visible(boolean val) 
        { visible = val; return this; }
        
        public Builder state(State val)       
        { state = val; return this; }
        
        public Builder text(String val)
        { text = val; return this; } 
        
        public Builder textSize(float val)
        { textSize = val; return this; } 
        
        public Builder pad(int val)
        { pad = val; return this; }
        
        public RadioItem end()
        {
            return new RadioItem(this);
        }                
    }
    
    @Override
    public void draw()
    {
        switch (state)
        {
            case ON:
                spriteOn.draw(x + offsetX, y + offsetY, 
                        spriteOn.getWidth(), spriteOn.getHeight(),
                        0, opacity);
                break;
                
            case OFF:
                spriteOff.draw(x + offsetX, y + offsetY, 
                        spriteOn.getWidth(), spriteOn.getHeight(),
                        0, opacity);
                break;
                
            default: throw new AssertionError();
        }
        
        label.draw();
    }        
    
}
