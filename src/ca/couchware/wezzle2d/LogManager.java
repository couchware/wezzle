package ca.couchware.wezzle2d;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

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
    public static String newline = System.getProperty("line.separator");
    
	/**
	 * The file path to the log file.
	 */
	private static File logFile;
	
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
		// Check if the directory exists.
		File dir = new File(PropertyManager.DIR_PATH);
		
		// If the directory doesn't exist. Create it.
		if(dir.isDirectory() == false)
		{
			dir.mkdir();
		}
		
		// Create the file.
		logFile = new File(PropertyManager.DIR_PATH + "/log.txt");
		
		// If the file does not exist, create it.
		if (logFile.exists() == false)
		{
			try
			{
				logFile.createNewFile();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		// Create the writer.
		try
		{
			writer = new BufferedWriter(new FileWriter(logFile, true));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
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
	public static void appendToLog( String text )
	{
		try
		{
			writer.write(text + newline);
			writer.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * A method to close the log. This method closes the bufferedWriter
	 * and flushes the buffer. 
	 */
	public static void closeLog()
	{
		try
		{
			writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
