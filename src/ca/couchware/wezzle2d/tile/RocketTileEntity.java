package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.graphics.Sprite;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.Util;

/**
 * A bomb tile.
 * 
 * @author cdmckay
 */

public class RocketTileEntity extends TileEntity
{
    /**
     * The rocket is facing up.
     */
    final public static int ANGLE_UP = 0;
    
    /**
     * The rocket is facing up.
     */
    final public static int ANGLE_LEFT = 270;
    
    /**
     * The rocket is facing up.
     */
    final public static int ANGLE_RIGHT = 90;
    
    /**
     * The rocket is facing up.
     */
    final public static int ANGLE_DOWN = 180;
    
    /**
     * Path to the piece selector sprite.
     */
    final private String PATH = Game.SPRITES_PATH + "/ItemRocket.png";
    
    /**
     * The sprite representing the item graphic.
     */
    final private Sprite itemSprite;       
    
    /**
     * The rotation of the rocket.
     */
    private double itemTheta;
    
    /**
     * The direction of the rocket.
     */
    private int degrees;
    
    
    /**
     * The constructor.
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public RocketTileEntity(final BoardManager boardMan, final int color, 
            final int x, final int y)
    {
        // Invoke super.
        super(boardMan, color, x, y);
                       
        // Load bomb sprite.
        itemSprite = ResourceFactory.get().getSprite(PATH);
        
        // Determine a random rotation.
        degrees = Util.random.nextInt(4) * 90;
        itemTheta = Math.toRadians(degrees);               
    }
        
    /**
     * Override that draw muthafucka.
     */
    @Override
    public void draw()
    {
        if (isVisible() == false)
            return;
        
        // Invoke super draw.
        super.draw();        
        
        // Draw bomb on top of it.
        itemSprite.draw((int) x2, (int) y2, width, height, 
                itemTheta, opacity);
    }

    public double getItemRotation()
    {
        return itemTheta;
    }

    public void setItemRotation(double itemTheta)
    {
        this.itemTheta = itemTheta;
    }   
    
    public int getDirection()
    {
        return degrees;
    }

    public void setDirection(int direction)
    {
        this.degrees = direction;
    }        
}
