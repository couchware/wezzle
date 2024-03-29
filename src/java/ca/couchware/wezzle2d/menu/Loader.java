/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory;
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
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.ProgressBar;
import java.awt.Shape;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;
/**
 * A class for creating a loader object that pre-loads all the graphical and 
 * audio resources used by the game.
 * 
 * @author cdmckay
 */
public class Loader implements IDrawer        
{    
    
    /** The standard loader background. */
    final private static String BACKGROUND_PATH = Settings.getSpriteResourcesPath()
            + "/MenuBackground.png"; 
    
    /** An enum containing the possible states for the loader to be in. */
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
    
    /** The layer manager. */
    final private LayerManager layerMan;
    
    /** The queue of things to load. */
    final private Queue<Runnable> loaderQueue;
        
    /** The current state of the loader. */
    private State state = State.READY;
    
    /** The number of runnables run. */
    private int counter = 0;                 
    
    /** The progress bar. */
    private ProgressBar progressBar;
    
    /** The animation that fades out the loader. */
    private IAnimation animation;
        
    /**
     * The consturctor.
     * 
     * @param settingsMan
     */       
    public Loader(IWindow win, SettingsManager settingsMan, String title)
    {                
        // Create the layer manager.
        this.layerMan = LayerManager.newInstance(win);
        
        // Add the background.
        GraphicEntity backgroundGraphic = 
                new GraphicEntity.Builder(0, 0, BACKGROUND_PATH).build();
        layerMan.add(backgroundGraphic, Layer.BACKGROUND);
                
        // Set up the copyright label.               
        ITextLabel label1 = new LabelBuilder(10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))
                .color(settingsMan.getColor(Key.GAME_COLOR_DISABLED)).size(12)                
                .text(Game.COPYRIGHT).build();
        layerMan.add(label1, Layer.UI);
        
        // Set up the version label.	
        ITextLabel label2 = new LabelBuilder(800 - 10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.RIGHT))
                .color(settingsMan.getColor(Key.GAME_COLOR_DISABLED)).size(12)                
                .text(Game.TITLE).build();
        layerMan.add(label2, Layer.UI);
        
        // Create the loader title.
        ITextLabel label3 = new ResourceFactory.LabelBuilder(400, 273)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                
                .size(26).text(title).build();
        this.layerMan.add(label3, Layer.UI);
        
        // Create the progress bar.
        this.progressBar = new ProgressBar.Builder(400, 326)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .barWidth(ProgressBar.BarWidth.LARGE)
                .textPosition(ProgressBar.TextPosition.NONE).build();
        layerMan.add(progressBar, Layer.UI);
        
        // Create an entity group that will be used by the transition
        // animation.
        EntityGroup e = new EntityGroup(label3, progressBar);
        
        // Create the animation that will be used to transition the
        // loader to the menu screen.        
        this.animation = new FadeAnimation.Builder(FadeAnimation.Type.OUT, e)
                .wait(settingsMan.getInt(Key.LOADER_BAR_FADE_WAIT))
                .duration(settingsMan.getInt(Key.LOADER_BAR_FADE_DURATION))
                .build();
        
        // Initialize the loader list.
        this.loaderQueue = new LinkedList<Runnable>();
        
        // Add a blank runnable first.
        this.addTask(new Runnable()
        {
           public void run() { } 
        });
                
    }
            
    private void executeNextTask()
    {
        // Run the next loader runnable.
        loaderQueue.remove().run();
        
        // Incremenet the counter.
        counter++;
        
        // Update the progress.
        progressBar.setProgressValue(counter);
    }        
    
    public void addTask(Runnable task)
    {
        // Make sure task is not null.
        if(task == null)
        {
           throw new IllegalArgumentException("task must not be null.");
        }
             
        // Add it to the queue.
        loaderQueue.add(task);
        
        // The loader is now ready.
        state = State.READY;
        
        // Adjust progress bar.
        progressBar.setProgressUpper(loaderQueue.size());
    }   
    
    public void addTasks(Collection<Runnable> collection)
    {
        for (Runnable task : collection)
        {
            this.addTask(task);
        }
    }
        
    public void updateLogic(Game game, ManagerHub hub)
    {       
        switch (state)
        {
            case READY:
               
                // Load the next runnable.
                this.executeNextTask();
                
                // See if the queue is empty.  If it is, then switch
                // to the animation.
                if (loaderQueue.isEmpty() == true)
                {                    
                    hub.gameAnimationMan.add(animation);
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
    
    public boolean draw(Shape region, boolean exact)
    {
        return layerMan.draw(region, exact);
    }

    public boolean draw(Shape region)
    {
        return layerMan.draw(region);
    }
        
}
