package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.ui.IButton.IButtonListener;
import ca.couchware.wezzle2d.util.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * A class for creating a rectangular boolean button.
 * 
 * @author cdmckay
 */
public class Button extends AbstractButton
{    
    
    /** The default color. */
    protected static Color defaultColor = Color.RED;
    
    /**
     * Change the default color for all sprite buttons.
     * 
     * @param The new color.
     */
    public static void setDefaultColor(Color color)
    { defaultColor = color; }    
    
    /** The graphic file type. */
    final protected static String FILE_TYPE = ".png";
    
    /** The left sprite. */
    final private static String LEFT_SPRITE_PATH = Settings.getSpriteResourcesPath() 
            + "/Button_Thin_Left" 
            + FILE_TYPE;
    
    /** The middle sprite. */
    final private static String MIDDLE_SPRITE_PATH = Settings.getSpriteResourcesPath() 
            + "/Button_Thin_Middle" 
            + FILE_TYPE;
    
    /** The right sprite. */
    final private static String RIGHT_SPRITE_PATH = Settings.getSpriteResourcesPath() 
            + "/Button_Thin_Right" 
            + FILE_TYPE;
           
    /** The color of the button. */
    protected Color textColor;   

    /** The left sprite of the button. */
    protected ISprite leftSprite;
    
    /** The middle sprite of the button. */
    protected ISprite middleSprite;
    
    /** The right sprite of the button. */
    protected ISprite rightSprite;
    
    /** The normal label. */
    final protected ITextLabel normalLabel;   
    
    /** The hover label. */
    final protected ITextLabel hoverLabel;
    
    /** The active label. */
    final protected ITextLabel activeLabel;       
    
    /** The size of the text on the button. */
    final protected int textSize;
       
    /** The normal opacity when the button is off. */
    protected int normalOpacity;
    
    /** The hover opacity. */
    protected int hoverOpacity;
    
    /** The pressed opacity. */
    protected int pressedOpacity;
    
    /** The active opacity. */
    protected int activeOpacity;
    
    /** The normal text. */
    protected final String normalText;
    
    /** The hover text. */
    protected final String hoverText;
    
    /** The active text. */
    protected final String activeText;       
    
    /**
     * Creates a button at the coordinates provided.
     * @param x
     * @param y
     */
    protected Button(Builder builder)
    {      
        // Invoke the super on the required arguments.
        // This will set their variables as well.
        super(builder.x, builder.y);
        
        // Assign values from builder.      
        this.textSize       = builder.textSize;
        this.textColor      = builder.textColor;
        this.normalText     = builder.normalText;       
        this.hoverText      = builder.hoverText;
        this.activeText     = builder.activeText;        
        this.normalOpacity  = limitOpacity(builder.normalOpacity);
        this.hoverOpacity   = limitOpacity(builder.hoverOpacity);
        this.pressedOpacity = limitOpacity(builder.pressedOpacity);
        this.activeOpacity  = limitOpacity(builder.activeOpacity);
        this.opacity        = limitOpacity(builder.opacity);
                
        // Set the visibility.
        this.visible = builder.visible;
        this.disabled = builder.disabled;
                                                                   
        // Load the sprites.
        leftSprite   = ResourceFactory.get().getSprite(LEFT_SPRITE_PATH);      
        middleSprite = ResourceFactory.get().getSprite(MIDDLE_SPRITE_PATH);      
        rightSprite  = ResourceFactory.get().getSprite(RIGHT_SPRITE_PATH);      
        
        // Create the normal label.
        this.normalLabel = new LabelBuilder(0, 0)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(textColor).size(textSize).text(normalText).end(); 
        
        // Create the other labels, using the normal label as a template.
        if (this.hoverText != null)   
        {
            this.hoverLabel = new LabelBuilder(normalLabel).text(hoverText).end();                
        }
        else
        {
            this.hoverLabel = null;
        }
        
        if (this.activeText != null)
        {
            this.activeLabel = new LabelBuilder(normalLabel).text(activeText).end();                
        }
        else
        {
            this.activeLabel = null;
        }
        
        // Assign values based on the values from builder.        
        this.width = builder.autoWidth 
               ? normalLabel.getWidth() + 30
               : builder.width;
        
        this.height = middleSprite.getHeight();
        
         // Determine the offsets.
        this.alignment = builder.alignment;
        this.offsetX = determineOffsetX(alignment, width);
        this.offsetY = determineOffsetY(alignment, height);
        
        // Set the shape.
        this.shape = new ImmutableRectangle(x + offsetX, y + offsetY, width, height);    
        
        // Set the label position now that we have the width figured out.
        final int POS_X = x + offsetX + width / 2;
        final int POS_Y = y + offsetY + height / 2;
        normalLabel.setPosition(POS_X, POS_Y);
        if (hoverLabel  != null) hoverLabel.setPosition(POS_X, POS_Y);
        if (activeLabel != null) activeLabel.setPosition(POS_X, POS_Y);
               
        if (!validateWidth())
            throw new RuntimeException("The button width is too narrow.");                                                                                 
    }
    
