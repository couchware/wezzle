/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.lwjgl;

import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class LWJGLLabel extends AbstractEntity implements ILabel
{

    /**
     * The text in the baseline text layout.
     */
    final private static String baselineText = "A";
    
//    /** 
//     * The game window to which this text is going to be drawn 
//     */
//	final private Java2DGameWindow window;	       
    
    /**
     * The color of the label.
     */
    final private Color color;
    
    /**
     * The size of the label.
     */
    final private float size;
    
    /**
     * The text of the label.
     */
    final private String text;
    
//	/** 
//     * The font. 
//     */
//	final private Font font;         
//    
//    /**
//     * The baseline text layout.
//     */
//    final private TextLayout baselineLayout;
//    
//    /**
//     * The text layout instance.
//     */
//	final private TextLayout textLayout;		
//    
//    /**
//     * Should the text layout be cached?
//     */
//    final private boolean cached;
    
    public LWJGLLabel(int x, int y,
            EnumSet<Alignment> alignment,
            Color color,
            int opacity,
            float size,
            String text,
            boolean visible)
    {
        this.x  = x;
        this.x_ = x;
        this.y  = y;
        this.y_ = y;        
        this.alignment = alignment;
        this.color = color;
        this.opacity = limitOpacity(opacity);
        this.size = size;
        this.text = text;
        this.visible = visible;
    }  

    public Color getColor()
    {
        return color;
    }

    public float getSize()
    {
        return size;
    }

    public String getText()
    {
        return text;
    }

    public boolean isCached()
    {
        return false;
    }

    @Override
    public boolean draw()
    {
        return true;
    }
    
}
