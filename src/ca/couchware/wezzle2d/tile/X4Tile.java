package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.manager.Settings;

/**
 * A 4x tile.
 * 
 * @author cdmckay
 */

public class X4Tile extends ItemTile
{
    
    /**
     * Path to the piece selector sprite.
     */
    final private static String PATH = 
            Settings.getSpriteResourcesPath() + "/Item4x.png";
        
    /**
     * The constructor.
     * 
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public X4Tile(final TileColor color, final int x, final int y)
    {
        // Invoke super.
        super(PATH, color, x, y);
        
        // Set the type.
        this.type = TileType.X4;
    }
    
}
