/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tutorial;

import ca.couchware.wezzle2d.*;
import java.util.ArrayList;

/**
 *
 * @author cdmckay
 */
public abstract class Tutorial 
{

    /**
     * The list of rules that must be true in order for this tutorial to 
     * activated.
     */
    protected Rule[] rules;
    
    /**
     * Is this tutorial activated?
     * Initially false.
     */
    protected boolean activated = false;
    
    /**
     * Create a new tutorial that is activated when the associated rule is
     * true.
     * 
     * @param rule
     */    
    public Tutorial() { }
    
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
            if (Rule.evaluate(rule, game) == false)
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
                setActivated(true);
        }
        
        // If we're activated, run the tutorial logic.
        tutorialLogic(game);
    }
    
    /**
     * The tutorial logic goes here.
     * 
     * @param game
     */
    protected abstract void tutorialLogic(Game game);

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
    
}
