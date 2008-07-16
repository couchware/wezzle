/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.util.*;
import java.awt.Dimension;
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
     * The normal speech bubble.
     */
    final public static int TYPE_NORMAL = 0;
    
    /**
     * The speech bubble dimensions.
     */
    final protected static Dimension[] DIMENSIONS = new Dimension[]
    {
        new Dimension(147, 61) // TYPE_NORMAL        
    };
    
    /**
     * The center of the bubble part of the speech bubble.
     */
    final protected static XYPosition[] CENTER = new XYPosition[]
    {
        new XYPosition(74, 40) // TYPE_NORMAL
    };
    
    /**
     * The label type.
     */
    protected int type;
    
    /**
     * The label representing the text in the speech bubble.
     */
    protected Label label;
    
    /**
     * Creates a new speech bubble of the given type with the given text
     * in it.
     * 
     * @param x
     * @param y
     * @param type
     * @param text
     */    
    public SpeechBubble(int x, int y, int type, String text)
    { 
        // Invoke super.
        super(x, y,
                Game.SPRITES_PATH + "/"
                + "SpeechBubble_" + DIMENSIONS[type].width
                + "x" + DIMENSIONS[type].height + ".png");
        
        // Set the type.
        this.type = type;
        
        // Set alignment.
        super.setAlignment(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER));
        
        // Create the speech label.
        label = ResourceFactory.get().getLabel(                
                x, y - CENTER[type].y);
        label.setSize(16);
        label.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));
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
            label.translate(0, +CENTER[type].y * 2 - 2);
            setRotation(Math.toRadians(180));
            super.setAlignment(alignment);
        }
        else if (alignment.containsAll(EnumSet.of(Alignment.BOTTOM, Alignment.CENTER)))
        {
            label.translate(0, -CENTER[type].y * 2 + 2);
            setRotation(Math.toRadians(-180));
            super.setAlignment(alignment);
        }
        else
        {
            throw new UnsupportedOperationException(
                "Only TOP | CENTER and BOTTOM | CENTER are supported.");
        }        
    }        
    
    @Override
    public void draw()
    {
        super.draw();
        label.draw();
    }          
}
