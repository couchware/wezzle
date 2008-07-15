package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.graphics.*;
import ca.couchware.wezzle2d.util.Util;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A class for managaing layer draw order.  Layer 0 is the bottom (lowest)
 * layer, followed by Layer 1, Layer 2, etc.  Negative layers are not 
 * permitted.
 * 
 * @author cdmckay
 */
public class LayerManager
{
    
    /**
     * The number of layers.
     */
    private int numberOfLayers;
        
    /**
     * TODO Add documentation.
     */
    private ArrayList<ArrayList<Drawable>> layerList;
    
    /**
     * The list of hidden layers.
     */
    private boolean[] hiddenLayers;
    
    /**
     * The game window.  Used when drawing regions of the screen.
     */
    private GameWindow window;
    
    /**
     * The remove clip.  This clip is used to make sure that things that are
     * removed are drawn over.
     */
    private Rectangle removeClip;
    
    /**
     * The constructor.
     */
    public LayerManager(GameWindow window, int numberOfLayers)
    {
        // Must have at least 1 layer.
        assert(window != null);
        assert(numberOfLayers > 0);       
        
        // Set the window reference.
        this.window = window;                
        
        // Record number of layers.
        this.numberOfLayers = numberOfLayers;
        
        // Initialize layer arraylist.
        layerList = new ArrayList<ArrayList<Drawable>>(numberOfLayers);
        
        // Initialize hidden layer map.
        hiddenLayers = new boolean[numberOfLayers];
        
        // Create layers.
        for (int i = 0; i < numberOfLayers; i++)
        {
            layerList.add(new ArrayList<Drawable>());
            hiddenLayers[i] = false;
        }        
        
        // Initialize remove clip.
        this.removeClip = new Rectangle();
    }
    
    /**
     * Adds a drawable to the layer specified.  If the layer does not exist,
     * it is created.
     */
    public void add(final Drawable element, final int layerNum)
    {            
        if (layerExists(layerNum) == false)
            return;

        // Add the element to the layer.
        layerList.get(layerNum).add(element);        
    }
    
    /**
     * Remove an element from the layer specified.
     * @return True if the element was removed, false if it was not found.
     */
    public boolean remove(final Drawable element, final int layerNum)
    {   
        if (layerExists(layerNum) == false)
            return false;
        
        // Get the layer.
        final ArrayList<Drawable> layer = layerList.get(layerNum);
        
        // Get the index.
        int index = layer.indexOf(element);
        
        // If the index is -1, the element is not in this layer.
        if (index != -1)
        {
            getRemoveClip().add(layer.get(index).getDrawRect());
            layer.remove(index);
            return true;
        }
        else
            return false;
    }    
    
    public void hide(final int layerNum)
    {             
        if (layerExists(layerNum) == false)
            return;
        
        if (hiddenLayers[layerNum] == true)
            return;
        
        hiddenLayers[layerNum] = true;
        
        // Grab the layer.
        final ArrayList<Drawable> layer = layerList.get(layerNum);
        
        for (Drawable d : layer)
            d.setDirty(true);
    }
    
    public void show(final int layerNum)
    {
        if (layerExists(layerNum) == false)
            return;
        
        if (hiddenLayers[layerNum] == false)
            return;
        
        hiddenLayers[layerNum] = false;
        
        // Grab the layer.
        final ArrayList<Drawable> layer = layerList.get(layerNum);
        
        for (Drawable d : layer)
            d.setDirty(true);
    }
    
    /**
     * Draws the layers to screen, in order from 0 to N (where N is the number
     * of layers).
     */
    public void draw()
    {
        Rectangle clip;
        
        if (window.getClip() !=  null)
            clip = window.getClip().getBounds();
        else
            clip = null;
                
        // Cycle through all the layers, drawing them.
        for (int i = 0; i < numberOfLayers; i++)
        {                        
            // Check if layer exists, if it doesn't, skip this iteration.
            if (hiddenLayers[i] == true)                 
                continue;
            
            // Grab this layer.
            final ArrayList<Drawable> layer = layerList.get(i);
            
            // Draw its contents.
            for (Drawable d : layer)
            {
                if (clip == null || d.getDrawRect().intersects(clip) == true)
                        d.draw();                
            }           
        } // end for            
    }
    
    /**
     * Draws anything dirty in that region.
     * 
     * @param rx
     * @param ry
     * @param rwidth
     * @param rheight
     */
    public boolean drawRegion(int rx, int ry, int rwidth, int rheight)
    {
        Rectangle region = new Rectangle(rx, ry, rwidth, rheight);
        Rectangle clip = null;
        
        // Cycle through all the layers, drawing them.
        for (int i = 0; i < numberOfLayers; i++)
        {                                             
            // Grab this layer.
            ArrayList<Drawable> layer = layerList.get(i);
            
            // See if it's in the region.
            for (Drawable d : layer)
            {
                if (d.isDirty() == false)
                    continue;                                
                
                // Clear dirtiness.
                d.setDirty(false);
                
                Rectangle r = d.getDrawRect();                
                
                if (r == null || r.getMinX() < 0 || r.getMinY() < 0)
                {
                    Util.handleWarning("Offending class is " 
                            + d.getClass().getSimpleName(), 
                            Thread.currentThread());
                    Util.handleWarning("Rectangle is " + r,
                            Thread.currentThread());
                }
                
                if (region.intersects(r) == true)
                {   
//                    Util.handleWarning("Offending class is " 
//                            + d.getClass().getSimpleName(), 
//                            Thread.currentThread());
//                    Util.handleWarning("Rectangle is " + r,
//                            Thread.currentThread());
                    
//                    if (d instanceof Java2DText)
//                    {
//                        Java2DText j = (Java2DText) d;
//                        Util.handleWarning(j.getText(), 
//                            Thread.currentThread());
//                    }
                    
                    if (clip == null)
                        clip = new Rectangle(r);
                    else
                        clip.add(r);
                }
            }           
        }     
        
        // If the remove clip is not empty.
        if (getRemoveClip().isEmpty() == false)
        {
            // See if there's a clip to add to, or if we need to make one.
            if (clip == null)
                clip = getRemoveClip();
            else
                clip.add(getRemoveClip());
        }
             
        if (clip != null)
        {
            //Util.handleMessage(clip.toString(), Thread.currentThread());
            
            window.setClip(clip);
            draw();            
            window.clearClip();
            
            // Uncomment the next line if you want boxes to be drawn around
            // each region being drawn.
            //window.drawClip(clip);
               
            // Reset the remove clip.
            resetRemoveClip();
            
            return true;
        }        
        else
        {
            return false;
        }
    }
    
    /**
     * Checks for existance of the passed layer number.
     * 
     * @param layer
     * @return
     */
    private boolean layerExists(int layerNum)
    {
        // Sanity check.
        assert(layerNum >= 0);
        
        // Check if layer exists then error out.
        if (layerNum >= layerList.size())
        {                                   
            return false;                    
        }
        else
        {
            return true;
        }
    }

    public Rectangle getRemoveClip()
    {
        return removeClip;
    }   
    
    public void resetRemoveClip()
    {
        if (removeClip.isEmpty() == false)
            removeClip = new Rectangle();
    }

}
