package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.graphics.*;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.util.Util;
import java.awt.Rectangle;
import java.util.EnumSet;

/**
 * A class for representing progress bars of any length 
 * (but a fixed height).
 * 
 * @author cdmckay
 */
public class ProgressBar extends AbstractEntity
{
    
    /**
     * The path to the progress bar container for 200 pixel bar.
     */    
    final private static String PATH_CONTAINER =
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
    private boolean useLabel;
    
    /**
     * The progress text.
     */
    private ILabel progressLabel;
    
    /**
     * The container sprite.
     */   
    private ISprite containerSprite;
    
    /**
     * The left sprite.
     */
    private ISprite leftSprite;
    
    /**
     * The right sprite.
     */
    private ISprite rightSprite;
    
    /**
     * The middle sprite.
     */
    private ISprite middleSprite;          		
	
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
    public ProgressBar(Builder builder)
    {                   
        // Set progress to 0.
        // Set progressMax to 100.
        this.progress = builder.progress;
        this.progressMax = builder.progressMax;  
                
        // Set width max.
        this.progressMaxWidth = 200 - 14;

         // Load the container sprite.
        containerSprite = 
                ResourceFactory.get().getSprite(PATH_CONTAINER);
        padX = 7;
        padY = 7;
               
        // Load the sprites.
        leftSprite = ResourceFactory.get().getSprite(PATH_SPRITE_LEFT);
        rightSprite = ResourceFactory.get().getSprite(PATH_SPRITE_RIGHT);
        middleSprite = ResourceFactory.get().getSprite(PATH_SPRITE_MIDDLE);     
               
        // Set coordinates.
        this.x = builder.x;
        this.y = builder.y;
        
        this.x_ = x;
        this.y_ = y;
        
        // Set initial alignment.
        this.alignment = builder.alignment;
        this.offsetX = determineProgressBarOffsetX(alignment);
        this.offsetY = determineOffsetY(alignment);
        
        // Add text if specified.
        this.useLabel = builder.useLabel;
		if (useLabel == true)
		{
            // Set the label.
            progressLabel = new LabelBuilder(x + getWidth() / 2, y + 47)
                    .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                    .color(Game.TEXT_COLOR).size(14)
                    .text(progress + "/" + progressMax).end();   
            
            // Update the text if necessary.        
            progressLabel.setX(progressLabel.getX() + offsetX);
            progressLabel.setY(progressLabel.getY() + offsetY);
        }                                                
        
        // Initialize.
        setProgress(progress);
        
        // Set dirty so it will be drawn.        
        dirty = true;
    }
    
    public static class Builder implements IBuilder<ProgressBar>
    {
        // Required values.        
        private int x;
        private int y;     
        
        // Optional values.
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);
        private boolean useLabel = true;
        private int progress = 0;
        private int progressMax = 100;        
        private int opacity = 100;
        private boolean visible = true;
        
        public Builder(int x, int y)
        {            
            this.x = x;
            this.y = y;
        }
        
        public Builder(ProgressBar progressBar)
        {            
            this.x = progressBar.x;
            this.y = progressBar.y;
            this.alignment = progressBar.alignment;
            this.useLabel = progressBar.useLabel;
            this.progress = progressBar.progress;
            this.progressMax = progressBar.progressMax;
            this.opacity = progressBar.opacity;                        
            this.visible = progressBar.visible;
        }
        
        public Builder x(int val) { x = val; return this; }        
        public Builder y(int val) { y = val; return this; }
               
        public Builder alignment(EnumSet<Alignment> val) 
        { alignment = val; return this; }
        
        public Builder useLabel(boolean val)
        { useLabel = val; return this; }
        
        public Builder progress(int val)
        { progress = val; return this; }
        
        public Builder progressMax(int val)
        { progressMax = val; return this; }
        
        public Builder opacity(int val)
        { opacity = val; return this; }
        
        public Builder visible(boolean val) 
        { visible = val; return this; }
        
        public ProgressBar end()
        {
            return new ProgressBar(this);
        }                
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
        if (useLabel == true)
            progressLabel.draw();
        
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
		if (useLabel == true)
        {
			//progressText.setText(progress + "/" + progressMax);
            progressLabel = new LabelBuilder(progressLabel)
                    .text(progress + "/" + progressMax).end();
        }
		
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
	final public void setProgress(int progress)
	{
        // Don't do anything if it's the same.
        if (this.progress == progress)
            return;
        
		// Update the text, if needed.
		if (useLabel == true)
        {            
			progressLabel = new LabelBuilder(progressLabel)
                    .text(progress + "/" + progressMax).end();
        }
		
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

    @Override
    public int getWidth()
    {
        return containerSprite.getWidth();
    }

    @Override
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
   
    protected int determineProgressBarOffsetX(EnumSet<Alignment> alignment)
    {       
		if (alignment.contains(Alignment.LEFT))
		{
			return 0;
		}
		else if (alignment.contains(Alignment.CENTER))
		{
			return -progressMaxWidth / 2;			
		}
		else if (alignment.contains(Alignment.RIGHT))
		{
			return -progressMaxWidth;
		}
		else
		{
			throw new IllegalStateException("No X alignment set!");
		}	              
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
            if (useLabel == true)
                rect.add(progressLabel.getDrawRect());
            
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
