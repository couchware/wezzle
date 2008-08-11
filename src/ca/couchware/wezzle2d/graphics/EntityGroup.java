/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.graphics;

import java.awt.Rectangle;
import java.util.EnumSet;

/**
 * Wraps a group of entities, allowing certain operations to be applied
 * in parallel.
 * 
 * @author cdmckay
 */
public class EntityGroup extends AbstractEntity
{
        
    protected AbstractEntity[] entities;        
    
    public EntityGroup(AbstractEntity ... entities)
    {
        // Remember the entities.
        this.entities = entities;
    }
    
    /**
     * Sets the visibility of the entity.
     * 
     * @param visible
     */
    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        
        for (IEntity e : entities)
            e.setVisible(visible);
    }            
    
	/**
	 * @param x The x to set.
	 */
    @Override
	public void setX(final int x)
	{                            
		super.setX(x);
        
        for (IEntity e : entities)
            e.setX(x);
	}
    
    /**
	 * @param y The y to set.
	 */
    @Override
	public void setY(final int y)
	{
		super.setX(y);
        
        for (IEntity e : entities)
            e.setY(y);
	}    
    
    @Override
    public void translate(final int dx, final int dy)
    {
        for (IEntity e : entities)
            e.translate(dx, dy);
    }       

    @Override
    public void setHeight(int height)
    {
        super.setHeight(height);
        
        for (IEntity e : entities)
            e.setHeight(height);
    }   

    @Override
    public void setWidth(int width)
    {
        super.setWidth(width);
        
        for (IEntity e : entities)
            e.setWidth(width);
    }

    /**
     * Sets the opacity of the sprite (in percent).
     * 
     * @param opacity The opacity.
     */
    @Override
    public void setOpacity(final int opacity)
    {       
        super.setOpacity(opacity);
        
        for (IEntity e : entities)
            e.setOpacity(opacity);
    }       
    
    /**
     * Rotates the image by theta.
     */
    @Override
    public void setRotation(double theta)
    {
        super.setRotation(theta);
        
        for (IEntity e : entities)
            e.setRotation(theta);
    }        
    
    /**
	 * Draw this entity to the graphics context unless it is not visible
     * or an animation is attached (the animation will handle the drawing).
	 */
    public void draw()
    {
        for (AbstractEntity e : entities)
            e.draw();
    }

    @Override
    public EnumSet<Alignment> getAlignment()
    {
        return alignment;
    }   

    @Override
    public void setDirty(boolean dirty)
    {
        super.setDirty(dirty);
        
        for (IEntity e : entities)
            e.setDirty(dirty);
    }  
    
    @Override
    public Rectangle getDrawRect()
    {
        drawRect = entities[0].getDrawRect();
                        
        for (IEntity e : entities)
            drawRect.add(e.getDrawRect());
        
        return drawRect;
    }

    @Override
    public void resetDrawRect()
    {
        for (IEntity e : entities)
            e.resetDrawRect();
    }
    
}
