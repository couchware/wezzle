
package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.CouchLogger;
import java.util.prefs.Preferences;

public class Trial
{
    private final static long MILLISECONDS_PER_MINUTE = 1000 * 60;
    private final static long TOTAL_TIME = 60 * MILLISECONDS_PER_MINUTE;
    private final static String KEY = "TRIAL";

    private static long startInstant;
    private static long timePlayedInMilliseconds;
    private static Preferences preferences;
    private static boolean inited = false;
    private static boolean started = false;

    private Trial()
    {
        // To make singletonized.
    }

    static
    {
        preferences = Preferences.userNodeForPackage(Trial.class);
        timePlayedInMilliseconds = preferences.getLong(KEY, 0);
    }
    
    public synchronized static void start(IWindow win)
    {
        if (!started)
        {
            CouchLogger.get().recordMessage(Trial.class,
                    String.format("Time played at launch was %d minutes (%d milliseconds)",
                    getTimePlayedInMinutes(), getTimePlayedInMilliseconds()));
            
            started = true;
            startInstant = win.getTime();
        }
    }

    public synchronized  static long getTimePlayedInMilliseconds()
    {
        return timePlayedInMilliseconds;
    }

    public synchronized  static int getTimePlayedInMinutes()
    {
        return (int) (timePlayedInMilliseconds / MILLISECONDS_PER_MINUTE);
    }

    public synchronized static boolean hasStarted()
    {
        return started;
    }

    public synchronized static void updateRegistry(IWindow win)
    {
        updateRegistry(win, false);
    }

    public synchronized static void updateRegistry(IWindow win, boolean forceUpdate)
    {
        if (!started)
        {
            throw new RuntimeException("Attempted to update registry before starting the trial");
        }

        final long currentInstant = win.getTime();

        if ((currentInstant - startInstant > MILLISECONDS_PER_MINUTE) || forceUpdate)
        {
            final long offset = (currentInstant - startInstant);
            timePlayedInMilliseconds += offset;
            startInstant = currentInstant;
            preferences.put(KEY, String.valueOf(timePlayedInMilliseconds));
            CouchLogger.get().recordMessage(Trial.class,
                    String.format("Time played is now %d minutes (%d milliseconds)",
                    getTimePlayedInMinutes(), getTimePlayedInMilliseconds()));            
        }
    }

    public synchronized static boolean hasTrialExpired()
    {
        return timePlayedInMilliseconds >= TOTAL_TIME;
    }
}
