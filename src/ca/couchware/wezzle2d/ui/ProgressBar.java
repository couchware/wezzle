package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.util.IBuilder;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.util.NumUtil;
import ca.couchware.wezzle2d.util.StringUtil;
import java.awt.Color;
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
    
    /** The default color. */
    private static Color defaultColor = Color.RED;
    
    /**
     * Change the default color for all sprite buttons.
     * 
     * @param The new color.
     */
    public static void setDefaultColor(Color color)
    { defaultColor = color; }    
    
    /** The different widths of progress bars. */
    public enum BarWidth
    {
        MEDIUM, 
        LARGE
    }
    
    /** The different positions for the text. */
    public enum TextPosition
    {
        NONE,
        BOTTOM
    }
    
    /** The different colours fo the bar. */
    public enum BarColor
    {
        YELLOW,
        BLUE
    }
    
    /** The file extension for graphics files. */
    final private static String FILE_EXT = ".png";
    
    /** The path to the progress bar container for 200 pixel bar. */    
    final private static String PATH_CONTAINER_200 =            
            Settings.getSpriteResourcesPath() + "/ProgressBar_Container_200" + FILE_EXT;
    
    /** The path to the progress bar container for 400 pixel bar. */    
    final private static String PATH_CONTAINER_400 =
            Settings.getSpriteResourcesPath() + "/ProgressBar_Container_400" + FILE_EXT;        
    
    /** The path to the left sprite. */
    final private static String PATH_SPRITE_LEFT_PREFIX =             
            Settings.getSpriteResourcesPath() + "/ProgressBar_Left_";
    
    /** The path to the right sprite. */
    final private static String PATH_SPRITE_RIGHT_PREFIX =
            Settings.getSpriteResourcesPath() + "/ProgressBar_Right_";
    
    /** The path to the middle sprite. */
    final private static String PATH_SPRITE_MIDDLE_PREFIX =
            Settings.getSpriteResourcesPath() + "/ProgressBar_Middle_";
   
    /** The progress bar color. */
    final private BarColor barColor;
    
    /** The color of the progress bar text. */
    final private Color textColor;
    
    /** The type of progress bar. */
    final private BarWidth barWidth;
    
    /** The position of the bar text. */
    final private TextPosition textPosition;
    
    /** The progress text. */
    private ITextLabel progressLabel;
    
    /** The container sprite. */   
    final private ISprite containerSprite;
    
    /** The left sprite. */
    final private ISprite leftSprite;
    
    /** The right sprite. */
    final private ISprite rightSprite;
    
    /** The middle sprite. */
    final private ISprite middleSprite;          		
	
	/** The current progress. */
	private int progressValue;
    
    /** The progress lower bound. */
    private int progressLower;
    
    /** The progress upper bound. */
	private int progressUpper;
	
	/** The current progress  */
	private int progressWidth;
    
    /** The width at which the element is at maximum progress. */
	private int maxProgressWidth;   
    
    /** The inner padding of the progress bar container. */
    private Padding padding;        
    
    /**
     * Create a progress bar with the top-left coordinate at the position
     * specified.  Optionally may have text under it.
     * 
     * @param builder
     */    
    public ProgressBar(Builder builder)
    {            
        // Save opacity and visiblity.
        this.visible = builder.visible;
        this.opacity = builder.opacity;
        
        // Save the color.
        this.barColor  = builder.barColor;
        this.textColor = builder.textColor;
        
        // Save the width.
        this.barWidth = builder.barWidth;                
        
        // Set progress to 0.
        // Set progressMax to 100.
        this.progressValue = builder.progressValue;
        this.progressLower = builder.progressLower;
        this.progressUpper = builder.progressUpper;                                  

        if (progressUpper <= progressLower)
            throw new IllegalStateException("progressUpper <= progressLower.");
        if (progressValue < progressLower)
            throw new IllegalStateException("progressValue < progressLower.");
        if (progressValue > progressUpper)
            throw new IllegalStateException("progressValue > progressUpper.");

        
         // Load the container sprite.
        switch (barWidth)
        {
            case MEDIUM:
                // Set width max.
                this.maxProgressWidth = 200 - 14;
                this.containerSprite = ResourceFactory.get().getSprite(PATH_CONTAINER_200);
                break;
                
            case LARGE:
                // Set width max.
                this.maxProgressWidth = 400 - 14;
                this.containerSprite = ResourceFactory.get().getSprite(PATH_CONTAINER_400);
                break;
                
            default: throw new AssertionError();
        }
        
        // Set the padding.
        this.padding = Padding.newInstance(7);
               
        // Load the sprites.
        final String SUFFIX = StringUtil.capitalizeFirst(barColor.toString()) + FILE_EXT;
        leftSprite   = ResourceFactory.get().getSprite(PATH_SPRITE_LEFT_PREFIX   + SUFFIX);        
        rightSprite  = ResourceFactory.get().getSprite(PATH_SPRITE_RIGHT_PREFIX  + SUFFIX);
        middleSprite = ResourceFactory.get().getSprite(PATH_SPRITE_MIDDLE_PREFIX + SUFFIX);     
               
        // Set coordinates.
        this.x = builder.x;
        this.y = builder.y;
        
        this.x_ = x;
        this.y_ = y;
        
        // Set initial alignment.
        this.alignment = builder.alignment;
        this.offsetX = determineOffsetX(alignment, getWidth());
        this.offsetY = determineOffsetY(alignment, getHeight());
        
        // Add text if specified.
        this.textPosition = builder.textPosition;
		switch (this.textPosition)
		{
            case BOTTOM:
                
                // Set the label.
                progressLabel = new LabelBuilder(x + getWidth() / 2, y + 47)
                        .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                        .color(builder.textColor).size(14)
                        .text(progressValue + "/" + progressUpper).cached(false).build();

                // Update the text if necessary.        
                progressLabel.setX(progressLabel.getX() + offsetX);
                progressLabel.setY(progressLabel.getY() + offsetY);
                
                break;
        }                                                
        
        // Initialize.
        setProgressValue(progressValue);
        
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
        private Color textColor = defaultColor;
        private BarWidth barWidth = BarWidth.MEDIUM;        
        private TextPosition textPosition = TextPosition.NONE;
        private BarColor barColor = BarColor.YELLOW;
        private int progressValue = 0;
        private int progressLower = 0;
        private int progressUpper = 100;        
        private int opacity = 100;
        private boolean visible = true;
        
        public Builder(int x, int y)
        {            
            this.x = x;
            this.y = y;
        }               
        
        public Builder x(int val) { x = val; return this; }        
        public Builder y(int val) { y = val; return this; }
               
        public Builder alignment(EnumSet<Alignment> val) 
        { alignment = val; return this; }
        
        public Builder textColor(Color val)
        { textColor = val; return this; }
        
        public Builder barColor(BarColor val)
        { barColor = val; return this; }
        
        public Builder barWidth(BarWidth val)
        { barWidth = val; return this; }
        
        public Builder textPosition(TextPosition val)
        { textPosition = val; return this; }
        
        public Builder progressValue(int val)
        { progressValue = val; return this; }
        
        public Builder progressLower(int val)
        { progressLower = val; return this; }
        
        public Builder progressUpper(int val)
        { progressUpper = val; return this; }
        
        public Builder opacity(int val)
        { opacity = val; return this; }
        
        public Builder visible(boolean val) 
        { visible = val; return this; }
        
        public ProgressBar build()
        {
            return new ProgressBar(this);
        }                
    }   
    
    public boolean draw()
    {
        x_ = x;
        y_ = y;
        
        // Check visibility.
        if (!visible)
        {
            return false;
        }
        
        // Draw the container.
        containerSprite.draw(x + offsetX, y + offsetY)                                
                .opacity(NumUtil.scaleInt(0, 100, 0, 90, opacity)).end();
        
        // Draw the text.
        if (textPosition != TextPosition.NONE) 
        {
            progressLabel.draw();
        }
        
        // Adjust the local x and y.
        int alignedX = x + padding.getLeft() + offsetX;
        int alignedY = y + padding.getTop()  + offsetY;
        
        // Draw the bar.        
        if (progressValue == 0)
            ; // Do nada.
        else if (progressWidth <= 8)
        {                        
            leftSprite.draw(alignedX, alignedY).opacity(opacity)                    
                    .region(0, 0, progressWidth, leftSprite.getHeight())
                    .end();        
            
            rightSprite.draw(alignedX + 4, alignedY).opacity(opacity)
                    .region(0, 0, progressWidth - 4, leftSprite.getHeight())
                    .end();
        }
        else
        {
            leftSprite.draw(alignedX, alignedY).opacity(opacity).end();                    
            
            for (int i = 4; i < progressWidth - 4; i += 2)
            {
                // Look at the case of the last piece.  If it's exactly equal
                // to the desired width, then draw normally.  Otherwise
                // draw only half of it.
                if (i + 2 > progressWidth - 4)
                {
                    middleSprite.draw(alignedX + i, alignedY).opacity(opacity)
                            .region(0, 0, 1, middleSprite.getHeight())
                            .end();
                    continue;
                }
                                
                middleSprite.draw(alignedX + i, alignedY)
                        .opacity(opacity).end();                        
            }            
            
            rightSprite.draw(alignedX + progressWidth - 4, alignedY)
                    .opacity(opacity).end();                    
        }
        
        return true;
    }    

    /**
	 * @return The progressMax.
	 */
	public int getProgressMax()
	{
		return progressUpper;
	}

	/**
	 * @param progressMax The progressMax to set.
	 */
	public void setProgressUpper(int progressUpper)
	{
        CouchLogger.get().recordMessage(this.getClass(), "setProgressUpper called");

		// Update the text, if needed.
		if (textPosition != TextPosition.NONE)
        {			
            progressLabel.setText(
                    String.format("%,d/%,d", progressValue, progressUpper));
        }
		
		// Update the progress upper bound.
		this.progressUpper = progressUpper;
        
        // Recalculate the width.
        this.setProgressValue(this.progressValue);
        
        // Set dirty so it will be drawn.        
        this.dirty = true;
	}
    
    /**
	 * @return The progress.
	 */
	public int getProgressValue()
	{
		return progressValue;
	}

	/**
	 * @param progressPercent the progress to set
	 */
	final public void setProgressValue(int progressValue)
	{
        //CouchLogger.get().recordMessage(this.getClass(), "setProgressValue called");
        // Make sure progress is positive.
//        assert progressValue >= this.progressLower
//                : String.format("%d was >= %d", progressValue, this.progressLower);
        
        // Don't do anything if it's the same.
        //if (this.progressValue == progressValue)
        //{
        //    return;
        //}
        
		// Update the text, if needed.
		if (textPosition != TextPosition.NONE)
        {            
			progressLabel.setText(
                    String.format("%,d/%,d", progressValue, progressUpper));
        }
		
        // Save the progress value.
        this.progressValue = progressValue;
        		
        // Determine the progress width.
//        CouchLogger.get().recordWarning(this.getClass(),
//                String.format("%d in [%d, %d]",
//                    progressValue, progressLower, progressUpper));
        if (progressUpper - progressLower == 0)
        {
            this.progressWidth = 0;
        }
        else
        {
            this.progressWidth = ((progressValue - progressLower) * maxProgressWidth)
                / (progressUpper - progressLower);
        }
//        CouchLogger.get().recordWarning(this.getClass(),
//                String.format("%d in [%d, %d] - %d/%d width",
//                    progressValue, progressLower, progressUpper,
//                    progressWidth, maxProgressWidth));
        
        // This code ensures that the progress width does not exceed the
        // maximum progress width.        
        this.progressWidth = Math.max(0, progressWidth);
        this.progressWidth = Math.min(progressWidth, maxProgressWidth);
		
        // Set dirty so it will be drawn.        
        this.dirty = true;
	}
	
	public void increaseProgress(int deltaProgress)
	{
		// Increment the progress.
		setProgressValue(progressValue + deltaProgress);				
	}

    @Override
    public int getWidth()
    {
        return containerSprite.getWidth();
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
            if (textPosition != TextPosition.NONE)
            {
                rect.add(progressLabel.getDrawRect());
            }
            
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
