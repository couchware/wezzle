package ca.couchware.wezzle2d.ui.button;

import ca.couchware.wezzle2d.graphics.Sprite;
import ca.couchware.wezzle2d.ui.Label;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.util.*;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.EnumSet;

/**
 * A circular pause button.
 * @author cdmckay
 */
public class RectangularBooleanButton extends BooleanButton
{          
    /**
     * The normal button, used in most menues.
     */
    final public static int TYPE_NORMAL = 0;
    
    /**
     * The large button.
     */
    final public static int TYPE_LARGE = 1;    
    
    /**
     * The button dimensions.
     */
    final protected static Dimension[] DIMENSIONS = new Dimension[]
    {
        new Dimension(153, 49), // TYPE_NORMAL
        new Dimension(210, 130) // TYPE_LARGE
    };
    
    /**
     * The normal sprite.
     */
    final protected Sprite spriteNormal;       
    
    /**
     * The button text.
     */
    final protected ILabel buttonLabel;   
    
    /**
     * The hover opacity.
     */
    protected int hoverOpacity;
    
    /**
     * The active opacity.
     */
    protected int activeOpacity;

    /**
     * Creates a button at the coordinates provided.
     * @param x
     * @param y
     */
    public RectangularBooleanButton(final GameWindow window, 
            final int x, final int y, int type, String text)
    {
        // Invoke super.
        super(window, x, y, 
                DIMENSIONS[type].width,
                DIMENSIONS[type].height, 
                new Rectangle(x, y, 
                    DIMENSIONS[type].width,
                    DIMENSIONS[type].height));
        
        // Construct the sprite name.
        String spriteNormalName = "RectangularButton_" 
                + width + "x" + height + ".png";
        
        // Load the normal sprite.
        spriteNormal = ResourceFactory.get()
                .getSprite(Game.SPRITES_PATH + "/" + spriteNormalName);             
        
        // Set the text.
        setText(text);
        
        // Create the button text.
//        buttonLabel = ResourceFactory.get().getLabel(0, 0);        
//        buttonLabel.setSize(18);
//        buttonLabel.setColor(Game.TEXT_COLOR);
//        buttonLabel.setAlignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER));    
        buttonLabel = new LabelBuilder(x, y)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR).size(18).text(getText()).end();                
        
        // Set the normal and active opacities.        
        hoverOpacity = 100;
        activeOpacity = 100;
    }
    
    public RectangularBooleanButton(final GameWindow window, 
            final int x, final int y, String text)
    {
        this(window, x, y, TYPE_NORMAL, text);
    }
    
    @Override
    public void drawNormal()
    {                
        spriteNormal.draw(x + offsetX, y + offsetY, 
                width, height, 0.0, opacity);
        
//        buttonLabel.setX(x + offsetX + width / 2);
//        buttonLabel.setY(y + offsetY + height / 2);
//        buttonLabel.setText(getText());
        buttonLabel.draw();
    }

    @Override
    public void drawActive()
    {        
        spriteNormal.draw(x + offsetX, y + offsetY, 
                width, height, 0.0, activeOpacity);
        
//        buttonLabel.setX(x + offsetX + width / 2);
//        buttonLabel.setY(y + offsetY + height / 2);
//        buttonLabel.setText(getText());
        buttonLabel.draw();
    }

    @Override
    public void drawHover()
    {
//        if (opacity == hoverOpacity)
//        {
            spriteNormal.draw(x + offsetX, y + offsetY, 
                    width, height, 0.0, hoverOpacity);
//        }
//        else
//        {
//            
//        }
        
//        buttonLabel.setX(x + offsetX + width / 2);
//        buttonLabel.setY(y + offsetY + height / 2);
//        buttonLabel.setText(getText());
        buttonLabel.draw();
    }

    @Override
    public void drawPressed()
    {
        spriteNormal.draw(x + offsetX, y + offsetY, width, height, 0.0, 70);
        
//        buttonLabel.setX(x + offsetX + width / 2);
//        buttonLabel.setY(y + offsetY + height / 2 + 1);
//        buttonLabel.setText(getText());
        buttonLabel.draw();
    }   

    public int getActiveOpacity()
    {
        return activeOpacity;
    }

    public void setActiveOpacity(int activeOpacity)
    {
        this.activeOpacity = activeOpacity;
        setDirty(true);
    }

    public int getHoverOpacity()
    {
        return hoverOpacity;
    }

    public void setHoverOpacity(int hoverOpacity)
    {
        this.hoverOpacity = hoverOpacity;
        setDirty(true);
    }        

    public int getNormalOpacity()
    {
        return opacity;
    }

    public void setNormalOpacity(int normalOpacity)
    {
        this.opacity = normalOpacity;
        setDirty(true);
    }               

}
