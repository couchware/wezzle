package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.difficulty.Difficulty;
import ca.couchware.wezzle2d.util.IXMLizable;
import org.jdom.Element;

/**
 * TODO Describe the class.
 * 
 * @author Kevin
 */
public class HighScore implements IXMLizable
{
    
    private final String name;
    private final int score;   
    private final int level;
    private final Difficulty difficulty;
    
    private HighScore(
            String name, int score, int level, Difficulty difficulty)
    {
        this.name = name;
        this.score = score;
        this.level = level;
        this.difficulty = difficulty;
    }
    
    public static HighScore newInstance(
            String name, int score, int level, Difficulty difficulty)
    {
        return new HighScore(name, score, level, difficulty);
    }
    
    public static HighScore newInstanceFromXml(Element element)
    {
        String nameStr = element.getAttributeValue("name");
        if (nameStr == null) nameStr = "";
        final String name = nameStr;

        String scoreStr = element.getAttributeValue("score");
        if (scoreStr == null) scoreStr = "0";
        final int score = Integer.parseInt(scoreStr);

        String levelStr = element.getAttributeValue("level");
        if (levelStr == null) levelStr = "0";
        int level = Integer.parseInt(levelStr);

        String difficultyStr = element.getAttributeValue("difficulty");
        if (difficultyStr == null) difficultyStr = Difficulty.NONE.toString();
        final Difficulty difficulty = Difficulty.valueOf(difficultyStr);
        
        return newInstance(name, score, level, difficulty);
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

    public Difficulty getDifficulty()
    {
        return difficulty;
    }
    
    public Element toXmlElement()
    {
        Element element = new Element("high-score");
        element.setAttribute("name",  this.name);
        element.setAttribute("score", String.valueOf(this.score));
        element.setAttribute("level", String.valueOf(this.level));
        element.setAttribute("difficulty", this.difficulty.toString());
        return element;
    }

    @Override
    public String toString()
    {
        return String.format(
                "{ name: %s, score: %s, level: %s, difficulty: %s }",
                this.name, this.score, this.level, this.difficulty);
    }
    
}
