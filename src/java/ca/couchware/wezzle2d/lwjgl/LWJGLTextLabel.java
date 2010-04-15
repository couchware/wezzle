/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */
package ca.couchware.wezzle2d.lwjgl;

import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.util.NumUtil;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class LWJGLTextLabel extends AbstractEntity implements ITextLabel
{

    /**
     * The game window to which this text is going to be drawn 
     */
    final private LWJGLWindow win;

    /**
     * The size of the label.
     */
    final private int size;

    /**
     * The text of the label.
     */
    private String text;

    /**
     * The color of the label.
     */
    private Color color;

    /**
     * The font. 
     */
    final private TrueTypeFont font;

    public LWJGLTextLabel(
            LWJGLWindow win,
            int x, int y,
            EnumSet<Alignment> alignment,
            Color color,
            int opacity,
            int size,
            String text,
            boolean visible)
    {
        this.win = win;
        this.x = x;
        this.x_ = x;
        this.y = y;
        this.y_ = y;
        this.alignment = alignment;
        this.color = color;
        this.size = size;
        this.text = text;
        this.visible = visible;

        // Set the opacity.
        this.setOpacity(opacity);

        // Get the font.
        this.font = win.getFontStore().getFont(size, win.getTextureLoader());

        // Set width and height based on the font.
        this.width = font.stringWidth(text);
        this.height = font.getHeight();

        // Determine the offset.
        this.offsetX = determineLabelOffsetX(alignment);
        this.offsetY = determineLabelOffsetY(alignment);
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public int getSize()
    {
        return size;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;

        // Set width and height based on the font.
        this.width = font.stringWidth(text);
        this.height = font.getHeight();

        // Determine the offset.
        this.offsetX = determineLabelOffsetX(alignment);
        this.offsetY = determineLabelOffsetY(alignment);
    }

    public boolean isCached()
    {
        return false;
    }

    protected int determineLabelOffsetX(EnumSet<Alignment> alignment)
    {
        if (alignment.contains(Alignment.LEFT))
        {
            return 0;
        }
        else if (alignment.contains(Alignment.CENTER))
        {
            return -width / 2;
        }
        else if (alignment.contains(Alignment.RIGHT))
        {
            return -width;
        }
        else
        {
            throw new IllegalStateException("No X alignment set!");
        }
    }

    protected int determineLabelOffsetY(EnumSet<Alignment> alignment)
    {
        if (alignment.contains(Alignment.BOTTOM))
        {
            return 0;
        }
        else if (alignment.contains(Alignment.MIDDLE))
        {
            return height / 2;
        }
        else if (alignment.contains(Alignment.TOP))
        {
            return height;
        }
        else
        {
            throw new IllegalStateException("No Y alignment set!");
        }
    }
    /** The black colour used to draw the drop shadow. */
    private Color colorBlack;

    @Override
    public void setOpacity(int opacity)
    {
        super.setOpacity(opacity);
        int alpha = NumUtil.scaleInt(0, 100, 0, 255, opacity);
        this.colorBlack = new Color(0, 0, 0, alpha);
        this.color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    @Override
    public boolean draw()
    {
        if (this.visible == false)
        {
            return true;
        }

        font.drawString(x + offsetX + 1, y + offsetY + 1, text, colorBlack);
        font.drawString(x + offsetX, y + offsetY, text, color);
        return true;
    }
    /** An empty rectangle for use with getDrawRect(). */
    final private static Rectangle EMPTY_RECTANGLE = new Rectangle();

    @Override
    public String toString()
    {
        return "LWJGLLabel with text '" + this.text + "'";
    }
}
