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
     * Is it dirty (i.e. does it need to be redrawn)?
     */
    private boolean dirty;
    
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
     * The alignment bitmask.
     */
    private int alignment;
    
    /**
     * The x offset.
     */
    private int offsetX;
    
    /**
     * The y offset.
     */
    private int offsetY;
    
    /**
     * Create a progress bar with the top-left coordinate at the position
     * specified.  Optionally may have text under it.
     * 
     * @param x
     * @param y
     * @param withText
     */    
    public ProgressBar(int x, int y, int width, boolean withText)
    {
        // Set coordinates.
        setX(x);
        setY(y);   
        
        // Set progress to 0.
        // Set progressMax to 100.
        this.progress = 0;
        this.progressMax = 100;        
        this.width = width;
        
        // Set initial alignment.
        this.alignment = TOP | LEFT;
        
        // Add text if specified.
		if (withText == true)
		{
			// Set the variable.
			this.withText = withText;
			
			// Create progress text.		
			progressText = ResourceFactory.get().getText();
			
			// Set text attributes.
			progressText.setXYPosition(x + width / 2, y + 40);
            progressText.setSize(14);
            progressText.setAlignment(Text.VCENTER | Text.HCENTER);
            progressText.setColor(Game.TEXT_COLOR);
            progressText.setText(progress + "/" + progressMax);
		}	
        
        // Load the sprites.
        leftSprite = ResourceFactory.get().getSprite(PATH_SPRITE_LEFT);
        rightSprite = ResourceFactory.get().getSprite(PATH_SPRITE_RIGHT);
        middleSprite = ResourceFactory.get().getSprite(PATH_SPRITE_MIDDLE);
        
        // Initialize.
        setProgress(progress);
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }
    
    public void draw()
    {
        // Draw the text.
        if (withText == true)
            progressText.draw();
        
        // Adjust the local x and y.
        int alignedX = x + offsetX;
        int alignedY = y + offsetY;
        
        // Draw the bar.
        if (progressWidth == 0)
            return;        
        else if (progressWidth <= 8)
        {
            int w = progressWidth / 2;
            
            leftSprite.drawClipped(
                    alignedX, alignedY,
                    leftSprite.getWidth(), leftSprite.getHeight(),
                    0, 0, 
                    w, leftSprite.getHeight(), 
                    0.0, 100);                 
            
            rightSprite.drawClipped(                    
                    alignedX + w, alignedY,
                    rightSprite.getWidth(), rightSprite.getHeight(),
                    rightSprite.getWidth() - w, 0,
                    w, rightSprite.getHeight(), 
                    0.0, 100);
            
            return;
        }
        else
        {
            leftSprite.draw(alignedX, alignedY);
            for (int i = 4; i < progressWidth - 4; i++)
                middleSprite.draw(alignedX + i, alignedY);
            rightSprite.draw(alignedX + progressWidth - 4, alignedY);
        }
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
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
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
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
        
        // Set dirty so it will be drawn.        
        setDirty(true);
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
        
        // Set dirty so it will be drawn.        
        setDirty(true);
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
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }
    
    public int getWidthMax()
    {
        return progressWidth;
    }

    public void setWidthMax(int progressWidth)
    {
        this.progressWidth = progressWidth;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }    

    public int getHeight()
    {
        return middleSprite.getHeight();
    }

    public void setHeight(int height)
    {
        // Wig out.
        throw new UnsupportedOperationException(
                "Height cannot be set on progress bar.");
    }

    public int getAlignment()
    {
        return alignment;
    }

    public void setAlignment(int alignment)
    {
        // Remember the alignment.
		this.alignment = alignment;
        
		// The Y alignment.
		if((alignment & BOTTOM) == BOTTOM)
		{
			this.offsetY = 0;
		}
		else if((alignment & VCENTER) == VCENTER)
		{
			this.offsetY = -getHeight() / 2;
		}
		else if((alignment & TOP) == TOP)
		{
			this.offsetY = -getHeight();
		}
		else
		{
			Util.handleWarning("No Y alignment set!", Thread.currentThread());
		}
		
		// The X alignment. 
		if((alignment & LEFT) == LEFT)
		{
			this.offsetX = 0;
		}
		else if((alignment & HCENTER) == HCENTER)
		{
			this.offsetX = -width / 2;			
		}
		else if((alignment & RIGHT) == RIGHT)
		{
			this.offsetX = -width;
		}
		else
		{
			Util.handleWarning("No X alignment set!", Thread.currentThread());
		}	
        
        // Update the text if necessary.
        if (withText == true)
        {
            progressText.setX(progressText.getX() + offsetX);
            progressText.setY(progressText.getY() + offsetY);
        }
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }
    
    public void setDirty(boolean dirty)
    {
        this.dirty = dirty;
    }

    public boolean isDirty()
    {
        return dirty;
    }
    
}
