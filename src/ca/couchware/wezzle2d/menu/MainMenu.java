/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu2;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.LayerManager;
import ca.couchware.wezzle2d.LayerManager.Layer;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation;
import ca.couchware.wezzle2d.animation.ZoomAnimation;
import ca.couchware.wezzle2d.graphics.GraphicEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ILabel;
import ca.couchware.wezzle2d.ui.SpriteButton;
import java.util.EnumSet;

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
         * The main menu is waiting for the user to do something.
         */
        WAITING,
        
        /**
         * The main menu is animating.
         */
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
     * The animation that the main menu runs when it is first shown.
     */
    private final IAnimation showAnimation;
    
    /**
     * The animation that the main menu runs when it is hidden.
     */
    //private final IAnimation hideAnimation;
    
    /**
     * The animation that the main menu is showing.
     */
    private IAnimation animation;
    
    /**
     * The wezzle logo.
     */
    final private GraphicEntity wezzleLogoGraphic;
    
    /**
     * The play now button.
     */
    final private IButton playNowButton;
    
    /**
     * The tutorial button.
     */
    final private IButton tutorialButton;
    
    /**
     * The options button.
     */
    final private IButton optionsButton;
    
//    /**
//     * The upgrade button.
//     */
//    final private IButton upgradeButton;
//    
//    /**
//     * The achievements button.
//     */
//    final private IButton achievementsButton;
//    
//    /**
//     * The high scores button.
//     */
//    final private IButton highScoresButton;
//    
//    /**
//     * The exit button.
//     */
//    final private IButton exitButton;
    
    /**
     * The layer manager.
     */
    final private LayerManager layerMan;
    
    /**
     * Create a new main menu.
     */
    public MainMenu()
    {
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
        
        // Create the show animation.
        IAnimation a = new FadeAnimation.Builder(FadeAnimation.Type.IN, wezzleLogoGraphic)
                .wait(500).duration(1500).end();
        
        // Create the buttons.
        playNowButton = new SpriteButton.Builder(620, 191)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .type(SpriteButton.Type.THIN).text("Play Now").textSize(20)
                .hoverOpacity(70).offOpacity(0).end();
        layerMan.add(playNowButton, Layer.UI);
        
        tutorialButton = new SpriteButton.Builder((SpriteButton) playNowButton)
                .y(231).text("Tutorial").end();
        layerMan.add(tutorialButton, Layer.UI);
        
        optionsButton = new SpriteButton.Builder((SpriteButton) playNowButton)
                .y(271).text("Options").end();
        layerMan.add(optionsButton, Layer.UI);
                        
        this.showAnimation = a; //new MetaAnimation.Builder().add(a).end();
    }
    
    public State updateLogic(Game game)
    {       
        switch (state)
        {
            case READY:                              
                
                if (showAnimation.isFinished() == false)
                {
                    game.animationMan.add(showAnimation);
                    state = State.ANIMATING;
                }
                
                break;
                
            case ANIMATING:                 
                
                if (showAnimation.isFinished() == true)
                    state = State.READY;
                
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
