/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.graphics.IEntity;

/**
 * An interface for interacting with a text box UI element.
 * 
 * @author cdmckay
 */
public interface ITextField extends IEntity, IButton
{
    /**
     * Get the text currently contained in the text field.
     * 
     * @return
     */
    public String getText();
    
    /**
     * Set the text currently shown in the text field.
     * 
     * @param text
     */
    public void setText(String text);    
        
    /**
     * Gets the maximum allowable length for this text field.
     * 
     * @return
     */
    public int getMaximumLength();  
}
