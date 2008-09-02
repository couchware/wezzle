package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.*;

/**
 * A 3x tile.
 * 
 * @author cdmckay
 */

public class X3TileEntity extends ItemTileEntity
{
    
    /**
     * Path to the piece selector sprite.
     */
    final private static String PATH = Game.SPRITES_PATH + "/Item3x.png";
    
    
    /**
     * The constructor.
     * 
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public X3TileEntity(final BoardManager boardMan, final TileColor color, 
            final int x, final int y)
    {
        // Invoke super.
        super(PATH, boardMan, color, x, y);  
        
        // Set the type.
        this.type = TileType.X3;
    }
    
}
