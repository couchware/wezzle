package ca.couchware.wezzle2d;

import java.awt.Rectangle;

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
 * @author Kevin Glass
 */
public abstract class Entity implements Drawable
{
	/** The current x location of this entity */
	protected double x;
	
	/** The current y location of this entity */
	protected double y;
	
	/** The sprite that represents this entity */
	protected Sprite sprite;
	
	/** The current speed of this entity horizontally (pixels/s). */
	protected double dx;
	
	/** The current speed of this entity vertically (pixels/s). */
	protected double dy;
	
	/** The rectangle used for this entity during collisions resolution */
	private Rectangle me = new Rectangle();
	
	/** The rectangle used for other entities during collision resolution */
	private Rectangle him = new Rectangle();

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
	public Entity(String path, int x, int y) 
	{
		this.sprite = ResourceFactory.get().getSprite(path);
		this.x = x;
		this.y = y;
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

	/**
	 * Draw this entity to the graphics context provided.
	 */
	public void draw()
	{
		sprite.draw((int) x, (int) y);
	}

	/**
	 * Do the logic associated with this entity. This method will be called
	 * periodically based on game events.
	 */
	public void doLogic()
	{
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
}