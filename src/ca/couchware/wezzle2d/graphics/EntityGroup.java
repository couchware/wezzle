/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.graphics;

import ca.couchware.wezzle2d.util.CouchLogger;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Wraps a group of entities, allowing certain operations to be applied
 * in parallel.
 * 
 * @author cdmckay
 */
public class EntityGroup extends AbstractEntity
{
        
    protected List<IEntity> entityList;        
    
    public EntityGroup(IEntity ... entities)
    {
        // Remember the entities.
        this.entityList = Arrays.asList(entities);
    }
    
    public EntityGroup(List<? extends IEntity> entityList)
    {
        // Remember the entities.
        this.entityList = new ArrayList<IEntity>(entityList);
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
        
        for (IEntity e : entityList)
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
        
        for (IEntity e : entityList)
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
        
        for (IEntity e : entityList)
            e.setY(y);
        
        this.dirty = true;
	}    
    
    @Override
    public void translate(final int dx, final int dy)
    {
        for (IEntity e : entityList)
            e.translate(dx, dy);
        
        this.dirty = true;
    }       

    @Override
    public void setHeight(int height)
    {
        super.setHeight(height);
        
        for (IEntity e : entityList)
            e.setHeight(height);
        
        this.dirty = true;
    }   

    @Override
    public void setWidth(int width)
    {
        super.setWidth(width);
        
        for (IEntity e : entityList)
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
        
        for (IEntity e : entityList)
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
        
        for (IEntity e : entityList)
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
        
        for (IEntity e : entityList)                    
            if (e.draw() == true) updated = true;        
        
        return updated;
    }

    @Override
    public EnumSet<Alignment> getAlignment()
    {
        return alignment;
    }      
    
    private Rectangle EMPTY_RECTANGLE = new Rectangle();
    
    @Override
    public Rectangle getDrawRect()
    {
        if (entityList.size() == 0)
            return EMPTY_RECTANGLE;
        
        drawRect = entityList.get(0).getDrawRect();
                        
        for (IEntity e : entityList)
            drawRect.add(e.getDrawRect());
        
        return drawRect;
    }

    @Override
    public void resetDrawRect()
    {        
        for (IEntity e : entityList)
            e.resetDrawRect();
    }
    
    public int size()
    {
        return entityList.size();
    }
    
}
