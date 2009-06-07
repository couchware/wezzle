package ca.couchware.wezzle2d.manager;

import ca.couchware.wezzle2d.util.CouchLogger;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.Settings.Value;
import ca.couchware.wezzle2d.util.CouchColor;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
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
    
    /** The enum map of the default settings. */
    final private Map<Key, Object> defaultMap;
    
    /** The high performance enum map of the settings. */
    final private Map<Key, Object> currentMap;
    		
	/**
	 * The constructor.
	 */
	private SettingsManager() 
	{		       
		// Create new enum map. 
        this.defaultMap = new EnumMap<Key, Object>(Key.class);
        this.currentMap = new EnumMap<Key, Object>(Key.class);		

        // Load defaults settings.                
        loadDefaultSettings();
        
        // Copy them into the default map so we can compare them later when 
        // writing the current map to disk.
        this.defaultMap.putAll(currentMap);
        
        // Load user-made settings on top of that.
        loadExternalSettings();
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
                   
	// -------------------------------------------------------------------------
	// Instance Methods
	// -------------------------------------------------------------------------          
    
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
            CouchLogger.get().recordWarning(this.getClass(), "Could not create file with path: " + f.getAbsolutePath());
            CouchLogger.get().recordException(this.getClass(), e);
        }
    }   
    
    /**
     * Loads default values for all keys that need defaults.
     */
    public void loadDefaultSettings(String path)
    {   
        // The ClassLoader.getResource() ensures we get the sprite
        // from the appropriate place, this helps with deploying the game
        // with things like webstart. You could equally do a file look
        // up here.
        URL url = this.getClass().getClassLoader().getResource(path);

        if (url == null)
        {
            CouchLogger.get().recordWarning(this.getClass(),
                    "Can't find required resource: " + path);
            System.exit(0);
        }

        // Load the data from XML.
        loadFromXML(url);        
    }
    
    public void loadDefaultSettings()
    {
        loadDefaultSettings(Settings.getDefaultGameSettingsFilePath());
        loadDefaultSettings(Settings.getDefaultUserSettingsFilePath());
        loadDefaultSettings(Settings.getDefaultAchievementsFilePath());
    }        
    
    public void loadExternalSettings(String path, String file)
    {
        // Check if the directory exists.
        File dir = new File(path);

        // If the directory doesn't exist. Create it.
        if (dir.isDirectory() == false)
        {
            dir.mkdir();
        }
       
        File f = new File(path + "/" + file);
        
        if (f.exists() == true && f.length() > 0)               
        {            
            try
            {
                // Load from XML.
                loadFromXML(f.toURI().toURL());
            }
            catch (MalformedURLException ex)
            {
                CouchLogger.get().recordException(this.getClass(), ex);
            }           
        } // end if  
        else
        {
            CouchLogger.get().recordWarning(this.getClass(), "Could not load external settings.");
        }
    }
    
    public void loadExternalSettings()
    {
        loadExternalSettings(Settings.getExternalSettingsPath(), Settings.getGameSettingsFileName());
        loadExternalSettings(Settings.getExternalSettingsPath(), Settings.getUserSettingsFileName());
        loadExternalSettings(Settings.getExternalSettingsPath(), Settings.getAchievementsFileName());
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
                Element entry = (Element) o;
                                
                String name = toUnderScoreFormat(entry.getAttributeValue("name"));
                
                // Check for children.
                List children = entry.getChildren();
                Object value = null;
                
                if (children.isEmpty() == true)                
                {                                        
                    value = entry.getText();
                }                
                else
                {                       
                    List<Object> list = new ArrayList<Object>();

                    for (Object object : children)
                    {
                        Element element = (Element) object;

                        Object instance = createInstanceFromXml(element);
                        if (instance != null) 
                            list.add(instance);
                        else
                            throw new IllegalArgumentException("Unknown tag type");
                    }      

                    // Return the list as the value.
                    value = list;                                                                                                                                                     
                } // end if                           
                
                try 
                {
                    Key key = Key.valueOf(name);                    

                    if (currentMap.containsKey(key))
                    {
                        CouchLogger.get().recordMessage(
                                this.getClass(), "Value overwritten: " + key);
                    }

                    currentMap.put(key, value);

                    CouchLogger.get().recordMessage(this.getClass(), key + " = " + value);
                }
                catch (IllegalArgumentException e)
                {
                    throw new IllegalArgumentException("Unknown key: " + name);
                }
            }
        }
        catch (JDOMException ex)
        {
            CouchLogger.get().recordException(this.getClass(), ex);
        }
        catch (IOException ex)
        {
            CouchLogger.get().recordException(this.getClass(), ex);
        }               
    }       
    
	/**
	 * Save the properties to disk.
     * 
	 * @throws IOException
	 */
	public void saveSettings(
            Set<Key> keySet,
            String filePath) throws IOException
	{
        // Create a new document.
        Document doc = new Document();
        
        // Create the root element and add root.
        Element root = new Element("settings");
        doc.addContent(root);
        
        // Add all the entries.
        for (Key key : Key.values())
        {
            if (!keySet.contains(key))
                continue;           
                        
            if (currentMap.containsKey(key))
            {                               
                Element entry = new Element("entry");
                entry.setAttribute("name", toDotFormat(key.toString()));                
                Object currentValue = currentMap.get(key);
                
                if (defaultMap.containsKey(key))
                {
                    Object defaultValue = defaultMap.get(key);                    
                    if (defaultValue.equals(currentValue))
                        continue;
                }
                
                if (currentValue instanceof List)
                {
                    List list = (List) currentValue;
                    for (Object object : list)
                    {
                        entry.addContent(createXmlFromInstance(object));
                    }
                }
                else
                {
                    entry.addContent(createXmlFromInstance(currentValue));
                }                         
                
                // Add it to the XML document.
                root.addContent(entry);
            }            
        } // end for
        
        // Make sure the file exists.
        File f = new File(filePath);
        if (f.exists() == false)
        {
            createFile(f);
        }
        
        // Write to it.
        Format format = Format.getPrettyFormat();
        //format.setExpandEmptyElements(true);                
        XMLOutputter outputter = new XMLOutputter(format);                                                        
        OutputStream out = new FileOutputStream(filePath);
        outputter.output(doc, out);
        out.close();        
    }		    	       
    
    public void saveSettings() throws IOException
    {
        EnumSet<Key> specialKeys = EnumSet.noneOf(Key.class);
        specialKeys.addAll(Settings.getUserKeys());
        specialKeys.addAll(Settings.getAchievementKeys());
                
        saveSettings(EnumSet.complementOf(specialKeys), Settings.getGameSettingsFilePath());
        saveSettings(Settings.getUserKeys(), Settings.getUserSettingsFilePath());        
        saveSettings(Settings.getAchievementKeys(), Settings.getAchievementsFilePath());        
    }
    
    private Object createInstanceFromXml(Element element)
    {
        Object instance;
        
        if (element.getName().equals("color"))
        {                        
            instance = CouchColor.newInstanceFromXml(element);
        }
        else if (element.getName().equals("high-score"))
        {                            
            instance = HighScore.newInstanceFromXml(element);
        }
        else if (element.getName().equals("achievement"))
        {                            
            instance = Achievement.newInstanceFromXml(element);
        }
        else
        {
            instance = null;
        }
       
        return instance;        
    }
    
    private Content createXmlFromInstance(Object object)
    {
         if (object instanceof CouchColor)
        {
            CouchColor color = (CouchColor) object;
            return color.toXmlElement();
        }
        else if (object instanceof HighScore)
        {
            HighScore score = (HighScore) object;
            return score.toXmlElement();
        }
        else if (object instanceof Achievement)
        {
            Achievement achievement = (Achievement) object;
            return achievement.toXmlElement();
        }
        else
        {
            return new Text((String) object);
        }  
    }
    
    public boolean containsKey(Key key)
    {
        return currentMap.containsKey(key);
    }
    
    public void setObject(Key key, Object value)
    {
        currentMap.put(key, value);
    }
    
    public void setString(Key key, String value)
    {
        currentMap.put(key, value);
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
     * Get the raw object.
     * 
     * @param key
     * @return
     */
    public Object getObject(Key key)
    {
        return currentMap.get(key);
    }
    
    /**
	 * Get a string property. 
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public String getString(Key key)
	{		
		return (String) currentMap.get(key);
	}
	
	/**
	 * Get an integer property. 
     * 
     * Returns null if the property is not set. 
	 * 
	 * @param key The properties key.
	 * @return The property
	 */
	public final int getInt(Key key)
	{	
        int val = 0;
        
        try 
        {
            if (getString(key) == null)
            {
                throw new NullPointerException("Key did not exist.");
            }
            
            val = Integer.parseInt(getString(key));               
        }
        catch (NumberFormatException e)
        {
            CouchLogger.get().recordWarning(this.getClass(),
                    "Could not convert " + key + ": " + getString(key));
            throw e;
        }
        
        return val;
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
            ? false
            : Boolean.valueOf(getString(key));
	}      
    
    /**
     * Get a color property.
     * 
     * @param key
     * @return
     */
    public Color getColor(Key key)
    {
        return ((CouchColor) getList(key).get(0)).toColor();
    }
    
    /**
     * Get a list property.
     * 
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public List getList(Key key)
    {
        // Return an unmodifiable list.
        Object object = getObject(key);
        
        List list = null;
        
        if (object instanceof List)
        {
            list = (List) object;
        }
        else
        {
            throw new UnsupportedOperationException("Cannot cast non-list to list.");
        }
        
        return Collections.unmodifiableList(list);
    }
    
    /**
     * Converts a string passed in with the format "THE_SWIFT_RED_FOX" to
     * the format "The.Swift.Red.Fox".  Used mainly for the settings file.
     * 
     * @param str
     * @return
     */
    public static String toDotFormat(String str)
    {
        StringBuffer buffer = new StringBuffer();
        
        for (int i = 0; i < str.length(); i++)
        {
            char ch = str.charAt(i);
            
            if (i == 0) 
                buffer.append(Character.toUpperCase(ch));
            else if (ch == '_') 
            {                               
                buffer.append('.');
            }  
            else
            {
                if (buffer.charAt(i - 1) == '.')
                    buffer.append(Character.toUpperCase(ch));                          
                else
                    buffer.append(Character.toLowerCase(ch));                          
            }
        }
        
        return buffer.toString();
    }
    
     /**
     * Converts a string passed in with the format "The.Swift.Red.Fox" to
     * the format "THE_SWIFT_RED_FOX".  Used mainly for the settings file.
     * 
     * @param str
     * @return
     */
    public static String toUnderScoreFormat(String str)
    {
        return str.replace('.', '_').toUpperCase();
    }
    
}
