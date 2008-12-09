/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.event;

import java.util.EventObject;

/**
 * A key event.
 * 
 * @author cdmckay
 */
public class KeyEvent extends EventObject
{
    
    private char ch;
    
    public KeyEvent(Object source, char ch)
    {
        super(source);
        this.ch = ch;
    }
    
    public char getCharacter()
    {
        return ch;
    }
    
}
