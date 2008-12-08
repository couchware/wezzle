package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.util.*;
import java.awt.Color;
import java.util.EnumSet;

/**
 * A class for creating a rectangular boolean button.
 * 
 * @author cdmckay
 */
public class SpriteButton extends AbstractSpriteButton
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
    
    /** The different sizes of the buttons. */
    public enum Type
    {
        /** A small circular button suitable for use in +/- clickers. */
        SMALL_CIRCULAR("Button_SmallCircular.png", 18), 
        
        /** A thinner, longer button used in the main menu. */
        THIN          ("Button_Thin.png",          20),        
        
        /** The "normal" sized button used in the in-game UI. */
        NORMAL        ("Button_Normal.png",        18), 
        
        /** A larger, squarish button used in the main menu. */
        LARGE         ("Button_Large.png",         24), 
        
        /** A huge, squarish button used in the in-game button as the high-score button. */
        HUGE          ("Button_Huge.png",          26);       
                
        /** The file name of the sprite that represents the button. */
        private String filename;
        
        /** The text size of the text that will be written on the button */
        private int textSize;
        
        /** 
         * Constructor that stores the filename. 
         * 
         * @param filename The filename of the button's appearance.
         * @param textSize The text size.
         */
        Type(String filename, int textSize)
        { this.filename = filename; this.textSize = textSize; }
        
        /** 
         * An accessor for getting the sprite's filename. 
         * 
         * @return The filename.
         */
        public String getFilename()
        { return filename; }
        
        /** 
         * An accessor for getting the sprite's text size. 
         * 
         * @return The text size.
         */
        public int getTextSize()
        { return textSize; }
    }        
           
    /** The color of the button. */
    final private Color textColor;
    
//    /** The type of button. */
    final private Type type;
    
    /** The normal sprite. */
    final private ISprite sprite;       
    
    /** The normal label. */
    final private ILabel normalLabel;   
    
    /** The hover label. */
    final private ILabel hoverLabel;
    
    /** The active label. */
    final private ILabel activeLabel;       
    
    /** The size of the text on the button. */
    final private int textSize;
       
    /** The normal opacity when the button is off. */
    private int normalOpacity;
    
    /** The hover opacity. */
    private int hoverOpacity;
    
    /** The pressed opacity. */
    private int pressedOpacity;
    
    /** The active opacity. */
    private int activeOpacity;
    
    /** The normal text. */
    private final String normalText;
    
    /** The hover text. */
    private final String hoverText;
    
    /** The active text. */
    private final String activeText;       
    
    /**
     * Creates a button at the coordinates provided.
     * @param x
     * @param y
     */
    private SpriteButton(Builder builder)
    {      
        // Invoke the super on the required arguments.
        // This will set their variables as well.
        super(builder.x, builder.y);
        
        // Assign values from builder.      
        this.textColor      = builder.textColor;
        this.normalText     = builder.normalText;       
        this.hoverText      = builder.hoverText;
        this.activeText     = builder.activeText;        
        this.normalOpacity  = limitOpacity(builder.normalOpacity);
        this.hoverOpacity   = limitOpacity(builder.hoverOpacity);
        this.pressedOpacity = limitOpacity(builder.pressedOpacity);
        this.activeOpacity  = limitOpacity(builder.activeOpacity);
        this.opacity        = limitOpacity(builder.opacity);
        this.type           = builder.type;
        
        // Determine which text size to use.  If it is 0, then use the 
        // one from the text-size map.  Otherwise, use the one provided 
        // by the builder.
        if (builder.textSize == 0)
            this.textSize = this.type.getTextSize();
        else
            this.textSize = builder.textSize;
        
        // Set the visibility.
        this.visible = builder.visible;
        this.disabled = builder.disabled;
                                                                   
        // Load the normal sprite.
        sprite = ResourceFactory.get()
                .getSprite(Settings.getSpriteResourcesPath() + "/" + this.type.getFilename());      
        
        // Assign values based on the values from builder.        
        this.width = sprite.getWidth();
        this.height = sprite.getHeight();
        
        // Determine the offsets.
        this.alignment = builder.alignment;
        this.offsetX = determineOffsetX(alignment, width);
        this.offsetY = determineOffsetY(alignment, height);
        
        // Set the shape.
        this.shape = new ImmutableRectangle(x + offsetX, y + offsetY, width, height);                                      
        
        // Create the normal label.
        this.normalLabel = new LabelBuilder(x + offsetX + width / 2, y + offsetY + height / 2)
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
    }
    
    public static class Builder implements IBuilder<SpriteButton>
    {
        // Required values.       
        private int x;
        private int y;        
        
        // Optional values.
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);
        private Color textColor = defaultColor;
        private String normalText = "Button";
        private String hoverText = null;
        private String activeText = null;
        private int textSize = 0;        
        private int pressedOpacity = 100;
        private int hoverOpacity   = 100;        
        private int activeOpacity  = 100;
        private int normalOpacity  = 80;
        private int opacity        = 100;
        private Type type = Type.NORMAL;  
        private boolean visible = true;
        private boolean disabled = false;
        
        public Builder(int x, int y)
        {            
            this.x = x;
            this.y = y;
        }
        
        public Builder(SpriteButton button)
        {            
            this.x                = button.x;
            this.y                = button.y;
            this.alignment        = button.alignment.clone();
            this.textColor            = button.textColor;
            this.normalText       = button.normalText;
            this.hoverText        = button.hoverText;
            this.textSize         = button.textSize;            
            this.hoverOpacity     = button.hoverOpacity;
            this.pressedOpacity   = button.pressedOpacity;
            this.activeOpacity    = button.activeOpacity;
            this.normalOpacity    = button.normalOpacity;       
            this.opacity          = button.opacity;
            this.type             = button.type;
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
        
        public Builder type(Type val) 
        { type = val; return this; }
        
        public Builder visible(boolean val) 
        { visible = val; return this; }
        
        public Builder disabled(boolean val)
        { disabled = val; return this; }
        
        public SpriteButton end()
        {
            SpriteButton button = new SpriteButton(this);
            
            if (visible == true && disabled == false)
                button.window.addMouseListener(button);           
            
            return button;
        }                
    }    
        
