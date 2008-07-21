/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tutorial;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.util.Util;

/**
 *
 * @author cdmckay
 */
public abstract class Tutorial 
{

    /**
     * The name of the tutorial.
     */
    private String name;
    
    /**
     * The list of rules that must be true in order for this tutorial to 
     * activated.
     */
    private Rule[] rules;
    
    /**
     * Is this tutorial activated?
     * Initially false.
     */
    private boolean activated = false;
    
    /**
     * Has the tutorial been completed?
     * Initially false.
     */
    private boolean done = false;
    
    /**
     * Create a new tutorial that is activated when the associated rule is
     * true.
     * 
     * @param rule
     */    
    public Tutorial(String name) 
    { 
        // Set the tutorial name.
        this.name = name;
    }
    
    /**
     * Evaluates the tutorial activation rules.
     * 
     * @param game
     * @return
     */
    public boolean evaluateRules(Game game)
    {
        // Check all the rules.  If any of them are false, return false.
        for (Rule rule : rules)
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
        // Check to see if the tutorial is activated.
        if (isActivated() == false)
        {
            if (evaluateRules(game) == false)
                return;
            else
            {      
                Util.handleMessage("Activating tutorial: " + name + ".", 
                        "Tutorial#updateLogic");
                setActivated(true);
                initializeTutorial(game);
            }
        }
        
        // If we're activated, run the tutorial logic.
        if (updateTutorial(game) == false)
        {
            setActivated(false);
            finishTutorial(game);
            done = true;
        }
    }
    
    /**
     * Deals with the initialization of the tutorial.  Typically includes
     * logic that adjusts the managers to work with the tutorial.
     * 
     * @param game
     */
    protected abstract void initializeTutorial(Game game);    
    
    /**
     * The tutorial logic goes here.
     * 
     * @param game
     * @return True if the tutorial is still running, false otherwise.
     */
    protected abstract boolean updateTutorial(Game game);

    /**
     * Is run when the tutorial is completed.  Typically deals with resetting 
     * the managers to their previous states before the tutorial started.
     * 
     * @param game
     */
    protected abstract void finishTutorial(Game game);
    
    public Rule[] getRules()
    {
        return rules;
    }

    public void setRules(Rule[] rules)
    {
        this.rules = rules;
    }        
    
    public boolean isActivated()
    {
        return activated;
    }

    public void setActivated(boolean activated)
    {
        this.activated = activated;
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
