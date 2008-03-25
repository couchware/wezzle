package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.XYPosition;

/**
 * A class for representing progress bars of any length 
 * (but a fixed height).
 * 
 * @author cdmckay
 */
public class ProgressBar implements Drawable, Positionable
{

    /**
     * The path to the left sprite.
     */
    final private static String PATH_SPRITE_LEFT = 
            Game.SPRITES_PATH + "/ProgressBar_LeftEnd.png";
    
    /**
     * The path to the right sprite.
     */
    final private static String PATH_SPRITE_RIGHT =
            Game.SPRITES_PATH + "/ProgressBar_RightEnd.png";
    
    /**
     * The path to the middle sprite.
     */
    final private static String PATH_SPRITE_MIDDLE =
            Game.SPRITES_PATH + "/ProgressBar_Middle.png";
    
    /**
     * Is the progress bar visible?
     */
    private boolean visible;
    
    /**
     * Does the progress bar have text?
     */
    private boolean withText;
    
    /**
     * The x-coordinate.
     */
    private int x;
    
    /**
     * The y-coordinate.
     */
    private int y;
    
    /**
     * The left sprite.
     */
    private Sprite leftSprite;
    
    /**
     * The right sprite.
     */
    private Sprite rightSprite;
    
    /**
     * The middle sprite.
     */
    private Sprite middleSprite;          		
	
	/**
	 * The current progress.
	 */
	private int progress;
    
    /**
	 * The progress max.
	 */
	private int progressMax;
	
	/**
	 * The current progress (in width).	 
	 */
	private int progressWidth;
    
    /**
	 * The width at which the clip element is at maximum progress.
	 */
	private int progressWidthMax;
    
    /**
     * Create a progress bar with the top-left coordinate at the position
     * specified.  Optionally may have text under it.
     * 
     * @param x
     * @param y
     * @param withText
     */    
    public ProgressBar(int x, int y, boolean withText)
    {
        // Set coordinates.
        setX(x);
        setY(y);
        
        // Set with-text flag.
        this.withText = withText;
        
        // Load the sprites.
        leftSprite = ResourceFactory.get().getSprite(PATH_SPRITE_LEFT);
        rightSprite = ResourceFactory.get().getSprite(PATH_SPRITE_RIGHT);
        middleSprite = ResourceFactory.get().getSprite(PATH_SPRITE_MIDDLE);
    }
    
    public void draw()
    {
        
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public XYPosition getXYPosition()
    {
        return new XYPosition(x, y);
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

}
