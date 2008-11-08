package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.Settings.Value;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * A class for accessing a plethora of settings.
 * 
 * @author Cameron
 * @author Kevin
 */
public class SettingsManager
{   	               
    
    /** The high performance enum map of the settings. */
    private Map<Key, String> map;
    		
	/**
	 * The constructor.
	 */
	private SettingsManager() 
	{		       
		// Create new enum map.    
        this.map = new EnumMap<Key, String>(Key.class);		

        // Load defaults settings.                
        loadDefaultSettings();        		

        // Load user-made settings on top of that.
        loadUserSettings();
	}
        
    /**
     * Returns a new property manager instance.
     * 
     * @param settings
     * @param keyType
     * @return
     */
    public static SettingsManager newInstance()
    {
        return new SettingsManager();
    }            
                   
	// ---------------------------------------------------------------------------
	// Instance Methods
	// ---------------------------------------------------------------------------	    
    
    /**
     * Checks if a file exists, and if it doesn't, tries to create it.
     * @param filePath
     * @return True if the file existed or was successfully created.  False otherwise.
     */
    private boolean fileExists(String filePath)
    {
        // Check if the file exists.        
        File f = new File(filePath);	
        
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
                LogManager.recordWarning("URL is " + f.getAbsolutePath(), 
                        "PropertyManager#this");
                LogManager.recordException(e);
                return false;
            }
        } // end if
        
        return true;
    }
    
    /**
     * Loads default values for all keys that need defaults.
     */
    public void loadDefaultSettings()
    {   
        // The ClassLoader.getResource() ensures we get the sprite
        // from the appropriate place, this helps with deploying the game
        // with things like webstart. You could equally do a file look
        // up here.
        URL url = this.getClass().getClassLoader().getResource(Settings.DEFAULT_SETTINGS_FILEPATH);

        if (url == null)
        {
            LogManager.recordWarning(
                    "Can't find required resource: " + Settings.DEFAULT_SETTINGS_FILEPATH, 
                    "SettingsManager#loadDefaultSettings");
            System.exit(0);
        }

        // Load the data from XML.
        loadFromXML(url);
    }
    
    private void loadUserSettings()
    {
        // Check if the directory exists.
        File dir = new File(Settings.USER_SETTINGS_PATH);

        // If the directory doesn't exist. Create it.
        if (dir.isDirectory() == false)
        {
            dir.mkdir();
        }
       
        if (fileExists(Settings.USER_SETTINGS_FILEPATH) == true)               
        {
            try
            {
                // Load from XML.
                loadFromXML(new File(Settings.USER_SETTINGS_FILEPATH).toURL());
            }
            catch (MalformedURLException ex)
            {
                LogManager.recordException(ex);
            }
           
        } // end if  
        else
        {
            LogManager.recordWarning("Error loading user settings.");
        }
    }
    
    private void loadFromXML(URL url)
    {
        try
        {
            // Read the XML document into memory.
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(url);

            // Get the children.
            for (Object o : doc.getRootElement().getChildren("entry"))
            {
                Element e = (Element) o;
                String name = e.getAttributeValue("name");
                String value = e.getTextTrim();
                map.put(Key.valueOf(name), value);
                LogManager.recordMessage(name + " = " + value);
            }
        }
        catch (JDOMException ex)
        {
            LogManager.recordException(ex);
        }
        catch (IOException ex)
        {
            LogManager.recordException(ex);
        }
    }
    
	/**
	 * Save the properties to disk.
	 * @throws IOException
	 */
	public void saveSettings() throws IOException
	{
        // Create a new document.
        Document doc = new Document();
        
        // Create the root element and add root.
        Element root = new Element("settings");
        doc.addContent(root);
        
        // Add all the entries.
        for (Key key : Key.values())
        {
            Element entry = new Element("entry");
            entry.setAttribute("name", key.toString());
            if (map.containsKey(key))
            {
                entry.setText(map.get(key));
            }
            root.addContent(entry);
        } // end for
        
        // Write the document to file.
        if (fileExists(Settings.USER_SETTINGS_FILEPATH))
        {
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            OutputStream out = new FileOutputStream(Settings.USER_SETTINGS_FILEPATH);
            outputter.output(doc, out);
            out.close();
        }
    }		    	       
    
    public void setStringProperty(Key key, String value)
    {
        map.put(key, value);
    }       
    
    public void setProperty(Key key, Value value)
	{
		setStringProperty(key, value.toString());
	} 
    
    public void setIntProperty(Key key, int value)
    {
        setStringProperty(key, String.valueOf(value));
    }
    
    public void setLongProperty(Key key, long value)
    {
        setStringProperty(key, String.valueOf(value));
    }
    
    public void setFloatProperty(Key key, float value)
    {
        setStringProperty(key, String.valueOf(value));
    }
    
    public void setDoubleProperty(Key key, double value)
    {
        setStringProperty(key, String.valueOf(value));
    }
    
    public void setBooleanProperty(Key key, boolean value)
    {
        setStringProperty(key, String.valueOf(value));
    }		                   	
    
    /**
	 * Get a string property. 
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public String getStringProperty(Key key)
	{		
		return map.get(key);
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
    
}
