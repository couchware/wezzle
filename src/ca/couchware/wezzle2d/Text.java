package ca.couchware.wezzle2d;
import java.awt.Color;


/**
 * The Text abstract class is used to draw text to the screen in a particular 
 * font.  The appropriate methods should be updated for each graphics module. 
 * 
 * @author Kevin
 *
 */
public abstract class Text implements Drawable
{
	
	/**
     * Align at the vertical top.
     */
    final public static int TOP = 1;
    
    /**
     * Align at the vertical center.
     */
	final public static int VCENTER = 2;
    
    /**
     * Align at the vertical bottom.
     */
	final public static int BOTTOM = 4;
    
    /**
     * Align at the horizontal left.
     */
	final public static int LEFT = 8;
    
    /**
     * Align at the horizontal centre.
     */
	final public static int HCENTER = 16;
    
    /**
     * Align at the horizontal right.
     */
	final public static int RIGHT = 32;
    
    /**
     * The visibility of the text.
     */
    protected boolean visible;
    
    /**
     * The X-cooridinate of the text.
     */
    protected int x;
    
    /**
     * The Y-coordinate of the text.
     */
    protected int y;
    
    /** 
     * The size of the font.
     */
	protected float size;
	
	/** 
     * The color of the text.
     */
	protected Color color;
    
    /** 
     * The text.
     */
	protected String text;
    
    /**
     * The current anchor. 
     */
	protected int anchor;
    
    /**
     * The current opacity (in percent, from 0 to 100).
     */
    protected int opacity;
	
    /**
     * The constructor.
     */
    public Text()
    {
        // Intentionally empty.
    }    
    
    /**
     * Get the X-coordinate.
     * @return The X-coordinate.
     */
    public int getX()
    {
        return x;
    }
    
    /**
     * Set the X-coordinate.
     * @param x
     */
    public void setX(final int x)
    {
        this.x = x;
    }

    /**
     * Get the Y-coordinate.
     * @return The Y-coordinate.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Set the Y-coordinate.
     * @param y
     */
    public void setY(final int y)
    {
        this.y = y;
    }        
    
    /**
     * Set the XY-coordinates.
     * @param x
     * @param y
     */
    public void setXYPosition(final int x, final int y)
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Gets the text.
     * 
     * @return The text string.
     */
    public String getText()
    {
        return text;
    }
    
	/**
	 * Set the text.
	 * 
	 * @param t The text.
	 */
	public void setText(String text)
    {
        this.text = text;
    }
	
    /**
     * Gets the color of the text.
     * 
     * @return The color of text.
     */
    public float getSize()
    {
        return size;
    }
    
	/**
	 * Set the color of the text.
	 * 
	 * @param size The size.
	 */
	public void setSize(final float size)
    {
        this.size = size;
    }        
	
    /**
     * Get the text color.
     * 
     * @return The current text color. 
     */
    public Color getColor()
    {
        return color;
    }
    
	/**
	 * Set the text color.
	 * The initial color should be black.
	 * 
	 * @param color The color to set to.
	 */
	public void setColor(final Color color)
    {
        this.color = color;
    }
	
    /**
     * Get the current anchor of the text box.
     * 
     * @return The current anchor bitmask.
     */
    public int getAnchor()
    {
        return anchor;
    }
    
	/**
	 * Set the anchor of the text box. The anchor is initially set to the top left. 
	 * 
	 * @param x The x anchor coordinate with respect to the top left corner of the text box.
	 * @param y The y anchor coordinate with respect to the top left corner of the text box.
	 */
	public void setAnchor(final int anchor)
    {
        this.anchor = anchor;
    }
    
    /**
     * Sets the visibility.
     * 
     * @param visible True for visible, false for not.
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    /**
     * Gets the visibility.
     * 
     * @return True for visible, false for not.
     */
    public boolean isVisible()
    {
        return visible;
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
		
}