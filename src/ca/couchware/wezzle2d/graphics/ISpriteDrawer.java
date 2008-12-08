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
    public ISpriteDrawer regionX(int val);
    public ISpriteDrawer regionY(int val);
    public ISpriteDrawer regionWidth(int val);
    public ISpriteDrawer regionHeight(int val);
    public void end(); 
}
