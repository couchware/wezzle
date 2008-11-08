/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.audio.*;
import ca.couchware.wezzle2d.*;
import ca.couchware.wezzle2d.audio.Music;
import ca.couchware.wezzle2d.manager.Settings.Key;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import javazoom.jlgui.basicplayer.BasicPlayerException;

/**
 * A class to handle background music.
 * 
 * @author Kevin, Cameron
 */

public class MusicManager 
{            
    
    /**
     * Is the music manager paused?
     */
    private boolean paused = false;       
    
    /**
     * The different possible music themes.
     */
    public enum Theme
    {
        /** No theme selected. */
        NONE,
        
        /** TRON */
        A,
        
        /** ELECTRONIC */
        B,
        
        /** HIP POP */
        C,
        
        /** All themes, in a random order. */
        ALL,
        
        /** A random theme. */
        RANDOM
    }
    
    /**
     * The current music theme.  Default to A.
     */
    private Theme theme = Theme.NONE;
    
    /**
     * A link to the executor that the manager uses to play sounds.
     */
    private Executor executor;
    
    /** 
     * A link to the property manager. 
     */
    private SettingsManager settingsMan;
    
    /** 
     * The list of the music.
     */
    private List<Music> playList;      
    
    /** 
     * The track number we are on.
     */
    private int index;        
    
    /**
     * The current player for the track we are on.  Set to null if the track
     * has not been played yet.
     */
    private MusicPlayer player;
        
    /**
     * The normalized gain.
     */
    private double normalizedGain = 0.0; 
    
    /**
     * Creates the song list.
     */
    private MusicManager(Executor executor, SettingsManager settingsMan) 
    {        
        // The executor.
        this.executor = executor;
        
        // The property manager.
        this.settingsMan = settingsMan;                
                        
        // Initiate the array list and song number.
        this.playList = new ArrayList<Music>();                     
       
        // Set the starting player to be just before the first track.  
        this.index = 0;    
                        
        // Get the default volume.
        setNormalizedGain(
                settingsMan.getDoubleProperty(Key.GAME_MUSIC_VOLUME));                
    }
        
    /**
     * Static constructor.
     */
    public static MusicManager newInstance(
            Executor executor, SettingsManager settingsMan)
    {
        return new MusicManager(executor, settingsMan);
    }           
    
    public void setTheme(Theme theme)
    {
        // Remember the theme.
        this.theme = theme;       
        
        // Record what it was changed to.
        LogManager.recordMessage("Theme set to " + theme + ".");               
        
        // Stop and discard the player.
        stop();        
        this.player = null;
        
        // Reset the play list.
        this.index = 0;
        this.playList.clear();                
        
        // If the theme is the NONE theme then just return.
        if (theme == Theme.NONE) return;
        
        // Create a theme list (used below).
        List<Theme> themeList = new ArrayList<Theme>(3);
        themeList.add(Theme.A);
        themeList.add(Theme.B);
        themeList.add(Theme.C);
        Collections.shuffle(themeList);
        
        // See which theme it is.
        switch (theme)
        {
            case A:                                                
            case B:
            case C:
                
               enqueueTheme(theme);
               break;
                
            case ALL:
                                                
                for (Theme t : themeList)
                    enqueueTheme(t);
                
                break;
                
            case RANDOM:
                               
                enqueueTheme(themeList.get(0));
                
                break;
                
            default: throw new AssertionError();
        } // end switch                                           
    }
    
    public Theme getTheme()
    {
        return theme;
    }     
    
    private void enqueueTheme(Theme theme)
    {
        // See which theme it is.
        switch (theme)
        {
            case A:
                
                this.playList.add(Music.TRON1);
                this.playList.add(Music.TRON2);
                this.playList.add(Music.TRON3);
                   
                break;
                
            case B:
                
                this.playList.add(Music.ELECTRONIC1);
                this.playList.add(Music.ELECTRONIC2);
                this.playList.add(Music.ELECTRONIC3);
                
                break;
                
            case C:
                
                this.playList.add(Music.HIPPOP1);
                this.playList.add(Music.HIPPOP2);
                this.playList.add(Music.HIPPOP3);
                
                break;
                
            case ALL:
            case RANDOM:
                
                throw new IllegalArgumentException("Only A, B, C are valid for this method.");                                                                            
                
            default: throw new AssertionError();
        }  
    }
   
