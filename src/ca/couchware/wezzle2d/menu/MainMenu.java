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
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.graphics.EntityGroup;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.transition.ITransition;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.ui.SpriteButton;
import ca.couchware.wezzle2d.ui.group.AbstractGroup;
import ca.couchware.wezzle2d.ui.group.EmptyGroup;
import ca.couchware.wezzle2d.ui.group.IGroup;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.EnumMap;
import java.util.EnumSet;

/**
 *
 * @author cdmckay
 */
public class MainMenu extends AbstractGroup
{
    
    /**
     * The opacity of the menu windows.
     */
    final static int WINDOW_OPACITY = 60;
    
    /**
     * The speed at which the menu windows move.
     */
    final static double WINDOW_SPEED = 0.5;
    
    /**
     *  The speed the buttons come in.
     */
    final private static double SLIDE_SPEED = 0.8;

    /**
     * The final X position the buttons go to.
     */    
    final private static int SLIDE_MIN_X = 630;

    /**
     * The amount of time between each slide in.
     */
    final private static int SLIDE_WAIT = 100;
    
    /** 
     * The standard menu background path.
     */
    final private static String BACKGROUND_PATH = Game.SPRITES_PATH 
            + "/MenuBackground.png"; 
    
    /** 
     * The wezzle logo path.
     */
    final private static String WEZZLE_LOGO_PATH = Game.SPRITES_PATH 
            + "/WezzleLogo.png"; 
    
    /**
     * The wezzle logo starburst path.
     */
    final private static String WEZZLE_LOGO_STARBURST_PATH = Game.SPRITES_PATH
            + "/WezzleLogoStarburst.png";
    
    /**
     * An enum containing the possible states for the loader to be in.
     */
    public enum State
    {                       
        /** 
         * The main menu has been initialized and is ready to go.
         */
        READY,                
              
        ANIMATING,
        
        /**
         * The main menu is done and is waiting for the game to take control.
         */
        FINISHED
    }
    
    /**
     * The current state of the menu.
     */
    private State state = State.READY;
        
    /**
     * An enum containing the main menu buttons.
     */
    private enum Button
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
        
        Button(int rank)
        { this.rank = rank; }
        
