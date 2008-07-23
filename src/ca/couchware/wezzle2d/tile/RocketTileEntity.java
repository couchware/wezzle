package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.Util;

/**
 * A rocket tile.
 * 
 * @author cdmckay
 */

public class RocketTileEntity extends ItemTileEntity
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
    final private static String PATH = Game.SPRITES_PATH + "/ItemRocket.png";          
    
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
    public RocketTileEntity(final BoardManager boardMan, final TileColor color, 
            final int x, final int y)
    {
        // Invoke super.
        super(PATH, boardMan, color, x, y);
                               
        // Determine a random rotation.
        degrees = Util.random.nextInt(4) * 90;
        itemTheta = Math.toRadians(degrees);               
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
