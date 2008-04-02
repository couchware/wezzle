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
    final private Sprite spriteGlow;
    
    /**
     * The hover sprite.
     */
    final private Sprite spriteHover;     
    
    /**
     * The button text.
     */
    final private Label buttonLabel;

    /**
     * Creates a button at the coordinates provided.
     * @param x
     * @param y
     */
    public CircularBooleanButton(final int x, final int y)
    {
        // Invoke super.
        super(x, y, WIDTH, HEIGHT, new Ellipse2D.Double(x, y, WIDTH, HEIGHT));
        
        // Load the normal sprite.
        spriteNormal = ResourceFactory.get()
                .getSprite(Game.SPRITES_PATH + "/Button_Normal.png");
        
        // Load the active sprite glow.
        spriteGlow = ResourceFactory.get()
                .getSprite(Game.SPRITES_PATH + "/Button_Glow.png");
        
        // Load the hover sprite.
        spriteHover = ResourceFactory.get()
                .getSprite(Game.SPRITES_PATH + "/Button_Hover.png");
        
        // Create the button text.
        buttonLabel = ResourceFactory.get().getText();        
        buttonLabel.setSize(22);
        buttonLabel.setColor(Game.TEXT_COLOR);
        buttonLabel.setAlignment(Label.HCENTER | Label.VCENTER);      
        
        // Create the draw rectangle.
        drawRect = 
                new Rectangle(x - 18, y - 18, x + width + 18, y + height + 18);
    }
    
    @Override
    public void drawNormal()
    {
        Rectangle bounds = shape.getBounds();
        
        spriteNormal.draw(x + offsetX, y + offsetY);
        
        buttonLabel.setX(x + offsetX + bounds.width / 2);
        buttonLabel.setY(y + offsetY + bounds.height / 2);
        buttonLabel.setText(text);
        buttonLabel.draw();
    }

//    @Override
    public void drawActive()
    {
        spriteGlow.draw(x + offsetX - 18, y + offsetY - 18);
        drawNormal();
    }

//    @Override
    public void drawHover()
    {
        Rectangle bounds = shape.getBounds();
        
        if (activated == true)
            drawGlow();
        
        spriteHover.draw(x + offsetX, y + offsetY);
        
        buttonLabel.setX(x + offsetX + bounds.width / 2);
        buttonLabel.setY(y + offsetY + bounds.height / 2);
        buttonLabel.setText(text);
        buttonLabel.draw();
    }

//    @Override
    public void drawPressed()
    {
        Rectangle bounds = shape.getBounds();
        
        if (activated == true)
            drawGlow();
        
        spriteHover.draw(x + offsetX, y + offsetY);
        
        buttonLabel.setX(x + offsetX + bounds.width / 2);
        buttonLabel.setY(y + offsetY + bounds.height / 2 + 1);
        buttonLabel.setText(text);
        buttonLabel.draw();
    }

    private void drawGlow()
    {
        spriteGlow.draw(x + offsetX - 18, y + offsetY - 18);
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
    
    public void setDirty(boolean dirty)
    {
        this.dirty = dirty;
    }

    public boolean isDirty()
    {
        return dirty;
    }
    
    public Rectangle getDrawRect()
    {
        Rectangle rect = new Rectangle(x_ - 18, y_ - 18, 
                width + 36 + 2, height + 36 + 2);
        
        if (x_ != x || y_ != y)
            rect.add(new Rectangle(x - 18, y - 18,
                    width + 36 + 2, height + 36 + 2));
        
        rect.translate(offsetX, offsetY);
        
        return rect;
    }        

}
