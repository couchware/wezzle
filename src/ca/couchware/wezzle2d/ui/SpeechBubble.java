/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.util.*;
import java.util.EnumMap;
import java.util.EnumSet;

/**
 * A class for creating speech bubbles.  This is usually used for popping up
 * "click here" bubbles in the tutorials.
 * 
 * @author cdmckay
 */
public class SpeechBubble extends AbstractEntity
{
    
    /**
     * The two speech bubble types, vertical or horizontal.
     */
    public static enum BubbleType
    {
        VERTICAL, HORIZONTAL;
        
        @Override
        public String toString()
        {
            String s = super.toString();
            return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        } 
    }
    
    /**
     * The offset array.
     */
    private static EnumMap<BubbleType, Integer> offsetList;        
    
    /**
     * The label type.
     */
    private BubbleType type;
    
    /**
     * The text of the bubble.
     */
    private String text;
    
    /**
     * The bubble sprite.
     */
    private ISprite sprite;
    
    /**
     * The label representing the text in the speech bubble.
     */
    private ILabel label;
    
    /**
     * Static constructor.
     */
    static
    {
        offsetList = new EnumMap<BubbleType, Integer>(BubbleType.class);    
        offsetList.put(BubbleType.VERTICAL, 40);
        offsetList.put(BubbleType.HORIZONTAL, 88);
    }
    
    /**
     * Creates a new speech bubble of the given type with the given text
     * in it.
     * 
     * @param x
     * @param y
     * @param type
     * @param text
     */    
    public SpeechBubble(Builder builder)
    {                 
        // Set the type.
        this.type = builder.type;
        
        // The sprite path.
        String path = Game.SPRITES_PATH + "/" + "SpeechBubble" + type + ".png";
        
        // Create the sprite.
        sprite = ResourceFactory.get().getSprite(path);
        
        // Set the x and y.
        this.x = builder.x;
        this.y = builder.y;
        this.x_ = x;
        this.y_ = y;
        
        // The width and height.
        this.width = sprite.getWidth();
        this.height = sprite.getHeight();
        this.width_ = width;
        this.height_ = height;
        
        // Set opacity.
        this.opacity = limitOpacity(builder.opacity);
        
        // Set visible.
        this.visible = builder.visible;
        
        // Set the text.
        this.text = builder.text;
        
        // Set alignment.
        switch (type)
        {
            case VERTICAL:
                
                alignment = EnumSet.of(Alignment.BOTTOM, Alignment.CENTER);
                                
                label = new LabelBuilder(x, y - offsetList.get(type))
                        .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                        .color(Game.TEXT_COLOR).size(16).text(text)
                        .cached(false).end();
                
                break;
                
            case HORIZONTAL:
                
                alignment = EnumSet.of(Alignment.MIDDLE, Alignment.RIGHT);
                
                label = new LabelBuilder(x - offsetList.get(type), y)
                        .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                        .color(Game.TEXT_COLOR).size(16).text(text).end();
                
                break;
        }             
        
        // Determine offsets.
        offsetX = determineOffsetX(alignment, width);
        offsetY = determineOffsetY(alignment, height);
    }      
    
    public static class Builder implements IBuilder<SpeechBubble>
    {
        // Required values.          
        private int x;
        private int y;     
        
        // Optional values.        
        private int opacity = 100;
        private boolean visible = true;
        private String text = "Default";
        private BubbleType type = BubbleType.VERTICAL;
        
        public Builder(int x, int y)
        {                        
            this.x = x;
            this.y = y;
        }
        
        public Builder(SpeechBubble bubble)
        {                      
            this.x = bubble.x;
            this.y = bubble.y;            
            this.opacity = bubble.opacity;                        
            this.visible = bubble.visible;
            this.text = bubble.text;
            this.type = bubble.type;
        }
        
        public Builder x(int val) { x = val; return this; }        
        public Builder y(int val) { y = val; return this; }                       
                        
        public Builder opacity(int val)
        { opacity = val; return this; }
        
        public Builder text(String val)  
        { text = val; return this; }
        
        public Builder type(BubbleType val)
        { type = val; return this; }                    
        
        public SpeechBubble end()
        {
            return new SpeechBubble(this);
        }                
    }
    
    @Override
    public void setOpacity(int opacity)
    {
        super.setOpacity(opacity);
        if (label != null)
            label.setOpacity(opacity);
    }
    
    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        if (label != null)
            label.setVisible(visible);               
    }
    
    @Override
    public boolean draw()
    {
        x_ = x;
        y_ = y;
        
        width_ = width;
        height_ = height;
        
        if (visible == false)
            return false;
        
        sprite.draw(x + offsetX, y + offsetY, width, height, theta, opacity);
        label.draw();
        
        return true;
    }              
}
