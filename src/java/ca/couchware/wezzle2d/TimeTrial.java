
package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.CouchLogger;
import java.util.prefs.Preferences;

public class TimeTrial
{
    private final static long TOTAL_TIME = 1000 * 60 * 60;
    private final static long MILLISECONDS_PER_MINUTE = 1000 * 60;
    private final static String KEY = "TRIAL";

    private static long startInstant;
    private static int timePlayedInMinutes;
    private static Preferences preferences;
    private static boolean started = false;
    private static final IWindow win = ResourceFactory.get().getWindow();

    private TimeTrial()
    {
        // To make singletonized.
    }

    public synchronized static void start()
    {
        if (!started)
        {
            preferences = Preferences.userNodeForPackage(TimeTrial.class);
            timePlayedInMinutes = preferences.getInt(KEY, 0);
            CouchLogger.get().recordMessage(TimeTrial.class, "Time played at start is " + timePlayedInMinutes + " minutes");
            
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
        final long currentInstant = win.getTime();

        if (currentInstant - startInstant > MILLISECONDS_PER_MINUTE)
        {
            final long offset = (currentInstant - startInstant);
            timePlayedInMinutes += offset / MILLISECONDS_PER_MINUTE;
            startInstant = currentInstant - (offset % MILLISECONDS_PER_MINUTE);
            preferences.put(KEY, String.valueOf(timePlayedInMinutes));
            CouchLogger.get().recordMessage(TimeTrial.class, "Time played is now " + timePlayedInMinutes + " minutes");
        }
    }

    public synchronized static boolean hasTrialExpired()
    {
        return timePlayedInMinutes >= TOTAL_TIME;
    }
}
