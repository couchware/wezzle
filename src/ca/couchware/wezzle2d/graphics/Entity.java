package ca.couchware.wezzle2d.graphics;

import ca.couchware.wezzle2d.util.Util;
import ca.couchware.wezzle2d.util.XYPosition;
import java.awt.Rectangle;
import java.util.EnumSet;

/**
 * An entity represents any element that appears in the game. The entity is
 * responsible for resolving collisions and movement based on a set of
 * properties defined either by subclass or externally.
 * 
 * Note that doubles are used for positions. This may seem strange given that
 * pixels locations are integers. However, using double means that an entity can
 * move a partial pixel. It doesn't of course mean that they will be display
 * half way through a pixel but allows us not lose accuracy as we move.
 * 
 * @author Cameron McKay (based on code by Kevin Glass)
 */
public abstract class Entity implements IEntity
{
    /** 
     * Is this visible? 
     */
    protected boolean visible = true;
    
    /**
     * Is it dirty (i.e. does it need to be redrawn)?
     */
    protected boolean dirty = true;
    
    /**
     * The cached draw rectangle.
     */
    protected Rectangle drawRect;
    
     /**
     * The rotation.
     */
    protected double theta = 0.0;
    
    /**
     * The opacity (in percent).
     */
    protected int opacity = 100;
    
    /** 
     * The current x location of this entity. 
     */
    protected int x = 0;
	
	/** 
     * The current y location of this entity .
     */
	protected int y = 0;
    
    /**
     * The previous x location.
     */
    protected int x_ = x;
    
    /**
     * The previous y location.
     */
    protected int y_ = y;
    
    /** 
     * The current width of the entity.
     */
    protected int width = 0;
    
    /**
     * The current height of the entity.
     */
    protected int height = 0;
    
    /**
     * The previous width.
     */
    protected int width_ = width;
    
    /**
     * The previous height.
     */
    protected int height_ = height;      
    
    /**
     * The alignment of the entity.
     */
    protected EnumSet<Alignment> alignment = 
            EnumSet.of(Alignment.TOP, Alignment.LEFT);
    
    /**
     * The x-offset.
     */
    protected int offsetX = 0;
    
    /**
     * The y-offset.
     */
    protected int offsetY = 0;		

    /**
     * Sets the visibility of the entity.
     * 
     * @param visible
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
        
        // Set dirty so it will be drawn.
        setDirty(true);
    }

    /**
     * Gets the visibility of the entity.
     * 
     * @return
     */
    public boolean isVisible()
    {
        return visible;
    }
        
    /**
	 * Get the x location of this entity.
	 * 
	 * @return The x location of this entity.
	 */
	public int getX()
	{
		return x;
	}
    
	/**
	 * @param x The x to set.
	 */
	public void setX(final int x)
	{                            
		this.x = x;                
        
        // Set dirty so it will be drawn.        
        setDirty(true);
	}

	/**
	 * Get the y location of this entity.
	 * 
	 * @return The y location of this entity.
	 */
	public int getY()
	{               
		return y;
	}
    
    /**
	 * @param y The y to set.
	 */
	public void setY(final int y)
	{
		this.y = y;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
	}
    
    public XYPosition getXYPosition()
    {
        return new XYPosition(getX(), getY());
    }

    public void setXYPosition(int x, int y)
    {
        setX(x);
        setY(y);
    }

    public void setXYPosition(XYPosition p)
    {
        setX(p.x);
        setY(p.y);
    }
    
    public void translate(final int dx, final int dy)
    {
        setX(this.x + dx);
        setY(this.y + dy);
    }
    
    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }

    /**
     * Sets the opacity of the sprite (in percent).
     * 
     * @param opacity The opacity.
     */
    public void setOpacity(final int opacity)
    {       
        if (opacity < 0)
            this.opacity = 0;
        else if (opacity > 100)
            this.opacity = 100;
        else
            this.opacity = opacity;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }
    
    /**
     * Gets the opacity of the sprite.
     * 
     * @return The opacity.
     */
    public int getOpacity()
    {
        return opacity;
    }
    
    /**
     * Rotates the image by theta.
     */
    public void setRotation(double theta)
    {
        this.theta = theta;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }
    
    /**
     * Gets the current theta.
     */
    public double getRotation()
    {
        return this.theta;
    }
    
    /**
	 * Draw this entity to the graphics context unless it is not visible
     * or an animation is attached (the animation will handle the drawing).
	 */
    public abstract void draw();    	

    public EnumSet<Alignment> getAlignment()
    {
        return alignment;
    }

    public void setAlignment(final EnumSet<Alignment> alignment)
    {
        // Remember the anchor.
		this.alignment = alignment;               
				
		// The Y anchors.
		if (alignment.contains(Alignment.BOTTOM))
		{
			this.offsetY = -height;
		}
		else if (alignment.contains(Alignment.MIDDLE))
		{
			this.offsetY = -height / 2;
		}
		else if (alignment.contains(Alignment.TOP))
		{
			this.offsetY = 0;
		}
		else
		{
			Util.handleWarning("No Y alignment set!", "Entity#setAlignment");
		}
		
		// The X anchors. 
		if (alignment.contains(Alignment.LEFT))
		{
			this.offsetX = 0;
		}
		else if (alignment.contains(Alignment.CENTER))
		{
			this.offsetX = -width / 2;			
		}
		else if (alignment.contains(Alignment.RIGHT))
		{
			this.offsetX = -width;
		}
		else
		{
			Util.handleWarning("No X alignment set!", "Entity#setAlignment");
		}	
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }

    public void setDirty(boolean dirty)
    {
        this.dirty = dirty;
        
        // Set draw rect to null so it'll be regenerated.
        this.drawRect = null;
    }

    public boolean isDirty()
    {
        return dirty;
    }
    
    public Rectangle getDrawRect()
    {
        // Check if the draw rect is null.  If it is, generate a new one.
        if (drawRect == null)
        {        
            Rectangle rect1 = new Rectangle(x_, y_, width_ + 2, height_ + 2);                
            Rectangle rect2 = new Rectangle(x, y, width + 2, height + 2);        

            rect2.translate(offsetX, offsetY);            
            rect1.add(rect2);
            rect2 = null;
            
            drawRect = rect1;
        }
        else
        {
//            Util.handleMessage("Using cached draw-rect.", 
//                    Thread.currentThread());
        }
        
        return drawRect;
    }

    public void resetDrawRect()
    {
        x_ = x;
        y_ = y;
        
        width_ = width;
        height_ = height;
    }
    
    /**
     * This method should be run whenever you are done with an entity to
     * clean up things like animations and resources.
     */
    public void dispose()
    {
        // Release the draw rectangle.
        drawRect = null;
        
        // Release the alignment.
        alignment = null;        
    }
    
}