/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.Rule;
import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.tile.TileType;
import ca.couchware.wezzle2d.util.IXMLizable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.jdom.Element;

/**
 * An achievement will hold all the state information required for that 
 * achievement to be successfully completed. Each field will also hold whether
 * it is greater than, less than, or equal to the required value.
 * 
 * for example, an achievement can hold a value such as:  level &lt; 10, 
 * lines = 100, moves &lt; 15.
 *
 * achievemtns will be held within the achievement manager and take a game state
 * as a parameter to their update function. Every iteration of the game loop
 * will have the achievement compare its internal requirements to the game state
 * and evaluate whether or not it has been achieved. If it has been achieved,
 * the evaluate(gamestate) function will return true.
 * 
 * The achievement manager will simply iterate through the list of achievements
 * passing in the current game state and waiting for a response.
 * 
 * If the achievement has been successfully completed, it will be removed from
 * the achievement list and added to the completed list. This will dynamically
 * shrink the amount of searching necessary to check the achievements.
 * 
 * @author Kevin
 */
public class Achievement implements IXMLizable
{
    /** The levels of achievement difficulty. */
    public static enum Difficulty
    {
        BRONZE, 
        SILVER, 
        GOLD, 
        PLATINUM
    }
       
    private final List<Rule> ruleList;
    private final String name;
    private final String description;
    private final Difficulty difficulty;   
    private Calendar dateCompleted = null;
    //private static Calendar cal = Calendar.getInstance();

    /**
     * The achievement is a list of rules which all have to be true for an
     * achievement to be achieved. As well as an associated text description 
     * and a difficulty type.
     * 
     * @param rules
     * @param description
     * @param difficulty
     */
    private Achievement(List<Rule> ruleList, 
            String title,
            String description, 
            Difficulty difficulty, 
            Calendar completed)
    {
        this.ruleList    = ruleList;
        this.name       = title;
        this.description = description;
        this.difficulty  = difficulty;
        dateCompleted = completed;
    }
        
    public static Achievement newInstance(List<Rule> ruleList, 
            String title,
            String description, 
            Difficulty difficulty, 
            Calendar date)
    {
       return new Achievement(ruleList, title, description, difficulty, date);
    }
    
    public static Achievement newInstanceFromXML(Element element)
    {
        String name = element.getAttributeValue("name");
                
        Element e = element.getChild("description");
        String description = e == null ? "" : e.getTextTrim().replaceAll("\n +", "\n");         
        
        Difficulty difficulty = Difficulty.valueOf(element.getAttributeValue("difficulty"));
        Element dateElement = element.getChild("date");
        
        Calendar storedDate = null;
        
        if (dateElement != null)
        {
            storedDate = Calendar.getInstance();
            
            int month = Integer.parseInt(dateElement.getAttributeValue("month").toString());
            int day = Integer.parseInt(dateElement.getAttributeValue("day").toString());
            int year = Integer.parseInt(dateElement.getAttributeValue("year").toString());
            
            storedDate.set(year, month, day);
        
        }
        
        List<Rule> rules = new ArrayList<Rule>();
       
        Element rule = element.getChild("rule");
        // Get all the rules.
        while (rule != null)
        {
            Rule.Type type = Rule.Type.valueOf(rule.getAttributeValue("type").toString());
            Rule.Operation operation = Rule.Operation
                    .valueOf(rule.getAttributeValue("operation").toString()); 
            
            // Get the collisions.
            if (type == Rule.Type.COLLISION)
            {
                Element item = rule.getChild("item");
                List<TileType> tileTypeList = new ArrayList<TileType>();
                while( item != null)
                {
                    TileType t = TileType.valueOf(item.getAttributeValue("type").toString());
                    tileTypeList.add(t);
                    rule.removeChild("item");
                    item = rule.getChild("item");
                }
                
                // add the rule and continue to get the next rule.
                rules.add(new Rule(type, operation, tileTypeList));
                element.removeChild("rule");
                rule = element.getChild("rule");
                
                continue;
            }
                         
            int value = Integer.parseInt(rule.getAttributeValue("value").toString());
            
            rules.add(new Rule(type, operation, value));
            element.removeChild("rule");
            rule = element.getChild("rule");
        }
        
        // Get the collisions.  
        return newInstance(rules, name, description, difficulty, storedDate);
    }
    
    /**
     * A method to evaluate an achievement to check if it has been completed.
     * 
     * @param game The state of the game.
     * @return Whether or not the achievement has been completed.
     */
    public boolean evaluate(Game game)
    {
        // Use the private helper method to test if all of the fields
        // meet the requirements. any null values are automatically
        // accepted.
        
        for (Rule rule : ruleList)
        {
           if (rule.evaluate(game) == false)
               return false;
        }
       
        return true;       
    }
    
    public void setCompleted()
    {
        assert this.dateCompleted == null;
        this.dateCompleted = Calendar.getInstance();                
    }
    
    public boolean evaluateCollision(List<Tile> collisionList)
    {
        // Use the private helper method to test if all of the fields
        // meet the requirements. any null values are automatically
        // accepted.
        
        for (Rule rule : ruleList)
        {
           if (rule.evaluateCollision(collisionList) == false)
               return false;
        }
       
        return true;       
    }

    public Difficulty getDifficulty()
    {
        return difficulty;
    }   
    
    public String getTitle()
    {
        return name;
    }    
    
    /**
     * Get the date completed.
     * @return the date.
     */
    public Calendar getDateCompleted()
    {
        return dateCompleted;
    }
    
    /**
     * Get the description of the achievement.
     * 
     * @return The description.
     */
    public String getDescription()
    {
        return this.description;
    }
    
    @Override
    public String toString()
    {
        return "[" + this.name + " - " + this.difficulty + "] " + this.description;
    }    
    
    public Element toXMLElement() 
    {
        Element element = new Element("achievement");
        element.setAttribute("name",  this.name);        
        element.setAttribute("difficulty", String.valueOf(this.difficulty));
       
        Element descriptionElement = new Element("description");
        descriptionElement.setText(this.description);
        element.addContent(descriptionElement);
        
        // Date.
        if (dateCompleted != null)
        {
            
            Element dateElement = new Element("date");
            dateElement.setAttribute("day",   String.valueOf(dateCompleted.get(Calendar.DATE)));
            dateElement.setAttribute("month", String.valueOf(dateCompleted.get(Calendar.MONTH)));
            dateElement.setAttribute("year",  String.valueOf(dateCompleted.get(Calendar.YEAR)));
            element.addContent(dateElement);
        }
        
        for (int i = 0; i < this.ruleList.size(); i++)
        {
            Element rule = new Element("rule");
            
            String type = String.valueOf(this.ruleList.get(i).getType());
            
            rule.setAttribute("type", type);
            rule.setAttribute("operation", String.valueOf(this.ruleList.get(i).getOperation()));
            
            if (type.equals("COLLISION"))
            {
                TileType[] itemList = ruleList.get(i).getItemList();
               
                for(int j = 0; j < itemList.length; j++)
                {
                    Element item = new Element("item");
                    item.setAttribute("type", itemList[j].toString());
                    rule.addContent(item);
                }
                
                element.addContent(rule);
                continue;
            }
            
            rule.setAttribute("value", String.valueOf(this.ruleList.get(i).getValue()));            
            element.addContent(rule);
        }
        
        return element;
    }
    
}
