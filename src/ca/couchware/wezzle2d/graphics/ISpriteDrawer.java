/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.couchware.wezzle2d.graphics;

/**
 *
 * @author cdmckay
 */
public interface ISpriteDrawer 
{    
    public ISpriteDrawer width(int val);
    public ISpriteDrawer height(int val);
    public ISpriteDrawer theta(double val);
    public ISpriteDrawer opacity(int val);
    public ISpriteDrawer region(int x, int y, int width, int height);
    public void end(); 
}
