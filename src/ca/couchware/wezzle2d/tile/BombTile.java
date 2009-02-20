package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.manager.Settings;

/**
 * A bomb tile.
 * 
 * @author cdmckay
 */

public class BombTile extends ItemTile
{
    
    /**
     * Path to the piece selector sprite.
     */
    final private static String PATH = 
            Settings.getSpriteResourcesPath() + "/ItemBomb.png";
    
    /**
     * The constructor.
     * 
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public BombTile(final TileColor color, final int x, final int y)
    {
        // Invoke super.
        super(PATH, color, x, y);
        
        // Set the type.
        this.type = TileType.BOMB;
    }
       
}
