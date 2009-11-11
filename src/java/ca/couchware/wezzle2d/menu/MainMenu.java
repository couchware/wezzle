/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.animation.AnimationAdapter;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.animation.FinishedAnimation;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.audio.Music;
import ca.couchware.wezzle2d.audio.MusicPlayer;
import ca.couchware.wezzle2d.graphics.EntityGroup;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.graphics.IDrawer;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.group.AbstractGroup;
import ca.couchware.wezzle2d.group.EmptyGroup;
import ca.couchware.wezzle2d.group.IGroup;
import ca.couchware.wezzle2d.manager.MusicManager;
import ca.couchware.wezzle2d.util.CouchLogger;
import java.awt.Shape;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jlgui.basicplayer.BasicPlayerException;

/**
 *
 * @author cdmckay
 */
public class MainMenu extends AbstractGroup implements IDrawer, IMenu
{       
    
    /** The standard menu background path. */
    final private static String BACKGROUND_PATH = Settings.getSpriteResourcesPath()
            + "/MenuBackground.png"; 
    
    /** The wezzle logo path. */
    final private static String WEZZLE_LOGO_PATH = Settings.getSpriteResourcesPath()
            + "/WezzleLogo.png"; 
    
    /** The wezzle logo starburst path. */
    final private static String WEZZLE_LOGO_STARBURST_PATH = Settings.getSpriteResourcesPath()
            + "/WezzleLogoStarburst.png";
    
    /** An enum containing the possible states for the loader to be in. */
    public enum State
    {                       
        /** The main menu has been initialized and is ready to go. */
        READY,                
              
        ANIMATING,
        
        /** The main menu is done and is waiting for the game to take control. */
        FINISHED
    }
    
    /** The current state of the menu. */
    private State state = State.READY;
        
    /** An enum containing the main menu buttons. */
    private enum Menu
    {
        NONE(-1),
        PLAY_NOW(0),                
        ACHIEVEMENTS(1),
        OPTIONS(2),
        HIGH_SCORES(3),
        ABOUT(4);
                
        private int rank;
        
        Menu(int rank)
        { this.rank = rank; }
        
        public int getRank()
        { return rank; }
    }                       
    
    /** The wezzle logo. */
    private EntityGroup logoEntity;
    
    /** The logo spinner animation. */
    private IAnimation rotateAnimation;
    
    /** The manager hub */
    final private ManagerHub hub;

    /** The private layer manager. */
    final private LayerManager menuLayerMan;
    
    /** A map containing all the buttons in the main menu. */
    private EnumMap<Menu, IButton> buttonMap;
    
    /** A map containing all the groups in the main menu. */
    private EnumMap<Menu, IGroup> menuMap;
    
    /** The current button that is activated. */
    private Menu currentButton = Menu.NONE;    
      
    /** The animation that slides in the options when the menu is first shown. */        
    private IAnimation slideAnimation = FinishedAnimation.get();    
    
    /** The animation that is currently being run. */        
    private IAnimation currentAnimation = FinishedAnimation.get();

    /** The main menu music player. */
    private MusicPlayer player;
    
    /**
     * Create a new main menu.
     */
    public MainMenu(ManagerHub hub)
    {               
        // Invoke super.
        if (hub == null)
           throw new IllegalArgumentException("Hub must not be null");
        
        this.hub = hub;
                
        // Set the main menu as activated.
        this.activated = true;

        // Create the menu's layer manager.
        this.menuLayerMan = LayerManager.newInstance();

        // Initialize the menu music.
        this.player = MusicManager.createPlayer( Music.ERHU );
        try
        {
            int intGain = SettingsManager.get().getInt(Settings.Key.USER_MUSIC_VOLUME);
            double gain = (double) intGain / 100.0;
            this.player.setLooping(true);
            this.player.play();

            boolean isOn = SettingsManager.get().getBool(Settings.Key.USER_MUSIC);
            if (isOn)
            {
                this.player.setNormalizedGain( 0.0 );
                this.player.fadeToGain( gain );
            }
            else
            {
                this.player.setNormalizedGain( gain );
                this.player.pause();
            }
        }
        catch ( BasicPlayerException ex )
        {
            CouchLogger.get().recordException( this.getClass(), ex );
        }

        initializeBackground(hub);
        initializeLogo(hub);
        initializeButtons(hub);
        initializeGroups(hub);
                
        // Create the slide animation.
        this.slideAnimation = new MetaAnimation.Builder()
                .runRule(MetaAnimation.RunRule.SEQUENCE)
                .add(animateShow())                
                .build();
        
        // Add it to the manager.
        hub.gameAnimationMan.add(this.slideAnimation);       
    };
    
