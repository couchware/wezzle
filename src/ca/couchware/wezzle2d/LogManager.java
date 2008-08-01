package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.Util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A class to manage writing out to a log file.
 * @author Kevin
 *
 */
public class LogManager 
{
	// ---------------------------------------------------------------------------
	// Static Attributes
	// ---------------------------------------------------------------------------
	      
    /**
     * The platform specific newline character.
     */
    private static String NL = System.getProperty("line.separator");
    
    /**
	 * Write out to the log. If this is set to true, all messages and
	 * errors will be written out to the log file.
	 */
	private static final boolean USE_LOG = true;
    
    /**
     * Is the writer opened?
     */
    private static boolean opened = false;;
    
	/**
	 * The file path to the log file.
	 */
	private static File logFile;
	
    /**
     * A string buffer to store the log information in before writing it.
     */
    private static StringBuilder buffer = new StringBuilder();
    
	/**
	 * The file writer to write to the log.
	 */
	private static BufferedWriter writer;
	
	// ---------------------------------------------------------------------------
	// Static Constructor
	// ---------------------------------------------------------------------------
	
	/**
	 * The static constructor will create or write to a log file
	 * named "log.txt".
	 * 
	 * The method first checks if the Wezzle directory exists. Then checks if
	 * the log file already exists. If it does not exist, it will create the director
	 * and or the file.
	 * 
	 * @param file_name The name of the log file.
	 */
	static
	{
		
	}
	
	// ---------------------------------------------------------------------------
	// Static Methods
	// ---------------------------------------------------------------------------
	
	/**
	 * A method to append a string of text to the log file.
	 * The text is written and the buffer is flushes so that
	 * errors can be logged and read in real time while the 
	 * program is open.
	 * 
	 * @param text The text to append.
	 */
	private static void append(String text)
	{	
        buffer.append(text + NL);		
	}
    
    /**
     * A method to write the contexts of the buffer to the log file.
     */
    public static void write()
    {
        try
        {
            // Make sure file is opened.
            if (!opened) open();
            
            // Write the buffer.
            writer.write(buffer.toString());            
            writer.flush();
            
            // Clear the buffer.
            buffer = new StringBuilder();
            
            // Close the file.
            close();
        }
        catch (Exception e)
        {
            handleException(e);
        }
    }
	
    private static void open()
    {
        // See if we already opened it.
        if (opened == true)
            return;
        
        // Set the opened variable.
        opened = true;
        
        // Check if the directory exists.
		File dir = new File(PropertyManager.DIR_PATH);
		
		// If the directory doesn't exist. Create it.
		if (dir.isDirectory() == false)		
			dir.mkdir();		
		
		// Create the file.
		logFile = new File(PropertyManager.DIR_PATH + "/log.txt");								
		
		try
		{
            // If the file does not exist, create it.
            if (logFile.exists() == false)
                logFile.createNewFile();
            
            // Create the writer.
			writer = new BufferedWriter(new FileWriter(logFile, true));
		}
		catch (Exception e)
		{
			handleException(e);
		}
    }
    
	/**
	 * A method to close the log. This method closes the bufferedWriter
	 * and flushes the buffer. 
	 */
	private static void close()
	{
		try
		{
			writer.close();
            opened = false;
		}
		catch(Exception e)
		{
			handleException(e);
		}
	}
    
    /**
	 * Prints an error to standard error and dumps the stack.
	 * @param message The error message.
	 * @param t The current thread, usually Thread.currentThread().
	 */
	public static void recordException(Exception e, String method)
	{
		if (method == null)
			method = "...";
		
        StringWriter out = new StringWriter();
        
		out.write("[Exception] (" + System.currentTimeMillis() + ") " 
                + method + " - \"" + e.getMessage() + "\"" + NL);                
		e.printStackTrace(new PrintWriter(out, true));
        
		System.err.println(out.toString());
		
		if (USE_LOG == true)
			LogManager.append(out.toString());				
	}
    
    public static void handleException(Exception e)
    {
        recordException(e, null);
    }
	
	/**
	 * Prints a warning to standard error.  Does not dump the stack.
	 * @param message The error message.
	 * @param t The current thread, usually Thread.currentThread().
	 */
	public static void recordWarning(String message, String method)
	{
		if (method == null)
			method = "...";
		
		String output = "[Warning] (" + System.currentTimeMillis() + ") " 
                + method + " - \"" + message + "\"";        
		
		System.err.println(output);
		
		if (USE_LOG == true)
			LogManager.append(output);
	}
    
    public static void handleWarning(String message)
    {
        recordWarning(message, null);
    }
	
	/**
	 * Prints a message to standard out.  Does not dump the stack.
	 * @param message The error message.
	 * @param t The current thread, usually Thread.currentThread().
	 */
	public static void recordMessage(String message, String method)
	{				
		if (method == null)
			method = "...";
		
		String output = "[Message] (" + System.currentTimeMillis() + ") " 
                + method + " - \"" + message + "\"";
		
		System.out.println(output);	
		
		if (USE_LOG == true)
			LogManager.append(output);
	}
    
    public static void handleMessage(String message)
    {
        recordMessage(message, null);
    }
    
}
