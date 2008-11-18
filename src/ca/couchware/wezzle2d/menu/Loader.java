/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.graphics.EntityGroup;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.graphics.IDrawer;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.ui.ProgressBar;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class for creating a loader object that pre-loads all the graphical and 
 * audio resources used by the game.
 * 
 * @author cdmckay
 */
public class Loader implements IDrawer        
{    
    
    /** 
     * The standard menu background.
     */
    final private static String BACKGROUND_PATH = Settings.getSpriteResourcesPath()
            + "/MenuBackground.png"; 
    
    /**
     * An enum containing the possible states for the loader to be in.
     */
    public enum State
    {                       
        /** 
         * The loader has some runnables queued and is ready to run.
         */
        READY,
        
        /**
         * The loader is animating.
         */
        ANIMATING,
              
        /**
         * The loader is done loading all of the runnables.
         */
        FINISHED
    }        
    
    /**
     * The layer manager.
     */
    final private LayerManager layerMan;
    
    /**
     * The list of things to load.
     */
    final private Queue<Runnable> loaderQueue;
        
    /**
     * The current state of the loader.
     */
    private State state = State.READY;
    
    /**
     * The number of runnables run.
     */
    private int counter = 0;                 
    
    /**
     * The progress bar.
     */
    private ProgressBar progressBar;
    
    /**
     * The animation that fades out the loader.
     */
    private IAnimation animation;
        
    /**
     * The consturctor.
     * 
     * @param settingsMan
     */       
    public Loader()
    {                
        // Create the layer manager.
        this.layerMan = LayerManager.newInstance();
        
        // Add the background.
        GraphicEntity backgroundGraphic = 
                new GraphicEntity.Builder(0, 0, BACKGROUND_PATH).end();
        layerMan.add(backgroundGraphic, Layer.BACKGROUND);
                
        // Set up the copyright label.               
        ILabel l1 = new LabelBuilder(10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))
                .color(Game.TEXT_COLOR_DISABLED).size(12)                
                .text(Game.COPYRIGHT).end();
        layerMan.add(l1, Layer.UI);
        
        // Set up the version label.	
        ILabel l2 = new LabelBuilder(800 - 10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.RIGHT))
                .color(Game.TEXT_COLOR_DISABLED).size(12)                
                .text(Game.TITLE).end();                        
        layerMan.add(l2, Layer.UI);
        
        // Create the loader label.
        ILabel l3 = new ResourceFactory.LabelBuilder(400, 273)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(Game.TEXT_COLOR1)
                .cached(false).size(26).text("Loading Wezzle...").end();
        this.layerMan.add(l3, Layer.UI);
        
        // Create the progress bar.
        this.progressBar = new ProgressBar.Builder(400, 326)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .type(ProgressBar.Type.LARGE).useLabel(false).end();
        layerMan.add(progressBar, Layer.UI);
        
        // Create an entity group that will be used by the transition
        // animation.
        EntityGroup e = new EntityGroup(l3, progressBar);
        
        // Create the animation that will be used to transition the
        // loader to the menu screen.
        SettingsManager settingsMan = SettingsManager.get();
        this.animation = new FadeAnimation.Builder(FadeAnimation.Type.OUT, e)
                .wait(settingsMan.getInt(Key.LOADER_BAR_FADE_WAIT))
                .duration(settingsMan.getInt(Key.LOADER_BAR_FADE_DURATION))
                .end();
        
        // Initialize the loader list.
        this.loaderQueue = new LinkedList<Runnable>();
        
        // Add a blank runnable first.
        addRunnable(new Runnable()
        {
           public void run() { } 
        });
        
        // Add the graphics to the loader.
        addRunnable(new Runnable()
        {
            public void run() { loadSprites(); }
        });
    }
    
    /**
     * This method will preload all the sprites in the sprite directory.  It
     * is always the first thing the loader does.
     */
    private void loadSprites()
    {
        // The list of the sprites.
        List<String> spriteList = new ArrayList<String>();
        
        // Detect whether or not we're using a JAR file.
        URL jarPathUrl = Loader.class.getProtectionDomain().getCodeSource().getLocation();
        boolean isJar = jarPathUrl.toString().endsWith(".jar");
              
        // If we're running from a JAR, then we need to read the JAR entries to
        // figure out all the names of the sprites.
        if (isJar == true)
        {
            try
            {
                // Open the jar.
                JarInputStream in = new JarInputStream(jarPathUrl.openStream());

                while (true)
                {
                    JarEntry entry = in.getNextJarEntry();
                   
                    if (entry == null) 
                        break;
                    
                    if (entry.isDirectory() == true)
                        continue;                    

                    if (entry.getName().startsWith(Settings.getSpriteResourcesPath()))
                        spriteList.add(entry.getName());                    
                }
            }
            catch (IOException e)
            {
                LogManager.recordException(e);
            }        
        }
        // If we're running from the file system, all we need to do is use
        // the File class to get a list of the sprites.
        else
        {
            // The directory of sprites.
            File dir = null;

            try
            {            
                // Get a list of all the sprites in the sprites directory.
                URL url = this.getClass().getClassLoader()
                        .getResource(Settings.getSpriteResourcesPath());                        

                // Convert to file.
                dir = new File(url.toURI());
                
                // Construct the full URL to the sprite.
                for (String spriteName : dir.list())
                    spriteList.add(Settings.getSpriteResourcesPath() + "/" + spriteName);
            }
            catch (URISyntaxException e)
            {            
                LogManager.recordException(e);
                
            } // end try 
        }
                                           
        // Get the contents of the directory.
        for (String spriteFilePath : spriteList)
        {
            LogManager.recordMessage("Preloading " + spriteFilePath + "...");
            ResourceFactory.get().getSprite(spriteFilePath);         
        }   
    }
    
    private void loadNextRunnable()
    {
        // Run the next loader runnable.
        loaderQueue.remove().run();
        
        // Incremenet the counter.
        counter++;
        
        // Update the progress.
        progressBar.setProgress(counter);
    }        
    
    public void addRunnable(Runnable r)
    {
        if (r == null)
            throw new NullPointerException("Runnable must not be null.");
     
        
        // Add it to the queue.
        loaderQueue.add(r);
        
        // The loader is now ready.
        state = State.READY;
        
        // Adjust progress bar.
        progressBar.setProgressMax(loaderQueue.size());
    }        
        
    public void updateLogic(Game game)
    {       
        switch (state)
        {
            case READY:
               
                // Load the next runnable.
                loadNextRunnable();
                
                // See if the queue is empty.  If it is, then switch
                // to the animation.
                if (loaderQueue.isEmpty() == true)
                {                    
                    game.animationMan.add(animation);
                    state = state.ANIMATING;
                }
                
                break;
                
            case ANIMATING:
                
                // See if animation is done yet.
                if (animation.isFinished() == true)
                    state = state.FINISHED;
                
                break;
                
            case FINISHED:
                
                // Do nothing, we're done.
                
                break;
                
            default: throw new AssertionError();
        } // end switch                               
    }

    public State getState()
    {
        return state;
    }        
    
    public boolean draw()
    {
        return layerMan.draw();
    }
    
    public void forceRedraw()
    {
        layerMan.forceRedraw();
    }
        
}
