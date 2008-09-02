package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.*;

/**
 * A bomb tile.
 * 
 * @author cdmckay
 */

public class BombTileEntity extends ItemTileEntity
{
    
    /**
     * Path to the piece selector sprite.
     */
    final private static String PATH = Game.SPRITES_PATH + "/ItemBomb.png";
    
    /**
     * The constructor.
     * 
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public BombTileEntity(final BoardManager boardMan, final TileColor color, 
            final int x, final int y)
    {
        // Invoke super.
        super(PATH, boardMan, color, x, y);
        
        // Set the type.
        this.type = TileType.BOMB;
    }
       
}
