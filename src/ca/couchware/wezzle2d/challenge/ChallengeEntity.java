package ca.couchware.wezzle2d.challenge;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.XYPosition;
import java.awt.Rectangle;

/**
 * A class for:
 * 1) popping up a challenge notifier,
 * 2) keeping track of said challenge,
 * 3) removing the notifier.
 * 
 * @author cdmckay
 */
public abstract class ChallengeEntity extends Entity
{

    //--------------------------------------------------------------------------
    // Static Attributes
    //--------------------------------------------------------------------------
    
    final protected static String PATH = Game.SPRITES_PATH 
            + "/ChallengeCircle.png";
    
    //--------------------------------------------------------------------------
    // Instance Attributes
    //--------------------------------------------------------------------------
        
    /**
     * The visibility of the challenge notifier.
     */
    protected boolean visible;
    
    /**
     * The dirtiness of the challenge notifier.
     */
    protected boolean dirty;
    
    /**
     * The x coordinate.
     */
    protected int x;
    
    /**
     * The y coordinate.
     */
    protected int y;
    
    /**
     * The previous x coordinate.
     */
    protected int x_;
    
    /**
     * The previous y coordinate.
     */
    protected int y_;
    
    /**
     * The width of the challenge notifier.
     */
    protected int width;
    
    /**
     * The height of the challenge notifier.
     */
    protected int height;
    
    /**
     * The previous width.
     */
    protected int width_;
    
    /**
     * The previous height.
     */
    protected int height_;
    
    /**
     * The alignment.
     */
    protected int alignment;
    
    /**
     * The x offset.
     */
    protected int offsetX;
    
    /**
     * The y offset.
     */
    protected int offsetY;
        
    /**
     * The challenge background sprite.
     */
    protected Sprite sprite;
    
    /**
     * The challenge header text.
     */
    protected Label headerLabel;
    
    /**
     * The challenge body text, line 1.
     */
    protected Label bodyLabel1;
    
    /**
     * The challenge body text, line 2.
     */
    protected Label bodyLabel2;
        
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a new challenge with its notifier's top-left corner placed
     * at (x,y).
     * 
     * @param x
     * @param y
     */
    public ChallengeEntity(final int x, final int y)
    {
        // Load the the sprite.
        sprite = ResourceFactory.get().getSprite(PATH);
        
        // Set the (x,y) position and the previous ones too.
        this.x = x;
        this.y = y;
        this.x_ = x;
        this.y_ = y;
        
        // Set the dimensions.
        this.width = sprite.getWidth();
        this.height = sprite.getHeight();
        this.width_ = width;
        this.height_ = height;                
        
        // Create the header text.
        headerLabel = ResourceFactory.get().getText();                
        headerLabel.setSize(18);
        headerLabel.setColor(Game.TEXT_COLOR);
        headerLabel.setAlignment(Label.HCENTER | Label.VCENTER);
        headerLabel.setXYPosition(x + width / 2, y + 62);
        headerLabel.setText("Challenge!");      
        
        // Create the body text.
        bodyLabel1 = ResourceFactory.get().getText();
        bodyLabel1.setSize(12);
        bodyLabel1.setColor(Game.TEXT_COLOR);
        bodyLabel1.setAlignment(Label.HCENTER | Label.VCENTER);
        bodyLabel1.setXYPosition(x + width / 2, y + 90);        
        
        bodyLabel2 = ResourceFactory.get().getText();
        bodyLabel2.setSize(12);
        bodyLabel2.setColor(Game.TEXT_COLOR);
        bodyLabel2.setAlignment(Label.HCENTER | Label.VCENTER);
        bodyLabel2.setXYPosition(x + width / 2, y + 105);        
    }
    
    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------
    
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setDirty(boolean dirty)
    {
        this.dirty = dirty;
    }

    public boolean isDirty()
    {
        return dirty;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public XYPosition getXYPosition()
    {
        return new XYPosition(x, y);
    }

    public void setXYPosition(int x, int y)
    {
        setX(x);
        setY(y);
    }

    public void setXYPosition(XYPosition p)
    {
        setX(p.x);
        setY(p.y);
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }    
    
    public int getAlignment()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setAlignment(int alignment)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    //--------------------------------------------------------------------------
    // Logic
    //--------------------------------------------------------------------------
    
    public abstract void updateLogic(final Game game);    
    
    //--------------------------------------------------------------------------
    // Draw
    //--------------------------------------------------------------------------
    
    public void draw()
    {
        sprite.draw(x, y, width, height, 0.0, 85);
        headerLabel.draw();
        bodyLabel1.draw();
        bodyLabel2.draw();
    }

    public Rectangle getDrawRect()
    {
        Rectangle rect = new Rectangle(x_, y_, width + 2, height + 2);
        rect.add(new Rectangle(x, y, width + 2, height + 2));        
        //rect.translate(offsetX, offsetY);
        
        return rect;
    }

    public void resetDrawRect()
    {
        x_ = x;
        y_ = y;
        width_ = width;
        height_ = height;
    }

}
