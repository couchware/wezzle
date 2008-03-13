package ca.couchware.wezzle2d;


import ca.couchware.wezzle2d.Game;
import java.applet.AudioClip;
import java.net.URL;



/**
 *A class for managing the playing of game sounds.
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
    private final URL lineUrl = this.getClass().getClassLoader().getResource("resources/SoundLine.wav");

    /**
     * Path to the bomb audio clip.
     */
    private final URL bombUrl = this.getClass().getClassLoader().getResource("resources/SoundExplosion.wav");

    /**
     * Path to the bleep audio clip.
     */
    private final URL bleepUrl = this.getClass().getClassLoader().getResource( "resources/SoundBleep.wav");

    /**
     * Path to the click audio clip.
     */
    private final URL clickUrl = this.getClass().getClassLoader().getResource("resources/SoundClick.wav");

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
     * The click clip.
     */
    private final AudioClip clickClip;

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
        clickClip = java.applet.Applet.newAudioClip(clickUrl);
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
                synchronized (lineClip)
                {
                        lineClip[lineCounter].play();
                        lineCounter = (lineCounter + 1) % lineClip.length;
                }
        }
        else if (soundClip == BOMB)
        {
                Util.handleMessage("It's a bomb!", Thread.currentThread());
                synchronized (bombClip)
                {
                        bombClip[bombCounter].play();
                        bombCounter = (bombCounter + 1) % bombClip.length;
                }
        }
        else if(soundClip == BLEEP)
        {
           Util.handleMessage("It's a bleep!", Thread.currentThread());
            synchronized (bleepClip)
            {
                bleepClip[bleepCounter].play();
                bleepCounter = (bleepCounter + 1) % bleepClip.length;
            }
        }
        else if(soundClip == CLICK)
        {
            Util.handleMessage("It's a click!", Thread.currentThread());
            synchronized (clickClip)
            {
                clickClip.play();
            } 
        }
    }
	
//	// ---------------------------------------------------------------------------
//	// Listeners
//	// ---------------------------------------------------------------------------
//	
//	public void lineEventOccurred(LineEvent evt)
//	{
//		// Play the line sound. Blah.
//		if (evt.getLineType() == LineEvent.TYPE_NORMAL)
//		{
//			Util.handleMessage("It's a normal!", Thread.currentThread());
//			synchronized (lineClip)
//			{
//				lineClip[lineCounter].play();
//				lineCounter = (lineCounter + 1) % lineClip.length;
//			}
//		}
//		else if (evt.getLineType() == LineEvent.TYPE_BOMB)
//		{
//			Util.handleMessage("It's a bomb!", Thread.currentThread());
//			synchronized (bombClip)
//			{
//				bombClip[bombCounter].play();
//				bombCounter = (bombCounter + 1) % bombClip.length;
//			}
//		}
//	}
//
//	public void commitCompleted(CommitEvent evt)
//	{
//		// Intentionally left blank.
//	}
//	
//	public void commitStarted(CommitEvent evt) 
//	{
//		// Play the click.
//		clickClip.play();		
//	}
//
//	public void tileAdded(GameBoardEvent evt)
//	{
//		// Play the bleep.
//		synchronized (bleepClip)
//		{
//			bleepClip[bleepCounter].play();
//			bleepCounter = (bleepCounter + 1) % bleepClip.length;
//		}
//	}
}

