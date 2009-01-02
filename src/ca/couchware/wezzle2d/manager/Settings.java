/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.manager;

import java.util.EnumSet;
import java.util.Set;

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
        
        // User values.
                
        USER_MUSIC,
        USER_MUSIC_VOLUME,
        USER_SOUND,
        USER_SOUND_VOLUME,
        USER_HIGHSCORE,   
        USER_ACHIEVEMENT,
        
        // Game mechanic values.
        
        /** The number of ticks (frames) per second. */
        GAME_TICKS_PER_SECOND,
        GAME_COLOR_PRIMARY,
        GAME_COLOR_SECONDARY,
        GAME_COLOR_DISABLED,  
        GAME_COLOR_WEZZLE_TIMER,
        
        // Debug values.
        
        /** Whether or not to show the clip rectangle.  Only works in JAVA2D mode. */
        DEBUG_SHOW_CLIP_RECT,
        
        // Refactor values.
        
        REFACTOR_SPEED_X_SLOWER,
        REFACTOR_SPEED_Y_SLOWER,
        REFACTOR_SPEED_X_SLOW,
        REFACTOR_SPEED_Y_SLOW,
        REFACTOR_SPEED_X_NORMAL,
        REFACTOR_SPEED_Y_NORMAL,
        REFACTOR_SPEED_X_SHIFT,
        REFACTOR_SPEED_Y_SHIFT,
        
        // Animation values.
            
        ANIMATION_DROP_ZOOM_OUT_SPEED,
        
        ANIMATION_WEZZLE_ZOOM_IN_SPEED,
        ANIMATION_WEZZLE_ZOOM_OUT_SPEED,
        ANIMATION_WEZZLE_FADE_MIN_OPACITY,
        ANIMATION_WEZZLE_FADE_WAIT,
        ANIMATION_WEZZLE_FADE_DURATION,
        
        ANIMATION_JUMP_MOVE_SPEED,
        ANIMATION_JUMP_MOVE_DURATION,
        ANIMATION_JUMP_MOVE_GRAVITY,
        ANIMATION_JUMP_FADE_WAIT,
        ANIMATION_JUMP_FADE_DURATION,
        
        ANIMATION_LEVEL_MOVE_SPEED,
        ANIMATION_LEVEL_MOVE_DURATION,
        ANIMATION_LEVEL_MOVE_GRAVITY,
        ANIMATION_LEVEL_FADE_DURATION,
        
        ANIMATION_LINE_REMOVE_ZOOM_SPEED,
        ANIMATION_LINE_REMOVE_FADE_WAIT,
        ANIMATION_LINE_REMOVE_FADE_DURATION,
        
        ANIMATION_ITEM_ACTIVATE_ZOOM_SPEED,
        ANIMATION_ITEM_ACTIVATE_ZOOM_DURATION,
        ANIMATION_ITEM_ACTIVATE_FADE_WAIT,
        ANIMATION_ITEM_ACTIVATE_FADE_DURATION,
        
        ANIMATION_ROCKET_MOVE_DURATION,
        ANIMATION_ROCKET_MOVE_SPEED,
        ANIMATION_ROCKET_MOVE_GRAVITY,
        ANIMATION_ROCKET_FADE_WAIT,
        ANIMATION_ROCKET_FADE_DURATION,
        
        ANIMATION_BOMB_TILE_FADE_WAIT,
        ANIMATION_BOMB_TILE_FADE_DURATION,
        
        ANIMATION_BOMB_SHRAPNEL_FADE_WAIT,
        ANIMATION_BOMB_SHRAPNEL_FADE_DURATION,
        ANIMATION_BOMB_SHRAPNEL_MOVE_WAIT,
        ANIMATION_BOMB_SHRAPNEL_MOVE_DURATION,
        ANIMATION_BOMB_SHRAPNEL_MOVE_SPEED,
        ANIMATION_BOMB_SHRAPNEL_MOVE_GRAVITY,
        ANIMATION_BOMB_SHRAPNEL_MOVE_OMEGA,
               
        ANIMATION_BOMB_EXPLODE_ZOOM_SPEED,
        ANIMATION_BOMB_EXPLODE_ZOOM_DURATION,
        ANIMATION_BOMB_EXPLODE_FADE_WAIT,
        ANIMATION_BOMB_EXPLODE_FADE_DURATION,
        
        ANIMATION_EXPLOSION_SPEED,        
        ANIMATION_EXPLOSION_OPACITY,
        ANIMATION_EXPLOSION_INITIAL_WIDTH,
        ANIMATION_EXPLOSION_INITIAL_HEIGHT,
        
        ANIMATION_ROWFADE_WAIT,
        ANIMATION_ROWFADE_DURATION,
        
        ANIMATION_SLIDEFADE_FADE_WAIT,
        ANIMATION_SLIDEFADE_FADE_DURATION,
        ANIMATION_SLIDEFADE_MOVE_WAIT,
        ANIMATION_SLIDEFADE_MOVE_DURATION,
        ANIMATION_SLIDEFADE_MOVE_SPEED,
        
        ANIMATION_PIECE_PULSE_SPEED_SLOW,
        ANIMATION_PIECE_PULSE_SPEED_FAST,
                
        // SCT values.

        SCT_COLOR_PIECE,
        SCT_COLOR_LINE,        
        SCT_COLOR_ITEM,
        SCT_SCORE_MOVE_DURATION,
        SCT_SCORE_MOVE_SPEED,
        SCT_SCORE_MOVE_THETA,
        SCT_SCORE_FADE_WAIT,
        SCT_SCORE_FADE_DURATION,
        SCT_SCORE_FADE_MIN_OPACITY,
        SCT_SCORE_FADE_MAX_OPACITY,
        SCT_LEVELUP_TEXT,
        SCT_LEVELUP_TEXT_SIZE,
        SCT_LEVELUP_MOVE_DURATION,
        SCT_LEVELUP_MOVE_SPEED,
        SCT_LEVELUP_MOVE_THETA,                
                
        // Menu values.
        
        LOADER_BAR_FADE_WAIT,
        LOADER_BAR_FADE_DURATION,        
        MAIN_MENU_STARBURST_OMEGA,
        MAIN_MENU_WINDOW_OPACITY,
        MAIN_MENU_WINDOW_SPEED,
        MAIN_MENU_SLIDE_SPEED,
        MAIN_MENU_SLIDE_MIN_X,
        MAIN_MENU_SLIDE_WAIT,
        MAIN_MENU_LOGO_FADE_IN_WAIT,
        MAIN_MENU_LOGO_FADE_IN_DURATION,
        MAIN_MENU_LOGO_FADE_OUT_WAIT,
        MAIN_MENU_LOGO_FADE_OUT_DURATION    
    }
    
    public enum Value
    {
        CONF_VERSION_1,
    }
    
    // Static settings.
    
    /** The upgrade URL. */
    final private static String upgradeUrl = "http://couchware.ca";
    
    /** The cross platform line separator. */
    final private static String lineSeparator = System.getProperty("line.separator");
    
    /** The path to the resources. */
    final private static String resourcesPath = "resources";        

    /** The path to the fonts. */
    final private static String fontResourcesPath = resourcesPath + "/fonts";
    
    /** The path to the sprites. */
    final private static String spriteResourcesPath = resourcesPath + "/sprites";
    
    /** The path to the sounds. */
    final private static String soundResourcesPath = resourcesPath + "/sounds";
    
    /** The path to the music. */
    final private static String musicResourcesPath = resourcesPath + "/music";
    
     /** The path to the XML data. */
    final private static String textResourcesPath = resourcesPath + "/text";
        
    /** The name of the settings file. */
    final private static String gameSettingsFileName = "game-settings.xml";     
    
    /** The name of the user settings file. */
    final private static String userSettingsFileName = "user-settings.xml";
    
    /** The file path of default game settings file. */
    final private static String defaultGameSettingsFilePath = textResourcesPath 
            + "/" + gameSettingsFileName;
    
    /** The file path of default user settings file. */    
    final private static String defaultUserSettingsFilePath = textResourcesPath 
            + "/" + userSettingsFileName;
    
    /** The path to the user settings file. */
    final private static String externalSettingsPath = 
            System.getProperty("user.home") + "/.Couchware/Wezzle";
                    
    /** The file path of default game settings file. */
    final private static String gameSettingsFilePath = externalSettingsPath 
            + "/" + gameSettingsFileName;
    
    /** The file path of default user settings file. */
    final private static String userSettingsFilePath = externalSettingsPath 
            + "/" + userSettingsFileName;
    
    /** The path to the log file. */
    final private static String logPath = externalSettingsPath;
    
    /** The file path to the log file. */    
    final private static String logFilePath = logPath + "/log.txt";

    /** A set of all the user keys. */
    final private static EnumSet<Key> userKeys = calculateUserKeys();
    
    /**
     * Determines all the user settings keys and returns them.
     * @return
     */
    final private static EnumSet<Key> calculateUserKeys()
    {
        EnumSet<Key> set = EnumSet.noneOf(Key.class);
        
        for (Key key : Key.values())
            if (key.toString().startsWith("USER_"))
                set.add(key);       
        
        return set;
    }

    public static EnumSet<Key> getUserKeys()
    {
        return userKeys;
    }        
    
    public static String getUpgradeUrl()
    {
        return upgradeUrl;
    }   
    
    public static String getDefaultGameSettingsFilePath()
    {
        return defaultGameSettingsFilePath;
    }
    
    public static String getDefaultUserSettingsFilePath()
    {
        return defaultUserSettingsFilePath;
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

    public static String getGameSettingsFilename()
    {
        return gameSettingsFileName;
    }
    
     public static String getUserSettingsFilename()
    {
        return userSettingsFileName;
    }

    public static String getTextResourcesPath()
    {
        return textResourcesPath;
    }   

    public static String getSoundResourcesPath()
    {
        return soundResourcesPath;
    }

    public static String getSpriteResourcesPath()
    {
        return spriteResourcesPath;
    }   
    
    public static String getGameSettingsFilePath()
    {
        return gameSettingsFilePath;
    }
    
     public static String getUserSettingsFilePath()
    {
        return userSettingsFilePath;
    }

    public static String getExternalSettingsPath()
    {
        return externalSettingsPath;
    }        
    
    // Dynamic non-text file settings.
    
    /** Cached milliseconds per tick. */
    private static boolean millisecondsPerTickInitialized = false;
    private static int millisecondsPerTick;        
    
    /**
     * Calculates all the special settings.
     * @param settingsMan
     */
    public static void calculate()
    {
        calculateMillisecondsPerTick();
    }       
    
    /** Recalculates milliseconds per tick. */
    private static void calculateMillisecondsPerTick()
    {         
        millisecondsPerTick = 1000 / SettingsManager.get().getInt(Key.GAME_TICKS_PER_SECOND);                 
    }
    
    /** Returns the number of milliseconds per tick. */
    public static int getMillisecondsPerTick()
    {
        if (millisecondsPerTickInitialized == false)
        {
            millisecondsPerTickInitialized = true;
            calculateMillisecondsPerTick();
        }
        
        return millisecondsPerTick;
    }
}
