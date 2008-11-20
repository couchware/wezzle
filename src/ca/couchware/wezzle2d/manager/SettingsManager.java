package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.Settings.Value;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
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
    
    /** The singleton instance of the manager. */
    final private static SettingsManager single = new SettingsManager();
    
    /** The high performance enum map of the settings. */
    final private Map<Key, String> map;
    		
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
     * Returns a the settings manager.
     *     
     * @return
     */
    public static SettingsManager get()
    {
        return single;
    }            
                   
	// ---------------------------------------------------------------------------
	// Instance Methods
	// ---------------------------------------------------------------------------	            
    
    private void createFile(File f)
    {               
        try
        {                
            // If the file doesn't exist, create one.
            f.getParentFile().mkdirs();
            f.createNewFile();		
        }
        catch(Exception e)
        {
            LogManager.recordWarning("Could not create file with path: " + f.getAbsolutePath(), 
                    "SettingsManager");
            LogManager.recordException(e);            
        }
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
        URL url = this.getClass().getClassLoader().getResource(Settings.getDefaultSettingsFilePath());

        if (url == null)
        {
            LogManager.recordWarning(
                    "Can't find required resource: " + Settings.getDefaultSettingsFilePath(), 
                    "SettingsManager#loadDefaultSettings");
            System.exit(0);
        }

        // Load the data from XML.
        loadFromXML(url);
    }
    
    public void loadUserSettings()
    {
        // Check if the directory exists.
        File dir = new File(Settings.getUserSettingsPath());

        // If the directory doesn't exist. Create it.
        if (dir.isDirectory() == false)
        {
            dir.mkdir();
        }
       
        File f = new File(Settings.getUserSettingsFilePath());
        
        if (f.exists() == true && f.length() > 0)               
        {            
            try
            {
                // Load from XML.
                loadFromXML(new File(Settings.getUserSettingsFilePath()).toURL());
            }
            catch (MalformedURLException ex)
            {
                LogManager.recordException(ex);
            }           
        } // end if  
        else
        {
            LogManager.recordWarning("Could not load user settings.");
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
                try 
                {
                    map.put(Key.valueOf(name), value);
                    LogManager.recordMessage(name + " = " + value);    
                }
                catch (IllegalArgumentException ex)
                {
                    LogManager.recordMessage("Unknown key: " + name); 
                }
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
        
        // Make sure the file exists.
        File f = new File(Settings.getUserSettingsFilePath());
        if (f.exists() == false)
        {
            createFile(f);
        }
        
        // Write to it.
        Format format = Format.getPrettyFormat();
        format.setExpandEmptyElements(true);            
        XMLOutputter outputter = new XMLOutputter(format);                                                        
        OutputStream out = new FileOutputStream(Settings.getUserSettingsFilePath());
        outputter.output(doc, out);
        out.close();        
    }		    	       
    
    public void setString(Key key, String value)
    {
        map.put(key, value);
    }       
    
    public void setValue(Key key, Value value)
	{
		setString(key, value.toString());
	} 
    
    public void setInt(Key key, int value)
    {
        setString(key, String.valueOf(value));
    }
    
    public void setLong(Key key, long value)
    {
        setString(key, String.valueOf(value));
    }
    
    public void setFloat(Key key, float value)
    {
        setString(key, String.valueOf(value));
    }
    
    public void setDouble(Key key, double value)
    {
        setString(key, String.valueOf(value));
    }
    
    public void setBoolean(Key key, boolean value)
    {
        setString(key, String.valueOf(value));
    }		                   	
    
    /**
	 * Get a string property. 
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public String getString(Key key)
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
	public int getInt(Key key)
	{		
		return getString(key) == null 
            ? null 
            : Integer.parseInt(getString(key));
	}
        
    public long getLong(Key key)
	{		
		return getString(key) == null 
            ? null 
            : Long.parseLong(getString(key));
	}
    
    /**
	 * Get a float property. 
     * 
     * Returns null if the property is not set.
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public float getFloat(Key key)
	{		        
		return getString(key) == null 
            ? null 
            : Float.parseFloat(getString(key));
	}
    
    public double getDouble(Key key)
	{		        
		return getString(key) == null 
            ? null 
            : Double.parseDouble(getString(key));
	}		
	
	/**
	 * Get a boolean property. 
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public boolean getBoolean(Key key)
	{		
		return getString(key) == null 
            ? null 
            : Boolean.valueOf(getString(key));
	}        
    
}
