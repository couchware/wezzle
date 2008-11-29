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
    private List<TileEntity> collisionList;
    
    public CollisionEvent(Object source, List<TileEntity> collisionList)    
    {
        super(source);
        this.collisionList = collisionList;
    }
    
    public List<TileEntity> getCollisionList()
    {
        return this.collisionList;
    }
}
