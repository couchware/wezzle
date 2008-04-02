package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.Util;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    private LinkedList[] layers;
    
    /**
     * The list of hidden layers.
     */
    private boolean[] hiddenLayers;
    
    /**
     * The game window.  Used when drawing regions of the screen.
     */
    private GameWindow window;
    
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
        layers = new LinkedList[numberOfLayers];
        
        // Initialize hidden layer map.
        hiddenLayers = new boolean[numberOfLayers];
        
        // Create layers.
        for (int i = 0; i < numberOfLayers; i++)
        {
            layers[i] = new LinkedList();
            hiddenLayers[i] = false;
        }        
    }
    
    /**
     * Adds a drawable to the layer specified.  If the layer does not exist,
     * it is created.
     */
    public void add(final Drawable element, final int layer)
    {            
        if (layerExists(layer) == false)
            return;

        // Add the element to the layer.
        ((LinkedList) layers[layer]).add(element);        
    }
    
    /**
     * Remove an element from the layer specified.
     * @return True if the element was removed, false if it was not found.
     */
    public boolean remove(final Drawable element, final int layer)
    {   
        if (layerExists(layer) == false)
            return false;
        
        // Get the layer.
        final LinkedList layerList = (LinkedList) layers[layer];
        
        // Get the index.
        int index = layerList.indexOf(element);
        
        // If the index is -1, the element is not in this layer.
        if (index != -1)
        {
            layerList.remove(index);
            return true;
        }
        else
            return false;
    }    
    
    public void hide(final int layer)
    {             
        if (layerExists(layer) == false)
            return;
        
        hiddenLayers[layer] = true;
        
        // Grab the layer.
        final LinkedList layerList = (LinkedList) layers[layer];
        
        for (Iterator it = layerList.iterator(); it.hasNext(); )
            ((Drawable) it.next()).setDirty(true);
    }
    
    public void show(final int layer)
    {
        if (layerExists(layer) == false)
            return;
        
        hiddenLayers[layer] = false;
        
        // Grab the layer.
        final LinkedList layerList = (LinkedList) layers[layer];
        
        for (Iterator it = layerList.iterator(); it.hasNext(); )
            ((Drawable) it.next()).setDirty(true);
    }
    
    /**
     * Draws the layers to screen, in order from 0 to N (where N is the number
     * of layers).
     */
    public void draw()
    {
        // Cycle through all the layers, drawing them.
        for (int i = 0; i < numberOfLayers; i++)
        {                        
            // Check if layer exists, if it doesn't, skip this iteration.
            if (hiddenLayers[i] == true)                 
                continue;
            
            // Grab this layer.
            final LinkedList layerList = (LinkedList) layers[i];
            
            // Draw its contents.
            for (Iterator it = layerList.iterator(); it.hasNext(); )
            {
                ((Drawable) it.next()).draw();
            }           
        }            
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
            LinkedList layerList = (LinkedList) layers[i];
            
            // See if it's in the region.
            for (Iterator it = layerList.iterator(); it.hasNext(); )
            {
                Drawable d = (Drawable) it.next();
                
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
             
        if (clip != null)
        {
            //Util.handleMessage(clip.toString(), Thread.currentThread());
            
            window.setClip(clip);
            draw();            
            window.clearClip();
            //window.drawClip(clip);
                        
            return true;
        }        
        else
        {
            return false;
        }
    }
    
    private boolean layerExists(int layer)
    {
        // Sanity check.
        assert(layer >= 0);
        
        // Check if layer exists then error out.
        if (layer >= layers.length)
        {                        
            Util.handleMessage("Layer " + layer + " does not exist.", 
                    Thread.currentThread());
                        
            throw new RuntimeException("Non-existant layer number.");                        
        }
        else
        {
            return true;
        }
    }

}
