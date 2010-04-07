
package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.CouchLogger;
import java.util.Calendar;
import java.util.prefs.*;

public class TimeTrial
{
    private final static long TOTAL_TIME = 1000 * 60 * 60;
    private final static long MINUTE = 1000 * 60;
    private final static String KEY = "TRIAL";
    private static long startInstant;
    private static int timePlayed;
    private static Preferences prefs;
    private static boolean started = false;
    private static final IWindow win = ResourceFactory.get().getWindow();

    private TimeTrial()
    {
        
    }


    public synchronized static void start()
    {
        if(started == false)
        {
            prefs = Preferences.userNodeForPackage(TimeTrial.class);
            //prefs.put(KEY, "0");
            timePlayed = prefs.getInt(KEY, 0);
            CouchLogger.get().recordMessage(TimeTrial.class, "time played: " + timePlayed + " minutes.");
            started = true;
            startInstant = win.getTime();
        }
    }

    public synchronized static boolean isStarted()
    {
        return started;
    }

    public synchronized static void updateRegistry()
    {
        long currentInstant = win.getTime();

        if (currentInstant - startInstant > MINUTE)
        {
            long offset = (currentInstant - startInstant);
            timePlayed += offset / MINUTE;
            startInstant = currentInstant - (offset % MINUTE);
            prefs.put(KEY, String.valueOf(timePlayed));
            CouchLogger.get().recordMessage(TimeTrial.class, "time played: " + timePlayed + " minutes.");
        }
    }

    public synchronized static boolean isTrialExpired()
    {
        return timePlayed >= TOTAL_TIME;
    }
}
