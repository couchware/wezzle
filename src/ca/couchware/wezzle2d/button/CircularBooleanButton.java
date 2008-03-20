package ca.couchware.wezzle2d.button;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.*;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

/**
 * A circular pause button.
 * @author cdmckay
 */
public class CircularBooleanButton extends BooleanButton
{
    /**
     * The width of the clickable button area.
     */
    final private static int WIDTH = 106;
    
    /**
     * The height of the click button area.
     */
    final private static int HEIGHT = 106;   
    
    /**
     * The normal sprite.
     */
    final private Sprite spriteNormal;
    
    /**
     * The active sprite glow.
     */
    final private Sprite spriteActive;
    
    /**
     * The hover sprite.
     */
    final private Sprite spriteHover;     
    
    /**
     * The button text.
     */
    final private Text buttonText;

    /**
     * The constructor.
     */
    public CircularBooleanButton(final int x, final int y)
    {
        // Invoke super.
        super(x, y, new Ellipse2D.Double(x, y, WIDTH, HEIGHT));
        
        // Load the normal sprite.
        spriteNormal = ResourceFactory.get()
                .getSprite("resources/Button_Normal.png");
        
        // Load the active sprite glow.
        spriteActive = ResourceFactory.get()
                .getSprite("resources/Button_Active.png");
        
        // Load the hover sprite.
        spriteHover = ResourceFactory.get()
                .getSprite("resources/Button_Hover.png");
        
        // Create the button text.
        buttonText = ResourceFactory.get().getText();        
        buttonText.setSize(22);
        buttonText.setColor(Game.TEXT_COLOR);
        buttonText.setAnchor(Text.HCENTER | Text.VCENTER);                
    }
    
    @Override
    public void drawNormal()
    {
        Rectangle bounds = shape.getBounds();
        
        spriteNormal.draw(x - 18, y - 18);
        
        buttonText.setX(x + bounds.width / 2);
        buttonText.setY(y + bounds.height / 2);
        buttonText.setText(text);
        buttonText.draw();
    }

//    @Override
    public void drawActive()
    {
        spriteActive.draw(x - 18, y - 18);
        drawNormal();
    }

//    @Override
    public void drawHover()
    {
        Rectangle bounds = shape.getBounds();
        
        if (activated == true)
            spriteActive.draw(x - 18, y - 18);
        
        spriteHover.draw(x - 18, y - 18);
        
        buttonText.setX(x + bounds.width / 2);
        buttonText.setY(y + bounds.height / 2);
        buttonText.setText(text);
        buttonText.draw();
    }

//    @Override
    public void drawPressed()
    {
        Rectangle bounds = shape.getBounds();
        
        if (activated == true)
            spriteActive.draw(x - 18, y - 18);
        
        spriteHover.draw(x - 18, y - 18);
        
        buttonText.setX(x + bounds.width / 2);
        buttonText.setY(y + bounds.height / 2 + 1);
        buttonText.setText(text);
        buttonText.draw();
    }

}
