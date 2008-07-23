package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.graphics.Sprite;
import ca.couchware.wezzle2d.*;

/**
 * A bomb tile.
 * 
 * @author cdmckay
 */

public class ItemTileEntity extends TileEntity
{
    /**
     * The sprite representing the bomb graphic.
     */
    final protected Sprite itemSprite;
    
    /**
     * The constructor.
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public ItemTileEntity(final String path, 
            final BoardManager boardMan, final TileColor color, 
            final int x, final int y)
    {
        // Invoke super.
        super(boardMan, color, x, y);
        
        // Load bomb sprite.
        itemSprite = ResourceFactory.get().getSprite(path);
    }
    
    
    /**
     * Override that draw muthafucka.
     */
    @Override
    public void draw()
    {
        if (isVisible() == false)
            return;
        
        // Invoke super draw.
        super.draw();        
        
        // Draw bomb on top of it.
        itemSprite.draw((int) x2, (int) y2, width, height, theta, opacity);
    }
}
