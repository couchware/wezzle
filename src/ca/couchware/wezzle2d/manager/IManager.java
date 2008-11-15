/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

/**
 * The manager interface.
 * 
 * @author cdmckay
 */
public interface IManager 
{
    /**
     * Saves the state of the manager to memory.
     */
    public void saveState();
    
    /**
     * Loads the previously saved manager state.  If no state is saved, 
     * it warns you and does nothing.
     */
    public void loadState();
    
    /**
     * Resets the state of the manager to it's initial state.
     */   
    public void resetState();
}
