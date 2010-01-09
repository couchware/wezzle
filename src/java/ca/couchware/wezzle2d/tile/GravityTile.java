package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.manager.Settings;

/**
 * The gravity tile.
 * 
 * @author cdmckay
 */

public class GravityTile extends ItemTile
{
    
    /**
     * Path to the piece selector sprite.
     */
    final private static String PATH = 
            Settings.getSpriteResourcesPath() + "/ItemGravity.png";
    
    /**
     * The constructor.
     * 
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public GravityTile(final TileColor color, final int x, final int y)
    {
        // Invoke super.
        super(PATH, color, x, y);
        
        // Set the type.
        this.type = TileType.GRAVITY;
    }
       
}
