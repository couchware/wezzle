package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.Util;
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
public class LayerManager implements Drawable
{

    /**
     * The number of layers.
     */
    private int numberOfLayers;
    
    /**
     * The hash map of layer linked lists.
     * Using 1.5 generics, this would be
     *     HashMap<Integer, LinkedList<Drawable>>.
     */
    private HashMap layerMap;
    
    /**
     * The list of hidden layers.
     */
    private HashMap hiddenLayerMap;
    
    /**
     * The constructor.
     */
    public LayerManager(int numberOfLayers)
    {
        // Must have at least 1 layer.
        assert(numberOfLayers > 0);
        
        // Initialize layer map.
        layerMap = new HashMap();
        
        // Initialize hidden layer map.
        hiddenLayerMap = new HashMap();
        
        // Record number of layers.
        this.numberOfLayers = numberOfLayers;
        
        // Create layers.
        for (int i = 0; i < numberOfLayers; i++)
        {
            layerMap.put(new Integer(i), new LinkedList());
            hiddenLayerMap.put(new Integer(i), Boolean.FALSE);
        }        
    }
    
    /**
     * Adds a drawable to the layer specified.  If the layer does not exist,
     * it is created.
     */
    public void add(final Drawable element, final int layer)
    {            
        // Sanity check.
        assert(layer >= 0);
        
        // Check if layer exists.  If it does not, add it.
        if (layerMap.containsKey(new Integer(layer)) == false)
        {                        
            Util.handleMessage("Layer " + layer + " created.", 
                    Thread.currentThread());
            
            // Increase layer count.
            this.numberOfLayers = layer + 1;
            
            // Increase the layer count.
            layerMap.put(layer, new LinkedList());
            hiddenLayerMap.put(layer, new Boolean(false));
        }

        // Add the element to the layer.
        ((LinkedList) layerMap.get(new Integer(layer))).add(element);        
    }
    
    /**
     * Remove an element from the layer specified.
     * @return True if the element was removed, false if it was not found.
     */
    public boolean remove(final Drawable element, final int layer)
    {
        // Get the layer.
        LinkedList layerList = (LinkedList) layerMap.get(layer);
        
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
        hiddenLayerMap.put(new Integer(layer), Boolean.TRUE);
    }
    
    public void show(final int layer)
    {
        hiddenLayerMap.put(new Integer(layer), Boolean.FALSE);
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
            Integer index = new Integer(i);
            
            // Check if layer exists, if it doesn't, skip this iteration.
            if (((Boolean) hiddenLayerMap.get(index)) == Boolean.TRUE
                    || layerMap.containsKey(index) == false)
                continue;
            
            // Grab this layer.
            LinkedList layerList = (LinkedList) layerMap.get(index);
            
            // Draw its contents.
            for (Iterator it = layerList.iterator(); it.hasNext(); )
                ((Drawable) it.next()).draw();
        }            
    }

    public void setVisible(boolean visible)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isVisible()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setDirty(boolean dirty)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isDirty()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
