package ca.couchware.wezzle2d.graphics;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.graphics.Sprite;
import ca.couchware.wezzle2d.graphics.Entity;
import ca.couchware.wezzle2d.animation.Animation;

/**
 * An entity that represents a graphical image, like a button image or a tile
 * image.
 * 
 * @author cdmckay
 */
public class GraphicEntity extends Entity
{
    /** 
     * The sprite that represents this entity.
     */
	protected Sprite sprite;
    
    /**
     * The animation attached to this entity.
     */
    protected Animation animation;		
    
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
	public GraphicEntity(final String path, final int x, final int y)           
	{        
        // Load the sprite.
		this.sprite = ResourceFactory.get().getSprite(path);        		
        
        // Set the position.
        this.x = x;
        this.y = y;
        
        this.x_ = x;
        this.y_ = y;
        
        // Set the dimensions.
        this.width = sprite.getWidth();
        this.height = sprite.getHeight();
                
        this.width_ = width;
        this.height_ = height;
        
        // Set visible.
        setVisible(true);
        
        // Set dirty.
        setDirty(true);
	}
    
    public void draw()
	{
        this.x_ = x + offsetX;
        this.y_ = y + offsetY;
        
        this.width_ = width;
        this.height_ = height;
        
        if (isVisible() == false)
            return;
                        
        sprite.draw(x + offsetX, y + offsetY, 
                width, height, theta, opacity);                
	}     
    
    public Animation getAnimation()
    {
        return animation;
    }

    public void setAnimation(Animation animation)
    {
        this.animation = animation;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }        
    
    /**
     * Advances the animation depending on the amount of time that has passed.
     * 
     * @param delta The amount of time that has passed.
     */
    public void animate(long delta)
    {
        // Ignore if we have no animation.
        if (animation == null || animation.isDone() == true)
            return;
        
        // Pass through to the animation.
        animation.nextFrame(delta);  
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }   
    
}
