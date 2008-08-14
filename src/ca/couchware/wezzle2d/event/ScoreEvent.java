/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.event;

import java.util.EventObject;

/**
 *  A custom score event.
 * 
 * @author kgrad
 */
public class ScoreEvent extends EventObject
{
    private int deltaScore;
    
    public ScoreEvent(int deltaScore, Object eventSource)
    {
        super(eventSource);
        
        this.deltaScore = deltaScore;
    }
    
    public int getScore() { return deltaScore; }
}