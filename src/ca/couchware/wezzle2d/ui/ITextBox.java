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
public interface ITextBox extends IEntity
{
    public String getText();
    public void setText(String text);    
}
