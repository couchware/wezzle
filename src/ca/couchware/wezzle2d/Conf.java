/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

/**
 * A class containing configuration values.
 * 
 * @author cdmckay
 */
public final class Conf 
{
    private Conf()
    { }
    
    // Mechanics parameters.
    
    /** The number of ticks per second (same as FPS). */
    public static final int TICKS_PER_SECOND = 60;
    
    /** The number of milliseconds in a tick. */            
    public static final int MILLISECONDS_PER_TICK = 1000 / TICKS_PER_SECOND;
    
    // The debug parameters.
    
    /** Should the clip rectangle be shown?  This only makes sense for Java2D. */
    public static final boolean DEBUG_SHOW_CLIP_RECT = false;
    
    // The bounce animation parameters.
    
    /** The initial speed of the bounce animation. */
    public static final int ANIMATION_JUMP_MOVE_SPEED = 7;    
    
    /** The duration of the bounce animation. */
    public static final int ANIMATION_JUMP_MOVE_DURATION = 55;
    
    /** The gravity acting on the bounce animation. */
    public static final int ANIMATION_JUMP_MOVE_GRAVITY = 3;        
    
    /** The amount of ticks to wait before fading. */
    public static final int ANIMATION_JUMP_FADE_WAIT = 0;       
    
    /** The amount of time to fade for. */
    public static final int ANIMATION_JUMP_FADE_DURATION = 55;
    
    // The rocket animation parameters.
    
    public static final int ROCKET_MOVE_SPEED = 6;
    public static final int ROCKET_MOVE_GRAVITY = 0;
    public static final int ROCKET_FADE_WAIT = 0;
    public static final int ROCKET_FADE_DURATION = 55;
    
    // The bomb animation paramters.
    
    public static final int BOMB_FADE_WAIT = 0;
    public static final int BOMB_FADE_DURATION = 55;
    
    // The SCT parameters.
    
    /** The duration of the score SCT. */
    public static final int SCT_SCORE_MOVE_DURATION = 82;
    
    /** The speed of the score SCT. */
    public static final int SCT_SCORE_MOVE_SPEED_P = 1;
    
    /** The speed of the score SCT. */
    public static final int SCT_SCORE_MOVE_SPEED_Q = 2;
    
    /** The theta of the score SCT. */
    public static final int SCT_SCORE_MOVE_THETA = 90;
    
    /** The amount of time to wait before fading. */
    public static final int SCT_SCORE_FADE_WAIT = 30;
    
    /** The duration of the fade out. */
    public static final int SCT_SCORE_FADE_DURATION = 55;
    
    /** The minimum opacity. */
    public static final int SCT_SCORE_FADE_MIN_OPACITY = 0;
    
    /** The maximum opacity. */
    public static final int SCT_SCORE_FADE_MAX_OPACITY = 100;
    
    /** The text shown on a level up. */
    public static final String SCT_LEVELUP_TEXT = "Level Up!";
    
    /** The size of the text shown on a level up. */
    public static final int SCT_LEVELUP_TEXT_SIZE = 26;
    
    /** The duration of the text shown on a level up. */
    public static final int SCT_LEVELUP_MOVE_DURATION = 82;
    
    /** The p-component of the speed of the text shown on a level up. */
    public static final int SCT_LEVELUP_MOVE_SPEED_P = 1;
    
    /** The q-component of the speed of the text shown on a level up. */
    public static final int SCT_LEVELUP_MOVE_SPEED_Q = 2;
    
    /** The angle of the text shown on a level up. */
    public static final int SCT_LEVELUP_MOVE_THETA = 0;
    
    // The loader and main menu parameters.
    
    public static final int LOADER_BAR_FADE_WAIT = 36;
    public static final int LOADER_BAR_FADE_DURATION = 80;
    
    /** The rotation velocity of the menu starburst. */
    public final static double MAIN_MENU_STARBURST_OMEGA = 0.002;
    
    /** The opacity of the menu windows. */
    public final static int MAIN_MENU_WINDOW_OPACITY = 60;
    
    /** The speed at which the menu windows move. */
    public final static int MAIN_MENU_WINDOW_SPEED = 8;
    
    /** The speed the buttons come in. */
    final public static int MAIN_MENU_SLIDE_SPEED = 6;

    /** The final X position the buttons go to. */    
    final public static int MAIN_MENU_SLIDE_MIN_X = 630;

    /** The amount of time between each slide in. */
    final public static int MAIN_MENU_SLIDE_WAIT = 14;
        
    public static final int MAIN_MENU_LOGO_FADE_IN_WAIT = 36;
    public static final int MAIN_MENU_LOGO_FADE_IN_DURATION = 150;
    public static final int MAIN_MENU_LOGO_FADE_OUT_WAIT = 0;
    public static final int MAIN_MENU_LOGO_FADE_OUT_DURATION = 100;
    
}
