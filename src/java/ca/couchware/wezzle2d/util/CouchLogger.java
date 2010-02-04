package ca.couchware.wezzle2d.util;

import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.manager.Settings;
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
        lc.reset(); // we want to override the default-config.

        // Console Appender
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<ILoggingEvent>();
        consoleAppender.setContext(lc);
        consoleAppender.setLayout(new EchoLayout<ILoggingEvent>());

        // Rolling appender
        RollingFileAppender<ILoggingEvent> rollingAppender = new RollingFileAppender<ILoggingEvent>();
        rollingAppender.setContext(lc);
        rollingAppender.setLayout(new EchoLayout<ILoggingEvent>());

        // Rolling Policy, set to roll every month and keep 6 month history.
        Context context = new ContextBase();
        TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy();
        rollingPolicy.setFileNamePattern(Settings.getLogPath() + "/log-%d{MM-yyyy}.txt");
        rollingPolicy.setContext(context);
        rollingPolicy.setMaxHistory(6);
        rollingPolicy.setParent(rollingAppender);      

        try
        {
            rollingPolicy.start();

            //OutputStream os = new FileOutputStream(Settings.getLogFilePath());
            //rollingAppender.setWriter(new OutputStreamWriter(os));
            rollingAppender.setImmediateFlush(true);
            rollingAppender.setRollingPolicy(rollingPolicy);
            
            rollingAppender.start();
            consoleAppender.start();

            Logger root = lc.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
            root.addAppender(rollingAppender);
            root.addAppender(consoleAppender);

        }
        catch(Exception e)
        {
            this.recordException(this.getClass(), e);
        }

        logger = lc.getLogger("CouchLogger");
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
    public void recordException(Class cls, Exception e, boolean fatal)
    {
        if ( cls == null )
        {
            throw new IllegalArgumentException( "Class must not be null" );
        }

        String method = extractClassName( cls );
        StringWriter out = new StringWriter();
        String exceptionString = "(" + getTimeStamp() + ") " + method + " - \"" + e.
                getMessage() + "\"" + Settings.getLineSeparator();

        out.write( exceptionString );

        e.printStackTrace( new PrintWriter( out, true ) );
        logger.error( out.toString() );

        if (fatal)
        {
            IWindow win = ResourceFactory.get().getWindow();
            win.alert("Wezzle", "Error!" + Settings.getLineSeparator()
                    + e.getMessage() + "." + Settings.getLineSeparator()
                    + Settings.getLineSeparator()
                    + "Please visit http://couchware.ca/www/support for help.");

            System.exit(0);
        }
    }
    

    public void recordException(Class cls, Exception e)
    {
        recordException(cls, e, false);
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

    private static String getTimeStamp()
    {
        Calendar now = Calendar.getInstance();
        return String.format( "%02d:%02d:%02d.%03d", now.get( Calendar.HOUR_OF_DAY ),
                now.get( Calendar.MINUTE ),
                now.get( Calendar.SECOND ),
                now.get( Calendar.MILLISECOND ) );
    }

}
