/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.ui.ProgressBar;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A class for creating a loader object that pre-loads all the graphical and 
 * audio resources used by the game.
 * 
 * @author cdmckay
 */
public class Loader 
{

    /** 
     * The standard menu background used by the loader. 
     */
    final private static String BACKGROUND_PATH = Game.SPRITES_PATH 
            + "/MenuBackground.png";        
    
    /**
     * The layer manager.
     */
    private LayerManager layerMan;
    
    /**
     * The list of things to load.
     */
    private Queue<Runnable> loaderQueue;
    
    /**
     * The number of runnables run.
     */
    private int counter = 0;
       
    /** 
     * The menu background graphic. 
     */
    private GraphicEntity background;
    
    /**
     * The "Loading Wezzle..." text.
     */
    private ILabel loaderLabel;
    
    /**
     * The progress bar.
     */
    private ProgressBar progressBar;
    
    /**
     * The consturctor.
     * 
     * @param layerMan
     */       
    public Loader(LayerManager layerMan)
    { 
        // Check arguments.
        if (layerMan == null)
            throw new NullPointerException("Layer Manager must not be null.");
        
        // Save reference.
        this.layerMan = layerMan;
        
        // Initialize the loader list.
        loaderQueue = new LinkedList<Runnable>();                
        
        // Create the loading screen.
        this.background = new GraphicEntity.Builder(0, 0, BACKGROUND_PATH).end();
        layerMan.add(background, Game.LAYER_BACKGROUND);
        
        // Create the loader label.
        this.loaderLabel = new ResourceFactory.LabelBuilder(400, 273)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR)
                .cached(false).size(26).text("Loading Wezzle...").end();
        layerMan.add(loaderLabel, Game.LAYER_UI);
        
        // Create the progress bar.
        this.progressBar = new ProgressBar.Builder(400, 326)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .type(ProgressBar.Type.LARGE).useLabel(false).end();
        layerMan.add(progressBar, Game.LAYER_UI);
        
        // Add a blank runnable first.
        add(new Runnable()
        {
           public void run() { } 
        });
        
        // Add the graphics to the loader.
        add(new Runnable()
        {
            public void run() { loadSprites(); }
        });
    }
    
    public void add(Runnable r)
    {
        if (r == null)
            throw new NullPointerException("Runnable must not be null.");
        
        loaderQueue.add(r);
        
        // Adjust progress bar.
        progressBar.setProgressMax(loaderQueue.size());
    }
    
    public void loadNext()
    {
        // Run the next loader runnable.
        loaderQueue.remove().run();
        
        // Incremenet the counter.
        counter++;
        
        // Update the progress.
        progressBar.setProgress(counter);
    }
    
    public boolean hasNext()
    {        
        return !loaderQueue.isEmpty();
    }
    
    /**
     * This method will preload all the sprites in the sprite directory.  It
     * is always the first thing the loader does.
     */
    private void loadSprites()
    {
        // Get a list of all the sprites in the sprites directory.
        URL url = this.getClass().getClassLoader().getResource(Game.SPRITES_PATH);
        
        // The directory of sprites.
        File dir = null;
        
        try
        {            
            // Convert to file.
            dir = new File(url.toURI());                        
        }
        catch (URISyntaxException e)
        {
            LogManager.recordException(e);
        }        
        
        // Get the contents of the directory.
        for (String spriteName : dir.list())
        {
            LogManager.recordMessage("Preloading " + spriteName + "...");
            ResourceFactory.get().getSprite(Game.SPRITES_PATH + "/" + spriteName);         
        }   
    }           
        
}
