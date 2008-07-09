package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.graphics.*;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.graphics.Entity;
import ca.couchware.wezzle2d.util.Util;
import java.awt.Rectangle;

/**
 * A class for representing progress bars of any length 
 * (but a fixed height).
 * 
 * @author cdmckay
 */
public class ProgressBar extends Entity
{

    /**
     * The availble progress bar widths.
     */
    final public static int WIDTH_200 = 0;
    //final public static int WIDTH_400 = 1;
    //final public static int WIDTH_600 = 2;
    
    /**
     * The path to the progress bar container for 200 pixel bar.
     */    
    final private static String PATH_CONTAINER_200 =
            Game.SPRITES_PATH + "/ProgressBarContainer_200.png";
    
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
     * Does the progress bar have text?
     */
    private boolean withText;
    
    /**
     * The progress text.
     */
    private Label progressText;
    
    /**
     * The container sprite.
     */   
    private Sprite containerSprite;
    
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
	 * The width at which the element is at maximum progress.
	 */
	private int progressMaxWidth;   
    
    /**
     * The inner x padding of the progress bar container.
     */
    private int padX;
    
    /**
     * The inner y padding of the progress bar container.
     */
    private int padY;
    
    /**
     * Create a progress bar with the top-left coordinate at the position
     * specified.  Optionally may have text under it.
     * 
     * @param x
     * @param y
     * @param withText
     */    
    public ProgressBar(int x, int y, int type, boolean withText)
    {                   
        // Set progress to 0.
        // Set progressMax to 100.
        this.progress = 0;
        this.progressMax = 100;  
        
        switch (type)
        {
            case WIDTH_200:
                
                // Set width max.
                this.progressMaxWidth = 200 - 14;
                
                 // Load the container sprite.
                containerSprite = 
                        ResourceFactory.get().getSprite(PATH_CONTAINER_200);
                padX = 7;
                padY = 7;
                
                break;
                
            default:
                throw new IllegalArgumentException("Unknown width type!");
        }        
                      
        // Load the sprites.
        leftSprite = ResourceFactory.get().getSprite(PATH_SPRITE_LEFT);
        rightSprite = ResourceFactory.get().getSprite(PATH_SPRITE_RIGHT);
        middleSprite = ResourceFactory.get().getSprite(PATH_SPRITE_MIDDLE);     
               
        // Set coordinates.
        this.x = x;
        this.y = y;
        
        this.x_ = x;
        this.y_ = y;
        
        // Set initial alignment.
        this.alignment = TOP | LEFT;
        
        // Add text if specified.
		if (withText == true)
		{
			// Set the variable.
			this.withText = withText;
			
			// Create progress text.		
			progressText = ResourceFactory.get().getLabel(
                    x + getWidth() / 2, y + 47);
			
			// Set text attributes.			
            progressText.setSize(14);
            progressText.setAlignment(Label.VCENTER | Label.HCENTER);
            progressText.setColor(Game.TEXT_COLOR);
            progressText.setText(progress + "/" + progressMax);
		}	                
        
        // Initialize.
        setProgress(progress);
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }
    
    public void draw()
    {
        x_ = x;
        y_ = y;
        
        // Draw the container.
        containerSprite.draw(x + offsetX, y + offsetY,
                containerSprite.getWidth(), containerSprite.getHeight(),
                0.0, 90);
        
        // Draw the text.
        if (withText == true)
            progressText.draw();
        
        // Adjust the local x and y.
        int alignedX = x + padX + offsetX;
        int alignedY = y + padY + offsetY;
        
        // Draw the bar.
        if (progressWidth == 0)
            return;        
        else if (progressWidth <= 8)
        {
            int w = progressWidth / 2;
            
            leftSprite.drawRegion(
                    alignedX, alignedY,
                    leftSprite.getWidth(), leftSprite.getHeight(),
                    0, 0, 
                    w, leftSprite.getHeight(), 
                    0.0, 100);                 
            
            rightSprite.drawRegion(                    
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
        // Don't do anything if it's the same.
        if (this.progress == progress)
            return;
        
		// Update the text, if needed.
		if (withText == true)
			progressText.setText(progress + "/" + progressMax);
		
		// Update the progress.
		this.progress = progress;	
		this.progressWidth = (int) ((double) progressMaxWidth 
                * ((double) progress / (double) progressMax));
		this.progressWidth = progressWidth > progressMaxWidth ? progressMaxWidth : progressWidth; 
        
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
        return containerSprite.getWidth();
    }

    public void setWidth(int width)
    {       
        this.progressMaxWidth = width;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }
    
    public int getProgressWidth()
    {
        return progressWidth;
    }

    public void setProgressWidth(int progressWidth)
    {
        this.progressWidth = progressWidth;
        
        // Set dirty so it will be drawn.        
        setDirty(true);
    }    

    @Override
    public int getHeight()
    {
        return containerSprite.getHeight();
    }

    @Override
    public void setHeight(int height)
    {
        // Wig out.
        throw new UnsupportedOperationException(
                "Height cannot be set on progress bar.");
    }

    @Override
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
			this.offsetX = -progressMaxWidth / 2;			
		}
		else if((alignment & RIGHT) == RIGHT)
		{
			this.offsetX = -progressMaxWidth;
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
    
    @Override
    public Rectangle getDrawRect()
    {
        // If the draw rect is null, generate it.
        if (drawRect == null)
        {
            Rectangle rect = new Rectangle(x_, y_, 
                    getWidth() + 2, getHeight() + 2);

            if (x_ != x || y_ != y)
                rect.add(new Rectangle(x, y, getWidth() + 2, getHeight() + 2));

            rect.translate(offsetX, offsetY);

            // Add the text too.
            if (withText == true)
                rect.add(progressText.getDrawRect());
            
            drawRect = rect;
        }
        
        return drawRect;
    }
    
    @Override
    public void resetDrawRect()
    {
        x_ = x;
        y_ = y;
    }
    
}
