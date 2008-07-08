/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.Util;
import java.util.LinkedList;

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
public class Achievement
{
    public static final int DIFFICULTY_BRONZE = 0;
    public static final int DIFFICULTY_SILVER = 1;
    public static final int DIFFICULTY_GOLD = 2;
    public static final int DIFFICULTY_PLATINUM = 3;
    
    public static final int TYPE_SCORE = 0;
    public static final int TYPE_LEVEL = 1;
    public static final int TYPE_MOVES = 2;
    public static final int TYPE_LINES = 3;    
    
    private LinkedList<Rule> ruleList;
    private String description;
    private int difficulty;    

    /**
     * The achievement is a list of rules which all have to be true for an
     * achievement to be achieved. As well as an associated text description 
     * and a difficulty type.
     * 
     * @param rules
     * @param description
     * @param difficulty
     */
    public Achievement(LinkedList<Rule> ruleList, String description, 
            int difficulty)
    {
        this.ruleList = ruleList;
        this.description = description;
        this.difficulty = difficulty;
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
           if (this.evaluate(rule, game) == false)
               return false;
        }
       
        return true;       
    }
    
    /**
     * A private method that compares the achievement tuple with the actual 
     * value.
     * 
     * @param val The value of the field.
     * @param rule The achievement rule.
     */
    private boolean evaluate(Rule rule, Game game)
    {        
        // Find the appropriate field value from the type.
        int val = -1;
  
        switch (rule.getType())
        {
            case TYPE_SCORE:
                val = game.scoreMan.getTotalScore();
                break;
                
            case TYPE_LEVEL:
                val = game.worldMan.getLevel();
                break;
                
            case TYPE_MOVES:
                val = game.moveMan.getMoveCount();
                break;
                
            case TYPE_LINES:
                val = game.getTotalLineCount();
                break;
                
            default:
                throw new IllegalArgumentException("Unknown type.");                
        }
                                  
        // If the test is successful, return true, otherwise, return false         
        switch (rule.getOperation())
        {
            case Rule.GREATER_THAN:
                if (val > rule.getValue())
                    return true;
                break;
                
            case Rule.LESS_THAN:
                 if (val < rule.getValue())
                    return true;
                break;
                
            case Rule.EQUAL_TO:
                 if (val == rule.getValue())
                    return true;
                break;
                
            case Rule.GREATER_THAN_OR_EQUAL_TO:
                if (val >= rule.getValue())
                    return true;
                break;
                
            case Rule.LESS_THAN_OR_EQUAL_TO:
                 if (val <= rule.getValue())
                    return true;
                break;
                
            default:
                throw new IllegalArgumentException("Unknown test.");
        }
        
        return false;
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
}
