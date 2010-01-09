package ca.couchware.wezzle2d.tile;

import ca.couchware.wezzle2d.graphics.ISprite;
import ca.couchware.wezzle2d.*;

/**
 * An abstract class for making item tiles like bombs and rockets.
 * 
 * @author cdmckay
 */

public abstract class ItemTile extends Tile
{
    /** The sprite representing the item graphic. */
    final protected ISprite itemSprite;       
    
    /** The rotation of the item. */
    protected double itemTheta;
    
    /**
     * The constructor.
     * @param boardMan
     * @param color
     * @param x
     * @param y
     */    
    public ItemTile(final String path, final TileColor color, 
            final int x, final int y)
    {
        // Invoke super.
        super(color, x, y);
        
        // Load bomb sprite.
        itemSprite = ResourceFactory.get().getSprite(path);
        
        // Initialize the item theta.
        itemTheta = 0;
    }
    
    
    /**
     * Override that draw muthafucka.
     */
    @Override
    public boolean draw()
    {
        if (isVisible() == false)
            return false;
        
        // Invoke super draw.
        super.draw();        
        
        // Draw bomb on top of it.
        //itemSprite.draw((int) x2, (int) y2, width, height, itemTheta, opacity);
        itemSprite.draw(x, y).width(width).height(height)
                .theta(itemTheta, width / 2, height /2)
                .opacity(opacity).end();
        
        return true;
    }

    public double getItemTheta()
    {
        return itemTheta;
    }

    public void setItemTheta(double itemTheta)
    {
        this.itemTheta = itemTheta;
    }
        
}
