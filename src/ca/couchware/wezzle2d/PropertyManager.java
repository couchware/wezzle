package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.util.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

/**
 * A wrapper class for writing out and reading in properties from files.
 * 
 * @author Kevin
 */
public class PropertyManager
{
	public static final String KEY_DIFFICULTY = "wezzle.difficulty";
    public static final String KEY_MUSIC = "wezzle.music";
    public static final String KEY_MUSIC_MIN = "wezzle.musicMinimum";
    public static final String KEY_MUSIC_MAX = "wezzle.musicMaximum";    
    public static final String KEY_MUSIC_VOLUME = "wezzle.musicVolume";
    public static final String KEY_SOUND = "wezzle.sound";
    public static final String KEY_SOUND_MIN = "wezzle.soundMinimum";
    public static final String KEY_SOUND_MAX = "wezzle.soundMaximum";    
    public static final String KEY_SOUND_VOLUME = "wezzle.soundVolume";
    
    public static final String VALUE_ON = "on";
    public static final String VALUE_OFF = "off";
	
    public static final String DIR_PATH = 
            System.getProperty("user.home") + "/Wezzle";
	
	PersistenceService pService;
	
	private String filePath;
	private Properties properties;
	
	private boolean webStart;
		
	/**
	 * The default constructor.
	 */
	public PropertyManager()
	{        
		this("/settings.txt");		
	}
	
	/**
	 * The constructor.
	 */
	public PropertyManager(String fileName) 
	{		
		// Create new properties.
		this.properties = new Properties();

        // Load defaults.
        setDefaults();
        
		// WebStart.
		this.webStart = true;
		
		//Check WebStart.
//		try
//		{
//			// Only create a property manager if this isn't the WebStart version.
//			this.checkWebStart();
//		}
//		catch(UnavailableServiceException e)
//		{
//			// Web start is not running.
//			this.setWebStart();
//            //Util.handleException(e);
//		}		        
		
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
            }
            catch(Exception e)
            {
                Util.handleWarning("Url is " + f.getAbsolutePath(), 
                        Thread.currentThread());
                Util.handleException(e);
            }
        }
        else
        {
            // Get the url.
            try
            {
                FileInputStream in = new FileInputStream(filePath);
                this.properties.load(in);
                in.close();
            }
            catch(Exception e)
            {
                Util.handleWarning("Url is " + f.getAbsolutePath(), Thread.currentThread());
                Util.handleException(e);
            }
        } // end if
		
	}	
		
    /**
     * Loads default values for all keys that need defaults.
     */
    public void setDefaults()
    {   
        properties.put(KEY_SOUND, VALUE_ON);
        properties.put(KEY_SOUND_MIN, "-80.0f");
        properties.put(KEY_SOUND_MAX, "6.0206f");
        properties.put(KEY_SOUND_VOLUME, "-1.5f");
        properties.put(KEY_MUSIC, VALUE_ON);
        properties.put(KEY_MUSIC_MIN, "-80.0f");
        properties.put(KEY_MUSIC_MAX, "6.0206f");
        properties.put(KEY_MUSIC_VOLUME, "-1.5f");
    }
    
	/**
	 * Checks if webstart is running.
	 */
	public void checkWebStart() throws UnavailableServiceException
	{
		this.pService = (PersistenceService) ServiceManager
                .lookup("javax.jnlp.PersistenceService"); 		
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
        FileOutputStream out = new FileOutputStream(this.filePath);
		this.properties.store(out, "Wezzle Settings File");
        out.close();		
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
	 * returned property to whatever it should be. 
     * This function returns an object.
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
	 * Get a float property. 
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public float getFloatProperty(String key)
	{
		// If the property doesnt exist, set it to 0.
      
        if (this.properties.get(key) == null)
		{           
            this.setProperty(key, "0.0f");
		}
		
		return Float.parseFloat(this.properties.get(key).toString());
	}
	
	/**
	 * Get a String property. 
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public String getStringProperty(String key)
	{
		// If the property doesn't exist, set it to on.
		if (this.properties.get(key) == null)
		{
			this.setProperty(key, "on");
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
		this.webStart = false;
	}
	
	public boolean isWebStart()
	{
		return this.webStart;
	}
}
