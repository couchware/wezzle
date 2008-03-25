package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.Util;
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
     * The progress text.
     */
    private Text progressText;
    
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
	private int width;
    
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
        
        // Set progress to 0.
        // Set progressMax to 100.
        progress = 4;
        progressMax = 100;        
        width = 100;
        
        // Add text if specified.
		if (withText == true)
		{
			// Set the variable.
			this.withText = withText;
			
			// Create progress text.		
			progressText = ResourceFactory.get().getText();
			
			// Set text attributes.
			progressText.setXYPosition(x, y + 50);
            progressText.setSize(14);
            progressText.setAnchor(Text.VCENTER | Text.HCENTER);
            progressText.setColor(Game.TEXT_COLOR);
            progressText.setText(progress + "/" + progressMax);
		}	
        
        // Load the sprites.
        leftSprite = ResourceFactory.get().getSprite(PATH_SPRITE_LEFT);
        rightSprite = ResourceFactory.get().getSprite(PATH_SPRITE_RIGHT);
        middleSprite = ResourceFactory.get().getSprite(PATH_SPRITE_MIDDLE);
        
        // Initialize.
        setProgress(progress);
    }
    
    public void draw()
    {
        // Draw the text.
        if (withText == true)
            progressText.draw();
        
        // Draw the bar.
        if (progressWidth == 0)
            return;        
        else if (progressWidth <= 8)
        {
            int w = progressWidth / 2;
            
            leftSprite.drawRegion(
                    x, y, 
                    0, 0, 
                    w, leftSprite.getHeight(), 
                    100);                 
            
            rightSprite.drawRegion(                    
                    x + w, y,
                    rightSprite.getWidth() - w, 0,
                    w, rightSprite.getHeight(), 
                    100);
            
            return;
        }
        else
        {
            leftSprite.draw(x, y);
            for (int i = 4; i < progressWidth - 4; i++)
                middleSprite.draw(x + i, y);
            rightSprite.draw(x + progressWidth - 4, y);
        }
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

    /**
	 * @return The progressMax.
	 */
	public int getProgressMax()
	{
		return progressMax;
	}

	/**
	 * @param progressMax The progressMax to set.
	 */
	public void setProgressMax(int progressMax)
	{
		// Update the text, if needed.
		if (withText == true)
			progressText.setText(progress + "/" + progressMax);
		
		// Update the progress.
		this.progressMax = progressMax;
	}
    
    /**
	 * @return The progress.
	 */
	public int getProgress()
	{
		return progress;
	}

	/**
	 * @param progressPercent the progress to set
	 */
	public void setProgress(int progress)
	{
		// Update the text, if needed.
		if (withText == true)
			progressText.setText(progress + "/" + progressMax);
		
		// Update the progress.
		this.progress = progress;	
		this.progressWidth = (int) ((double) width 
                * ((double) progress / (double) progressMax));
		this.progressWidth = progressWidth > width ? width : progressWidth; 
	}
	
	public void increaseProgress(int deltaProgress)
	{
		// Increment the progress.
		setProgress(progress + deltaProgress);				
	}

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }
    
    public int getWidthMax()
    {
        return progressWidth;
    }

    public void setWidthMax(int progressWidth)
    {
        this.progressWidth = progressWidth;
    }    

    public int getHeight()
    {
        return middleSprite.getHeight();
    }

    public void setHeight(int height)
    {
        // Warn.
        Util.handleWarning("Attempted to set height on progress bar.", 
                Thread.currentThread());
    }

    public int getAnchor()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setAnchor(int bitmask)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
