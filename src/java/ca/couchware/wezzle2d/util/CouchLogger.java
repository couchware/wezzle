package ca.couchware.wezzle2d.util;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.manager.Settings;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.layout.EchoLayout;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import org.slf4j.LoggerFactory;

/**
 * A class to manage writing out to a log file.
 * @author Kevin
 *
 */
public class CouchLogger
{
    // ---------------------------------------------------------------------------
    // Static Attributes
    // ---------------------------------------------------------------------------

    /** The only instance of the log manager. */
    final private static CouchLogger SINGLE = new CouchLogger();
    final private Logger logger;
    final private int MONTHS = 1;
    
    // ---------------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------------

    /**
     * The constructor configures a static logger. The logger contains 2
     * appenders, one to the console and one to files. The file appender
     * is a rolling one.
     */
    private CouchLogger()
    {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset(); // We want to override the default config.

        try
        {
            Logger root = lc.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

            // Console Appender
            ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<ILoggingEvent>();
            consoleAppender.setContext(lc);
            consoleAppender.setLayout(new EchoLayout<ILoggingEvent>());
            consoleAppender.start();
            root.addAppender(consoleAppender);

            // Rolling appender
            RollingFileAppender<ILoggingEvent> rollingAppender = new RollingFileAppender<ILoggingEvent>();

            if (!Game.isApplet())
            {
                rollingAppender.setContext(lc);
                rollingAppender.setLayout(new EchoLayout<ILoggingEvent>());

                // Rolling Policy, set to roll every month and keep 6 month history.
                Context context = new ContextBase();
                TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy();
                rollingPolicy.setFileNamePattern(Settings.getLogPath() + "/log-%d{MM-yyyy}.txt");
                rollingPolicy.setContext(context);
                rollingPolicy.setMaxHistory(MONTHS);
                rollingPolicy.setParent(rollingAppender);



                rollingPolicy.start();
                rollingAppender.setImmediateFlush(true);
                rollingAppender.setRollingPolicy(rollingPolicy);

                rollingAppender.start();
                root.addAppender(rollingAppender);
            }           
        }
        catch(Exception e)
        {
            this.recordException(this.getClass(), e);
        }

        logger = lc.getLogger("CouchLogger");
        logger.setLevel(Level.WARN);
    }

    /**
     * Get the single instance of log manager.
     * @return
     */
    public static CouchLogger get()
    {
        return SINGLE;
    }

    // ---------------------------------------------------------------------------
    // Static Methods
    // ---------------------------------------------------------------------------

    /**
     * Prints an error to standard error, dumps the stack, and then terminates
     * the program.
     * 
     * @param message The error message.
     * @param t The current thread, usually Thread.currentThread().
     */
    public void recordException(Class cls, Throwable t, boolean fatal)
    {
        if ( cls == null )
        {
            throw new IllegalArgumentException( "Class must not be null" );
        }

        String method = extractClassName( cls );
        StringWriter out = new StringWriter();
        String exceptionString = "(" + getTimeStamp() + ") " + method + " - \"" + t.
                getMessage() + "\"" + Settings.getLineSeparator();

        out.write( exceptionString );

        t.printStackTrace( new PrintWriter( out, true ) );
        logger.error( out.toString() );

        if (fatal)
        {
            IWindow win = ResourceFactory.get().getWindow();
            win.setFullscreen(false);
            win.alert("Wezzle", "Error!" + Settings.getLineSeparator()
                    + t.getMessage() + "." + Settings.getLineSeparator()
                    + Settings.getLineSeparator()
                    + "Please visit http://couchware.ca/www/support for help.");
            win.stop();
        }
    }
    

    public void recordException(Class cls, Throwable t)
    {
        recordException(cls, t, false);
    }

    /**
     * Prints a warning to standard error.  Does not dump the stack.
     * @param message The error message.
     * @param t The current thread, usually Thread.currentThread().
     */
    public void recordWarning(Class cls, String message)
    {
        if ( cls == null )
        {
            throw new IllegalArgumentException( "Class must not be null" );
        }

        String method = extractClassName( cls );
        String output = "(" + getTimeStamp() + ") " + method + " - \"" + message + "\"";

        logger.warn( output );
        
    }

    /**
     * Prints a message to standard out.  Does not dump the stack.
     * @param message The error message.
     * @param t The current thread, usually Thread.currentThread().
     */
    public void recordMessage(Class cls, String message)
    {
        if ( cls == null )
        {
            throw new IllegalArgumentException( "Class must not be null" );
        }

        String method = extractClassName( cls );
        String output = "(" + getTimeStamp() + ") " + method + " - \"" + message + "\"";

        logger.info( output );
        
    }

    private static String extractClassName(Class cls)
    {
        if (cls.isAnonymousClass())
        {
            return cls.getEnclosingClass().getSimpleName();
        }
        else
        {
            return cls.getSimpleName();
        }
    }

    /**
     * Attempt to set the log level to the level specified.  If the log level
     * passed is unrecognized, it will default to WARN.
     * 
     * @param logLevel
     */
    public void setLogLevel(String logLevel)
    {
        if (null == logLevel)
            throw new IllegalArgumentException("Log level must not be null");

        try
        {
            Level level = Level.valueOf(logLevel);
            logger.setLevel(level);
        }
        catch(IllegalArgumentException e)
        {
            logger.setLevel(Level.WARN);
        }       
    }

    private static String getTimeStamp()
    {
        Calendar now = Calendar.getInstance();
        return String.format( "%02d:%02d:%02d.%03d", now.get( Calendar.HOUR_OF_DAY ),
                now.get( Calendar.MINUTE ),
                now.get( Calendar.SECOND ),
                now.get( Calendar.MILLISECOND ) );
    }

}