    /**
     * Plays the currently selected track index.     
     */
    public void play()
    {
        // Make sure the play list has items, if not, ignore the play command.
        if (this.playList.isEmpty() == true)
            return;
        
        // Grab the player for the current track if it hasn't been grabbed
        // already.
        if (this.player == null)
        {
            this.player = createPlayer(playList.get(index));
            this.player.fadeToGain(normalizedGain);
        }
        
        try
        {            
            // Play.
            this.player.play();
        }
        catch (BasicPlayerException e)
        {
            LogManager.recordException(e);
        }
    }
    
    public void stop()
    {
        // If the player is not assigned, then stop has no effect.
        if (this.player == null)
            return;
                
        try
        {            
            // Otherwise, stop the player.
            this.player.stop();
        }
        catch (BasicPlayerException e)
        {
            LogManager.recordException(e);
        }
    }
    
    public void next()
    {
        // Make sure the play list has items, if not, ignore the command.
        if (this.playList.isEmpty() == true)
            return;
        
        // Stop the current track, advance the index and release the old player.
        stop();
        this.player = null;
        index = (index + 1) % playList.size();               
    }
    
    public void previous()
    {
        // Make sure the play list has items, if not, ignore the play command.
        if (this.playList.isEmpty() == true)
            return;
        
        // Stop the current track, reduce the index and release the old player.
        stop();
        this.player = null;
        index = (index - 1) < 0 ? playList.size() - 1 : index - 1;  
    }             
    
    public boolean isPaused()
    {
        return paused;
    }        
    
    /**
     * Toggle the paused variable
     * @param paused whether or not to pause.
     */
    public void setPaused(boolean paused)
    {        
        this.paused = paused;
        
        // Pause the player if it exists.
        if (this.player != null)
        {
            try
            {
                if (paused == true)
                {
                    player.pause();
                }
                else
                {
                    player.resume();
                }
            }
            catch (BasicPlayerException e)
            {
                LogManager.recordException(e);
            }
        } // end if
    }   
    
    public double getNormalizedGain()
    {
        return normalizedGain;
    }

    public void setNormalizedGain(double nGain)
    {
        // Make sure it's between 0.0 and 1.0.
        if (nGain < 0.0)
        {
            nGain = 0.0;
        }
        else if (nGain > 1.0)
        {
            nGain = 1.0;
        }
        // Adjust the property;
        settingsMan.setDoubleProperty(Key.GAME_MUSIC_VOLUME, nGain);

        // Rememeber it.
        this.normalizedGain = nGain;
        
        try
        {            
            // Adjust the current player.
            if (this.player != null)
            {
                this.player.setNormalizedGain(nGain);
            }
        }
        catch (BasicPlayerException e)
        {
            LogManager.recordException(e);
        }
    }
    
    public void updateLogic(Game game)
    {
        // See if there's even something playing.  
        // If there is, then check to see if it's done playing.
        // If it is, then move to the next track and start playing it.
        if (this.player != null && this.player.isFinished() == true)
        {
            next();
            play();
        }
    }
    
    /**
     * Creates a basic player that immediately opens the passed path.
     * 
     * @param String path
     * @return A new player with that passed path already opened.
     */
    private static MusicPlayer createPlayer(String path)
    {
        // Create the new basic player.
        MusicPlayer player = MusicPlayer.newInstance(); 

        // Load the reserouce.
        URL url = MusicManager.class.getClassLoader().getResource(path);

        // Check the URL.
        if (url == null)
        {
            throw new RuntimeException("Url Error: " + path + " does not exist.");
        }
        
        try
        {                    
            // Try top oen the file.
            player.open(url);            
        }
        catch (BasicPlayerException e)
        {
            LogManager.recordException(e);
        }
        
        // Return the player.
        return player;
    }
    
    /**
     * Creates a player for the passed track.
     * 
     * @param track
     * @return
     */
    public static MusicPlayer createPlayer(Music track)
    {
        return createPlayer(track.getPath());
    }
    
}
