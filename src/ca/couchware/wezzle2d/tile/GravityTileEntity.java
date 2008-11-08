package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.manager.Settings;

/**
 * The gravity tile.
 * 
 * @author cdmckay
 */

public class GravityTileEntity extends ItemTileEntity
{
    
    /**
     * Path to the piece selector sprite.
     */
    final private static String PATH = Settings.SPRITE_RESOURCES_PATH + "/ItemGravity.png";
    
    /**
     * The constructor.
     * 
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public GravityTileEntity(final BoardManager boardMan, final TileColor color, 
            final int x, final int y)
    {
        // Invoke super.
        super(PATH, boardMan, color, x, y);
        
        // Set the type.
        this.type = TileType.GRAVITY;
    }
       
}
