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
    private int targetScore;
    
    public ScoreEvent(Object eventSource, int deltaScore, int targetScore)
    {
        super(eventSource);
        
        this.deltaScore  = deltaScore;
        this.targetScore = targetScore; 
    }

    public int getDeltaScore()
    {
        return deltaScore;
    }

    public int getTargetScore()
    {
        return targetScore;
    }
    
}