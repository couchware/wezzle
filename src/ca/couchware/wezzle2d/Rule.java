/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.manager.Achievement;
import ca.couchware.wezzle2d.manager.AchievementManager;
import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.tile.TileHelper;
import ca.couchware.wezzle2d.tile.TileType;
import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.util.Node;
import ca.couchware.wezzle2d.util.Node.Filter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/*
 * A class that holds two associated values.
 * 
 * The achievement tuple will be used by achievements to set values and tests
 * for those values.
 * 
 * For example. if we have an achievement which requires a player to achieve
 * a score of greater than 2500 points. The value would be 2500 and the 
 * test would be GREATER_THAN.
 * 
 * @author Kevin
 */
public class Rule 
{
           
    /** The type of rule. */
    public static enum Type
    {
        SCORE, 
        LEVEL,         
        MOVES, 
        LINES,
        START_LEVEL,
        COLLISION,
        META
    };       
    
    /** The operation being performed on the value. */
    public static enum Operation
    {
        LT, 
        LTEQ, 
        EQ, 
        GTEQ, 
        GT,
        BETWEEN
    }

    /** The completion status. */
    public static enum Status
    {
      COMPLETE,
      INCOMPLETE
    }
        
    protected final Type type;
    protected final Operation operation;
    protected final int value;    
    protected final Node<Set<TileType>> itemSubTree;
    protected final List<String> achievementNameList;
    protected final Status status;
    
    //--------------------------------------------------------------------------
    // Constructor
    //--------------------------------------------------------------------------
    
    /**
     * The constructor constructs a tuple with a given value and test
     * 
     * @param type The type of rule, i.e. a rule that triggers based on score,
     *             level, etc.
     * @param operation The operation to perform, i.e. less than, equal to, etc.
     * @param value The value we are testing against, i.e. less than 2.
     */
    public Rule(Type type, Operation operation, int value)
    {
        if (type == null)
        {
            throw new NullPointerException("Type cannot be null.");
        }
        if (operation == null)
        {
            throw new NullPointerException("Operation cannot be null.");
        }
        if (value < 0)
        {
            throw new IllegalArgumentException("Value must be >= 0.");
        }

        this.type = type;
        this.operation = operation;
        this.value = value;
        this.itemSubTree = null;
        this.achievementNameList = null;
        status = Status.COMPLETE;
    }

    public Rule(Type type, Operation operation, Node<Set<TileType>> tree)
    {
        if (type == null)
        {
            throw new NullPointerException("Type cannot be null");
        }
        if (operation == null)
        {
            throw new NullPointerException("Operation cannot be null");
        }
        if (tree == null)
        {
            throw new IllegalArgumentException("Tree cannot be null");
        }

        this.type = type;
        this.operation = operation;
        this.itemSubTree = tree;
        this.value = -1;
        this.achievementNameList = null;
        status = Status.COMPLETE;
    }

     public Rule (Type type, Operation operation, int value, List<String> achievementNameList, Status status)
     {
        if (type == null)
        {
            throw new NullPointerException("Type cannot be null.");
        }
        if (operation == null)
        {
            throw new NullPointerException("Operation cannot be null.");
        }
        if (value < 0)
        {
            throw new IllegalArgumentException("Value cannot be negative.");
        }

        this.type = type;
        this.operation = operation;
        this.value = value;
        this.itemSubTree = null;
        this.achievementNameList = achievementNameList;
        this.status = status;
     }
    
    public void onMatch()
    {
        // Optionally overridden.
    }
    
    //--------------------------------------------------------------------------
    // Static Methods
    //--------------------------------------------------------------------------
    
    /**
     * A public method that compares the achievement tuple with the actual 
     * value.  This will only give a reliable answer for non-COLLISION 
     * achievements.  It will always return false for collision type achievements.
     * 
     * @param game The game state.
     */
    public boolean evaluate(Game game, ManagerHub hub)
    {        
        // Sanity check.
        if(game == null || hub == null)
        {
            throw new IllegalArgumentException("game and hub cannot be null.");
        }

        
        // Make sure we're not a COLLISION-type.
        if (this.type == Type.COLLISION)
            return false;

        // Make sure we're not a META-type.
        if (this.type == Type.META)
            return false;
        
        // Find the appropriate field value from the type.
        int x = -1;          
        // Check the type.
        switch (getType())
        {
            case SCORE:
                x = hub.scoreMan.getTotalScore();
                break;
                
            case LEVEL:
                x = hub.levelMan.getLevel();
                break;
                
            case MOVES:
                x = hub.statMan.getMoveCount();
                break;
                
            case LINES:
                x = hub.statMan.getLineCount();
                break;
                
            case START_LEVEL:
                x = hub.statMan.getStartLevel();
                break;
                
            default:
                throw new IllegalArgumentException("Unknown type.");                
        }
                                  
        // If the test is successful, return true, otherwise, return false         
        switch (getOperation())
        {
            case GT:
                if (x > getValue())
                    return true;
                break;
                
            case LT:
                 if (x < getValue())
                    return true;
                break;
                
            case EQ:
                 if (x == getValue())
                    return true;
                break;
                
            case GTEQ:
                if (x >= getValue())
                    return true;
                break;
                
            case LTEQ:
                 if (x <= getValue())
                    return true;
                break;
                
            default:
                throw new IllegalArgumentException("Unknown operation.");
        }
        
        return false;
    }
    
