/*
 * Wezzle
 * Copyight (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.audio.Sound;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.ui.Box.Border;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * A class representing an abstract notification.
 * @author Cameron McKay
 */
public abstract class AbstractNotification extends AbstractEntity implements INotification
{
    /** The width of the notification. */
    final protected static int WIDTH = 230;

    /** The height of the noticiation. */
    final protected static int HEIGHT = 104;

    /**
     * The window that box is in.  This is for adding and removing
     * the certain listeners.
     */
    final protected IWindow window;
 
    /** The box part of the notification. */
    final protected Box box;

    /** The list of entities on the achievement. */
    final List<IEntity> entityList;
      
    public AbstractNotification(IWindow window, int x, int y,
            int opacity, boolean visible, EnumSet<Alignment> alignment)
    {
        // Save the reference.
        this.window = window;

        // Set the x and y.
        this.x  = x;
        this.y  = y;
        this.x_ = x;
        this.y_ = y;

        // Set the width and height.
        this.width   = WIDTH;
        this.height  = HEIGHT;
        this.width_  = this.width;
        this.height_ = this.height;

        // Set various other values.
        this.opacity = opacity;
        this.visible = visible;

        // Set default anchor.
        this.alignment = alignment;
        this.offsetX = determineOffsetX(alignment, width);
        this.offsetY = determineOffsetY(alignment, height);

         // Create the box.
        this.box = new Box.Builder(x + offsetX, y + offsetY)
                .border(Border.MEDIUM)
                .width(this.width).height(this.height)
                .opacity(this.opacity)
                .build();

        this.entityList = new ArrayList<IEntity>();
        this.entityList.add(this.box);
    }

    @Override
    public void setOpacity(int opacity)
    {
        super.setOpacity(opacity);
        for (IEntity entity : entityList)
        {
            entity.setOpacity(opacity);
        }        
    }

    @Override
    public void setX(int x)
    {
        int dx = x - this.x;
        super.setX(x);
        for (IEntity entity : entityList)
        {            
            entity.translate(dx, 0);
        }
    }

    @Override
    public void setY(int y)
    {
        int dy = y - this.y;
        super.setY(y);
        for (IEntity entity : entityList)
        {
            entity.translate(0, dy);
        }
    }

    @Override
    public void setWidth(int width)
    {
        throw new UnsupportedOperationException("Achievement notification width is immutable.");
    }

    @Override
    public void setHeight(int height)
    {
        throw new UnsupportedOperationException("Achievement notification width is immutable.");
    }

    @Override
    public boolean draw()
    {
        boolean ret = false;
        for (IEntity entity : entityList)
        {
            // Use | instead of || so it doesn't short circuit.
            ret |= entity.draw();
        }
        return ret;
    }

    public Sound getAssociatedSound()
    {
        return null;
    }

}
