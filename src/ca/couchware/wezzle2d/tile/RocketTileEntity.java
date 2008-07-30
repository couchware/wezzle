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
     * The possible directions for the rocket.
     */    
    public static enum Direction
    {
        UP, DOWN, LEFT, RIGHT;
        
        public int toDegrees()
        {
            switch (this)
            {
                case UP:
                    return 90;
                case DOWN:
                    return 270;
                case LEFT:
                    return 180;
                case RIGHT:
                    return 0;
                default:
                    throw new AssertionError();
            }
        }
    }
    
    /**
     * Path to the piece selector sprite.
     */
    final private static String PATH = Game.SPRITES_PATH + "/ItemRocket.png";          
    
    /**
     * The direction of the rocket.
     */
    private final Direction direction;         
    
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
        Direction[] values = Direction.values();
        direction = values[Util.random.nextInt(values.length)];
        
        // Set the item theta.
        switch (direction)
        {
            case UP:
                // Do nothing, it is already pointing up.
                break;
                
            case DOWN:
                itemTheta = Math.toRadians(180);
                break;
                
            case LEFT:
                itemTheta = Math.toRadians(-90);
                break;
                
            case RIGHT:
                itemTheta = Math.toRadians(90);
                break;
        }
    }          

    public double getItemRotation()
    {
        return itemTheta;
    }

    public void setItemRotation(double itemTheta)
    {
        this.itemTheta = itemTheta;
    }   
    
    public Direction getDirection()
    {
        return direction;
    }   
    
}