    /**
     * A special evaluate for collisions.  Will always return false for
     * non-collision achievements.
     * 
     * @param tileTree
     * @return
     */
    public boolean evaluateCollision(Node<Tile> tileTree)
    {
        if (tileTree == null)
            throw new NullPointerException("Tile tree must not be null");

        if (this.type != Type.COLLISION)
            return false;                

        CouchLogger.get().recordMessage(this.getClass(), this.itemSubTree.toString());

        // This filter is used with the .findAll() method of the Node class
        // to find all item tiles.
        Node.Filter<Tile> filter = new Node.Filter<Tile>()
        {
            @Override
            public boolean apply(Tile nodeTile, Tile notUsed)
            { return TileHelper.getItemTileTypeSet().contains(nodeTile.getType()); }
        };

        // Right here, we are finding all item tiles in the tree.  These will
        // be searched for the item sub-tree if it's not found in the root.
        // We add the tile tree root at the beginning of the list so that it is
        // searched first.
        List<Node<Tile>> targetSubTreeList = tileTree.findAll(filter);
        targetSubTreeList.add(0, tileTree);
        for ( Node<Tile> subTree : targetSubTreeList )
        {
            if (isSubTree(subTree, this.itemSubTree))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if <code>subTree</code> is a sub-tree of <code>tree</code>.
     * @param tree
     * @param subTree
     * @return
     */
    private boolean isSubTree(Node<Tile> tree, Node<Set<TileType>> subTree)
    {
        // If the subtree has no children, then we return true.
        if (subTree.getChildren().isEmpty()) return true;

        // If the tree has no children, but the subtree does, then return false.
        if (tree.getChildren().isEmpty()) return false;

        // Get an iterator for the children of the subtree.
        Iterator<Node<Set<TileType>>> subTreeIt = subTree.getChildren().iterator();

        // We know we have at least one child, so this is fine.
        Node<Set<TileType>> targetNode = subTreeIt.next();

        // Set to true if this is a sub tree.
        boolean match = false;

        // Cycle through the children of the tree, looking for matches.
        for ( Node<Tile> node : tree.getChildren() )
        {
            if (targetNode.getData().contains(node.getData().getType())
                    && isSubTree(node, targetNode))
            {
                if (subTreeIt.hasNext()) targetNode = subTreeIt.next();
                else match = true;
            }
        }

        return match;
    }

    /**
     * A special evaluate for meta acheivements. Will always return false for
     * non-collision achievements.
     *
     * @param achieveMentNameList The list of the achievementNames.
     * @return true or false.
     */
    public boolean evaluateMeta(AchievementManager achievementMan)
    {
        if (this.type != Type.META)
        {
            return false;
        }

        List<Achievement> achievementList = new ArrayList<Achievement>();
      
        switch (this.status)
        {
            case COMPLETE:
                achievementList = achievementMan.getCompletedAchievementList();
                break;

            case INCOMPLETE:
                achievementList = achievementMan.getIncompletedAchievementList();
                break;

            default: throw new AssertionError();
        }       

        // Count the achievements in the achievement list that match the
        // names list.
        int x = 0;
        if (this.getAchievementNameList() != null)
        {
            for (Achievement a : achievementList)
            {
                if (this.getAchievementNameList().contains(a.getTitle()))
                {
                    x++;
                }
            }
        }
        // If there is no list, implicitly count the number of achievements.
        else
        {
            x = achievementList.size();
        }

        switch (this.operation)
        {
            case GT:
                if (x > this.getValue())
                {
                    return true;
                }
                break;

            case LT:
                if (x < getValue())
                {
                    return true;
                }
                break;

            case EQ:
                if (x == getValue())
                {
                    return true;
                }
                break;

            case GTEQ:
                if (x >= getValue())
                {
                    return true;
                }
                break;

            case LTEQ:
                if (x <= getValue())
                {
                    return true;
                }
                break;

            default: throw new AssertionError();
        }

        return false;
    }

    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------
            
    public Operation getOperation() 
    {
        return operation;
    }   

    public int getValue() 
    {
        return value;
    }  
    
    public Node<Set<TileType>> getTileTree()
    {
        return this.itemSubTree;
    }

    public List<String> getAchievementNameList()
    {
        return this.achievementNameList;
    }

    public Type getType() 
    {
        return type;
    }

    public Status getStatus()
    {
        return this.status;
    }
    
}