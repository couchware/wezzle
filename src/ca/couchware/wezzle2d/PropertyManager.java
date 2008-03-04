package ca.couchware.wezzle2d;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Properties;

import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

/**
 * A wrapper class for writing out and reading in properties from files.
 * Will be using XML files.
 * 
 * @author Kevin
 */
public class PropertyManager
{
	/**
	 * The high score key.
	 */
	public static final String HIGH_SCORE = "High Score";
	public static final String DIFFICULTY = "Difficulty";
	public static final String DISPLAY_NAME = "Display Name";
	public static final String NUM_LEVELS = "Number of Levels";
	public static final String DIR_PATH = System.getProperty("user.home") + "/Wezzle";
	
	PersistenceService pService;
	
	private String filePath;
	private Properties properties;
	
	private boolean webstart;
		
	/**
	 * The default Constructor.
	 */
	public PropertyManager()
	{
		this("/settings.xml");		
	}
	
	/**
	 * The constructor.
	 */
	public PropertyManager(String fileName) 
	{		
		// Create new properties.
		this.properties = new Properties();

		// Webstart.
		this.webstart = true;
		
		//Check webstart
		try
		{
			// Only create a property manager if this isn't the WebStart version.
			this.checkWebStart();
		}
		catch(UnavailableServiceException e)
		{
			// Do nothing.
			this.setWebStart();
		}
		
		if (this.webstart == false)
		{
			// Check if the directory exists.
			File dir = new File(DIR_PATH);
			
			// If the directory doesn't exist. Create it.
			if(dir.isDirectory() == false)
			{
				dir.mkdir();
			}
			
			// Store the file name.
			this.filePath = DIR_PATH + fileName;
			
			// Check if the file exists.
			File f = new File(this.filePath);	
			
			if (f.exists() == false)
			{
				try
				{
					// If the file doesn't exist, create one with highscore 0.
					f.createNewFile();
					this.setProperty(HIGH_SCORE, "0");
					this.saveProperties();				
				}
				catch(Exception e)
				{
					Util.handleWarning("Url is " + f.getAbsolutePath(), Thread.currentThread());
					Util.handleException(e);
				}
			}
			else
			{
				// Get the url.
				try
				{
					this.properties.loadFromXML(new FileInputStream(this.filePath));
				}
				catch(Exception e)
				{
					Util.handleWarning("Url is " + f.getAbsolutePath(), Thread.currentThread());
					Util.handleException(e);
				}
			} // end if
		} // end if
	}	
		
	/**
	 * Checks if webstart is running.
	 */
	public void checkWebStart() throws UnavailableServiceException
	{
		this.pService = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService"); 		
	}
	
	// ---------------------------------------------------------------------------
	// Instance Methods
	// ---------------------------------------------------------------------------
	
	/**
	 * Save the properties to disc.
	 * @throws IOException
	 */
	public void saveProperties() throws IOException
	{
		this.properties.storeToXML(new FileOutputStream(this.filePath), "Save");
		
	}
	
	/**
	 * A method to write an integer value to a property file.
	 * @param key The key to set.
	 * @param value The value.
	 */
	public void setProperty(String key, String value )
	{
		this.properties.put(key, value);
	}
	
	/**
	 * Get a property. It is the programmers responsibility to cast the
	 * returned property to whatever it should be. This function returns an  object.
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public Object getProperty(String key)
	{
		// If the property doesn't exist, set it to 0.
		if (this.properties.get(key) == null)
		{
			this.setProperty(key, "0");
		}
		
		return this.properties.get(key);
	}
	
	/**
	 * Get an integer property. 
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public int getIntegerProperty(String key)
	{
		// If the property doesnt exist, set it to 0.
		if (this.properties.get(key) == null)
		{
			this.setProperty(key, "0");
		}
		
		return Integer.parseInt(this.properties.get(key).toString());
	}
	
	/**
	 * Get a String property. 
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public String getStringProperty(String key)
	{
		// If the property doesn't exist, set it to 0.
		if (this.properties.get(key) == null)
		{
			this.setProperty(key, "0");
		}
		
		return this.properties.get(key).toString();
	}
	
	/**
	 * Get a boolean property. 
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public boolean getBooleanProperty(String key)
	{
		// If the property doesn't exist, set it to 0.
		if (this.properties.get(key) == null)
		{
			this.setProperty(key, "0");
		}
		
		return Boolean.getBoolean(this.properties.get(key).toString());
	}
	
	public void setWebStart()
	{
		this.webstart = false;
	}
	
	public boolean isWebstart()
	{
		return this.webstart;
	}
}
