/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.event;

import ca.couchware.wezzle2d.tile.TileEntity;
import ca.couchware.wezzle2d.tile.TileType;
import java.util.ArrayList;
import java.util.EventObject;

/**
 *
 * @author kgrad
 */
public class CollisionEvent extends EventObject 
{
    private ArrayList<TileEntity> items;
    
    public CollisionEvent(Object source, ArrayList<TileEntity> collisions)    
    {
        super(source);
        this.items = collisions;
    }
    
    public ArrayList<TileEntity> getSet()
    {
        return this.items;
    }
}
