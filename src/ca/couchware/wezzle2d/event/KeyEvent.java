/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.event;

import java.util.Collections;
import java.util.EventObject;
import java.util.Set;

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

    public enum Arrow
    {
        NONE,
        KEY_UP,
        KEY_DOWN,
        KEY_LEFT,
        KEY_RIGHT
    }
    
    final private char ch;
    final private Set<Modifier> modifers;
    final private Arrow arrow;
    
    public KeyEvent(Object source, char ch, Set<Modifier> modifiers, Arrow arrow)
    {
        super(source);
        this.ch = ch;
        this.modifers = Collections.unmodifiableSet(modifiers);
        this.arrow = arrow;
    }
        
    public char getChar()
    {
        return ch;
    }

    public Set<Modifier> getModifierSet()
    {
        return modifers;
    }

    public Arrow getArrow()
    {
        return arrow;
    }
    
}
