/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.event;

import java.util.EventObject;

/**
 *  A custom move event.
 * 
 * @author kgrad
 */
public class MoveEvent extends EventObject
{
    private int moveCount;
    
    public MoveEvent(int deltaMoves, Object eventSource)
    {
        super(eventSource);
        
        moveCount = deltaMoves;
    }
    
    public int getMoveCount(){ return this.moveCount; }
}