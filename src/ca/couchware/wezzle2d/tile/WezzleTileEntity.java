package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.manager.Settings;

/**
 * A Wezzle tile.
 * 
 * @author cdmckay
 */

public class WezzleTileEntity extends ItemTileEntity
{
    
    /**
     * Path to the tile sprite.
     */
    final private static String PATH = 
            Settings.getSpriteResourcesPath() + "/ItemWezzle2.png";
    
    /**
     * The constructor.
     * 
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public WezzleTileEntity(
            final BoardManager boardMan, 
            final TileColor color, 
            final int x, final int y)
    {
        // Invoke super.
        super(PATH, boardMan, color, x, y);
        
        // Set the type.
        this.type = TileType.WEZZLE;
    }
    
}
