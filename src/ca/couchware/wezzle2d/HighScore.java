package ca.couchware.wezzle2d;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kevin
 */
public class HighScore 
{

    private int score;
    private String key;
    
    public HighScore(String key, int score)
    {
        this.key = key;
        this.score = score;
    }
    
    public int getScore()
    {
        return this.score;
    }
    
    public String getKey()
    {
        return this.key;
    }
    
}
