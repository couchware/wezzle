package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.animation.Animation;

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
public class Entity implements Drawable
{
    /** 
     * Is this visible? 
     */
    protected boolean visible;
    
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
    protected double x;
	
	/** 
     * The current y location of this entity .
     */
	protected double y;
    
    /** 
     * The current width of the entity.
     */
    protected int width;
    
    /**
     * The current height of the entity.
     */
    protected int height;
	
	/** 
     * The sprite that represents this entity.
     */
	protected Sprite sprite;
    
    /**
     * The animation attached to this entity.
     */
    protected Animation animation;
	
	/** 
     * The current speed of this entity horizontally (pixels/s). 
     */
	protected double dx;
	
	/** 
     * The current speed of this entity vertically (pixels/s). 
     */
	protected double dy;

	/**
	 * Construct a entity based on a sprite image and a location.
	 * 
	 * @param path
	 *            The reference to the image to be displayed for this entity
	 * @param x
	 *            The initial x location of this entity
	 * @param y
	 *            The initial y location of this entity
	 */
	public Entity(final String path, final int x, final int y)           
	{
        this.visible = true;
		this.sprite = ResourceFactory.get().getSprite(path);
		this.x = x;
		this.y = y;
        this.width = sprite.getWidth();
        this.height = sprite.getHeight();
	}

	/**
	 * Request that this entity move itself based on a certain amount of time
	 * passing.
	 * 
	 * @param delta
	 *            The amount of time that has passed in milliseconds.
	 */
	public void move(long delta)
	{
		// Update the location of the entity based on move speeds.
		x += (delta * dx) / 1000;
		y += (delta * dy) / 1000;
	}	
	
	/**
	 * Set the horizontal speed of this entity
	 * 
	 * @param dx
	 *            The horizontal speed of this entity (pixels/s).
	 */
	public void setXMovement(double dx)
	{
		this.dx = dx;
	}

	/**
	 * Set the vertical speed of this entity
	 * 
	 * @param dy
	 *            The vertical speed of this entity (pixels/s).
	 */
	public void setYMovement(double dy)
	{
		this.dy = dy;
	}

	/**
	 * Get the horizontal speed of this entity
	 * 
	 * @return The horizontal speed of this entity (pixels/s).
	 */
	public double getXMovement()
	{
		return dx;
	}

	/**
	 * Get the vertical speed of this entity
	 * 
	 * @return The vertical speed of this entity (pixels/ms).
	 */
	public double getYMovement()
	{
		return dy;
	}
	
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public boolean isVisible()
    {
        return visible;
    }
    
	/**
	 * @param x The x to set.
	 */
	public void setX(int x)
	{
		this.x = x;
	}

	/**
	 * @param y The y to set.
	 */
	public void setY(int y)
	{
		this.y = y;
	}

	/**
	 * Get the x location of this entity.
	 * 
	 * @return The x location of this entity.
	 */
	public int getX()
	{
		return (int) x;
	}

	/**
	 * Get the y location of this entity.
	 * 
	 * @return The y location of this entity.
	 */
	public int getY()
	{
		return (int) y;
	}

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

   /**
     * Sets the opacity of the sprite (in percent).
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
    }
    
     /**
     * Gets the opacity of the sprite.
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
	public void draw()
	{
        if (isVisible() == false)
            return;
                        
        sprite.draw((int) x, (int) y, width, height, theta, opacity);
	}       

    public Animation getAnimation()
    {
        return animation;
    }

    public void setAnimation(Animation animation)
    {
        this.animation = animation;
    }        
    
    /**
     * Advances the animation depending on the amount of time that has passed.
     * 
     * @param delta The amount of time that has passed.
     */
    public void animate(long delta)
    {
        // Ignore if we have no animation.
        if (animation == null)
            return;
        
        // Pass through to the animation.
        animation.nextFrame(delta);        
    }
}