    public static class Builder implements IBuilder<Button>
    {
        // Required values.       
        protected int x;
        protected int y;        
        
        // Optional values.
        protected EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);
        protected Color textColor = defaultColor;
        protected String normalText = "Button";
        protected String hoverText = null;
        protected String activeText = null;
        protected int width = 220;
        protected int textSize = 20;        
        protected int pressedOpacity = 100;
        protected int hoverOpacity   = 100;        
        protected int activeOpacity  = 100;
        protected int normalOpacity  = 80;
        protected int opacity        = 100;
        protected boolean visible = true;
        protected boolean disabled = false;
        protected boolean autoWidth = false;
        
        public Builder(int x, int y)
        {            
            this.x = x;
            this.y = y;
        }
        
        public Builder(Button button)
        {            
            this.x                = button.x;
            this.y                = button.y;
            this.alignment        = button.alignment.clone();
            this.textColor        = button.textColor;
            this.normalText       = button.normalText;
            this.hoverText        = button.hoverText;
            this.activeText       = button.activeText;
            this.width            = button.width;
            this.textSize         = button.textSize;            
            this.hoverOpacity     = button.hoverOpacity;
            this.pressedOpacity   = button.pressedOpacity;
            this.activeOpacity    = button.activeOpacity;
            this.normalOpacity    = button.normalOpacity;       
            this.opacity          = button.opacity;            
            this.visible          = button.visible;
            this.disabled         = button.disabled;
        }
        
        public Builder x(int val) { x = val; return this; }        
        public Builder y(int val) { y = val; return this; }
        
        public Builder alignment(EnumSet<Alignment> val) 
        { alignment = val; return this; }
        
        public Builder color(Color val)
        { textColor = val; return this; }
        
        public Builder text(String val)
        {
            normalText(val); hoverText(null); activeText(null);
            return this;
        }
        
        public Builder normalText(String val) 
        { normalText = val; return this; }
        
        public Builder hoverText(String val) 
        { hoverText = val; return this; }
        
        public Builder activeText(String val)                 
        { activeText = val; return this; }
        
        public Builder width(int val)
        { width = val; return this; }
        
        public Builder autoWidth(boolean val)
        { autoWidth = val; return this; }
        
        public Builder textSize(int val)
        { textSize = val; return this; }
        
        public Builder normalOpacity(int val)                 
        { normalOpacity = val; return this; }
        
        public Builder hoverOpacity(int val) 
        { hoverOpacity = val; return this; }
        
        public Builder pressedOpacity(int val)
        { pressedOpacity = val; return this; }
        
        public Builder activeOpacity(int val) 
        { activeOpacity = val; return this; }
        
        public Builder opacity(int val)
        { opacity = val; return this; }
                
        public Builder visible(boolean val) 
        { visible = val; return this; }
        
        public Builder disabled(boolean val)
        { disabled = val; return this; }
        
