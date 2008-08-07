package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.util.*;
import java.awt.Rectangle;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * A class for creating a rectangular boolean button.
 * 
 * @author cdmckay
 */
public class SpriteButton extends AbstractSpriteButton implements IButton
{          
    /**
     * The different sizes of the boolean buttons.
     */
    public static enum ButtonType
    {
        NORMAL, LARGE
    }
    
    /**
     * The button dimensions.
     */
    final private static Map<ButtonType, WDimension> dimensionMap;       
    
    /**
     * The static constructor.
     */
    static
    {
        dimensionMap = new EnumMap<ButtonType, WDimension>(ButtonType.class);        
        dimensionMap.put(ButtonType.NORMAL, new WDimension(153, 49));
        dimensionMap.put(ButtonType.LARGE, new WDimension(210, 130));
    }
           
    /**
     * The type of button.
     */
    final private ButtonType type;
    
    /**
     * The normal sprite.
     */
    final private ISprite sprite;       
    
    /**
     * The normal label.
     */
    final private ILabel normalLabel;   
    
    /**
     * The hover label.
     */
    final private ILabel hoverLabel;
    
    /**
     * The active label.
     */
    final private ILabel activeLabel;       
    
    /**
     * The normal opacity.
     */
    // The inherited "opacity" variable is used.
    //private int normalOpacity;
    
    /**
     * The hover opacity.
     */
    private int hoverOpacity;
    
    /**
     * The active opacity.
     */
    private int activeOpacity;
    
    /**
     * The normal text.
     */
    private final String normalText;
    
    /**
     * The hover text.
     */
    private final String hoverText;
    
    /**
     * The active text.
     */
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
        super(builder.window, builder.x, builder.y);
        
        // Assign values from builder.               
        this.normalText = builder.normalText;       
        this.hoverText = builder.hoverText;
        this.activeText = builder.activeText;
        this.opacity = limitOpacity(builder.normalOpacity);
        this.hoverOpacity = limitOpacity(builder.hoverOpacity);
        this.activeOpacity = limitOpacity(builder.activeOpacity);
        this.type = builder.type;
        
        // Set the visibility.
        setVisible(builder.visible);
        
        // Assign values based on the values from builder.
        final WDimension d = dimensionMap.get(type);
        this.width = d.getWidth();
        this.height = d.getHeight();                
        
        // Determine the offsets.
         this.alignment = builder.alignment;
        offsetX = determineOffsetX(alignment);
        offsetY = determineOffsetY(alignment);
        
        // Set the shape.
        this.shape = new Rectangle(x + offsetX, y + offsetY, width, height);
        
        // Construct the sprite name.                
        String spriteNormalName = "RectangularButton_" 
            + width + "x" + height + ".png";
                                
        // Load the normal sprite.
        sprite = ResourceFactory.get()
                .getSprite(Game.SPRITES_PATH + "/" + spriteNormalName);                            
        
        // Create the normal label.
        normalLabel = new LabelBuilder(x, y)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR).size(18).text(normalText).end(); 
        
        // Create the other labels, using the normal label as a template.
        if (hoverText != null)            
            hoverLabel = new LabelBuilder(normalLabel).text(hoverText).end();                
        else
            hoverLabel = null;
        
        if (activeText != null)
            activeLabel = new LabelBuilder(normalLabel).text(activeText).end();                
        else
            activeLabel = null;                
    }
    
    public static class Builder implements IBuilder<SpriteButton>
    {
        // Required values.
        private final GameWindow window;
        private int x;
        private int y;        
        
        // Optional values.
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);
        private String normalText = "Default";
        private String hoverText = null;
        private String activeText = null;
        private int normalOpacity = 80;
        private int hoverOpacity = 100;
        private int activeOpacity = 100;
        private ButtonType type = ButtonType.NORMAL;  
        private boolean visible = true;
        
        public Builder(GameWindow window, int x, int y)
        {
            this.window = window;
            this.x = x;
            this.y = y;
        }
        
        public Builder(SpriteButton button)
        {
            this.window = button.window;
            this.x = button.x;
            this.y = button.y;
            this.alignment = button.alignment;
            this.normalText = button.normalText;
            this.hoverText = button.hoverText;
            this.normalOpacity = button.opacity;
            this.hoverOpacity = button.hoverOpacity;
            this.activeOpacity = button.activeOpacity;
            this.type = button.type;
            this.visible = button.visible;
        }
        
        public Builder x(int val) { x = val; return this; }        
        public Builder y(int val) { y = val; return this; }
        
        public Builder alignment(EnumSet<Alignment> val) 
        { alignment = val; return this; }
        
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
        
        public Builder normalOpacity(int val)                 
        { normalOpacity = val; return this; }
        
        public Builder hoverOpacity(int val) 
        { hoverOpacity = val; return this; }
        
        public Builder activeOpacity(int val) 
        { activeOpacity = val; return this; }
        
        public Builder type(ButtonType val) 
        { type = val; return this; }
        
        public Builder visible(boolean val) 
        { visible = val; return this; }
        
        public SpriteButton end()
        {
            return new SpriteButton(this);
        }                
    }    
        
    private void drawNormal()
    {                
        sprite.draw(x + offsetX, y + offsetY, width, height, 0.0, opacity);        
        normalLabel.draw();
    }
    
    private void drawActive()
    {        
        sprite.draw(x + offsetX, y + offsetY, width, height, 0.0, activeOpacity);
        
        if (activeLabel != null) activeLabel.draw();
        else normalLabel.draw();
    }
    
    private void drawHover()
    {
        sprite.draw(x + offsetX, y + offsetY, width, height, 0.0, hoverOpacity);
        
        if (hoverLabel != null) hoverLabel.draw();        
        else
        {
            if (activeLabel != null && isActivated() == true)
                activeLabel.draw();
            else
                normalLabel.draw();
        }
    }
    
    private void drawPressed()
    {
        sprite.draw(x + offsetX, y + offsetY, width, height, 0.0, 70);
        normalLabel.draw();
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

    public int getNormalOpacity()
    {
        return getOpacity();
    }

    public void setNormalOpacity(int normalOpacity)
    {
        setOpacity(normalOpacity);
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
    
    public void draw()
    {
        x_ = x;
        y_ = y;
        
        // Don't draw if not visible.
        if (isVisible() == false)
            return;

        // See what state we're in.
        switch (state)
        {
            case NORMAL:
                drawNormal();
                break;
                
            case ACTIVE:
                drawActive();
                break;
                
            case HOVER:
                drawHover();
                break;
                
            case PRESSED:
                drawPressed();
                break;                           
                
            default:
                throw new AssertionError();
        } // end switch
    }   

}