//    private void drawButton()
//    {
//        int scaledOpacity = Util.scaleInt(0, 100, 0, 255, opacitize(normalOpacity));
//        int border = 2;
//        
//        window.setColor(SuperColor.newInstance(Color.BLACK, scaledOpacity));
//        window.fillRoundRect(
//                x + offsetX, y + offsetY, 
//                width, height,
//                10);        
//        
//        window.setColor(SuperColor.newInstance(Color.DARK_GRAY, scaledOpacity));
//        window.fillRoundRect(
//                x + offsetX + border, y + offsetY + border, 
//                width - border * 2, height - border * 2,
//                10);
//    }
    
    private void drawNormal()
    {                
        sprite.draw(
                x + offsetX, y + offsetY, 
                width, height,                 
                0.0, opacitize(normalOpacity));   
//        drawButton();  
        
        normalLabel.setOpacity(opacity);
        normalLabel.draw();
    }
    
    private void drawActivated()
    {        
        sprite.draw(
                x + offsetX, y + offsetY, 
                width, height, 
                0.0, opacitize(activeOpacity));        
//        drawButton();   
        
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
    
    private void drawHovered()
    {
        sprite.draw(x + offsetX, y + offsetY, width, height, 0.0, opacitize(hoverOpacity));
        
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
    
    private void drawPressed()
    {
        sprite.draw(x + offsetX, y + offsetY, width, height, 0.0, opacitize(pressedOpacity));
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
    
    @Override
    public void setX(int x)
    {
        super.setX(x);
        
        normalLabel.setX(x);
        if (hoverLabel != null) hoverLabel.setX(x);
        if (activeLabel != null) activeLabel.setX(x);
    }
    
    @Override
    public void setY(int y)
    {
        super.setY(y);
        
        normalLabel.setY(y);
        if (hoverLabel != null) hoverLabel.setY(y);
        if (activeLabel != null) activeLabel.setY(y);
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
