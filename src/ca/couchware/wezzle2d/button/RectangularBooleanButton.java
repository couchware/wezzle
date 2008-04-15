package ca.couchware.wezzle2d.button;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.*;
import java.awt.Rectangle;

/**
 * A circular pause button.
 * @author cdmckay
 */
public class RectangularBooleanButton extends BooleanButton
{
    /**
     * The width of the clickable button area.
     */
    final private static int WIDTH = 153;
    
    /**
     * The height of the click button area.
     */
    final private static int HEIGHT = 49;           
    
    /**
     * The normal sprite.
     */
    final private Sprite spriteNormal;       
    
    /**
     * The button text.
     */
    final private Label buttonLabel;
    
    /**
     * The normal opacity.
     */
    private int normalOpacity;
    
    /**
     * The active opacity.
     */
    private int activeOpacity;

    /**
     * Creates a button at the coordinates provided.
     * @param x
     * @param y
     */
    public RectangularBooleanButton(final int x, final int y)
    {
        // Invoke super.
        super(x, y, WIDTH, HEIGHT, new Rectangle(x, y, WIDTH, HEIGHT));
        
        // Load the normal sprite.
        spriteNormal = ResourceFactory.get()
                .getSprite(Game.SPRITES_PATH + "/RectangularButton_Normal.png");             
        
        // Create the button text.
        buttonLabel = ResourceFactory.get().getLabel(0, 0);        
        buttonLabel.setSize(22);
        buttonLabel.setColor(Game.TEXT_COLOR);
        buttonLabel.setAlignment(Label.HCENTER | Label.VCENTER);    
        
        // Set the normal and active opacities.
        normalOpacity = 100;
        activeOpacity = 100;
    }
    
    @Override
    public void drawNormal()
    {                
        spriteNormal.draw(x + offsetX, y + offsetY, 
                width, height, 0.0, normalOpacity);
        
        buttonLabel.setX(x + offsetX + width / 2);
        buttonLabel.setY(y + offsetY + height / 2);
        buttonLabel.setText(text);
        buttonLabel.draw();
    }

//    @Override
    public void drawActive()
    {
        spriteNormal.draw(x + offsetX, y + offsetY, 
                width, height, 0.0, activeOpacity);
        
        buttonLabel.setX(x + offsetX + width / 2);
        buttonLabel.setY(y + offsetY + height / 2);
        buttonLabel.setText(text);
        buttonLabel.draw();
    }

//    @Override
    public void drawHover()
    {
        drawActive();
    }

//    @Override
    public void drawPressed()
    {
       spriteNormal.draw(x + offsetX, y + offsetY, width, height, 0.0, 70);
        
        buttonLabel.setX(x + offsetX + width / 2);
        buttonLabel.setY(y + offsetY + height / 2 + 1);
        buttonLabel.setText(text);
        buttonLabel.draw();
    }
   
    /**
     * Get the label object for this button.
     * 
     * @return The label object.
     */
    public Label getLabel()
    {
        return buttonLabel;
    }

    public int getActiveOpacity()
    {
        return activeOpacity;
    }

    public void setActiveOpacity(int activeOpacity)
    {
        this.activeOpacity = activeOpacity;
    }

    public int getNormalOpacity()
    {
        return normalOpacity;
    }

    public void setNormalOpacity(int normalOpacity)
    {
        this.normalOpacity = normalOpacity;
    }        
    
    @Override
    public Rectangle getDrawRect()
    {
        Rectangle rect = new Rectangle(x_, y_, width + 2, height + 2);
        
        if (x_ != x || y_ != y)
            rect.add(new Rectangle(x, y, width + 2, height + 2));
        
        rect.translate(offsetX, offsetY);
        
        return rect;
    }        

}
