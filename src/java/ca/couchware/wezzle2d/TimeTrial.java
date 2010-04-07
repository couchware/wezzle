
package ca.couchware.wezzle2d;

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
            System.out.println("time played: " + timePlayed + " minutes.");
            started = true;
            startInstant = ResourceFactory.get().getWindow().getTime();
        }
    }

    public synchronized static boolean isStarted()
    {
        return started;
    }

    public synchronized static void updateRegistry()
    {
        long currentInstant = ResourceFactory.get().getWindow().getTime();

        if (currentInstant - startInstant > MINUTE)
        {
            timePlayed += (currentInstant - startInstant) / MINUTE;
            startInstant = currentInstant;
            prefs.put(KEY, String.valueOf(timePlayed));
            System.out.println("time played: " + timePlayed + " minutes.");
        }
    }

    public synchronized static boolean isTrialExpired()
    {
        return timePlayed >= TOTAL_TIME;
    }
}
