package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.manager.Settings;

/**
 * A 3x tile.
 * 
 * @author cdmckay
 */

public class X3Tile extends ItemTile
{
    
    /**
     * Path to the piece selector sprite.
     */
    final private static String PATH = 
            Settings.getSpriteResourcesPath() + "/Item3x.png";
    
    
    /**
     * The constructor.
     * 
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public X3Tile(final BoardManager boardMan, final TileColor color, 
            final int x, final int y)
    {
        // Invoke super.
        super(PATH, boardMan, color, x, y);  
        
        // Set the type.
        this.type = TileType.X3;
    }
    
}
