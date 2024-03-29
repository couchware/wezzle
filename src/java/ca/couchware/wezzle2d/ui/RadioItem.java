/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.util.IBuilder;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.graphics.EntityGroup;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.util.ImmutableRectangle;
import java.awt.Color;
import java.util.EnumSet;

/**
 * A class for creating radio items for use in a RadioGroup.  See RadioGroup 
 * documentation for more details.
 * 
 * @author cdmckay
 */
public class RadioItem extends AbstractButton
{
    
    /** The default color. */
    private static Color defaultColor = Color.RED;
    
    /**
     * Change the default color for all sprite buttons.
     * 
     * @param The new color.
     */
    public static void setDefaultColor(Color color)
    { defaultColor = color; }    
    
    /** The path to the on sprite. */
    final public static String PATH_ON = 
            Settings.getSpriteResourcesPath() + "/" + "RadioOn.png";
    
    /** The path to the off sprite. */
    final public static String PATH_OFF = 
            Settings.getSpriteResourcesPath() + "/" + "RadioOff.png";  
    
    /** The path to the on sprite (hover). */
    final public static String PATH_HOVER_ON = 
            Settings.getSpriteResourcesPath() + "/" + "RadioHoverOn.png";
    
    /** The path to the off sprite (hover). */
    final public static String PATH_HOVER_OFF = 
            Settings.getSpriteResourcesPath() + "/" + "RadioHoverOff.png";  
    
    /**
     * An entity group holding all the radio graphics.
     */
    final private EntityGroup radioGraphic;
    
    /**
     * The sprite for the radio button being off.
     */
    final private GraphicEntity radioOn;
    
    /**
     * The sprite for the radio button being on.
     */
    final private GraphicEntity radioOff;
    
    /**
     * The sprite for the radio button being off (hover).
     */
    final private GraphicEntity radioHoverOn;
    
    /**
     * The sprite for the radio button being on (hover)
     */
    final private GraphicEntity radioHoverOff;        

    /**
     * The color of the text.
     */
    private Color color;
    
    /**
     * The label associated with the radio item.
     */
    private ITextLabel label;
    
    /**
     * The text that will appear in the label.
     */
    private String text;
    
    /**
     * The text size.
     */
    private int textSize;
    
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
        super(builder.win, builder.x, builder.y);
        
        // Set various trivia.
        this.color = builder.color;
        this.opacity = builder.opacity;
        this.visible = builder.visible;
        
        // Set the text.
        this.text = builder.text;
        this.textSize = builder.textSize;
        
        // Set the state and pad.
        this.state = builder.state;
        this.pad = builder.pad;
        
        // Create the sprites. 
        radioOn = new GraphicEntity.Builder(0, 0, PATH_ON)
                .alignment(EnumSet.of(Alignment.LEFT, Alignment.MIDDLE)).build();
        radioOff = new GraphicEntity.Builder(radioOn)
                .path(PATH_OFF).build();
        
        radioHoverOn = new GraphicEntity.Builder(0, 0, PATH_HOVER_ON)
                .alignment(EnumSet.of(Alignment.LEFT, Alignment.MIDDLE)).build();
        radioHoverOff = new GraphicEntity.Builder(radioHoverOn)
                .path(PATH_HOVER_OFF).build();
        
        radioGraphic = new EntityGroup(radioOn, radioOff, 
                radioHoverOn, radioHoverOff);
        
        // Make sure that all radio graphics are the same width and height.
        radioGraphic.setWidth(radioOn.getWidth());
        radioGraphic.setHeight(radioOn.getHeight());              
        
        // Create the label.
        label = new ResourceFactory.LabelBuilder(0, 0)  
                .alignment(EnumSet.of(Alignment.LEFT, Alignment.MIDDLE))
                .color(color).text(text)
                .visible(visible).opacity(opacity).size(textSize).build();
            
        // Adjust the height so that it'll include the label.
        // Set the width and height.
        this.width = radioGraphic.getWidth() + pad + label.getWidth();        
        this.width_ = width;        
        
