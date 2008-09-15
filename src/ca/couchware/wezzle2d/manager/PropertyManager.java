package ca.couchware.wezzle2d.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

//import javax.jnlp.PersistenceService;
//import javax.jnlp.ServiceManager;
//import javax.jnlp.UnavailableServiceException;

/**
 * A wrapper class for writing out and reading in properties from files.
 * 
 * @author Kevin
 */
public class PropertyManager
{
    /**
     * The key for the version of the configuration file.  If the config
     * file versions do not match, then the config file will be overwritten
     * with the defaults for the current version.
     * 
     * Note: This is not yet implemented.
     */
    public enum Key
    {
        VERSION("version"),
        RENDER_TYPE("renderType"),
        DIFFICULTY("difficulty"),
        MUSIC("music"),
        MUSIC_VOLUME("musicVolume"),
        SOUND("sound"),
        SOUND_VOLUME("soundVolume"),
        HIGH_SCORE1("highScore1"),
        HIGH_SCORE2("highScore2"),
        HIGH_SCORE3("highScore3"),
        HIGH_SCORE4("highScore4"),
        HIGH_SCORE5("highScore5");        
        
        /** 
         * The textual key. 
         */
        private String textualKey;
        
        Key(String textualKey)
        { this.textualKey = textualKey; }
        
        /**
         * Get the textual value.
         * 
         * @return The textual value that will be put in the settings file.
         */
        public String getTextualKey()
        { return textualKey; }
    }
    
    public enum Value
    {
        /** The version of the settings file. */
        VERSION("3"),
        RENDER_JAVA2D("java2d"),
        RENDER_LWJGL("lwjgl");
        
        /** 
         * The textual value. 
         */
        private String textualValue;
        
        Value(String textualValue)
        { this.textualValue = textualValue; }
        
        /**
         * Get the textual value.
         * 
         * @return The textual value that will be put in the settings file.
         */
        public String getTextualValue()
        { return textualValue; }        
    }       
    
	/**
     * The path to the settings file.
     */
    public static final String DIR_PATH = 
            System.getProperty("user.home") + "/.Couchware/Wezzle";
    
    /**
     * The default file name for the settings file.
     */
    public static final String DEFAULT_FILENAME = "settings.txt";
	
//	PersistenceService pService;
	
	private String filePath;
	private Properties properties;
	
	private boolean webStart;
		
	/**
	 * The constructor.
	 */
	private PropertyManager(String fileName) 
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
        this.filePath = DIR_PATH + "/" + fileName;

        // Check if the file exists.        
        File f = new File(this.filePath);	

        if (f.exists() == false)
        {
            try
            {                
                // If the file doesn't exist, create one.
                f.getParentFile().mkdirs();
                f.createNewFile();		
            }
            catch(Exception e)
            {
                LogManager.recordWarning("Url is " + f.getAbsolutePath(), 
                        "PropertyManager#this");
                LogManager.recordException(e);
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
                LogManager.recordWarning("Url is " + f.getAbsolutePath(), 
                        "PropertyManager#this");
                LogManager.recordException(e);
            }
        } // end if
		
	}
        
        // Public API
        public static PropertyManager newInstance()
        {
            return new PropertyManager(DEFAULT_FILENAME);
        }
        
        public static PropertyManager newInstance(String fName)
        {
            return new PropertyManager(fName);
        }
		
        
        
        
    /**
     * Loads default values for all keys that need defaults.
     */
    public void setDefaults()
    {   
        setProperty(Key.VERSION, Value.VERSION);
        setProperty(Key.RENDER_TYPE, Value.RENDER_JAVA2D);
        setIntProperty(Key.DIFFICULTY, 0);
        setBooleanProperty(Key.SOUND, true);
        setDoubleProperty(Key.SOUND_VOLUME, 0.5);
        setBooleanProperty(Key.MUSIC, true);        
        setDoubleProperty(Key.MUSIC_VOLUME, 0.5);
    }
    
	/**
	 * Checks if webstart is running.
	 */
//	public void checkWebStart() throws UnavailableServiceException
//	{
//		this.pService = (PersistenceService) ServiceManager
//                .lookup("javax.jnlp.PersistenceService"); 		
//	}
	
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
    public void setCustomProperty(String key, String value)
    {
        this.properties.put(key, value);
    }
    
	public void setProperty(Key key, Value value)
	{
		setCustomProperty(key.getTextualKey(), value.getTextualValue());
	}        
    
    public void setStringProperty(Key key, String value)
    {
        setCustomProperty(key.getTextualKey(), value);
    }                
    
    public void setIntProperty(Key key, int value)
    {
        setCustomProperty(key.getTextualKey(), String.valueOf(value));
    }
    
    public void setLongProperty(Key key, long value)
    {
        setCustomProperty(key.getTextualKey(), String.valueOf(value));
    }
    
    public void setFloatProperty(Key key, float value)
    {
        setCustomProperty(key.getTextualKey(), String.valueOf(value));
    }
    
    public void setDoubleProperty(Key key, double value)
    {
        setCustomProperty(key.getTextualKey(), String.valueOf(value));
    }
    
    public void setBooleanProperty(Key key, boolean value)
    {
        setCustomProperty(key.getTextualKey(), String.valueOf(value));
    }
		
    public String getCustomProperty(String key)
    {
        return this.properties.get(key) == null
                ? null
                : this.properties.get(key).toString();
    }                      	
    
    /**
	 * Get a string property. 
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public String getStringProperty(Key key)
	{		
		return getCustomProperty(key.getTextualKey()) == null 
            ? null 
            : getCustomProperty(key.getTextualKey());
	}
	
	/**
	 * Get an integer property. 
     * 
     * Returns null if the property is not set. 
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public int getIntProperty(Key key)
	{		
		return getStringProperty(key) == null 
            ? null 
            : Integer.parseInt(getStringProperty(key));
	}
    
    public long getLongProperty(Key key)
	{		
		return getStringProperty(key) == null 
            ? null 
            : Long.parseLong(getStringProperty(key));
	}
    
    /**
	 * Get a float property. 
     * 
     * Returns null if the property is not set.
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public float getFloatProperty(Key key)
	{		        
		return getStringProperty(key) == null 
            ? null 
            : Float.parseFloat(getStringProperty(key));
	}
    
    public double getDoubleProperty(Key key)
	{		        
		return getStringProperty(key) == null 
            ? null 
            : Double.parseDouble(getStringProperty(key));
	}		
	
	/**
	 * Get a boolean property. 
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public boolean getBooleanProperty(Key key)
	{		
		return getStringProperty(key) == null 
            ? null 
            : Boolean.valueOf(getStringProperty(key));
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
