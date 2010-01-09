package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.manager.Settings;

/**
 * A star tile.
 * 
 * @author cdmckay
 */

public class StarTile extends ItemTile
{
    
    /**
     * Path to the piece selector sprite.
     */
    final private static String PATH = 
            Settings.getSpriteResourcesPath() + "/ItemStar.png";
    
    /**
     * The constructor.
     * 
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public StarTile(final TileColor color, final int x, final int y)
    {
        // Invoke super.
        super(PATH, color, x, y);
        
        // Set the type.
        this.type = TileType.STAR;
    }
    
}