    private void initializeBackground(ManagerHub hub)
    {
        // Add the background.
        GraphicEntity backgroundGraphic = 
                new GraphicEntity.Builder(0, 0, BACKGROUND_PATH).build();
        this.menuLayerMan.add(backgroundGraphic, Layer.BACKGROUND);
                
        // Set up the copyright label.               
        ITextLabel l1 = new LabelBuilder(10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_DISABLED))
                .size(12)                
                .text(Game.COPYRIGHT).build();
        this.menuLayerMan.add(l1, Layer.UI);
        
        // Set up the version label.	
        ITextLabel l2 = new LabelBuilder(800 - 10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.RIGHT))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_DISABLED))
                .size(12)                
                .text(Game.TITLE).build();
        this.menuLayerMan.add(l2, Layer.UI);
    }
    
    private void initializeLogo(ManagerHub hub)
    {
        // Create the wezzle logo.
        IEntity e1 = new GraphicEntity.Builder(268, 300, WEZZLE_LOGO_STARBURST_PATH)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).build();
        e1.setRotationAnchor(e1.getWidth() / 2, e1.getHeight() / 2);
        
        this.menuLayerMan.add(e1, Layer.BACKGROUND);
        this.rotateAnimation = new MoveAnimation.Builder(e1)
                .gravity(0).speed(0)
                .omega(SettingsManager.get().getDouble(Key.MAIN_MENU_STARBURST_OMEGA))
                .build();
        hub.gameAnimationMan.add(this.rotateAnimation);
        
        IEntity e2 = new GraphicEntity.Builder(268, 300, WEZZLE_LOGO_PATH)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).build();
        this.menuLayerMan.add(e2, Layer.BACKGROUND);
        
        this.logoEntity = new EntityGroup(e1, e2);
    }
    
    private void initializeButtons(ManagerHub hub) 
    {                
        // Create the button list.
        this.buttonMap = new EnumMap<Menu, IButton>(Menu.class);
        
        // The button that will be used as a template for all buttons.
        IButton templateButton = null;
        
        // A temporary button holder.
        IButton button = null;
        
        // Create the buttons.               
        templateButton = new Button.Builder(910, 0)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))                               
                .text("")
                .textSize(20)
                .hoverOpacity(70)
                .normalOpacity(0)               
                .build();

        // The Y-coordinate that we draw the first button at.
        int startY = 200;

        button = new Button.Builder((Button) templateButton)
                .y(startY + 50 * Menu.PLAY_NOW.getRank())
                .text("Play Now").build();
        this.menuLayerMan.add(button, Layer.UI);
        buttonMap.put(Menu.PLAY_NOW, button);                                          
        
        button = new Button.Builder((Button) templateButton)
                .y(startY + 50 * Menu.ACHIEVEMENTS.getRank())
                .text("Achievements").build();
        this.menuLayerMan.add(button, Layer.UI);
        buttonMap.put(Menu.ACHIEVEMENTS, button);

        button = new Button.Builder((Button) templateButton)
                .y(startY + 50 * Menu.OPTIONS.getRank())
                .text("Options").build();
        this.menuLayerMan.add(button, Layer.UI);
        buttonMap.put(Menu.OPTIONS, button);
        
        button = new Button.Builder((Button) templateButton)
                .y(startY + 50 * Menu.HIGH_SCORES.getRank())
                .text("High Scores").build();
        this.menuLayerMan.add(button, Layer.UI);
        buttonMap.put(Menu.HIGH_SCORES, button);
        
        button = new Button.Builder((Button) templateButton)
                .y(startY + 50 * Menu.ABOUT.getRank())
                .text("About").build();
        this.menuLayerMan.add(button, Layer.UI);
        buttonMap.put(Menu.ABOUT, button);
    };
    
    private void initializeGroups(ManagerHub hub)
    {
        // Create the group map.
        this.menuMap = new EnumMap<Menu, IGroup>(Menu.class);
        
        // A temporary group holder.
        IGroup menu = null;
        
        // Create the None group.
        menu = new EmptyGroup();
        menu.setActivated(true);
        this.menuMap.put(Menu.NONE, menu);
        
        // Create the "Play Now" group.
        menu = new PlayNowMenu(this, hub, this.menuLayerMan);
        this.menuMap.put(Menu.PLAY_NOW, menu);

        // Create the "Achievements" group.
        menu = new AchievementMenu(this, hub, this.menuLayerMan);
        this.menuMap.put(Menu.ACHIEVEMENTS, menu);

        // Create the "Achievements" group.
        menu = new OptionsMenu(this, hub, this.menuLayerMan);
        this.menuMap.put(Menu.OPTIONS, menu);
        
        // Create the "High Scores" group.
        menu = new HighScoreMenu(this, hub, this.menuLayerMan);
        this.menuMap.put(Menu.HIGH_SCORES, menu);
        
         // Create the "About" group.
        menu = new AboutMenu(this, hub, this.menuLayerMan);
        this.menuMap.put(Menu.ABOUT, menu);
    }
    
    public void updateLogic(Game game, ManagerHub hub)
    {       
        switch (state)
        {
            case READY:                                                                                             
                
                // Check for a new animation to load.
                if (this.currentAnimation.isFinished() == false)
                {
                    hub.gameAnimationMan.add(this.currentAnimation);
                    this.state = State.ANIMATING;
                    break;
                }
                
                // The button that was clicked.  Null if no button was clicked.
                IButton clickedButton = null;
                
                for (IButton button : this.buttonMap.values())
                {
                    if (button.clicked() == true)
                    {
                        clickedButton = button;                        
                        // Do not short-circuit!                        
                    }                   
                } // end for
                
                // See if a button was clicked.  If it was, then unclick all
                // the others.
                if (clickedButton != null)
                {
                    for (Menu menu : this.buttonMap.keySet())
                    {
                        // Make sure their clicked flag is clear.
                        IButton button = buttonMap.get(menu);
                        
                        // Activate the button if it is the clicked button.
                        if (button.equals(clickedButton) == true)
                        {
                            // Activate the new button.
                            button.setActivated(true);
                            button.setDisabled(true);
                            
                            // Animate the change.
                            this.currentAnimation = new MetaAnimation.Builder()
                                    .finishRule(MetaAnimation.FinishRule.ALL)
                                    //.runRule(MetaAnimation.RunRule.SEQUENCE)
                                    .add(this.menuMap.get(currentButton).animateHide())
                                    .add(this.menuMap.get(menu).animateShow())
                                    .build();
                            
                            // Set the new current button.                            
                            this.currentButton = menu;
                            
                            // Activate the current group.
                            this.menuMap.get(menu).setActivated(true);
                        }
                        else
                        {
                            button.setActivated(false);
                            button.setDisabled(false);
                            
                            // Deactivate the other groups.
                            this.menuMap.get(menu).setActivated(false);
                        }                        
                    } // end for
                } // end if
                
                // See if the running group deactivated itself.  If it did,
                // then we need to hide the group and deactivate the button.
                // This will bring us back to the initial menu screen with
                // no buttons activated.
                IGroup group = this.menuMap.get(currentButton);
                if ( !group.isActivated() )
                {
                    this.buttonMap.get(currentButton).setActivated(false);
                    this.buttonMap.get(currentButton).setDisabled(false);
                    
                    this.currentAnimation = group.animateHide();
                    //this.animationMan.add(this.animation);
                    
                    this.currentButton = Menu.NONE;
                                       
                    group.resetControls();
                }
                // Otherwise, update the logic.
                else
                {                       
                    // Update the group logic.
                    group.updateLogic(game, hub);
                    
                    // See if the group deactivated the main menu.  If the main
                    // menu is deactivated, then we need to start the game.                    
                    if ( !this.activated )
                    {                      
                        // Create the hide animation.
                        final IAnimation anim = animateHide();                        
                        
                        anim.addAnimationListener(new AnimationAdapter()
                        {                            
                            @Override
                            public void animationFinished()
                            { state = State.FINISHED; }
                        });
                        
                        // Disable the layer manager so nothing can be pressed.
                        this.menuLayerMan.setDisabled(true);
                        
                        // Set the animation.
                        this.currentAnimation = anim;
                        
                    } // end if
                } // end if
                
                break;         
                
            case ANIMATING:
                
                if ( this.currentAnimation.isFinished() )
                {                    
                    this.state = State.READY;
                }
                
            case FINISHED:
                
                // Do nothing, we're done.
                
                break;
                
            default: throw new AssertionError();
        }                             
    }   
    
    @Override
    public IAnimation animateShow()
    {                       
        // The meta animation builder.
        MetaAnimation.Builder builder = new MetaAnimation.Builder()
                .finishRule(MetaAnimation.FinishRule.ALL);
        
        // Add the wezzle logo fade in.
        this.logoEntity.setOpacity( 0 );
        builder.add(new FadeAnimation.Builder(FadeAnimation.Type.IN, logoEntity)
                .wait(hub.settingsMan.getInt(Key.MAIN_MENU_LOGO_FADE_IN_WAIT))
                .duration(hub.settingsMan.getInt(Key.MAIN_MENU_LOGO_FADE_IN_DURATION)).build());
        
        // Animate the buttons coming in.        
        for (Menu menu : this.buttonMap.keySet())
        {
            if (this.buttonMap.containsKey(menu) == false)
                continue;
            
            final IButton button = this.buttonMap.get(menu);
            
            final IAnimation anim = new MoveAnimation.Builder(button).theta(180)
                    .speed(hub.settingsMan.getInt(Key.MAIN_MENU_SLIDE_SPEED))
                    .minX(hub.settingsMan.getInt(Key.MAIN_MENU_SLIDE_MIN_X))
                    .wait(hub.settingsMan.getInt(Key.MAIN_MENU_SLIDE_WAIT) * menu.getRank())
                    .build();
            
            builder.add(anim);
            
            anim.addAnimationListener(new AnimationAdapter()
            {
                @Override
                public void animationStarted()
                {
                    button.setDisabled(true);
                }

                @Override
                public void animationFinished()
                { 
                    button.setDisabled(false);
                }
            });
        }
        
        // Complete the meta animation.
        return builder.build();
    }
    
    @Override
    public IAnimation animateHide()
    {
        // Create a new meta animation.
        MetaAnimation.Builder builder = new MetaAnimation.Builder()
                .finishRule(MetaAnimation.FinishRule.ALL);
        
        // Hide the current group.
        builder.add(this.menuMap.get(this.currentButton).animateHide());
        
        // Fade out the logo.
        IAnimation fade = new FadeAnimation.Builder(FadeAnimation.Type.OUT, logoEntity)
                .wait(hub.settingsMan.getInt(Key.MAIN_MENU_LOGO_FADE_OUT_WAIT))
                .duration(hub.settingsMan.getInt(Key.MAIN_MENU_LOGO_FADE_OUT_DURATION))
                .build();
        
        fade.addAnimationListener(new AnimationAdapter()
        {           
            @Override
            public void animationFinished()
            { 
                logoEntity.setVisible(false);
                rotateAnimation.setFinished();
            }
        });
        
        builder.add(fade);
         
        // Slide out the buttons.
        // Animate the buttons coming in.        
        for (Menu m : this.buttonMap.keySet())
        {                        
            final IButton button = this.buttonMap.get(m);
            
            final IAnimation anim = new MoveAnimation.Builder(button).theta(0)
                    .speed(hub.settingsMan.getInt(Key.MAIN_MENU_SLIDE_SPEED))
                    .maxX(910)
                    .wait(hub.settingsMan.getInt(Key.MAIN_MENU_SLIDE_WAIT) * m.getRank())
                    .build();
            
            builder.add(anim);
            
            anim.addAnimationListener(new AnimationAdapter()
            {
                @Override
                public void animationStarted()
                { 
                    button.setDisabled(true);
                }
            });
        }
        
        // Return the new meta animation.
        return builder.build();
    };

    public State getState()
    {
        return state;
    }

    public MusicPlayer getPlayer()
    {
        return player;
    }
    
    @Override
    public boolean draw()
    {
        return this.menuLayerMan.draw();
    }       
    
    public boolean draw(Shape region, boolean exact)
    {
        return this.menuLayerMan.draw(region, exact);
    }

    public boolean draw(Shape region)
    {
        return this.menuLayerMan.draw(region);
    }
    
    public void forceRedraw()
    {
        this.menuLayerMan.forceRedraw();
    }
    
    @Override
    public void dispose()
    {
        // Make sure all the groups are disposed.
        for (IButton button : buttonMap.values())
            button.dispose();
        
        for (IGroup group : menuMap.values())
            group.dispose();
    }
    
}