        public Button end()
        {
            Button button = new Button(this);
            
            if (visible == true && disabled == false)
                button.window.addMouseListener(button);           
            
            return button;
        }                
    }    
    
    final protected boolean validateWidth()
    {
        // Make sure the width is acceptable.
        int w1 = leftSprite.getWidth();        
        int w2 = rightSprite.getWidth();                             
        
        if (w1 + w2 > width)
            return false;
        else
            return true;
    }
        
    protected void drawButton(int o)
    {
        final int X = x + offsetX;
        final int Y = y + offsetY;
        
        leftSprite.draw(X, Y)
                .width(leftSprite.getWidth()).height(height).opacity(o).end();
                
        middleSprite.draw(X + leftSprite.getWidth(), Y)
                .width(width - leftSprite.getWidth() - rightSprite.getWidth())
                .height(height).opacity(o).end();
        
        rightSprite.draw(X + width - leftSprite.getWidth(), Y)
                .width(rightSprite.getWidth()).height(height).opacity(o).end();                
    }
    
    protected void drawNormal()
    {                
//        sprite.draw(
//                x + offsetX, y + offsetY, 
//                width, height,                 
//                0.0, opacitize(normalOpacity));   
        drawButton(opacitize(normalOpacity));  
        
        normalLabel.setOpacity(opacity);
        normalLabel.draw();
    }
    
    protected void drawActivated()
    {        
//        sprite.draw(
//                x + offsetX, y + offsetY, 
//                width, height, 
//                0.0, opacitize(activeOpacity));        
        drawButton(opacitize(activeOpacity));   
        
        if (activeLabel != null)
        {
            activeLabel.setOpacity(opacity);
            activeLabel.draw();            
        }
        else 
        {
            normalLabel.setOpacity(opacity);
            normalLabel.draw();
        }
    }
    
    protected void drawHovered()
    {
        //sprite.draw(x + offsetX, y + offsetY, width, height, 0.0, opacitize(hoverOpacity));
        drawButton(opacitize(hoverOpacity));
        
        if (hoverLabel != null) 
        {
            hoverLabel.setOpacity(opacity);
            hoverLabel.draw();        
        }
        else
        {
            if (activeLabel != null && isActivated() == true)
            {
                activeLabel.setOpacity(opacity);
                activeLabel.draw();
            }
            else
            {
                normalLabel.setOpacity(opacity);
                normalLabel.draw();
            }
        }
    }
    
    protected void drawPressed()
    {
        //sprite.draw(x + offsetX, y + offsetY, width, height, 0.0, opacitize(pressedOpacity));
        drawButton(opacitize(pressedOpacity));
        normalLabel.setOpacity(opacity);
        normalLabel.translate(0, 1);
        normalLabel.draw();
        normalLabel.translate(0, -1);
    }   
    
    
    private int opacitize(int targetOpacity)
    {
        return Util.scaleInt(0, 100, 0, targetOpacity, this.opacity);
    }

    public int getActiveOpacity()
    {
        return activeOpacity;
    }

    public void setActiveOpacity(int activeOpacity)
    {
        this.activeOpacity = limitOpacity(activeOpacity);
        setDirty(true);
    }

    public int getHoverOpacity()
    {
        return hoverOpacity;
    }

    public void setHoverOpacity(int hoverOpacity)
    {
        this.hoverOpacity = limitOpacity(hoverOpacity);
        setDirty(true);
    }     
    
     public int getPressedOpacity()
    {
        return pressedOpacity;
    }

    public void setPressedOpacity(int pressedOpacity)
    {
        this.pressedOpacity = limitOpacity(pressedOpacity);
        setDirty(true);
    }    

    public int getNormalOpacity()
    {
        return getOpacity();
    }

    public void setNormalOpacity(int offOpacity)
    {
        setOpacity(offOpacity);
    }             

    public String getNormalText()
    {
        return normalText;
    }

    public String getHoverText()
    {
        return hoverText;
    }
    
    public String getActiveText()
    {
        return activeText;
    }
    
    public Color getTextColor()
    {
        return textColor;
    }
    
    public void setTextColor(Color color)
    {
        this.textColor = color;
        normalLabel.setColor(color);
        if (hoverLabel != null)  hoverLabel.setColor(color);
        if (activeLabel != null) activeLabel.setColor(color);
    }
    
    @Override
    public void setX(int x)
    {
        super.setX(x);
        
        normalLabel.setX(x + offsetX + width / 2);
        if (hoverLabel != null)  hoverLabel.setX(x + offsetX + width / 2);
        if (activeLabel != null) activeLabel.setX(x + offsetX + width / 2);
    }
    
    @Override
    public void setY(int y)
    {
        super.setY(y);
        
        normalLabel.setY(y + offsetY + height / 2);
        if (hoverLabel != null)  hoverLabel.setY(y + offsetY + height / 2);
        if (activeLabel != null) activeLabel.setY(y + offsetY + height / 2);
    }
    
    public boolean draw()
    {
        x_ = x + offsetX;
        y_ = y + offsetY;
        
        // Don't draw if not visible.
        if (visible == false)
            return false;

        // See what state we're in.
        if (state.contains(State.PRESSED))        
            drawPressed();        
        else if (state.contains(State.HOVERED))
            drawHovered();
        else if (state.contains(State.ACTIVATED))
            drawActivated();
        else
            drawNormal();     
        
        // Uncomment for debugging the draw rect box.
        //window.drawRect(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
        
        return true;
    }          

}
