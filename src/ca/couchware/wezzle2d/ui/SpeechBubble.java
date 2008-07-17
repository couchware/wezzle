/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.util.*;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;

/**
 * A class for creating speech bubbles.  This is usually used for popping up
 * "click here" bubbles in the tutorials.
 * 
 * @author cdmckay
 */
public class SpeechBubble extends GraphicEntity
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
    protected static EnumMap<BubbleType, Integer> offsetList;        
    
    /**
     * The label type.
     */
    protected BubbleType type;
    
    /**
     * The label representing the text in the speech bubble.
     */
    protected Label label;
    
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
    public SpeechBubble(int x, int y, BubbleType type, String text)
    { 
        // Invoke super.
        super(x, y,
                Game.SPRITES_PATH + "/"
                + "SpeechBubble" + type + ".png");
        
        // Set the type.
        this.type = type;
        
        // Set alignment.
        switch (type)
        {
            case VERTICAL:
                
                super.setAlignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER));
                
                label = ResourceFactory.get()
                        .getLabel(x, y - offsetList.get(type));
                label.setAlignment(
                        EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
                
                break;
                
            case HORIZONTAL:
                
                super.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.RIGHT));
                
                label = ResourceFactory.get()
                        .getLabel(x - offsetList.get(type), y);
                label.setAlignment(
                        EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
                
                break;
        }
      
        // Create the speech label.        
        label.setSize(16);        
        label.setColor(Game.TEXT_COLOR);
        label.setText(text);
        
        //label.translate(offsetX, offsetY);               
    }
    
    @Override
    public void setAlignment(EnumSet<Alignment> alignment)
    {
        if (this.alignment == alignment)
            return;                
        
        if (alignment.containsAll(EnumSet.of(Alignment.TOP, Alignment.CENTER)))
        {
            // Do nothing.
        }
        else if (alignment.containsAll(EnumSet.of(Alignment.MIDDLE, Alignment.RIGHT)))
        {
            
        }
        else if (alignment.containsAll(EnumSet.of(Alignment.MIDDLE, Alignment.RIGHT)))
        {
            
        }
        else
        {
            throw new UnsupportedOperationException(
                "That alignment is not supported.");
        }        
    }        
    
    @Override
    public void draw()
    {
        super.draw();
        label.draw();
    }              
}
