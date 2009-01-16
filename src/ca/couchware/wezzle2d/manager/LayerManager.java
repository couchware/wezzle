package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.graphics.*;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.ITextLabel;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class for managaing layer draw order.  Layer 0 is the bottom (lowest)
 * layer, followed by Layer 1, Layer 2, etc.  Negative layers are not 
 * permitted.  
 * 
 * @author cdmckay
 */
public class LayerManager implements IDisposable, IDrawer
{           
    
    /**
     * The different layers supported by the layer manager.
     */
    public static enum Layer
    {
        BACKGROUND,        
        TILE,
        PIECE_GRID,
        EFFECT,        
        UI
    }        
        
    /**
     * Is the layer manager disabled?
     */
    private boolean disabled = false;
    
    /**
     * TODO Add documentation.
     */
    private ArrayList<ArrayList<IDrawable>> layerList;
    
    /**
     * The list of hidden layers.
     */
    private boolean[] isHidden;
    
    /**
     * The graphics instance.  Used when drawing regions of the screen.
     */
    private IGraphics gfx;   
    
    /**
     * The remove clip.  This clip is used to make sure that things that are
     * removed are drawn over.
     */
    private Rectangle removeRect;
    
    /**
     * The constructor.
     */
    private LayerManager()
    {
        // Set the window reference.
        this.gfx = ResourceFactory.get().getGraphics();          
        
        // Initialize layer arraylist.
        layerList = new ArrayList<ArrayList<IDrawable>>(Layer.values().length);
        
        // Initialize hidden layer map.
        isHidden = new boolean[Layer.values().length];
        
        // Create layers.
        for (int i = 0; i < Layer.values().length; i++)
        {
            layerList.add(new ArrayList<IDrawable>());
            isHidden[i] = false;
        }        
        
        // Initialize remove clip.
        this.removeRect = new Rectangle();
    }
        
    /**
     * Create a new layer manager instance.
     * 
     * @return
     */
    public static LayerManager newInstance()
    {
        return new LayerManager();
    }
    
    /**
     * Adds a drawable to the layer specified.  If the layer does not exist,
     * an exception is thrown
     */
    public void add(final IDrawable drawable, Layer layer)
    {         
        // The drawable cannot be null.
        assert drawable != null;
        
        if (layer == null)
            throw new NullPointerException("Layer does not exist!");

        // Set disabledness.
        if (disabled == true && drawable instanceof IEntity)
            ((IEntity) drawable).setDisabled(true);
        
        // Add the element to the layer.
        layerList.get(layer.ordinal()).add(drawable);        
    }
    
    /**
     * Remove an element from the layer specified.
     * @return True if the element was removed, false if it was not found.
     */
    public void remove(final IDrawable drawable, Layer layer)
    {   
        // The drawable cannot be null.
        if (drawable == null)
            throw new NullPointerException("Drawable does not exist!");
        
        if (layer == null)
            throw new NullPointerException("Layer does not exist!");
        
        // Get the layer.
        final ArrayList<IDrawable> list = layerList.get(layer.ordinal());
        
        // Get the index.
        int index = list.indexOf(drawable);
        
        // If the index is -1, the element is not in this layer.
        if (index != -1)
        {
            Rectangle r = list.get(index).getDrawRect();
            if (r != null) addRemoveRect(r);
            list.remove(index);            
        }
        else        
        {
            throw new RuntimeException("Tried to remove non-exist element!");            
        }
    }           
    
    /**
     * Returns a read-only list representing the specified layer.
     * 
     * @param layer
     * @return
     */
    public List<IDrawable> getLayer(Layer layer)
    {
        return Collections.unmodifiableList(layerList.get(layer.ordinal()));
    }
    
    /**
     * Clear a layer from the layer manager.
     * 
     * @param layer
     */
    public void removeLayer(Layer layer)
    {
        layerList.get(layer.ordinal()).clear();
    }
    
    /**
     * Gets all the entities in the layer manager as a list.
     * 
     * @return
     */
    public List<IEntity> getEntities()
    {
        List<IEntity> entityList = new ArrayList<IEntity>();
        
        for (Layer l : Layer.values())
            for (IDrawable d : layerList.get(l.ordinal()))
                if (d instanceof IEntity)
                    entityList.add((IEntity) d);
        
        return entityList;
    }
    
