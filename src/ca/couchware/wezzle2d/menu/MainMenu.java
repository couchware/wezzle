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
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.ui.SpriteButton;
import ca.couchware.wezzle2d.ui.group.IGroup;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

/**
 *
 * @author cdmckay
 */
public class MainMenu 
{
    
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
     * An enum containing the possible states for the loader to be in.
     */
    public enum State
    {                       
        /** 
         * The main menu has been initialized and is ready to go.
         */
        READY,                
              
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
        NONE,
        PLAY_NOW,
        TUTORIAL,
        OPTIONS,
        UPGRADE,
        ACHIEVEMENTS,
        HIGH_SCORES,
        EXIT
    }        
    
    /**
     * The animation that the main menu is showing.
     */
    private IAnimation animation;
    
    /**
     * The wezzle logo.
     */
    final private GraphicEntity wezzleLogoGraphic;
    
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
                
        // Add the background.
        GraphicEntity backgroundGraphic = 
                new GraphicEntity.Builder(0, 0, BACKGROUND_PATH).end();
        layerMan.add(backgroundGraphic, Layer.BACKGROUND);
                
        // Set up the copyright label.               
        ILabel l1 = new LabelBuilder(10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.LEFT))
                .color(Game.TEXT_COLOR2).size(12)                
                .text(Game.COPYRIGHT).end();
        layerMan.add(l1, Layer.UI);
        
        // Set up the version label.	
        ILabel l2 = new LabelBuilder(800 - 10, 600 - 10)
                .alignment(EnumSet.of(Alignment.BOTTOM, Alignment.RIGHT))
                .color(Game.TEXT_COLOR2).size(12)                
                .text(Game.TITLE).end();                        
        layerMan.add(l2, Layer.UI);
        
        // Create the wezzle logo.
        wezzleLogoGraphic = new GraphicEntity.Builder(268, 300, WEZZLE_LOGO_PATH)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER)).end();
        layerMan.add(wezzleLogoGraphic, Layer.BACKGROUND);
        
        // Create the animation that fades in the wezzle logo.
        IAnimation a1 = new FadeAnimation.Builder(FadeAnimation.Type.IN, wezzleLogoGraphic)
                .wait(500).duration(2100).end();
        
        // Create the animation the shoots in the buttons.
        IAnimation a2 = initializeButtons();
        
        // Create the groups.
        initializeGroups();
        
        // Set the animation.
        this.animation = new MetaAnimation.Builder()
                .finishRule(MetaAnimation.FinishRule.ALL)
                .add(a1)
                .add(a2)                
                .end();   
        
        // Add it to the manager.
        this.animationMan.add(this.animation);                
    };
    
    private IAnimation initializeButtons()
    {
        // The speed the buttons come in.
        final double speed = 0.8;
        
        // The final X position the buttons go to.
        final int minX = 630;
        
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
        
        IAnimation a1 = new MoveAnimation.Builder(button)
                .theta(180).v(speed).minX(minX).end();      
        
        a1.setFinishAction(new Runnable()
        {
            public void run()
            { buttonMap.get(Button.PLAY_NOW).setDisabled(false); }
        });
        
        // Make this button the template.
        templateButton = (SpriteButton) button;
        
        button = new SpriteButton.Builder((SpriteButton) templateButton)
                .y(202).text("Tutorial").end();
        layerMan.add(button, Layer.UI);
        buttonMap.put(Button.TUTORIAL, button);
        
        IAnimation a2 = new MoveAnimation.Builder(button).wait(300)
                .theta(180).v(speed).minX(minX).end();  
        
        a2.setFinishAction(new Runnable()
        {
            public void run()
            { buttonMap.get(Button.TUTORIAL).setDisabled(false); }
        });
        
        button = new SpriteButton.Builder((SpriteButton) templateButton)
                .y(251).text("Options").end();
        layerMan.add(button, Layer.UI);
        buttonMap.put(Button.OPTIONS, button);
        
        IAnimation a3 = new MoveAnimation.Builder(button).wait(600)
                .theta(180).v(speed).minX(minX).end();
        
        a3.setFinishAction(new Runnable()
        {
            public void run()
            { buttonMap.get(Button.OPTIONS).setDisabled(false); }
        });
        
        button = new SpriteButton.Builder((SpriteButton) templateButton)
                .y(300).text("Upgrade").end();
        layerMan.add(button, Layer.UI);
        buttonMap.put(Button.UPGRADE, button);
        
        IAnimation a4 = new MoveAnimation.Builder(button).wait(900)
                .theta(180).v(speed).minX(minX).end();
        
        a4.setFinishAction(new Runnable()
        {
            public void run()
            { buttonMap.get(Button.UPGRADE).setDisabled(false); }
        });
        
        button = new SpriteButton.Builder((SpriteButton) templateButton)
                .y(349).text("Achievements").end();
        layerMan.add(button, Layer.UI);
        buttonMap.put(Button.ACHIEVEMENTS, button);
        
        IAnimation a5 = new MoveAnimation.Builder(button).wait(1200)
                .theta(180).v(speed).minX(minX).end();
        
        a5.setFinishAction(new Runnable()
        {
            public void run()
            { buttonMap.get(Button.ACHIEVEMENTS).setDisabled(false); }
        });
        
        button = new SpriteButton.Builder((SpriteButton) templateButton)
                .y(398).text("High Scores").end();
        layerMan.add(button, Layer.UI);
        buttonMap.put(Button.HIGH_SCORES, button);
        
        IAnimation a6 = new MoveAnimation.Builder(button).wait(1500)
                .theta(180).v(speed).minX(minX).end();
        
        a6.setFinishAction(new Runnable()
        {
            public void run()
            { buttonMap.get(Button.HIGH_SCORES).setDisabled(false); }
        });
        
        button = new SpriteButton.Builder((SpriteButton) templateButton)
                .y(447).text("Exit").end();
        layerMan.add(button, Layer.UI);
        buttonMap.put(Button.EXIT, button);
        
        IAnimation a7 = new MoveAnimation.Builder(button).wait(1800)
                .theta(180).v(speed).minX(minX).end();
        
        a7.setFinishAction(new Runnable()
        {
            public void run()
            { buttonMap.get(Button.EXIT).setDisabled(false); }
        });       
                        
        return new MetaAnimation.Builder()
                .finishRule(MetaAnimation.FinishRule.ALL)                
                .add(a1)
                .add(a2)
                .add(a3)
                .add(a4)
                .add(a5)
                .add(a6)
                .add(a7)
                .end();
    };
    
    private void initializeGroups()
    {
        // Create the group map.
        this.groupMap = new EnumMap<Button, IGroup>(Button.class);
        
        // A temporary group holder.
        IGroup group = null;
        
        // Create the None group.
        group = new NoneGroup(this.layerMan);
        this.groupMap.put(Button.NONE, group);
        
        // Create the "Play Now" group.
        group = new PlayNowGroup(this.layerMan);
        this.groupMap.put(Button.PLAY_NOW, group);
        
        // Create the "Tutorial" group.
        group = new TutorialGroup(this.layerMan);
        this.groupMap.put(Button.TUTORIAL, group);
    }
    
    public State updateLogic(Game game)
    {       
        switch (state)
        {
            case READY:                                                                                             
                
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
                        }
                        else
                        {
                            btn.setActivated(false);
                            btn.setDisabled(false);
                        }                        
                    } // end for
                } // end if
                
                break;                            
                
            case FINISHED:
                
                // Do nothing, we're done.
                
                break;
                
            default: throw new AssertionError();
        }               
        
        // Return the state.
        return state;
    }   
    
    public boolean draw()
    {
        return layerMan.draw(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
    }
    
    public void forceRedraw()
    {
        layerMan.forceRedraw();
    }
    
}
