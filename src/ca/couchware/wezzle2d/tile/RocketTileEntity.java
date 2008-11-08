package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.manager.Settings;
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
    final private static String PATH = 
            Settings.getSpriteResourcesPath() + "/ItemRocket.png";          
    
    /**
     * The direction of the rocket.
     */
    private Direction direction;         
    
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
        itemTheta = determineItemTheta(direction);
        
        // Set the type.
        this.type = TileType.ROCKET;
    }              
    
    final private double determineItemTheta(Direction direction)
    {
        // Set the item theta.
        switch (direction)
        {
            case UP:
                return 0;                
                
            case DOWN:
                return Math.toRadians(180);                
                
            case LEFT:
                return Math.toRadians(-90);                
                
            case RIGHT:
                return Math.toRadians(90);
                
            default: throw new AssertionError();
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
    
    public void setDirection(Direction direction)
    {
        this.direction = direction;
        this.itemTheta = determineItemTheta(direction);
    }
    
}