        public int rank()
        { return rank; }
    }               
    
    /**
     * The animation that the main menu is showing.
     */
    private IAnimation animation;
    
    /**
     * The wezzle logo.
     */
    final private EntityGroup logoEntity;
    
    /**
     * The logo spinner animation.
     */
    final private IAnimation spinAnimation;
    
    /**
     * A map containing all the buttons in the main menu.
     */
    private EnumMap<Button, IButton> buttonMap;
    
    /**
     * A map containing all the groups in the main menu.
     */
    private EnumMap<Button, IGroup> groupMap;
    
    /**
     * The current button that is activated.
     */
    private Button currentButton = Button.NONE;
    
    /**
     * The animation manager.
     */
    private AnimationManager animationMan;
    
    /**
     * The layer manager.
     */
    private LayerManager layerMan;              
    
    /**
     * Create a new main menu.
     */
    public MainMenu(AnimationManager animationMan)
    {
        // Store the animation manager reference.
        this.animationMan = animationMan;
        
        // Create the layer manager.
        this.layerMan = LayerManager.newInstance();
        
        // Set the main menu as activated.
        this.activated = true;
                
        // Add the background.
        GraphicEntity backgroundGraphic = 
                new GraphicEntity.Builder(0, 0, BACKGROUND_PATH).end();
        layerMan.add(backgroundGraphic, Layer.BACKGROUND);
                
        // Set up the copyright label.               
        ILabel l1 = new LabelBuilder(10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))
                .color(Game.TEXT_COLOR_DISABLED).size(12)                
                .text(Game.COPYRIGHT).end();
        layerMan.add(l1, Layer.UI);
        
        // Set up the version label.	
        ILabel l2 = new LabelBuilder(800 - 10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.RIGHT))
                .color(Game.TEXT_COLOR_DISABLED).size(12)                
                .text(Game.TITLE).end();                        
        layerMan.add(l2, Layer.UI);
        
        // Create the wezzle logo.
        IEntity e1 = new GraphicEntity.Builder(268, 300, WEZZLE_LOGO_STARBURST_PATH)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).end();
        layerMan.add(e1, Layer.BACKGROUND);      
        spinAnimation = new MoveAnimation.Builder(e1).g(0).v(0).omega(0.0001).end();
        animationMan.add(spinAnimation);
        
        IEntity e2 = new GraphicEntity.Builder(268, 300, WEZZLE_LOGO_PATH)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).end();
        layerMan.add(e2, Layer.BACKGROUND);              
        
        logoEntity = new EntityGroup(e1, e2);
               
        // Create the buttons.
        initializeButtons();
        
        // Create the groups.
        initializeGroups();
                
        // Add it to the manager.
        //this.animationMan.add(animateShow());                
        this.animationMan.add(new MetaAnimation.Builder()
                .runRule(MetaAnimation.RunRule.SEQUENCE)
                .add(animateShow())
                //.add(animateHide())
                .end());
    };
    
    private void initializeButtons()
    {                
        // Create the button list.
        this.buttonMap = new EnumMap<Button, IButton>(Button.class);
        
        // The button that will be used as a template for all buttons.
        IButton templateButton = null;
        
        // A temporary button holder.
        IButton button = null;
        
        // Create the buttons.               
        button = new SpriteButton.Builder(910, 153)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .type(SpriteButton.Type.THIN).text("Play Now").textSize(20)
                .hoverOpacity(70).offOpacity(0).disabled(true).end();
        layerMan.add(button, Layer.UI);
        buttonMap.put(Button.PLAY_NOW, button);                
             
        // Make this button the template.
        templateButton = (SpriteButton) button;
        
        button = new SpriteButton.Builder((SpriteButton) templateButton)
                .y(202).text("Tutorial").end();
        layerMan.add(button, Layer.UI);
        buttonMap.put(Button.TUTORIAL, button);                        
        
//        button = new SpriteButton.Builder((SpriteButton) templateButton)
//                .y(251).text("Options").end();
//        layerMan.add(button, Layer.UI);
//        buttonMap.put(Button.OPTIONS, button);
//        
//        button = new SpriteButton.Builder((SpriteButton) templateButton)
//                .y(300).text("Upgrade").end();
//        layerMan.add(button, Layer.UI);
//        buttonMap.put(Button.UPGRADE, button);     
//        
//        button = new SpriteButton.Builder((SpriteButton) templateButton)
//                .y(349).text("Achievements").end();
//        layerMan.add(button, Layer.UI);
//        buttonMap.put(Button.ACHIEVEMENTS, button);
//        
//        button = new SpriteButton.Builder((SpriteButton) templateButton)
//                .y(398).text("High Scores").end();
//        layerMan.add(button, Layer.UI);
//        buttonMap.put(Button.HIGH_SCORES, button);
//                
        button = new SpriteButton.Builder((SpriteButton) templateButton)
                .y(447).text("Exit").end();
        layerMan.add(button, Layer.UI);
        buttonMap.put(Button.EXIT, button);                                   
    };
    
    private void initializeGroups()
    {
        // Create the group map.
        this.groupMap = new EnumMap<Button, IGroup>(Button.class);
        
        // A temporary group holder.
        IGroup group = null;
        
        // Create the None group.
        group = new EmptyGroup();
        group.setActivated(true);
        this.groupMap.put(Button.NONE, group);
        
        // Create the "Play Now" group.
        group = new PlayNowGroup(this, this.layerMan);
        this.groupMap.put(Button.PLAY_NOW, group);
        
        // Create the "Tutorial" group.
        group = new TutorialGroup(this.layerMan);
        this.groupMap.put(Button.TUTORIAL, group);
        
        // Create the "Exit" group.
        group = new ExitGroup(this.layerMan);
        this.groupMap.put(Button.EXIT, group);
    }
    
    public void updateLogic(Game game)
    {       
        switch (state)
        {
            case READY:                                                                                             
                
                // Check for a new animation to load.
                if (this.animation != null)
                {
                    this.animationMan.add(this.animation);
                    this.state = State.ANIMATING;
                    break;
                }
                
                // The button that was clicked.  Null if no button was clicked.
                IButton clickedButton = null;
                
                for (IButton btn : this.buttonMap.values())
                {
                    if (btn.clicked() == true)
                    {
                        clickedButton = btn;
                        break;
                    }
                } // end for
                
                // See if a button was clicked.  If it was, then unclick all
                // the others.
                if (clickedButton != null)
                {
                    for (Button b : this.buttonMap.keySet())
                    {
                        // Make sure their clicked flag is clear.
                        IButton btn = buttonMap.get(b);
                        
                        // Activate the button if it is the clicked button.
                        if (btn.equals(clickedButton) == true)
                        {
                            // Activate the new button.
                            btn.setActivated(true);
                            btn.setDisabled(true);
                            
                            // Animate the change.
                            this.animation = new MetaAnimation.Builder()
                                    .finishRule(MetaAnimation.FinishRule.ALL)
                                    //.runRule(MetaAnimation.RunRule.SEQUENCE)
                                    .add(this.groupMap.get(currentButton).animateHide())
                                    .add(this.groupMap.get(b).animateShow())
                                    .end();
                            this.animationMan.add(this.animation);
                            
                            // Set the new current button.                            
                            this.currentButton = b;
                            
                            // Activate the current group.
                            this.groupMap.get(b).setActivated(true);
                        }
                        else
                        {
                            btn.setActivated(false);
                            btn.setDisabled(false);
                            
                            // Deactivate the other groups.
                            this.groupMap.get(b).setActivated(false);
                        }                        
                    } // end for
                } // end if
                
                // See if the running group deactivated itself.  If it did,
                // then we need to hide the group and deactivate the button.
                // This will bring us back to the initial menu screen with
                // no buttons activated.
                IGroup group = this.groupMap.get(currentButton);
                if (group.isActivated() == false)
                {
                    this.buttonMap.get(currentButton).setActivated(false);
                    this.buttonMap.get(currentButton).setDisabled(false);
                    
                    this.animation = group.animateHide();
                    this.animationMan.add(this.animation);
                    
                    this.currentButton = Button.NONE;
                                       
                    group.resetControls();
                }
                // Otherwise, update the logic.
                else
                {                       
                    group.updateLogic(game);
                    
                    // See if the group deactivated the main menu.
                    if (this.activated == false)
                    {
                        // Create the hide animation.
                        IAnimation a = animateHide();
                        
                        // Attach a runnable setting the state to finished.
                        a.setFinishAction(new Runnable()
                        {
                            public void run()
                            { state = State.FINISHED; }
                        });
                        
                        // Disable the layer manager so nothing can be pressed.
                        this.layerMan.setDisabled(true);
                        
                        // Set the animation.
                        this.animation = a;
                    }
                }
                
                break;         
                
            case ANIMATING:
                
                if (this.animation.isFinished() == true)
                {
                    this.animation = null;
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
                .wait(500).duration(2100).end());
        
        // Animate the buttons coming in.        
        for (Button b : this.buttonMap.keySet())
        {
            if (this.buttonMap.containsKey(b) == false)
                continue;
            
            final IButton btn = this.buttonMap.get(b);
            
            final IAnimation a = new MoveAnimation.Builder(btn).theta(180).v(SLIDE_SPEED)
                    .minX(SLIDE_MIN_X).wait(SLIDE_WAIT * b.rank()).end();
            
            builder.add(a);
            
            a.setFinishAction(new Runnable()
            {
                public void run()
                { btn.setDisabled(false); }
            });
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
        builder.add(this.groupMap.get(this.currentButton).animateHide());
        
        // Fade out the logo.
        IAnimation f = new FadeAnimation.Builder(FadeAnimation.Type.OUT, logoEntity)
                .wait(0).duration(1000).end();
        
        f.setFinishAction(new Runnable()
        {
            public void run()
            { 
                logoEntity.setVisible(false);
                spinAnimation.setFinished();
            }
        });
        
        builder.add(f);
         
        // Slide out the buttons.
         // Animate the buttons coming in.        
        for (Button b : this.buttonMap.keySet())
        {                        
            final IButton btn = this.buttonMap.get(b);
            
            final IAnimation a = new MoveAnimation.Builder(btn).theta(0).v(SLIDE_SPEED)
                    .maxX(910).wait(SLIDE_WAIT * b.rank()).end();
            
            builder.add(a);
            
            a.setStartAction(new Runnable()
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
