/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.event;

import ca.couchware.wezzle2d.tile.TileEntity;
import java.util.EventObject;
import java.util.List;

/**
 *
 * @author kgrad
 */
public class CollisionEvent extends EventObject 
{
    private List<TileEntity> items;
    
    public CollisionEvent(Object source, List<TileEntity> collisions)    
    {
        super(source);
        this.items = collisions;
    }
    
    public List<TileEntity> getSet()
    {
        return this.items;
    }
}
