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
    public MoveEvent(Object eventSource)
    {
        super(eventSource);
    }
}