    public boolean exists(final IDrawable drawable, Layer layer)
    {
        // The drawable cannot be null.
        if (drawable == null)
            throw new NullPointerException("Drawable does not exist!");
        
        // Get the layer.
        final ArrayList<IDrawable> list = layerList.get(layer.ordinal());
        
        // Get the index.
        int index = list.indexOf(drawable);
        
        // If it's -1, it doesn't exist.
        return (index != -1);
    }
    
    public void show(Layer layer)
    {
        if (layer == null)
            throw new NullPointerException("Layer does not exist!");
        
        if (isHidden[layer.ordinal()] == false)
            return;
        
        isHidden[layer.ordinal()] = false;
        
        // Grab the layer.
        final ArrayList<IDrawable> list = layerList.get(layer.ordinal());
        
        for (IDrawable d : list)
            d.setDirty(true);
    }
    
    public void hide(Layer layer)
    {             
        if (layer == null)
            throw new RuntimeException("Layer does not exist!");
        
        if (isHidden[layer.ordinal()] == true)
            return;
        
        isHidden[layer.ordinal()] = true;
        
        // Grab the layer.
        final ArrayList<IDrawable> list = layerList.get(layer.ordinal());
        
        for (IDrawable d : list)
            d.setDirty(true);
    }
    
    public void toFront(final IDrawable d, Layer layer)
    {
        if (layer == null)     
            throw new RuntimeException("Layer does not exist!");      
                
        remove(d, layer);
        add(d, layer);
    }
    
    public void toBack(final IDrawable d, Layer layer)
    {
        if (layer == null)     
            throw new RuntimeException("Layer does not exist!");
        
        remove(d, layer);
        layerList.get(layer.ordinal()).add(0, d);
    }
    
    /**
     * Draws the layers to screen, in order from 0 to N (where N is the number
     * of layers).
     */
    public void drawAll()
    {
        // The number of sprites drawn.
        //int count = 0;
        //int total = 0;
        
        // The clipping area.        
        Rectangle clip;
        
        if (gfx.getClip() !=  null)
            clip = gfx.getClip().getBounds();
        else
            clip = null;
                
        // Cycle through all the layers, drawing them.
        for (int i = 0; i < Layer.values().length; i++)
        {                        
            // Check if layer exists, if it doesn't, skip this iteration.
            if (isHidden[i] == true)                 
                continue;
            
            // Grab this layer.
            final ArrayList<IDrawable> layer = layerList.get(i);
            
            // Draw its contents.
            for (IDrawable d : layer)
            {             
                d.draw();
//                if (clip == null 
//                        || (d.getDrawRect() != null 
//                            && d.getDrawRect().intersects(clip) == true))
//                {                   
//                    //count++;
//                    d.draw();                
//                }
//                //total++;
            }           
        } // end for
        
        // Report the number of sprites drawn.
        //Util.handleMessage("Drew " + count + " of " + total + " drawables.");
    }
    
    /**
     * Draws anything dirty touching the passed region.
     * 
     * @param shape The region to draw.
     * @param exact Whether or not we should draw the passed region exactly. If
     * exact is false, then only the area that has changed will be drawn.
     */
    public boolean draw(Shape region, boolean exact)
    {        
        // Draw the screen differently, depending on the renderer.
        switch(ResourceFactory.get().getRenderer())
        {
            case JAVA2D:
                return drawMinimal(region, exact);
                
            case LWJGL:
               
                // If we want the exact region, then we must clip.
                if (exact == true) 
                {
                    gfx.setClip(region);
                    drawAll();
                    gfx.setClip(null);
                    return true;
                }
                
                // Draw everything.
                drawAll();
                return true;
                
            default: throw new AssertionError();
        }
    }               
    
    public boolean draw(Shape region)
    {
        return draw(region, false);
    }
    
    public boolean draw()
    {
        return draw(Game.SCREEN_RECTANGLE, false);
    }
    
