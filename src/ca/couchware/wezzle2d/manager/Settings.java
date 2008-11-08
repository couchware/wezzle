/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

/**
 * Contains all the settings keys and values.
 * 
 * @author cdmckay
 */
public class Settings 
{   
    
    /** All the keys that may be used. */
    public enum Key
    {
        // Meta values.
        CONF_VERSION, 
        
        // Game values.
        GAME_RENDER_TYPE,        
        GAME_MUSIC,
        GAME_MUSIC_VOLUME,
        GAME_SOUND,
        GAME_SOUND_VOLUME,
        
        // User values.
        USER_HIGHSCORE_NAME_1,
        USER_HIGHSCORE_NAME_2,
        USER_HIGHSCORE_NAME_3,
        USER_HIGHSCORE_NAME_4,
        USER_HIGHSCORE_NAME_5,
        USER_HIGHSCORE_SCORE_1,
        USER_HIGHSCORE_SCORE_2,
        USER_HIGHSCORE_SCORE_3,
        USER_HIGHSCORE_SCORE_4,
        USER_HIGHSCORE_SCORE_5,
        USER_HIGHSCORE_LEVEL_1,
        USER_HIGHSCORE_LEVEL_2,
        USER_HIGHSCORE_LEVEL_3,
        USER_HIGHSCORE_LEVEL_4,
        USER_HIGHSCORE_LEVEL_5,
        
        // Game mechanic values.
        
        /** The number of ticks (frames) per second. */
        TICKS_PER_SECOND,
        
        // Debug values.
        
        /** Whether or not to show the clip rectangle.  Only works in JAVA2D mode. */
        DEBUG_SHOW_CLIP_RECT,
        
        // Animation values.
                
        ANIMATION_JUMP_MOVE_SPEED,
        ANIMATION_JUMP_MOVE_DURATION,
        ANIMATION_JUMP_MOVE_GRAVITY,
        ANIMATION_JUMP_FADE_WAIT,
        ANIMATION_JUMP_FADE_DURATION,
        
        ANIMATION_ROCKET_MOVE_SPEED,
        ANIMATION_ROCKET_MOVE_GRAVITY,
        ANIMATION_ROCKET_FADE_WAIT,
        ANIMATION_ROCKET_FADE_DURATION,
        
        ANIMATION_BOMB_FADE_WAIT,
        ANIMATION_BOMB_FADE_DURATION                
    }
    
    public enum Value
    {
        CONF_VERSION_1,
        GAME_RENDER_JAVA2D,
        GAME_RENDER_LWJGL;
    }
    
    // Static settings.
    
    /** The cross platform line separator. */
    final private static String lineSeparator = System.getProperty("line.separator");
    
    /** The path to the resources. */
    final private static String resourcesPath = "resources";        

    /** The path to the fonts. */
    final private static String fontResourcesPath = resourcesPath + "/fonts";
    
    /** The path to the sprites. */
    final private static String spirteResourcesPath = resourcesPath + "/sprites";
    
    /** The path to the sounds. */
    final private static String soundResourcesPath = resourcesPath + "/sounds";
    
    /** The path to the music. */
    final private static String musicResourcesPath = resourcesPath + "/music";
        
    /** The name of the settings file. */
    final private static String settingsFilename = "settings.xml";
    
    /** The path to the default settings file. */
    final private static String settingsPath = resourcesPath + "/settings";
    
     /** The file path of default settings file. */
    final private static String defaultSettingsFilePath = settingsPath 
            + "/" + settingsFilename;
    
    /** The path to the user settings file. */
    final private static String userSettingsPath = 
            System.getProperty("user.home") + "/.Couchware/Wezzle";
                    
    /** The file path of default settings file. */
    final private static String userSettingsFilePath = userSettingsPath 
            + "/" + settingsFilename;
    
    /** The path to the log file. */
    final private static String logPath = userSettingsPath;
    
    /** The file path to the log file. */    
    final private static String logFilePath = logPath + "/log.txt";

    public static String getDefaultSettingsFilePath()
    {
        return defaultSettingsFilePath;
    }

    public static String getFontResourcesPath()
    {
        return fontResourcesPath;
    }

    public static String getLineSeparator()
    {
        return lineSeparator;
    }

    public static String getLogFilePath()
    {
        return logFilePath;
    }

    public static String getLogPath()
    {
        return logPath;
    }

    public static String getMusicResourcesPath()
    {
        return musicResourcesPath;
    }

    public static String getResourcesPath()
    {
        return resourcesPath;
    }

    public static String getSettingsFilename()
    {
        return settingsFilename;
    }

    public static String getSettingsPath()
    {
        return settingsPath;
    }

    public static String getSoundResourcesPath()
    {
        return soundResourcesPath;
    }

    public static String getSpriteResourcesPath()
    {
        return spirteResourcesPath;
    }

    public static String getUserSettingsFilePath()
    {
        return userSettingsFilePath;
    }

    public static String getUserSettingsPath()
    {
        return userSettingsPath;
    }        
    
    // Dynamic non-text file settings.
    
    /** Cached milliseconds per tick. */
    private static int millisecondsPerTick;        
    
    /**
     * Calculates all the special settings.
     * @param settingsMan
     */
    public static void calculate(SettingsManager settingsMan)
    {
        calculateMillisecondsPerTick(settingsMan);
    }       
    
    /** Recalculates milliseconds per tick. */
    private static void calculateMillisecondsPerTick(SettingsManager settingsMan)
    {
         millisecondsPerTick = settingsMan.getInt(Key.TICKS_PER_SECOND);
    }
    
    /** Returns the number of milliseconds per tick. */
    public static int getMillisecondsPerTick()
    {
        return millisecondsPerTick;
    }
}
