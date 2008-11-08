package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.manager.Settings;

/**
 * A star tile.
 * 
 * @author cdmckay
 */

public class StarTileEntity extends ItemTileEntity
{
    
    /**
     * Path to the piece selector sprite.
     */
    final private static String PATH = Settings.SPRITE_RESOURCES_PATH + "/ItemStar.png";
    
    /**
     * The constructor.
     * 
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public StarTileEntity(final BoardManager boardMan, final TileColor color, 
            final int x, final int y)
    {
        // Invoke super.
        super(PATH, boardMan, color, x, y);
        
        // Set the type.
        this.type = TileType.STAR;
    }
    
}
