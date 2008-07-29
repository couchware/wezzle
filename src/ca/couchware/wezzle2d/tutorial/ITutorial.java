/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.tutorial;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.Rule;

/**
 *
 * @author cdmckay
 */
public interface ITutorial 
{

    /**
     * Add a new activation rule.
     * 
     * @param rule
     */
    public void addRule(Rule rule);
    
    /**
     * Remove an activation rule.
     * 
     * @param rule
     */
    public void removeRule(Rule rule);        

    /**
     * Gets the name of tutorial, suitable for showing on the screen.
     * i.e. "Rocket Tutorial".
     * 
     * @return
     */
    public String getName();  

    /**
     * Is the tutorial currently activated?
     * 
     * @return
     */
    public boolean isActivated();    

    /**
     * Has the tutorial completed running?
     * 
     * @return
     */
    public boolean isDone();
    
    /**
     * Updates the tutorial's internal logic.  If the tutorial is activated,
     * it will continue with the tutorial.  If the tutorial is not activated,
     * it will evaluate the activation rules to see if it should be activated.
     *
     * @param game
     */
    public void updateLogic(Game game);

}
