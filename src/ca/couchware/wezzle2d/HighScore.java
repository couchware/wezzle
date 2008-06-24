package ca.couchware.wezzle2d;

/**
 * TODO Describe the class.
 * 
 * @author Kevin
 */
public class HighScore 
{
    
    private final String key;
    private final int score;    
    
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
