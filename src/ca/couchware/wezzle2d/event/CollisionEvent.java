/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.event;

import ca.couchware.wezzle2d.tracker.Chain;
import java.util.EventObject;

/**
 *
 * @author kgrad
 */
public class CollisionEvent extends EventObject 
{
    private Chain chain;
    
    public CollisionEvent(Object source, Chain chain)
    {
        super(source);
        this.chain = chain;
    }
    
    public Chain getChain()
    {
        return chain;
    }
}
