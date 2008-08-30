package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.util.*;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * A class for creating a rectangular boolean button.
 * 
 * @author cdmckay
 */
public class SpriteButton extends AbstractSpriteButton
{          
    /**
     * The different sizes of the boolean buttons.
     */
    public static enum Type
    {
        NORMAL, LARGE, THIN
    }
    
    /**
     * The button dimensions.
     */
    final private static Map<Type, ImmutableDimension> dimensionMap;       
    
    /**
     * The static constructor.
     */
    static
    {
        dimensionMap = new EnumMap<Type, ImmutableDimension>(Type.class);        
        dimensionMap.put(Type.NORMAL, new ImmutableDimension(153, 49));
        dimensionMap.put(Type.LARGE, new ImmutableDimension(210, 130));
        dimensionMap.put(Type.THIN, new ImmutableDimension(220, 40));
    }
           
    /**
     * The type of button.
     */
    final private Type type;
    
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
     * The size of the text on the button.
     */
    final private float textSize;
    
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
     * The pressed opacity.
     */
    private int pressedOpacity;
    
    /**
     * The active opacity.
     */
    private int onOpacity;
    
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
        super(builder.x, builder.y);
        
        // Assign values from builder.               
        this.normalText = builder.normalText;       
        this.hoverText = builder.hoverText;
        this.activeText = builder.activeText;
        this.textSize = builder.textSize;
        this.opacity = limitOpacity(builder.offOpacity);
        this.hoverOpacity = limitOpacity(builder.hoverOpacity);
        this.pressedOpacity = limitOpacity(builder.pressedOpacity);
        this.onOpacity = limitOpacity(builder.onOpacity);
        this.type = builder.type;
        
        // Set the visibility.
        this.visible = builder.visible;
        this.disabled = builder.disabled;
        
        // Assign values based on the values from builder.
        final ImmutableDimension d = dimensionMap.get(type);
        this.width = d.getWidth();
        this.height = d.getHeight();                
        
        // Determine the offsets.
        this.alignment = builder.alignment;
        offsetX = determineOffsetX(alignment, width);
        offsetY = determineOffsetY(alignment, height);
        
        // Set the shape.
        this.shape = new ImmutableRectangle(x + offsetX, y + offsetY, width, height);
        
        // Construct the sprite name.                
        String spriteNormalName = "RectangularButton_" 
            + width + "x" + height + ".png";
                                
        // Load the normal sprite.
        sprite = ResourceFactory.get()
                .getSprite(Game.SPRITES_PATH + "/" + spriteNormalName);                            
        
        // Create the normal label.
        normalLabel = new LabelBuilder(x, y)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR1).size(textSize).text(normalText).end(); 
        
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
        private int x;
        private int y;        
        
        // Optional values.
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);
        private String normalText = "Default";
        private String hoverText = null;
        private String activeText = null;
        private float textSize = 18f;
        private int offOpacity = 80;
        private int hoverOpacity = 100;
        private int pressedOpacity = 100;
        private int onOpacity = 100;
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
            this.x = button.x;
            this.y = button.y;
            this.alignment = button.alignment.clone();
            this.normalText = button.normalText;
            this.hoverText = button.hoverText;
            this.textSize = button.textSize;
            this.offOpacity = button.opacity;           
            this.hoverOpacity = button.hoverOpacity;
            this.pressedOpacity = button.pressedOpacity;
            this.onOpacity = button.onOpacity;
            this.type = button.type;
            this.visible = button.visible;
            this.disabled = button.disabled;
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
        
        public Builder textSize(float val)
        { textSize = val; return this; }
        
        public Builder offOpacity(int val)                 
        { offOpacity = val; return this; }
        
        public Builder hoverOpacity(int val) 
        { hoverOpacity = val; return this; }
        
        public Builder pressedOpacity(int val)
        { pressedOpacity = val; return this; }
        
        public Builder onOpacity(int val) 
        { onOpacity = val; return this; }
        
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
        
    private void drawNormal()
    {                
        sprite.draw(x + offsetX, y + offsetY, width, height, 0.0, opacity);        
        normalLabel.draw();
    }
    
    private void drawActivated()
    {        
        sprite.draw(x + offsetX, y + offsetY, width, height, 0.0, onOpacity);
        
        if (activeLabel != null) activeLabel.draw();
        else normalLabel.draw();
    }
    
    private void drawHovered()
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
        sprite.draw(x + offsetX, y + offsetY, width, height, 0.0, pressedOpacity);
        normalLabel.translate(0, 1);
        normalLabel.draw();
        normalLabel.translate(0, -1);
    }   

    public int getOnOpacity()
    {
        return onOpacity;
    }

    public void setOnOpacity(int activeOpacity)
    {
        this.onOpacity = limitOpacity(activeOpacity);
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

    public int getOffOpacity()
    {
        return getOpacity();
    }

    public void setOffOpacity(int offOpacity)
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
        
        //window.drawRect(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());
        
        return true;
    }

}
