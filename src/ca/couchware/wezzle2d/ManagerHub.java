/*
 * Wezzle
 * Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d;

import ca.couchware.wezzle2d.manager.AchievementManager;
import ca.couchware.wezzle2d.manager.AnimationManager;
import ca.couchware.wezzle2d.manager.BoardManager;
import ca.couchware.wezzle2d.manager.GroupManager;
import ca.couchware.wezzle2d.manager.HighScoreManager;
import ca.couchware.wezzle2d.manager.ItemManager;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LevelManager;
import ca.couchware.wezzle2d.manager.ListenerManager;
import ca.couchware.wezzle2d.manager.ListenerManager.Listener;
import ca.couchware.wezzle2d.manager.MusicManager;
import ca.couchware.wezzle2d.manager.PieceManager;
import ca.couchware.wezzle2d.manager.ScoreManager;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.manager.SoundManager;
import ca.couchware.wezzle2d.manager.StatManager;
import ca.couchware.wezzle2d.manager.TimerManager;
import ca.couchware.wezzle2d.manager.TutorialManager;
import java.util.Set;

/**
 * A class that holds a reference to all the managers.
 * 
 * @author cdmckay
 */
public class ManagerHub 
{

    /** The single instance of the manager hub. */
    final private static ManagerHub SINGLE = new ManagerHub();
      
    /** An enum of the manager types. */
    public static enum Manager
    {       
        ACHIEVEMENT, 
        ANIMATION,
        BOARD, 
        GROUP, 
        HIGHSCORE,
        ITEM,
        LAYER, 
        LEVEL,
        LISTENER,
        MUSIC, 
        PIECE,         
        SCORE,
        SETTINGS,
        SOUND,
        STAT, 
        TIMER, 
        TUTORIAL                        
    }        
    
    /** The manager in charge of achievements */
    public AchievementManager achievementMan;	
    
    /** The animation manager in charge of animations. */
    public AnimationManager animationMan;
    
    /** The manager in charge of maintaining the board. */
    public BoardManager boardMan;
    
    /** The menu manager. */
    public GroupManager groupMan;
       
    /** The high score manager. */    
    public HighScoreManager highScoreMan;
    
    /** The item manager. */
    public ItemManager itemMan;
    	
    /** The game layer manager. */
    public LayerManager layerMan;     
    
    /** The manager in charge of the level. */
    public LevelManager levelMan;    
    
    /** The maanger in charge of (most) listeners. */
    public ListenerManager listenerMan;     
    
    /** The manager in charge of music. */
    public MusicManager musicMan;  

    /**
     * The manager in charge of moving the piece around with the
     * pointer and drawing the piece to the board.
     */
    public PieceManager pieceMan;	   
    
    /** The settings manager. */
    public SettingsManager settingsMan;
    
    /** The manager in charge of score. */
    public ScoreManager scoreMan;        
    
    /** The manager in charge of sound. */
    public SoundManager soundMan;      
    
    /** The manager in charge of the moves. */
    public StatManager statMan;	    
    
    /** The manager in charge of keeping track of the time. */
    public TimerManager timerMan;
    
    /** The manager in charge of running tutorials. */
    public TutorialManager tutorialMan;

    /**
     * The private constructor.
     */
    private ManagerHub()
    { }
    
    /** 
     * Get the only instance of the manager hub. 
     * 
     * @return
     */
    public static ManagerHub get()
    {
        return SINGLE;
    }    
    
    public void initialize(Set<Manager> set, Game game)   
    {    
       
        if (set.contains(Manager.LISTENER))
        {
            // Get the singleton.
            this.listenerMan = ListenerManager.get();                        
        }
        
        if (set.contains(Manager.SETTINGS))
        {
            // Get the singleton.
            this.settingsMan = SettingsManager.get();
        }
        
        if (set.contains(Manager.TIMER))
        {
            // Get the singleton.
            this.timerMan = TimerManager.newInstance(this.listenerMan);
            
            // Initialize some parameters.
            //this.timerMan.resetCurrentTime();
            this.listenerMan.registerListener(Listener.GAME,  this.timerMan);
            this.listenerMan.registerListener(Listener.LEVEL, this.timerMan);
            this.listenerMan.registerListener(Listener.MOVE,  this.timerMan);
        }
        
        if (set.contains(Manager.SOUND))
        {
            // Create the sound manager.
            this.soundMan = SoundManager.newInstance(game.getExecutor(), this.settingsMan);
        }
        
        if (set.contains(Manager.MUSIC))
        {
            // Create the music manager.            
            this.musicMan = MusicManager.newInstance(game.getExecutor(), this.settingsMan);  
        }   
        
        if (set.contains(Manager.LAYER))
        {
            // Create the layer manager.   
            this.layerMan = LayerManager.newInstance();  
        }                
        
        if (set.contains(Manager.ANIMATION))
        {
            // Create the animation manager.
            this.animationMan = AnimationManager.newInstance();                
        }
        
        if (set.contains(Manager.HIGHSCORE))
        {
            // Create the high score manager.
            this.highScoreMan = HighScoreManager.newInstance();  
        }      
        
        if (set.contains(Manager.ITEM))
        {
            // Create the manager.
            this.itemMan = ItemManager.newInstance();  
            
            this.listenerMan.registerListener(Listener.LEVEL, this.itemMan);
            this.listenerMan.registerListener(Listener.MOVE,  this.itemMan);
        } 
        
        if (set.contains(Manager.STAT))
        {
            // Create the move manager.
            this.statMan = StatManager.newInstance();
            this.listenerMan.registerListener(Listener.GAME, this.statMan);
            this.listenerMan.registerListener(Listener.LINE, this.statMan);
            this.listenerMan.registerListener(Listener.MOVE, this.statMan);            
        }
        
        if (set.contains(Manager.TUTORIAL))
        {
            // Create the tutorial manager.
            this.tutorialMan = TutorialManager.newInstance();           
        }                           
        
        if (set.contains(Manager.BOARD))
        {            
            // Create the board manager.
            this.boardMan = BoardManager.newInstance(
                    this.animationMan, 
                    this.layerMan, 
                    this.itemMan);             
            
            // Listen for key presses.
            game.getWindow().addKeyListener(boardMan);
        }
        
        if (set.contains(Manager.PIECE))
        {
            // Create the piece manager.
            this.pieceMan = PieceManager.newInstance(this);
            this.pieceMan.hidePieceGrid();            
            
            // Listen for the key and mouse.
            game.getWindow().addKeyListener(pieceMan);
            game.getWindow().addMouseListener(pieceMan);            
        }
        
        if (set.contains(Manager.GROUP))
        {
            // Create group manager.
            this.groupMan = GroupManager.newInstance(this.layerMan, this.pieceMan);
        }
	        
        if (set.contains(Manager.SCORE))
        {
            // Create the score manager.
            scoreMan = ScoreManager.newInstance(boardMan, highScoreMan, listenerMan);
                        
            listenerMan.registerListener(Listener.GAME,  scoreMan);
            listenerMan.registerListener(Listener.LEVEL, scoreMan);           
        }
        
        if (set.contains(Manager.LEVEL))
        {
            // Create the world manager.
            levelMan = LevelManager.newInstance(listenerMan, scoreMan);                  
        }                                         
        
        if (set.contains(Manager.ACHIEVEMENT))
        {
            // Create the achievement manager.
            achievementMan = AchievementManager.newInstance(settingsMan);
            
            listenerMan.registerListener(Listener.COLLISION, achievementMan);        
        }                     
    }            
    
}
