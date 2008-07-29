package ca.couchware.wezzle2d.graphics;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.animation.IAnimation;
import java.util.EnumSet;

/**
 * An entity that represents a graphical image, like a button image or a tile
 * image.
 * 
 * @author cdmckay
 */
public class GraphicEntity extends AbstractEntity
{
    
    /**
     * The path to the sprite.
     */
    protected String path;
    
    /** 
     * The sprite that represents this entity.
     */
	protected ISprite sprite;
    
    /**
     * The animation attached to this entity.
     */
    protected IAnimation animation;		        
    
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
	protected GraphicEntity(Builder builder)           
	{        
        // Load the sprite.
		this.sprite = ResourceFactory.get().getSprite(builder.path);        		
                
        // Set the position.
        this.x = builder.x;
        this.y = builder.y;
        
        this.x_ = x;
        this.y_ = y;
        
        // Set the dimensions.
        this.width = sprite.getWidth();
        this.height = sprite.getHeight();
                
        this.width_ = width;
        this.height_ = height;
        
        // Set the alignment.
        this.alignment = builder.alignment;
        
        // Set the opacity.
        this.opacity = limitOpacity(builder.opacity);
        
        // Set the offsets.        
        offsetX = determineOffsetX(alignment);
        offsetY = determineOffsetY(alignment);
        
        // Set visible.
        setVisible(builder.visible);
        
        // Set dirty.
        dirty = true;
	}
    
    public static class Builder implements IBuilder<GraphicEntity>
    {
        // Required values.        
        private int x;
        private int y;     
        private String path;
        
        // Optional values.
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);
        private IAnimation animation = null;
        private int opacity = 100;
        private boolean visible = true;
        
        public Builder(int x, int y, String path)
        {            
            this.x = x;
            this.y = y;
            this.path = path;
        }
        
        public Builder(GraphicEntity entity)
        {
            this.path = entity.path;
            this.x = entity.x;
            this.y = entity.y;
            this.alignment = entity.alignment;
            this.animation = entity.animation;
            this.opacity = entity.opacity;                        
            this.visible = entity.visible;
        }
        
        public Builder x(int val) { x = val; return this; }        
        public Builder y(int val) { y = val; return this; }
        
        public Builder path(String val)                
        { path = val; return this; }
        
        public Builder alignment(EnumSet<Alignment> val) 
        { alignment = val; return this; }
        
        public Builder animation(IAnimation val)
        { animation = val; return this; }
        
        public Builder opacity(int val)
        { opacity = val; return this; }
        
        public Builder visible(boolean val) 
        { visible = val; return this; }
        
        public GraphicEntity end()
        {
            return new GraphicEntity(this);
        }                
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
    
    public IAnimation getAnimation()
    {
        return animation;
    }

    public void setAnimation(IAnimation animation)
    {
        this.animation = animation;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }               
    
}
