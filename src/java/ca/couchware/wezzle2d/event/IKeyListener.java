/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.event;

/**
 * An interface for listening to key presses and releases.
 * 
 * @author cdmckay
 */
public interface IKeyListener 
{
    
    /**
     * A key was pressed.
     * 
     * @param event
     */
    public void keyPressed(KeyEvent event);
    
    /**
     * A key was released.
     * 
     * @param event
     */
    public void keyReleased(KeyEvent event);
    
}
