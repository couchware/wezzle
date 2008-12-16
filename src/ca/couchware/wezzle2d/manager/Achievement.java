/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.Rule;
import ca.couchware.wezzle2d.tile.TileEntity;
import ca.couchware.wezzle2d.util.IXMLizable;
import java.util.ArrayList;
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
     public static enum Status
    {
        INCOMPLETE, 
        COMPLETE
    }
       
    private final List<Rule> ruleList;
    private final String name;
    private final String description;
    private final Difficulty difficulty;   
    private final Status status;

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
            Status status)
    {
         this.ruleList    = ruleList;
        this.name       = title;
        this.description = description;
        this.difficulty  = difficulty;
        this.status = status;
    }
        
    public static Achievement newInstance(List<Rule> ruleList, 
            String title,
            String description, 
            Difficulty difficulty, 
            Status status)
    {
       return new Achievement(ruleList, title, description, difficulty, status);
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
    
    public boolean evaluateCollision(List<TileEntity> collisionList)
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
    
    public Status getStatus()
    {
        return status;
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

     public static Achievement newInstanceFromXML(Element element)
    {
        String name = element.getAttributeValue("name");
        String type = element.getAttributeValue("type");
        String operation = element.getAttributeValue("operation");
        
        if(operation == "COLLISION")
        {
            //to be implemented.
        }
        
        int value = Integer.parseInt(element.getAttributeValue("value"));
        String status = element.getAttributeValue("status");
        String description = element.getAttributeValue("description");
        String difficulty = element.getAttributeValue("difficulty");
        
        // Parse the enums.
        Rule.Type t = Rule.Type.valueOf(type);
        Rule.Operation o = Rule.Operation.valueOf(operation);
        Difficulty d = Difficulty.valueOf(difficulty);
        Status s = Status.valueOf(status);
        
        
        List<Rule> rules = new ArrayList<Rule>();
        rules.add(new Rule(t, o, value));
        
        
        return newInstance(rules, name, description, d, s);
    }
    
    public Element toXMLElement() 
    {
       Element element = new Element("achievement");
        element.setAttribute("name",  this.name);
        element.setAttribute("type", String.valueOf(this.ruleList.get(0).getType()));
        element.setAttribute("operation", String.valueOf(this.ruleList.get(0).getOperation()));
        element.setAttribute("value", String.valueOf(this.ruleList.get(0).getValue()));
        element.setAttribute("status", String.valueOf(this.status));
        element.setAttribute("description", this.description);
        element.setAttribute("difficulty", String.valueOf(this.difficulty));
        return element;
    }
}
