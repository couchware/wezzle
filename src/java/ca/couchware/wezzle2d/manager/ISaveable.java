/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

/**
 * An interface for describing components that can save and load their
 * state.
 * 
 * @author cdmckay
 */
public interface ISaveable 
{
    /**
     * Saves the state to memory.
     */
    public void saveState();
    
    /**
     * Loads the previously saved state.  If no state is saved, 
     * it warns you and does nothing.
     */
    public void loadState();        
}
