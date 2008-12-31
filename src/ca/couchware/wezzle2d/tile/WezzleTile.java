package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.util.Util;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * A Wezzle tile.
 * 
 * @author cdmckay
 */

public class WezzleTile extends ItemTile
{
    
    /**
     * Path to the tile sprite.
     */
    final private static String PATH = 
            Settings.getSpriteResourcesPath() + "/ItemWezzle2.png";
    
    /**
     * The possible types a wezzle tile can wrap.
     */
    final private static List<TileType> wrappedTypeList = 
            new ArrayList<TileType>(
                EnumSet.of(TileType.ROCKET));
    
    /**
     * The type of tile it is wrapping.
     */
    final private Tile wrappedTile;
    
    /**
     * The constructor.
     * 
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public WezzleTile(
            final BoardManager boardMan, 
            final TileColor color, 
            final int x, final int y)
    {
        // Invoke super.
        super(PATH, boardMan, color, x, y);
        
        // Set the type.
        this.type = TileType.WEZZLE;
        
        // Set the wrapped type.
        TileType wrappedType = wrappedTypeList.get(Util.random.nextInt(
                wrappedTypeList.size()));
        this.wrappedTile = boardMan.makeTile(wrappedType, color, x, y);
    }

    public Tile getWrappedTile()
    {
        return wrappedTile;
    }
        
}