    private boolean drawMinimal(Shape region, boolean exact)
    {
        Rectangle clip = null;
        
        // Cycle through all the layers, drawing them.
        for (int i = 0; i < Layer.values().length; i++)
        {                                             
            // Grab this layer.
            ArrayList<IDrawable> layer = layerList.get(i);
            
            // See if it's in the region.
            for (IDrawable d : layer)
            {
                if (d.isDirty() == false)
                    continue;                                
                
                // Clear dirtiness.                
                //LogManager.recordMessage(d + " is dirty.");
                d.setDirty(false);
                
                Rectangle r = d.getDrawRect();                                
                               
                if (r != null && region.intersects(r) == true)
                {                                          
                    if (clip == null) clip = new Rectangle(r);
                    else clip.add(r);
                }
            } // end for                       
        }     
        
        // If the remove clip is not empty.
        if (getRemoveRect() != null)
        {
            // See if there's a clip to add to, or if we need to make one.
            if (clip == null)
                clip = getRemoveRect();
            else
                clip.add(getRemoveRect());
        }
             
        // Check to see if we're doing an exact area or a minimal clip.
        if (exact == true || clip != null)
        {
            //Util.handleMessage(clip.toString(), Thread.currentThread());
                        
            gfx.setClip(exact == true ? region : clip);                
            drawAll();

            // Show the clip rect if required.
            if (SettingsManager.get().getBoolean(Key.DEBUG_SHOW_CLIP_RECT) == true)
            {
                Rectangle r = gfx.getClip().getBounds();
                LogManager.recordMessage("Bounds are " + r);
                gfx.drawRect(r.x, r.y, r.width - 1, r.height - 1);
            }
                        
            gfx.setClip(null);          
               
            // Reset the remove clip.
            resetRemoveRect();
            
            return true;
        }        
        else
        {
            // Nothing changed, so don't bother rendering this frame.
            return false;
        }
    }        
    
    private void addRemoveRect(Rectangle r)
    {
        if (removeRect == null)
            removeRect = new Rectangle(r);
        else
            removeRect.add(r);
    }
    
    private Rectangle getRemoveRect()
    {
        return removeRect;
    }   
    
    private void resetRemoveRect()
    {       
        removeRect = null;
    }
    
    public void forceRedraw()
    {
        removeRect = Game.SCREEN_RECTANGLE.toRectangle();
    }
    
    @Override
    public String toString()
    {
        // Create a string builda'.
        StringBuilder buffer = new StringBuilder();
        
        // Print out the number of drawables on each layer.
        int i = 0;
        for (List<IDrawable> layer : layerList)
        {            
            buffer.append("Layer " + i + ": " 
                    + layer.size() + " drawables." + Settings.getLineSeparator());
            i++;
        }       
        
        return buffer.toString();
    }   

    public boolean addAll(LayerManager layerMan)
    {
        // Since all layer managers have the same number of layers,
        // we can merge them by adding all the array lists together.
        boolean changed = false; 
        
        for (int i = 0; i < layerList.size(); i++)        
           if (layerList.get(i).addAll(layerMan.layerList.get(i)) == true) 
               changed = true;
        
        return changed;
    }       

    public boolean retainAll(LayerManager layerMan)
    {
        // Since all layer managers have the same number of layers,
        // we can merge them by adding all the array lists together.
        boolean changed = false; 
        
        for (int i = 0; i < layerList.size(); i++)        
           if (layerList.get(i).retainAll(layerMan.layerList.get(i)) == true) 
               changed = true;
        
        return changed;  
    }      

    public boolean removeAll(LayerManager layerMan)
    {
        // Since all layer managers have the same number of layers,
        // we can merge them by adding all the array lists together.
        boolean changed = false; 
        
        for (int i = 0; i < layerList.size(); i++)        
           if (layerList.get(i).removeAll(layerMan.layerList.get(i)) == true) 
               changed = true;
        
        return changed;
    }    

    public boolean isDisabled()
    {
        return disabled;
    }

    public void setDisabled(boolean disabled)
    {
        // Save the state.
        this.disabled = disabled;
       
        // Go through all the layers and call their disabled method if they
        // are entities.
        for (List<IDrawable> list : layerList)
        {
            for (IDrawable drawable : list)
            {
                if (drawable instanceof IEntity)
                {
                    ((IEntity) drawable).setDisabled(disabled);
                }
            }
        } // end for
    }

    /**
     * Empties the contents of the layer manager.
     */
    public void dispose()
    {
        for (List<IDrawable> list : layerList)
        {
            for (IDrawable drawable : list)
            {
                if (drawable instanceof IDisposable)
                {
                    ((IDisposable) drawable).dispose();
                }
            }
        } // end for
    }
    
}
