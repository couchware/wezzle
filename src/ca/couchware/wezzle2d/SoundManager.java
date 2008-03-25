package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.Util;
import java.applet.AudioClip;
import java.net.URL;

/**
 * A class for managing the playing of game sounds.
 * Sounds are played by calling the Play() method and passing in
 * an integer. 
 * 
 * The class also defines the constants BOMB, LINE, BLEEP, CLICK for the
 * sounds that are available.
 * 
 * @author Kevin
 */

public class SoundManager
{
    public final static int BOMB = 1;
    public final static int LINE = 2;
    public final static int BLEEP = 3;
    public final static int CLICK = 4;
    
    /**
     * Path to the line audio clip.
     */
    private final URL lineUrl = this.getClass().getClassLoader()
            .getResource(Game.SOUNDS_PATH + "/SoundLine.wav");

    /**
     * Path to the bomb audio clip.
     */
    private final URL bombUrl = this.getClass().getClassLoader()
            .getResource(Game.SOUNDS_PATH + "/SoundExplosion.wav");

    /**
     * Path to the bleep audio clip.
     */
    private final URL bleepUrl = this.getClass().getClassLoader()
            .getResource(Game.SOUNDS_PATH + "/SoundBleep.wav");

    /**
     * Path to the click audio clip.
     */
    private final URL clickUrl = this.getClass().getClassLoader()
            .getResource(Game.SOUNDS_PATH + "/SoundClick.wav");

    /**
     * The current line clip we are playing.
     */
    // @GuardBy("lineClip")
    private int lineCounter;

    /**
     * The line clip array.
     */
    private final AudioClip[] lineClip = new AudioClip[4];	

    /**
     * The current bomb clip we are playing.
     */
    // @GuardBy("bombClip")
    private int bombCounter;

    /**
     * The bomb clip array.
     */	
    private final AudioClip[] bombClip = new AudioClip[8];

    /**
     * The current bleep clip we are playing.
     */
    // @GuardBy("bleepClip")
    private int bleepCounter;

    /**
     * The bleep clip array.
     */
    private final AudioClip[] bleepClip = new AudioClip[4];

    /**
     * The click counter.
     */
    private int clickCounter;
    
    /**
     * The click clip.
     */
    private final AudioClip[] clickClip = new AudioClip[4];

    /**
     * The constructor.
     */
    public SoundManager()
    {
        // Initialize line clip.
        lineCounter = 0;
        for (int i = 0; i < lineClip.length; i++)
                lineClip[i] = java.applet.Applet.newAudioClip(lineUrl);	

        // Initialize bomb clips.
        bombCounter = 0;
        for (int i = 0; i < bombClip.length; i++)
                bombClip[i] = java.applet.Applet.newAudioClip(bombUrl);	

        // Initialize bleep clips.
        bleepCounter = 0;
        for (int i = 0; i < bleepClip.length; i++)
                bleepClip[i] = java.applet.Applet.newAudioClip(bleepUrl);

        // Initialize the click.
        clickCounter = 0;
         for (int i = 0; i < clickClip.length; i++)
                clickClip[i] = java.applet.Applet.newAudioClip(clickUrl);
    }
    
    /**
     * A method to play a soundclip.
     * @param soundClip The soundclip to play.
     */
    public void play(int soundClip)
    {
        //Play the line sound. Blah.
        if (soundClip == LINE)
        {
            Util.handleMessage("It's a normal!", Thread.currentThread());
            
            lineClip[lineCounter].play();
            lineCounter = (lineCounter + 1) % lineClip.length;
        }
        else if (soundClip == BOMB)
        {
            Util.handleMessage("It's a bomb!", Thread.currentThread());

            bombClip[bombCounter].play();
            bombCounter = (bombCounter + 1) % bombClip.length;       
        }
        else if(soundClip == BLEEP)
        {
           Util.handleMessage("It's a bleep!", Thread.currentThread());
           
            bleepClip[bleepCounter].play();
            bleepCounter = (bleepCounter + 1) % bleepClip.length;
        }
        else if(soundClip == CLICK)
        {
            Util.handleMessage("It's a click!", Thread.currentThread());
            
            clickClip[clickCounter].play();
            clickCounter = (clickCounter + 1) % clickClip.length;
            
        } // end if
    }
}

