package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.manager.Settings;

/**
 * A 2x tile.
 * 
 * @author cdmckay
 */

public class X2TileEntity extends ItemTileEntity
{
    
    /**
     * Path to the piece selector sprite.
     */
    final private static String PATH = Settings.SPRITE_RESOURCES_PATH + "/Item2x.png";       
    
    /**
     * The constructor.
     * 
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public X2TileEntity(final BoardManager boardMan, final TileColor color, 
            final int x, final int y)
    {
        // Invoke super.
        super(PATH, boardMan, color, x, y);
        
        // Set the type.
        this.type = TileType.X2;
    }
      
}
