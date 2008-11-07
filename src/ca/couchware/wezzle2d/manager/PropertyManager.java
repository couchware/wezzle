package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.properties.ISettings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

/**
 * A wrapper class for writing out and reading in properties from files.
 * 
 * @author Kevin
 */
public class PropertyManager<K, V>
{   	   
    
    /** The settings instance. */
    private ISettings settings;
    
	/** The path to the properties file. */    
	private String filePath;
    
    /** The properties instance. */
	private Properties properties;		
		
	/**
	 * The constructor.
	 */
	private PropertyManager(ISettings settings) 
	{		       
		// Create new properties.
		this.properties = new Properties();
        this.settings = settings;        

        // Load defaults.
        setDefaults();        		
		
        // Figure out the path and filename.
        String path = settings.getPath();
        String filename = settings.getFilename();
        
        // Check if the directory exists.
        File dir = new File(path);

        // If the directory doesn't exist. Create it.
        if (dir.isDirectory() == false)
        {
            dir.mkdir();
        }

        // Store the file name.
        this.filePath = path + "/" + filename;

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
            // Try loading from XML, then from non-XML.
            LogManager.recordMessage("Attempting to load config from XML.");
            if (loadFromXML(filePath) == false)
            {
                LogManager.recordMessage("Attempting to load config from flat file.");
                // Try loading from a flat file format.
                loadFromFlatFile(filePath);
            }   
           
        } // end if
		
	}
        
    // Public API
    public static PropertyManager newInstance(ISettings settings)
    {
        return new PropertyManager(settings);
    }    
    
    private boolean loadFromFlatFile(String filePath)       
    {
         // Get the url.
        FileInputStream in = null;
        try
        {
            in = new FileInputStream(filePath);
            this.properties.load(in);
            in.close();
        }       
        catch (IOException ex)
        {
            LogManager.recordException(ex);
            return false;
        }          
        finally
        {
            try
            {
                in.close();
            }
            catch (IOException ex)
            {
                LogManager.recordException(ex);
                return false;
            }
        } // end try
        
        return true;
    }
    
    private boolean loadFromXML(String filePath)
    {
        // Get the url.
        FileInputStream in = null;
        try
        {
            in = new FileInputStream(filePath);
            this.properties.loadFromXML(in);
            in.close();
        }
        catch (InvalidPropertiesFormatException ex)
        {                        
            return false;
        }            
        catch (FileNotFoundException ex)
        {
            LogManager.recordException(ex);
            return false;
        }
        catch (IOException ex)
        {
            LogManager.recordException(ex);
            return false;
        }          
        finally
        {
            try
            {
                in.close();
            }
            catch (IOException ex)
            {
                LogManager.recordException(ex);
                return false;
            }
        } // end try
        
        return true;
    }
    
    /**
     * Loads default values for all keys that need defaults.
     */
    public void setDefaults()
    {   
        Map<String, String> map = settings.getDefaults();        
        for (String key : map.keySet())
            this.setCustomProperty(key, map.get(key));         
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
		this.properties.storeToXML(out, settings.getComment());
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
    
	public void setProperty(K key, V value)
	{
		setCustomProperty(key.toString(), value.toString());
	}        
    
    public void setStringProperty(K key, String value)
    {
        setCustomProperty(key.toString(), value);
    }                
    
    public void setIntProperty(K key, int value)
    {
        setCustomProperty(key.toString(), String.valueOf(value));
    }
    
    public void setLongProperty(K key, long value)
    {
        setCustomProperty(key.toString(), String.valueOf(value));
    }
    
    public void setFloatProperty(K key, float value)
    {
        setCustomProperty(key.toString(), String.valueOf(value));
    }
    
    public void setDoubleProperty(K key, double value)
    {
        setCustomProperty(key.toString(), String.valueOf(value));
    }
    
    public void setBooleanProperty(K key, boolean value)
    {
        setCustomProperty(key.toString(), String.valueOf(value));
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
	public String getStringProperty(K key)
	{		
		return getCustomProperty(key.toString()) == null 
            ? null 
            : getCustomProperty(key.toString());
	}
	
	/**
	 * Get an integer property. 
     * 
     * Returns null if the property is not set. 
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public int getIntProperty(K key)
	{		
		return getStringProperty(key) == null 
            ? null 
            : Integer.parseInt(getStringProperty(key));
	}
    
    public long getLongProperty(K key)
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
	public float getFloatProperty(K key)
	{		        
		return getStringProperty(key) == null 
            ? null 
            : Float.parseFloat(getStringProperty(key));
	}
    
    public double getDoubleProperty(K key)
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
	public boolean getBooleanProperty(K key)
	{		
		return getStringProperty(key) == null 
            ? null 
            : Boolean.valueOf(getStringProperty(key));
	}
    
}
