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
    
    public enum Modifier
    {
        NONE,
        LEFT_SHIFT,
        LEFT_CTRL,
        LEFT_META,
        LEFT_ALT,
        RIGHT_ALT,
        RIGHT_META,
        APPLICATION,
        RIGHT_CTRL,
        RIGHT_SHIFT
    
    
    
    }
    
    final private char ch;
    final private Modifier modifer;
    
    public KeyEvent(Object source, char ch, Modifier modifier)
    {
        super(source);
        this.ch = ch;
        this.modifer = modifier;
    }
        
    public char getChar()
    {
        return ch;
    }

    public Modifier getModifer()
    {
        return modifer;
    }        
    
}
