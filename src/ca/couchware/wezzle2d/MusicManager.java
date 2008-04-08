package ca.couchware.wezzle2d;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import java.net.URL;
import javazoom.jl.player.Player;

/**
 * A class to handle background music.
 * @author Kevin
 */

public class MusicManager {
    private URL file;
    private Player player; 

    // constructor that takes the name of an MP3 file
    public MusicManager(String filepath) {
        this.file = this.getClass().getClassLoader()
            .getResource(filepath);
        
        if(this.file == null)
            System.out.println("ERROR");
    }
    
    // Loads a default song.
    public MusicManager()
    {
        this(Game.MUSIC_PATH + "/IntergalacticTron.mp3");
    }

    public void close() 
    { 
        if (player != null) player.close(); 
    }

    // play the MP3 file to the sound card
    public void play() 
    {
        try 
        {
            BufferedInputStream bis = new BufferedInputStream(file.openStream());
            player = new Player(bis);
        }
        catch (Exception e) 
        {
            System.out.println("Problem playing file " + file.toString());
            System.out.println(e);
        }

        // run in new thread to play in background
        new Thread() 
        {
            public void run() 
            {
                try 
                { 
                    player.play();   
                }
                catch (Exception e) 
                { 
                    System.out.println(e); 
                }
            }
        }.start();
    }
}
