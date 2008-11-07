/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.properties;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cdmckay
 */
public class UserSettings implements ISettings
{      
    
    /** The name of the user settings file. */
    final public static String FILENAME = "UserSettings.xml";
    
    /** The comment to show in the settings file. */
    final public static String COMMENT = "User Settings File";
    
    public enum Key
    {
        VERSION("VERSION_1"), 
        RENDER_TYPE("RENDER_LWJGL"),        
        MUSIC("true"),
        MUSIC_VOLUME("0.5"),
        SOUND("false"),
        SOUND_VOLUME("0.5"),
        HIGH_SCORE_NAME1("-"),
        HIGH_SCORE_NAME2("-"),
        HIGH_SCORE_NAME3("-"),
        HIGH_SCORE_NAME4("-"),
        HIGH_SCORE_NAME5("-"),
        HIGH_SCORE_SCORE1("-1"),
        HIGH_SCORE_SCORE2("-1"),
        HIGH_SCORE_SCORE3("-1"),
        HIGH_SCORE_SCORE4("-1"),
        HIGH_SCORE_SCORE5("-1"),
        HIGH_SCORE_LEVEL1("-1"),
        HIGH_SCORE_LEVEL2("-1"),
        HIGH_SCORE_LEVEL3("-1"),
        HIGH_SCORE_LEVEL4("-1"),
        HIGH_SCORE_LEVEL5("-1");
                
        private String $default;
        
        Key(String $default)
        {
            this.$default = $default;
        }
        
        public String getDefault()
        {
            return this.$default;
        }
    }
    
    public enum Value
    {
        VERSION_1,
        RENDER_JAVA2D,
        RENDER_LWJGL;
    }
    
    final public static UserSettings single = new UserSettings();
    
    private UserSettings()
    { }
    
    public static UserSettings get()
    {
        return single;
    }
    
    public String getPath()
    {
        return PATH;
    }

    public String getFilename()
    {
        return FILENAME;
    }
    
    public String getComment()
    {
        return COMMENT;
    }
    
    public Map<String, String> getDefaults()
    {
        Map<String, String> map = new HashMap<String, String>();
        
        for (Key k : Key.values())
            map.put(k.toString(),k.getDefault());
        
        return map;
    }
    
}
