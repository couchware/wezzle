/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.graphics;

import ca.couchware.wezzle2d.manager.LogManager;
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
        
    protected IEntity[] entities;        
    
    public EntityGroup(IEntity ... entities)
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
        
        this.dirty = true;
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
        
        this.dirty = true;
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
        
        this.dirty = true;
	}    
    
    @Override
    public void translate(final int dx, final int dy)
    {
        for (IEntity e : entities)
            e.translate(dx, dy);
        
        this.dirty = true;
    }       

    @Override
    public void setHeight(int height)
    {
        super.setHeight(height);
        
        for (IEntity e : entities)
            e.setHeight(height);
        
        this.dirty = true;
    }   

    @Override
    public void setWidth(int width)
    {
        super.setWidth(width);
        
        for (IEntity e : entities)
            e.setWidth(width);
        
        this.dirty = true;
    }

    /**
     * Sets the opacity of the sprite (in percent).
     * 
     * @param opacity The opacity.
     */
    @Override
    public void setOpacity(final int opacity)
    {       
        //LogManager.recordMessage("Opacity = " + opacity + ".");
        
        super.setOpacity(opacity);
        
        for (IEntity e : entities)
            e.setOpacity(opacity);
        
        this.dirty = true;                
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
        
        this.dirty = true;
    }        
    
    /**
	 * Draw this entity to the graphics context unless it is not visible
     * or an animation is attached (the animation will handle the drawing).
	 */
    public boolean draw()
    {
        if (visible == false)
            return false;
        
        boolean updated = false;
        
        for (IEntity e : entities)                    
            if (e.draw() == true) updated = true;        
        
        return updated;
    }

    @Override
    public EnumSet<Alignment> getAlignment()
    {
        return alignment;
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
    
    public int size()
    {
        return entities.length;
    }
    
}
