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
        
        for (AbstractEntity e : entities)
            e.setVisible(visible);
    }  
        
    /**
	 * Get the x location of this entity.
	 * 
	 * @return The x location of this entity.
	 */
    @Override
	public int getX()
	{
		throw new UnsupportedOperationException("Operation is not supported.");
	}
    
	/**
	 * @param x The x to set.
	 */
    @Override
	public void setX(final int x)
	{                            
		throw new UnsupportedOperationException("Operation is not supported.");
	}

	/**
	 * Get the y location of this entity.
	 * 
	 * @return The y location of this entity.
	 */
    @Override
	public int getY()
	{               
		throw new UnsupportedOperationException("Operation is not supported.");
	}
    
    /**
	 * @param y The y to set.
	 */
    @Override
	public void setY(final int y)
	{
		throw new UnsupportedOperationException("Operation is not supported.");
	}    
    
    @Override
    public void translate(final int dx, final int dy)
    {
        for (AbstractEntity e : entities)
            e.translate(dx, dy);
    }
    
    @Override
    public int getHeight()
    {
        throw new UnsupportedOperationException("Operation is not supported.");
    }

    @Override
    public void setHeight(int height)
    {
        throw new UnsupportedOperationException("Operation is not supported.");
    }

    @Override
    public int getWidth()
    {
        throw new UnsupportedOperationException("Operation is not supported.");
    }

    @Override
    public void setWidth(int width)
    {
        throw new UnsupportedOperationException("Operation is not supported.");
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
        
        for (AbstractEntity e : entities)
            e.setOpacity(opacity);
    }       
    
    /**
     * Rotates the image by theta.
     */
    @Override
    public void setRotation(double theta)
    {
        super.setRotation(theta);
        
        for (AbstractEntity e : entities)
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
        
        for (AbstractEntity e : entities)
            e.setDirty(dirty);
    }  
    
    @Override
    public Rectangle getDrawRect()
    {
        drawRect = entities[0].getDrawRect();
                        
        for (AbstractEntity e : entities)
            drawRect.add(e.getDrawRect());
        
        return drawRect;
    }

    @Override
    public void resetDrawRect()
    {
        for (AbstractEntity e : entities)
            e.resetDrawRect();
    }
    
}
