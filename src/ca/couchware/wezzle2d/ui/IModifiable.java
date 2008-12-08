/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.ui;

/**
 * An interface for implementing UI elements that need to run certain
 * code when they are modified by the user.
 * 
 * @author cdmckay
 */
public interface IModifiable 
{

    /**
     * The method that is run when the entity is clicked.
     */
    public void runModifiedHook();
    
    /**
     * A method for getting a runnable that is run when the entitiy is clicked.
     */
    public Runnable getModifiedHook();
    
    /**
     * A method for setting a runnable that is run when the entity is clicked.
     */
    public void setModifiedHook(Runnable hook);
    
}
