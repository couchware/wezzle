/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.event;

import java.util.EventObject;

/**
 * The move event.
 * 
 * @author kgrad
 */
public class MoveEvent extends EventObject
{
    final private int moveCount;
    
    public MoveEvent(Object source, int moveCount)
    {
        super(source);        
        this.moveCount = moveCount;
    }
    
    public int getMoveCount()
    { 
        return moveCount; 
    }
}