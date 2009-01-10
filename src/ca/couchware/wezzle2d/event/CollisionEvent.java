/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.event;

import ca.couchware.wezzle2d.tile.Tile;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

/**
 *
 * @author kgrad
 */
public class CollisionEvent extends EventObject 
{
    private List<Tile> collisionList;
    
    public CollisionEvent(Object source, List<Tile> collisionList)    
    {
        super(source);
        this.collisionList = Collections.unmodifiableList(collisionList);
    }
    
    public List<Tile> getCollisionList()
    {
        return this.collisionList;
    }
}
