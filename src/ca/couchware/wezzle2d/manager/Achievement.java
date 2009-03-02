/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.Rule;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.tile.TileType;
import ca.couchware.wezzle2d.util.IXMLizable;
import ca.couchware.wezzle2d.util.Node;
import ca.couchware.wezzle2d.util.SuperCalendar;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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
        PLATINUM;
        
        /** The color map for the difficulties. */
        final private static Map<Difficulty, Color> difficultyColorMap
                = new EnumMap<Difficulty, Color>(Difficulty.class);

        /**
         * Initializes the difficulty colour map.  Can only be called once
         * or it will raise an exception.
         * 
         * @param settingsMan
         */
        final public static void initializeDifficultyColorMap(SettingsManager settingsMan)
        {
            if (!difficultyColorMap.isEmpty())
            {
                throw new IllegalStateException("Color map already created!");
            }
            
            Map<Difficulty, Color> map = difficultyColorMap;
            map.put(Difficulty.BRONZE,   settingsMan.getColor(Key.ACHIEVEMENT_COLOR_BRONZE));
            map.put(Difficulty.SILVER,   settingsMan.getColor(Key.ACHIEVEMENT_COLOR_SILVER));
            map.put(Difficulty.GOLD,     settingsMan.getColor(Key.ACHIEVEMENT_COLOR_GOLD));
            map.put(Difficulty.PLATINUM, settingsMan.getColor(Key.ACHIEVEMENT_COLOR_PLATINUM));             
        }
        
        /**
         * Returns the colour associated with the difficulty level.
         * 
         * @return
         */
        public Color getColor()
        {
            if (difficultyColorMap.isEmpty())
            {
                throw new IllegalStateException("Color map has not been created!");
            }
            
            return difficultyColorMap.get(this);
        }
    }
       
    private final List<Rule> ruleList;
    private final String title;
    private final String formattedDescription;
    private final String description;
    private final Difficulty difficulty;    
    private SuperCalendar dateCompleted = null;
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
            String formattedDescription,
            String description, 
            Difficulty difficulty, 
            SuperCalendar dateCompleted)
    {
        this.ruleList             = ruleList;
        this.title                = title;
        this.formattedDescription = formattedDescription;
        this.description          = description;
        this.difficulty           = difficulty;
        this.dateCompleted        = dateCompleted;
    }
        
    public static Achievement newInstance(List<Rule> ruleList, 
            String title,
            String description, 
            Difficulty difficulty, 
            SuperCalendar dateCompleted)
    {
       return new Achievement(ruleList, 
               title, 
               description, 
               description, 
               difficulty, 
               dateCompleted);
    }
    
    public static Achievement newInstance(Achievement achievement, SuperCalendar dateCompleted)
    {
        return new Achievement(
                achievement.ruleList, 
                achievement.title,
                achievement.formattedDescription,
                achievement.description,
                achievement.difficulty,
                dateCompleted);
    }

    @SuppressWarnings("unchecked") 
    public static Achievement newInstanceFromXML(Element element)
    {
        // Get the name.
        String name = element.getAttributeValue("name");
                
        // Get the description.
        Element descriptionElement = element.getChild("description");
        String formattedDescription = descriptionElement == null ? "" : descriptionElement.getText();         
        String description = descriptionElement.getTextTrim().replaceAll("\n +", "\n");
        
        // Get the difficulty.
        Difficulty difficulty = Difficulty.valueOf(element.getAttributeValue("difficulty"));        
        
        // Get the date.
        Element dateElement = element.getChild("date");
        SuperCalendar dateCompleted = dateElement != null
                ? SuperCalendar.newInstanceFromXML(dateElement)
                : null;                
        
        // Get all the rules.
        Element rule = element.getChild("rule");
        List<Rule> rules = new ArrayList<Rule>();               
        
        while (rule != null)
        {
            Rule.Type type = Rule.Type.valueOf(rule.getAttributeValue("type").toString());

            switch (type)
            {
                case META:
                {
                    Element amount = rule.getChild("amount");
                    Rule.Status metaType = Rule.Status.valueOf(amount.getAttributeValue("metatype").toString());
                    int value = Integer.parseInt(amount.getAttributeValue("value").toString());
                    Rule.Operation operation = Rule.Operation.valueOf(amount.getAttributeValue("operation"));

                    Element achieve = rule.getChild("achievement");
                    List<String> achievementNamesList = new ArrayList<String>();

                    // If there are no specified achievement names...
                    if (achieve == null)
                    {
                        rules.add(new Rule(type, operation, value));
                        element.removeChild("rule");
                        rule = element.getChild("rule");
                        continue;
                    }
                    else
                    {
                        while (achieve != null)
                        {
                            achievementNamesList.add(achieve.getAttributeValue("name").toString());
                            rule.removeChild("achievement");
                            achieve = rule.getChild("achievement");
                        }

                        rules.add(new Rule(type, operation, value, achievementNamesList, metaType));
                        element.removeChild("rule");
                        rule = element.getChild("rule");
                    }

                    break;
                } // end case

                case COLLISION:
                {
                    Rule.Operation operation = Rule.Operation
                            .valueOf(rule.getAttributeValue("operation").toString());

                    // Get the collisions.
                    if (type == Rule.Type.COLLISION)
                    {
                        Node<TileType> tileTree = new Node<TileType>(null);
                        List<Element> elementList = (List<Element>) rule.getChildren("item");
                        Node<TileType> currentNode = tileTree;
                        transferElement(currentNode, elementList);                        

                        // Add the rule and continue to get the next rule.
                        rules.add(new Rule(type, operation, tileTree));
                        element.removeChild("rule");
                        rule = element.getChild("rule");
                    }
                    
                    break;
                } // end case

                default:
                {
                    Rule.Operation operation = Rule.Operation
                            .valueOf(rule.getAttributeValue("operation").toString());

                    int value = Integer.parseInt(rule.getAttributeValue("value").toString());

                    rules.add(new Rule(type, operation, value));
                    element.removeChild("rule");
                    rule = element.getChild("rule");
                    
                } // end default
            } // end witch
        } // end while
        
        // Get the collisions.  
        return new Achievement(rules, name, formattedDescription, description, difficulty, dateCompleted);
    }

    /**
     * Transfer an XML element (and all it's children) to an internal tree node.
     * @param parentNode
     * @param elementList
     */
    @SuppressWarnings("unchecked") 
    private static void transferElement(Node<TileType> parentNode, List<Element> elementList)
    {
        for ( Element e : elementList )
        {
            TileType t = TileType.valueOf(e.getAttributeValue("type").toString());
            Node<TileType> node = parentNode.addChild(t);
            transferElement(node, (List<Element>) e.getChildren("item"));
        }
    }
    
    /**
     * A method to evaluate an achievement to check if it has been completed.
     * 
     * @param game The state of the game.
     * @return Whether or not the achievement has been completed.
     */
    public boolean evaluate(Game game, ManagerHub hub)
    {
        // Use the private helper method to test if all of the fields
        // meet the requirements. any null values are automatically
        // accepted.
        
        for (Rule rule : ruleList)
        {
           if (!rule.evaluate(game, hub))
               return false;
        }
       
        return true;       
    }   
    
    public boolean evaluateCollision(Node<Tile> tileTree)
    {
        // Use the private helper method to test if all of the fields
        // meet the requirements. any null values are automatically
        // accepted.
        
        for (Rule rule : ruleList)
        {
           if (!rule.evaluateCollision(tileTree))
               return false;
        }
       
        return true;       
    }

    public boolean evaluateMeta(AchievementManager achievementMan)
    {
        // Use the private helper method to test if all of the fields
        // meet the requirements. any null values are automatically
        // accepted.

        for (Rule rule : ruleList)
        {
           if (!rule.evaluateMeta(achievementMan))
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
        return title;
    }    
    
    /**
     * Get the date completed.
     * 
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
        return "[" + this.title + " - " + this.difficulty + "] " + this.description;
    }    
    
    public Element toXMLElement() 
    {
        Element element = new Element("achievement");
        element.setAttribute("name",  this.title);        
        element.setAttribute("difficulty", String.valueOf(this.difficulty));
       
        Element descriptionElement = new Element("description");
        descriptionElement.setText(this.formattedDescription);
        element.addContent(descriptionElement);
        
        // Date.
        if (dateCompleted != null)
        {                       
            element.addContent(dateCompleted.toXMLElement());
        }
        
        for (int i = 0; i < this.ruleList.size(); i++)
        {
            Element rule = new Element("rule");
            
            String type = String.valueOf(this.ruleList.get(i).getType());
            
            rule.setAttribute("type", type);

            if (type.equals("META"))
            {
                Element amount = new Element("amount");
                amount.setAttribute("metatype", ruleList.get(i).getStatus().toString());
                amount.setAttribute("value", String.valueOf(ruleList.get(i).getValue()));
                amount.setAttribute("operation", String.valueOf(this.ruleList.get(i).getOperation()));
                rule.addContent(amount);

                List<String> achievementNamesList = ruleList.get(i).getAchievementNameList();
                if (achievementNamesList != null)
                {
                    for(String str : achievementNamesList)
                    {
                        Element achieve = new Element("achievement");

                        achieve.setAttribute("name", str);
                        rule.addContent(achieve);
                    }
                }

                element.addContent(rule);
                continue;
            }

            rule.setAttribute("operation", this.ruleList.get(i).getOperation().toString());
            
            if (type.equals("COLLISION"))
            {
                // XXX: come back and implement this
//                TileType[] itemList = ruleList.get(i).getItemList();
//
//                for (int j = 0; j < itemList.length; j++)
//                {
//                    Element item = new Element("item");
//                    item.setAttribute("type", itemList[j].toString());
//                    rule.addContent(item);
//                }
//
//                element.addContent(rule);
                continue;
            }

            rule.setAttribute("value", String.valueOf(this.ruleList.get(i).getValue()));            
            element.addContent(rule);
        }
        
        return element;
    }
    
    /**
     * Read this carefully, as the Acheivement equivalence is probably different
     * to what you intuitively would think.
     * 
     * An Achievement is equal to another Achievement if they both have the same
     * title and description.  That's it.  If you take two achievements, and
     * one of them has a completed date, and the other one doesn't, they are
     * still considered to be the same achievement.
     * 
     * Basically, Achievement equivalence ignores whether or not the achievement
     * has been completed.
     * 
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;            
        }
        if (!(o instanceof Achievement))
        {
            return false;
        }
        
        Achievement achievement = (Achievement) o;
        return this.title.equals(achievement.title)
                && this.description.equals(achievement.description);               
    }
    
    /**
     * Must override hashcode if you override <pre>equals()</pre>.
     * 
     * The method used here is taking from Effective Java (2nd Ed.) pp. 46-48.
     * 
     * @return
     */
    @Override
    public int hashCode()
    {
        int result = 17;
        result = 31 * result + title.hashCode();
        result = 31 * result + description.hashCode();        
        return result;
    }
    
}
