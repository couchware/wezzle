package ca.couchware.wezzle2d.manager;

/**
 * TODO Describe the class.
 * 
 * @author Kevin
 */
public class HighScore 
{
    
    private final String name;
    private final int score;   
    private final int level;
    
    private HighScore(String name, int score, int level)
    {
        this.name = name;
        this.score = score;
        this.level = level;
    }
    
    public static HighScore newInstance(String name, int score, int level)
    {
        return new HighScore(name, score, level);
    }
    
    public int getScore()
    {
        return this.score;
    }
    
    public String getName()
    {
        return this.name;
    }

    public int getLevel()
    {
        return level;
    }        
    
}
