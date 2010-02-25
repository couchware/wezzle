/*
 *  Wezzle
 *  Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.graphics;

import ca.couchware.wezzle2d.IGraphics;
import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.util.IBuilder;
import ca.couchware.wezzle2d.util.CouchColor;
import java.awt.Color;
import java.util.EnumSet;

/**
 * Create an ellipse primitive.
 * @author cdmckay
 */
public class Ellipse extends AbstractEntity
{
    private final IWindow win;
    private final IGraphics graphics;
    private final boolean filled;
    private Color color;

    private Ellipse(Builder builder)
    {
        // Set graphics reference.
        this.win = builder.win;
        this.graphics = win.getGraphics();
        this.filled = builder.filled;

        // Set the position.
        this.x = builder.x;
        this.y = builder.y;

        this.x_ = x;
        this.y_ = y;

        // Set the dimensions.
        this.width = builder.width;
        this.height = builder.height;
        this.width_ = width;
        this.height_ = height;

        // Set the opacity and disabledness.
        this.opacity = builder.opacity;
        this.disabled = builder.disabled;
        this.visible = builder.visible;

        // Set default anchor.
        this.alignment = builder.alignment;
        this.offsetX = determineOffsetX(alignment, width);
        this.offsetY = determineOffsetY(alignment, height);

        // Create the background colour.  This colour is changed each time
        // the opacity is changed.
        this.color = CouchColor.newInstance(builder.color, (builder.opacity * 255) / 100).toColor();
    }

    public static class Builder implements IBuilder<Ellipse>
    {
        // Required values.
        private IWindow win;
        private int x;
        private int y;       

        // Optional values.
        private int width = 100;
        private int height = 100;
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);
        private int opacity = 100;
        private boolean visible = true;
        private boolean disabled = false;
        private boolean filled = false;
        private Color color = Color.BLACK;
        
        public Builder(IWindow win, int x, int y)
        {
            this.win = win;
            this.x = x;
            this.y = y;
        }

        public Builder(Ellipse ellipse)
        {
            this.win = ellipse.win;
            this.x = ellipse.x;
            this.y = ellipse.y;
            this.alignment = ellipse.alignment.clone();
            this.width = ellipse.width;
            this.height = ellipse.height;
            this.visible = ellipse.visible;
            this.opacity = ellipse.opacity;
            this.disabled = ellipse.disabled;
            this.filled = ellipse.filled;           
            this.color = ellipse.color;
        }

        public Builder x(int val) { x = val; return this; }
        public Builder y(int val) { y = val; return this; }

        public Builder width(int val) { width = val; return this; }
        public Builder height(int val) { height = val; return this; }

        public Builder alignment(EnumSet<Alignment> val)
        { alignment = val; return this; }

        public Builder opacity(int val)
        { opacity = val; return this; }

        public Builder visible(boolean val)
        { visible = val; return this; }

        public Builder disabled(boolean val)
        { disabled = val; return this; }

        public Builder filled(boolean val)
        { filled = val; return this; }

        public Builder color(Color val)
        { color = val; return this; }

        public Ellipse build()
        {
            Ellipse ellipse = new Ellipse(this);
            return ellipse;
        }
    }

    @Override
    public boolean draw()
    {
        CouchColor oldColor = this.graphics.getColor();
        this.graphics.setColor(this.color);

        if (this.filled)
        {
            graphics.fillEllipse(
                    this.x + this.offsetX,
                    this.y + this.offsetY,
                    this.width,
                    this.height);
        }
        else
        {
            graphics.drawEllipse(
                    this.x + this.offsetX,
                    this.y + this.offsetY,
                    this.width,
                    this.height);
        }
        
        this.graphics.setColor(oldColor);
        return true;
    }

}
