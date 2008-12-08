/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.manager.AnimationManager;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.animation.FinishedAnimation;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation.FinishRule;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.graphics.EntityGroup;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.graphics.IDrawer;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.manager.MusicManager;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.ui.SpriteButton;
import ca.couchware.wezzle2d.ui.group.AbstractGroup;
import ca.couchware.wezzle2d.ui.group.EmptyGroup;
import ca.couchware.wezzle2d.ui.group.IGroup;
import java.util.EnumMap;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class MainMenuGroup extends AbstractGroup implements IDrawer
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
        TUTORIAL(1),
        OPTIONS(2),
        UPGRADE(3),
        ACHIEVEMENTS(4),
        HIGH_SCORES(5),
        EXIT(6);
                
        private int rank;
        
        Menu(int rank)
        { this.rank = rank; }
        
        public int getRank()
        { return rank; }
    }                       
    
    /** The wezzle logo. */
    final private EntityGroup logoEntity;
    
    /** The logo spinner animation. */
    final private IAnimation rotateAnimation;
    
    /** A map containing all the buttons in the main menu. */
    private EnumMap<Menu, IButton> buttonMap;
    
    /** A map containing all the groups in the main menu. */
    private EnumMap<Menu, IGroup> groupMap;
    
    /** The current button that is activated. */
    private Menu currentMenu = Menu.NONE;    
    
    /** The animation manager. */
    private AnimationManager animationMan;
        
    /** The animation that slides in the options when the menu is first shown. */        
    private IAnimation slideAnimation = FinishedAnimation.get();    
    
    /** The animation that is currently being run. */        
    private IAnimation currentAnimation = FinishedAnimation.get();    
    
    /** The music manager. */
    private SettingsManager settingsMan;
    
    /** The music manager. */
    private MusicManager musicMan;
    
    /** The layer manager. */
    private LayerManager layerMan;              
    
    /**
     * Create a new main menu.
     */
    public MainMenuGroup(Game game)
    {                
        // Store the manager references.
        this.settingsMan  = game.settingsMan;
        this.animationMan = game.animationMan;                        
        this.musicMan     = game.musicMan;
        this.layerMan     = LayerManager.newInstance();
        
        // Set the main menu as activated.
        this.activated = true;
                
        // Add the background.
        GraphicEntity backgroundGraphic = 
                new GraphicEntity.Builder(0, 0, BACKGROUND_PATH).end();
        layerMan.add(backgroundGraphic, Layer.BACKGROUND);
                
        // Set up the copyright label.               
        ILabel l1 = new LabelBuilder(10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))
                .color(settingsMan.getColor(Key.GAME_COLOR_DISABLED))
                .size(12)                
                .text(Game.COPYRIGHT).end();
        layerMan.add(l1, Layer.UI);
        
        // Set up the version label.	
        ILabel l2 = new LabelBuilder(800 - 10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.RIGHT))
                .color(settingsMan.getColor(Key.GAME_COLOR_DISABLED))
                .size(12)                
                .text(Game.TITLE).end();                        
        layerMan.add(l2, Layer.UI);
        
        // Create the wezzle logo.
        IEntity e1 = new GraphicEntity.Builder(268, 300, WEZZLE_LOGO_STARBURST_PATH)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).end();
        layerMan.add(e1, Layer.BACKGROUND);      
        this.rotateAnimation = new MoveAnimation.Builder(e1)
                .gravity(0).speed(0)
                .omega(SettingsManager.get().getDouble(Key.MAIN_MENU_STARBURST_OMEGA))
                .end();
        animationMan.add(this.rotateAnimation);
        
        IEntity e2 = new GraphicEntity.Builder(268, 300, WEZZLE_LOGO_PATH)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).end();
        layerMan.add(e2, Layer.BACKGROUND);                         
        
        logoEntity = new EntityGroup(e1, e2);
               
        // Create the buttons.
        initializeButtons();
        
        // Create the groups.
        initializeGroups(game);
                
        // Create the slide animation.
        this.slideAnimation = new MetaAnimation.Builder()
                .runRule(MetaAnimation.RunRule.SEQUENCE)
                .add(animateShow())                
                .end();
        
        // Add it to the manager.
        this.animationMan.add(this.slideAnimation);
    };
    
    /**
     * Initialize all the buttons.
     */
    private void initializeButtons()
    {                
        // Create the button list.
        this.buttonMap = new EnumMap<Menu, IButton>(Menu.class);
        
        // The button that will be used as a template for all buttons.
        IButton templateButton = null;
        
        // A temporary button holder.
        IButton button = null;              
        
        // Create the buttons.               
        button = new SpriteButton.Builder(630, 153)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))               
                .type(SpriteButton.Type.THIN).text("Play Now").textSize(20)
                .hoverOpacity(70).normalOpacity(0).disabled(true).end();       
        buttonMap.put(Menu.PLAY_NOW, button);             
             
        // Make this button the template.
        templateButton = (SpriteButton) button;
        
        button = new SpriteButton.Builder((SpriteButton) templateButton)
                .y(202).text("Tutorial").end();        
        buttonMap.put(Menu.TUTORIAL, button);                        
        
        button = new SpriteButton.Builder((SpriteButton) templateButton)
                .y(251).text("Options").end();        
        buttonMap.put(Menu.OPTIONS, button);
        
        button = new SpriteButton.Builder((SpriteButton) templateButton)
                .y(300).text("Upgrade").end();        
        buttonMap.put(Menu.UPGRADE, button);     
        
        button = new SpriteButton.Builder((SpriteButton) templateButton)
                .y(349).text("Achievements").end();        
        buttonMap.put(Menu.ACHIEVEMENTS, button);
        
        button = new SpriteButton.Builder((SpriteButton) templateButton)
                .y(398).text("High Scores").end();        
        buttonMap.put(Menu.HIGH_SCORES, button);
                
        button = new SpriteButton.Builder((SpriteButton) templateButton)
                .y(447).text("Exit").end();        
        buttonMap.put(Menu.EXIT, button);  
        
        for (IButton btn : buttonMap.values())
        {
            layerMan.add(btn, Layer.UI);
            btn.setClickHook(createButtonClickHandler(btn));
        }
    };
        
    /**
     * Initialize all the groups.
     */
    private void initializeGroups(Game game)
    {
        // Create the group map.
        this.groupMap = new EnumMap<Menu, IGroup>(Menu.class);
        
        // A temporary group holder.
        IGroup group = null;
        
        // Create the None group.
        group = new EmptyGroup();
        group.setActivated(true);
        this.groupMap.put(Menu.NONE, group);
        
        // Create the "Play Now" group.
        group = new PlayNowGroup(this, this.layerMan, game);
        this.groupMap.put(Menu.PLAY_NOW, group);
        
        // Create the "Tutorial" group.
        group = new TutorialGroup(this.settingsMan, this.layerMan);
        this.groupMap.put(Menu.TUTORIAL, group);
        
        // Create the "Options" group.
        group = new TutorialGroup(this.settingsMan, this.layerMan);
        this.groupMap.put(Menu.OPTIONS, group);
        
         // Create the "Upgrade" group.
        group = new TutorialGroup(this.settingsMan, this.layerMan);
        this.groupMap.put(Menu.UPGRADE, group);
        
         // Create the "Achievements" group.
        group = new TutorialGroup(this.settingsMan, this.layerMan);
        this.groupMap.put(Menu.ACHIEVEMENTS, group);
        
         // Create the "High Scores" group.
        group = new TutorialGroup(this.settingsMan, this.layerMan);
        this.groupMap.put(Menu.HIGH_SCORES, group);
        
        // Create the "Exit" group.
        group = new ExitGroup(this.settingsMan, this.layerMan);
        this.groupMap.put(Menu.EXIT, group);
    }
    
    /**
     * Creates the button handler runnable that handles the click event for the 
     * given button.
     * 
     * @param button
     * @return
     */
    private Runnable createButtonClickHandler(final IButton button)
    {
        return new Runnable()
        {
            public void run()
            {
                handleButtonClick(button);
            }
        };
    }
    
    /**
     * Handles the button click for the given button.
     * 
     * @param button
     */
    private void handleButtonClick(final IButton button)
    {
        assert button != null;
        
        // Make sure we're in the ready state, otherwise ignore the
        // click.
        if (this.state != State.READY)
            return;
                    
        for (final Menu menu : this.buttonMap.keySet())
        {
            // Make sure their clicked flag is clear.
            IButton btn = buttonMap.get(menu);

            // Activate the button if it is the clicked button.
            if (btn.equals(button) == true)
            {
                // Activate the new button.
                //LogManager.recordWarning("Activating...");
                btn.setActivated(true);
                btn.setDisabled(true);

                // Animate the change.
                this.currentAnimation = new MetaAnimation.Builder()
                        .finishRule(MetaAnimation.FinishRule.ALL)                            
                        .add(this.groupMap.get(currentMenu).animateHide())
                        .add(this.groupMap.get(menu).animateShow())
                        .end();     
                                
                this.currentAnimation.setFinishHook(new Runnable()
                {
                    public void run()
                    {
                        for (IButton btn : buttonMap.values())
                        {
                            if (!btn.equals(button))
                                btn.setDisabled(false);
                        }
                    }                    
                });

                // Set the new current button.                            
                this.currentMenu = menu;

                // Activate the current group.
                this.groupMap.get(menu).setActivated(true);
            }
            else
            {
                btn.setActivated(false);               

                // Deactivate the other groups.
                this.groupMap.get(menu).setActivated(false);
            }                        
            
            // Disable all buttons while the menu is changing.
            btn.setDisabled(true);
        } // end for        
    }    
    
    public void updateLogic(Game game)
    {       
        switch (state)
        {
            case READY:                                                                                             
                
                // Check for a new animation to load.
                if (this.currentAnimation.isFinished() == false)
                {
                    this.animationMan.add(this.currentAnimation);
                    this.state = State.ANIMATING;
                    break;
                }                
                
                // See if the running group deactivated itself.  If it did,
                // then we need to hide the group and deactivate the button.
                // This will bring us back to the initial menu screen with
                // no buttons activated.
                IGroup group = this.groupMap.get(currentMenu);
                if (group.isActivated() == false)
                {
                    this.buttonMap.get(currentMenu).setActivated(false);
                    this.buttonMap.get(currentMenu).setDisabled(false);
                    
                    this.currentAnimation = group.animateHide();
                    //this.animationMan.add(this.animation);
                    
                    this.currentMenu = Menu.NONE;
                                       
                    group.resetControls();
                }
                // Otherwise, update the logic.
                else
                {                       
                    group.updateLogic(game);
                    
                    // See if the group deactivated the main menu.  If the main
                    // menu is deactivated, then we need to start the game.                    
                    if (this.activated == false)
                    {                      
                        // Create the hide animation.
                        final IAnimation a = animateHide();
                        
                        // Attach a runnable setting the state to finished.
                        a.setFinishHook(new Runnable()
                        {
                            public void run()
                            { state = State.FINISHED; }
                        });
                        
                        // Disable the layer manager so nothing can be pressed.
                        this.layerMan.setDisabled(true);
                        
                        // Set the animation.
                        this.currentAnimation = a;
                        
                    } // end if
                } // end if
                
                break;         
                
            case ANIMATING:
                
                if (this.currentAnimation.isFinished() == true)
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
        builder.add(new FadeAnimation.Builder(FadeAnimation.Type.IN, logoEntity)
                .wait(settingsMan.getInt(Key.MAIN_MENU_LOGO_FADE_IN_WAIT))
                .duration(settingsMan.getInt(Key.MAIN_MENU_LOGO_FADE_IN_DURATION)).end());
        
        // Animate the buttons coming in.        
        for (Menu m : this.buttonMap.keySet())
        {
            if (this.buttonMap.containsKey(m) == false)
                continue;
            
            final IButton btn = this.buttonMap.get(m);
            btn.setDisabled(false);
            
//            final IAnimation a = new MoveAnimation.Builder(btn).theta(180)
//                    .wait(settingsMan.getInt(Key.MAIN_MENU_SLIDE_WAIT))
//                    .speed(settingsMan.getInt(Key.MAIN_MENU_SLIDE_SPEED))
//                    .minX(settingsMan.getInt(Key.MAIN_MENU_SLIDE_MIN_X))                    
//                    .end();
            
            final IAnimation a = new FadeAnimation.Builder(FadeAnimation.Type.IN, btn)
                    .wait(580)
                    .duration(2400)
                    .end();
            
            builder.add(a);
            
//            a.setFinishRunnable(new Runnable()
//            {
//                public void run()
//                { btn.setDisabled(false); }
//            });
        }
        
        // Complete the meta animation.
        return builder.end();
    }
    
    @Override
    public IAnimation animateHide()
    {
        // Create a new meta animation.
        MetaAnimation.Builder builder = new MetaAnimation.Builder()
                .finishRule(MetaAnimation.FinishRule.ALL);
        
        // Hide the current group.
        builder.add(this.groupMap.get(this.currentMenu).animateHide());
        
        // Fade out the logo.
        IAnimation f = new FadeAnimation.Builder(FadeAnimation.Type.OUT, logoEntity)
                .wait(settingsMan.getInt(Key.MAIN_MENU_LOGO_FADE_OUT_WAIT))
                .duration(settingsMan.getInt(Key.MAIN_MENU_LOGO_FADE_OUT_DURATION))
                .end();
        
        f.setFinishHook(new Runnable()
        {
            public void run()
            { 
                logoEntity.setVisible(false);
                rotateAnimation.setFinished();
            }
        });
        
        builder.add(f);
         
        // Slide out the buttons.
        // Animate the buttons coming in.        
        for (Menu menu : this.buttonMap.keySet())
        {                        
            final IButton btn = this.buttonMap.get(menu);
            
//            final IAnimation a1 = new MoveAnimation.Builder(btn).theta(0)
//                    //.speed(settingsMan.getInt(Key.MAIN_MENU_SLIDE_SPEED))
//                    .speed(200)
//                    .maxX(910)
//                    //.wait(settingsMan.getInt(Key.MAIN_MENU_SLIDE_WAIT))
//                    .wait(0)
//                    .end();
            
            final IAnimation a2 = new FadeAnimation.Builder(FadeAnimation.Type.OUT, btn)
                    .wait(0)
                    .duration(1000)
                    .end();
            
            final IAnimation meta = new MetaAnimation.Builder()
                    .finishRule(FinishRule.FIRST)
//                    .add(a1)
                    .add(a2)
                    .end();
            
            builder.add(meta);
            
            meta.setStartHook(new Runnable()
            {
                public void run()
                { btn.setDisabled(true); }
            });
        }
        
        // Return the new meta animation.
        return builder.end();
    };

    public State getState()
    {
        return state;
    }        
    
    @Override
    public boolean draw()
    {
        return layerMan.draw();
    }        
    
    public void forceRedraw()
    {
        layerMan.forceRedraw();
    }
    
}
