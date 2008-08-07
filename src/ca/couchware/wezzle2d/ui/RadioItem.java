/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.GameWindow;
import ca.couchware.wezzle2d.IBuilder;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.graphics.ISprite;
import java.awt.Rectangle;
import java.util.EnumSet;

/**
 * A class for creating radio items for use in a RadioGroup.  See RadioGroup 
 * documentation for more details.
 * 
 * @author cdmckay
 */
public class RadioItem extends AbstractSpriteButton
{
    /** The path to the on sprite. */
    final public static String PATH_ON = 
            Game.SPRITES_PATH + "/" + "RadioOn.png";
    
    /** The path to the off sprite. */
    final public static String PATH_OFF = 
            Game.SPRITES_PATH + "/" + "RadioOff.png";  
    
    /** The path to the on sprite (hover). */
    final public static String PATH_HOVER_ON = 
            Game.SPRITES_PATH + "/" + "RadioHoverOn.png";
    
    /** The path to the off sprite (hover). */
    final public static String PATH_HOVER_OFF = 
            Game.SPRITES_PATH + "/" + "RadioHoverOff.png";  
    
    /**
     * The sprite for the radio button being off.
     */
    final private ISprite spriteOn;
    
    /**
     * The sprite for the radio button being on.
     */
    final private ISprite spriteOff;
    
    /**
     * The sprite for the radio button being off (hover).
     */
    final private ISprite spriteHoverOn;
    
    /**
     * The sprite for the radio button being on (hover)
     */
    final private ISprite spriteHoverOff;

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
        super(builder.window, builder.x, builder.y);
        
        // Set the text.
        this.text = builder.text;
        this.textSize = builder.textSize;
        
        // Set the state and pad.
        this.state = builder.state;
        this.pad = builder.pad;
        
        // Create the sprites.
        spriteOn = ResourceFactory.get().getSprite(PATH_ON);
        spriteOff = ResourceFactory.get().getSprite(PATH_OFF);
        spriteHoverOn = ResourceFactory.get().getSprite(PATH_HOVER_ON);
        spriteHoverOff = ResourceFactory.get().getSprite(PATH_HOVER_OFF);
        
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
        
        // Adjust the height so that it'll include the label.
        this.height = label.getHeight();
        this.height_ = height;
        
        // Set the shape.
        this.shape = new Rectangle(x + offsetX, y + offsetY, width, height);
        
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
        private State state = State.NORMAL;
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
        x_ = x;
        y_ = y;
        
        // Don't draw if not visible.
        if (isVisible() == false)
            return;

        // See what state we're in.
        switch (state)
        {
            case NORMAL:
                drawNormal();
                break;
                
            case ACTIVE:
                drawActive();
                break;
                
            case HOVER:
                drawHover();
                break;
                
            case PRESSED:
                drawPressed();                
                break;                           
                
            default:
                throw new AssertionError();
        } // end switch                
        
        label.draw();
    }     
    
    private void drawNormal()
    {
        spriteOff.draw(x + offsetX, y + offsetY, 
                        spriteOn.getWidth(), spriteOn.getHeight(),
                        0, opacity);
    }
    
    private void drawActive()
    {
        spriteOn.draw(x + offsetX, y + offsetY, 
                        spriteOn.getWidth(), spriteOn.getHeight(),
                        0, opacity);
    }      
    
    private void drawHover()
    {
        if (isActivated() == true)
        {
            spriteHoverOn.draw(x + offsetX, y + offsetY, 
                        spriteOn.getWidth(), spriteOn.getHeight(),
                        0, opacity);
        }
        else
        {
            spriteHoverOff.draw(x + offsetX, y + offsetY, 
                        spriteOn.getWidth(), spriteOn.getHeight(),
                        0, opacity);
        }
    }
    
    private void drawPressed()
    {
        drawHover();
    }
    
    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        label.setVisible(visible);
    }
    
}