        this.height = (radioGraphic.getHeight() > label.getHeight()) 
                ? radioGraphic.getHeight() : label.getHeight();
        this.height_ = height;
        
        // If the height is the the same as the label, then the label is 
        // taller than the radio sprite, so we set the X to be at the passed X.
        radioGraphic.setX(x);
        radioGraphic.setY(y + height / 2);               
        label.setX(x + radioOn.getWidth() + pad);
        label.setY(y + height / 2);
        
        // Determine the offsets.
        this.alignment = builder.alignment;
        this.offsetX = determineOffsetX(alignment, width);
        this.offsetY = determineOffsetY(alignment, height);                    
        
        // Set the shape.
        this.shape = new ImmutableRectangle(x + offsetX, y + offsetY, width, height);
        
        // Adjust the label with the offsets.
        radioGraphic.translate(offsetX, offsetY);       
        label.translate(offsetX, offsetY);
    }
    
    public static class Builder implements IBuilder<RadioItem>
    {      
        // Optional values.
        private IWindow win;
        private int x = 0;
        private int y = 0;
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);        
        private Color color = defaultColor;
        private int opacity = 100;
        private boolean visible = true;
        private EnumSet<State> state = EnumSet.noneOf(State.class);
        private String text = "";
        private int textSize = 20;
        private int pad = 10;        
        
        public Builder(IWindow win)
        { 
            this.win = win;
        }
        
        public Builder(RadioItem radioItem)
        {
            this.win = radioItem.win;
            this.x = radioItem.x;
            this.y = radioItem.y;            
            this.alignment = radioItem.alignment.clone();            
            this.color = radioItem.color;
            this.opacity = radioItem.opacity;                        
            this.visible = radioItem.visible;
            this.state = radioItem.state.clone();
            this.text = radioItem.text;
            this.textSize = radioItem.textSize;
            this.pad = radioItem.pad;
        }
        
        public Builder x(int val) { x = val; return this; }        
        public Builder y(int val) { y = val; return this; }
               
        public Builder alignment(EnumSet<Alignment> val) 
        { alignment = val; return this; }
        
        public Builder color(Color val)
        { color = val; return this; }
                        
        public Builder opacity(int val)
        { opacity = val; return this; }
        
        public Builder visible(boolean val) 
        { visible = val; return this; }
        
        public Builder state(EnumSet<State> val)       
        { state = val; return this; }
        
        public Builder text(String val)
        { text = val; return this; } 
        
        public Builder textSize(int val)
        { textSize = val; return this; } 
        
        public Builder pad(int val)
        { pad = val; return this; }
        
        public RadioItem build()
        {
            RadioItem item = new RadioItem(this);
            
            if (visible == true)
                item.win.addMouseListener(item);
            
            return item;
        }                
    }
    
    @Override
    public void setX(int x)
    {
        super.setX(x);
        radioGraphic.setX(x + offsetX);
        label.setX(x + offsetX + radioGraphic.getWidth() + pad);
    }
    
    @Override
    public void setY(int y)
    {
        super.setY(y);
        radioGraphic.setY(y + offsetY + height / 2);
        label.setY(y + offsetY + height / 2);
    }

    public Color getColor()
    {
        return color;
    }
    
    @Override
    public boolean draw()
    {
        x_ = x;
        y_ = y;
        
        // Don't draw if not visible.
        if (visible == false)
            return false;

        // See what state we're in.        
        if (state.contains(State.PRESSED))        
            drawPressed();
        else if (state.contains(State.HOVERED))
            drawHovered();
        else if (state.contains(State.ACTIVATED))
            drawActivated();
        else
            drawNormal();               
        
        label.draw();
        
        return true;
    }          
    
    private void drawNormal()
    {
        radioOff.draw();
    }
    
    private void drawActivated()
    {
        radioOn.draw();
    }      
    
    private void drawHovered()
    {
        if (isActivated() == true)
        {
            radioHoverOn.draw();
        }
        else
        {
            radioHoverOff.draw();
        }
    }
    
    private void drawPressed()
    {
        drawHovered();
    }
    
    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        label.setVisible(visible);
    }
    
}
