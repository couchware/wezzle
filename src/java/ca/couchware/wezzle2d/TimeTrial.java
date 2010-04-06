
package ca.couchware.wezzle2d;

import java.util.Calendar;
import java.util.prefs.*;

public class TimeTrial
{
    private final static Integer TOTAL_TIME = 60;
    private final static String KEY = "TRIAL";
    private final static TimeTrial instance = new TimeTrial();
    private static Integer startSecond;
    private static Integer timePlayed;
    private static Preferences prefs;
    private static boolean wait = true;
    private static boolean started = false;

    private TimeTrial()
    {
        prefs = Preferences.userNodeForPackage(TimeTrial.class);
        //prefs.put(KEY, "0");
        timePlayed = prefs.getInt(KEY, 0);
        System.out.println("time played: " + timePlayed + " minutes.");
    }

    public static void start()
    {
        if(started == false)
        {
            System.out.println("time played: " + timePlayed + " minutes.");
            started = true;
            startSecond = Calendar.getInstance().get(Calendar.SECOND);
        }
    }

    public static boolean isStarted()
    {
        return started;
    }

    public static void updateRegistry()
    {
        int currentSecond = Calendar.getInstance().get(Calendar.SECOND);

        if (wait == true)
           wait = currentSecond == startSecond ? true : false;


        System.out.println(currentSecond + " : " + startSecond);
        if (currentSecond == startSecond && false == wait)
        {
            wait = true;
            timePlayed += 1;
            prefs.put(KEY, timePlayed.toString());
            System.out.println("time played:" + timePlayed);
        }
    }

    public static boolean isTrialExpired()
    {
        return timePlayed >= TOTAL_TIME;
    }
}
