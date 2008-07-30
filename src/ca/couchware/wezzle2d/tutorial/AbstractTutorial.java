/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tutorial;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.Util;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cdmckay
 */
public abstract class AbstractTutorial implements ITutorial 
{

    /**
     * Has this tutorial been initalized?
     * Initially false.
     */
    protected boolean initialized = false;
    
    /**
     * Has the tutorial been completed?
     * Initially false.
     */
    protected boolean done = false;
    
    /**
     * The name of the tutorial.
     */
    private String name;                  
    
    /**
     * The list of rules.
     */
    private List<Rule> ruleList = new ArrayList<Rule>();
    
    /**
     * Create a new tutorial that is activated when the associated rule is
     * true.
     * 
     * @param rule
     */    
    public AbstractTutorial(String name) 
    { 
        // Set the tutorial name.
        this.name = name;
    }
    
    public void addRule(Rule rule)
    {
        ruleList.add(rule);
    }

    public void removeRule(Rule rule)
    {
        ruleList.remove(rule);
    }
    
    /**
     * Evaluates the tutorial activation rules.  Returns true if they are
     * met, false otherwise.
     * 
     * @param game
     * @return
     */
    public boolean evaluateRules(Game game)
    {
        // Check all the rules.  If any of them are false, return false.
        for (Rule rule : ruleList)
            if (rule.evaluate(game) == false)
                return false;
        
        // If all the rules are true, then return true.        
        return true;
    }
    
    /**
     * Updates the tutorial's internal logic.  If the tutorial is activated,
     * it will continue with the tutorial.  If the tutorial is not activated,
     * it will evaluate the activation rules to see if it should be activated.
     * 
     * @param game
     */
    public void updateLogic(Game game)
    {                
        // If we're activated, run the tutorial logic.
        if (initialized == true && update(game) == false)
        {
            initialized = false;
            finish(game);
            done = true;
        }
    }       
    
    /**
     * The tutorial logic goes here.
     * 
     * @param game
     * @return True if the tutorial is still running, false otherwise.
     */
    protected abstract boolean update(Game game);

    /**
     * Is run when the tutorial is completed.  Typically deals with resetting 
     * the managers to their previous states before the tutorial started.
     * 
     * @param game
     */
    protected abstract void finish(Game game);
    
    /**
     * Restarts the tutorial at the beginning.  This method can be overwritten
     * to optimize it.
     * 
     * @return
     */
    protected void repeat(Game game)
    {
        finish(game);
        initialize(game);
    }              
    
    public boolean isInitialized()
    {
        return initialized;
    }   

    public boolean isDone()
    {
        return done;
    }

    public String getName()
    {
        return name;
    }        
    
}
