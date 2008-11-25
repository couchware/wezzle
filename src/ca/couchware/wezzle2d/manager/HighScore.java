package ca.couchware.wezzle2d.manager;

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
    
    public static HighScore newInstanceFromXML(Element element)
    {
        String name = element.getAttributeValue("name");
        int score   = Integer.parseInt(element.getAttributeValue("score"));
        int level   = Integer.parseInt(element.getAttributeValue("level"));
        
        return newInstance(name, score, level);
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
    
    public Element toXMLElement()
    {
        Element element = new Element("high-score");
        element.setAttribute("name",  this.name);
        element.setAttribute("score", String.valueOf(this.score));
        element.setAttribute("level", String.valueOf(this.level));
        return element;
    }

    @Override
    public String toString()
    {
        return "{HighScore - name: " + name + ", score: " + score + ", level: " + level + "}";
    }
    
}
