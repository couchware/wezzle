/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.Rule;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.graphics.AbstractEntity;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.Achievement;
import ca.couchware.wezzle2d.manager.LogManager;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.SliderBar;
import ca.couchware.wezzle2d.ui.group.AbstractGroup;
import ca.couchware.wezzle2d.ui.group.IGroup;
import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * The play now group, which holds all the configuration options for playing
 * a Wezzle game.
 * 
 * @author cdmckay
 */
public class AchievementGroup extends AbstractGroup
{        
    
    /** The x-position of the labels in the scroller. */
    final private static int X_POSITION = 90;
    
    /** The y-position of the top label in the scoller. */
    final private static int Y_POSITION = 156;
    
    /** The amount of space between each label in the scoller. */
    final private static int Y_SPACING = 37;
    
    /** The number of achievements showing at once in the scroller. */
    final private static int ACHIEVEMENTS_AT_ONCE = 5;   
    
    /** The layer manager. */
    final private LayerManager layerMan;
    
    /** The background window. */
    private Box box;
    
    /** The slider bar used to scroll the achievements. */
    private SliderBar sliderBar;
    
    /** The offset in the achievement list. */
    private int offset = 0;
    
    /** The list of achievements. */
    private List<Achievement> achievementList;        
    
    public AchievementGroup(IGroup parent, 
            final SettingsManager settingsMan,
            final LayerManager layerMan)            
    {
        // Invoke the super.
        super(parent);
               
        // Set the managers.
        this.layerMan = layerMan;
        
        // Fill the achievemnt with some dummy achievements.
        List<Rule> ruleList = new ArrayList<Rule>();
        ruleList.add(new Rule(Rule.Type.LEVEL, Rule.Operation.GT, 1));
        
        achievementList = new ArrayList<Achievement>();
        achievementList.add(Achievement.newInstance(ruleList, "Level Buster I", 
                "Dummy description.", Achievement.Difficulty.BRONZE, null));
        
        achievementList.add(Achievement.newInstance(ruleList, "Chain Gang", 
                "Dummy description.", Achievement.Difficulty.BRONZE, null));
        
        achievementList.add(Achievement.newInstance(ruleList, "Scoring Wizard", 
                "Dummy description.", Achievement.Difficulty.BRONZE, null));
        
        achievementList.add(Achievement.newInstance(ruleList, "Big Scorer", 
                "Dummy description.", Achievement.Difficulty.BRONZE, null));
        
        achievementList.add(Achievement.newInstance(ruleList, "Line Driver", 
                "Dummy description.", Achievement.Difficulty.BRONZE, null));
        
        achievementList.add(Achievement.newInstance(ruleList, "Level Buster II", 
                "Dummy description.", Achievement.Difficulty.BRONZE, null));
        
        achievementList.add(Achievement.newInstance(ruleList, "Level Buster III", 
                "Dummy description.", Achievement.Difficulty.BRONZE, null));
                        
//        // Create the list of titles for the first 5 achievements.
//        titleList = new ArrayList<ITextLabel>();
//        for (Achievement ach : achievementList)
//        {            
//            titleList.add(new ResourceFactory.LabelBuilder(0, 0)
//                    .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
//                    .size(18)
//                    .text(ach.getTitle())      
//                    .visible(false)
//                    .end());       
//        }               
                
        // The colors.
        final Color LABEL_COLOR  = settingsMan.getColor(Key.GAME_COLOR_PRIMARY);        
        final Color OPTION_COLOR = settingsMan.getColor(Key.GAME_COLOR_SECONDARY);
        
        // Create the window.
        box = new Box.Builder(268, 300).width(430).height(470)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .opacity(SettingsManager.get().getInt(Key.MAIN_MENU_WINDOW_OPACITY))
                .visible(false).end();
        this.layerMan.add(box, Layer.UI);                         
             
        // The title label.
        ITextLabel titleLabel = new LabelBuilder(74, 97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR).text("Achievements").size(20)                
                .visible(false).end();
        this.entityList.add(titleLabel);
        
        // The first box.
        Box listBox = new Box.Builder(68, 122)
                .width(400).height(214)
                .border(Box.Border.MEDIUM)
                .opacity(80)
                .visible(false)
                .end();
        this.entityList.add(listBox);  
        
//        this.sliderBar = new SliderBar.Builder(442, 229)
//                .height(170)
//                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
//                .orientation(SliderBar.Orientation.VERTICAL)
//                .virtualRange(0, Math.max(0, titleList.size() - ACHIEVEMENTS_AT_ONCE))
//                .virtualValue(0)
//                .visible(false)
//                .end();
//        entityList.add(sliderBar);
        
        // The first box.
        Box descriptionBox = new Box.Builder(68, 346)
                .width(400).height(174)
                .border(Box.Border.MEDIUM)
                .opacity(80)
                .visible(false)
                .end();
        this.entityList.add(descriptionBox);  
        
        // Add them all to the layer manager.
        for (IEntity e : this.entityList)
        {
            this.layerMan.add(e, Layer.UI);        
        }
        
        // Adjust the scroller.
        adjustScroller(false);        
    }
    
    /**
     * Moves the scroller into the proper position.
     */
    private void adjustScroller(boolean visible)
    {
//        // Make sure the offset isn't too high.
//        assert offset <= titleList.size() - ACHIEVEMENTS_AT_ONCE;
//            
//        // Make all labels invisible.
//        for (ITextLabel label : titleList)
//        {
//            entityList.remove(label);                                  
//        }
//        
//        // Move the labels into the right position.
//        for (int i = 0; i < ACHIEVEMENTS_AT_ONCE; i++)
//        {
//            ITextLabel label = titleList.get(i + offset);
//            label.setX(X_POSITION);
//            label.setY(Y_POSITION + Y_SPACING * i);
//            label.setVisible(visible);
//            
//            entityList.add(label);           
//        }
    }
    
    @Override
    public IAnimation animateShow()
    {       
        box.setPosition(268, -300);
        box.setVisible(true);        
        
        IAnimation a = new MoveAnimation.Builder(box).theta(-90).maxY(300)
                .speed(SettingsManager.get().getInt(Key.MAIN_MENU_WINDOW_SPEED))
                .end();   
        
        a.setFinishRunnable(new Runnable()
        {
           public void run()
           { setVisible(true); }
        });
        
        return a;
    }
    
    @Override
    public IAnimation animateHide()
    {        
        IAnimation a = new MoveAnimation.Builder(box).theta(-90)
                .maxY(Game.SCREEN_HEIGHT + 300)
                .speed(SettingsManager.get().getInt(Key.MAIN_MENU_WINDOW_SPEED))
                .end();
        
        a.setStartRunnable(new Runnable()
        {
           public void run()
           { setVisible(false); }
        });
        
        return a;
    }
        
    public void updateLogic(Game game)
    {    
//        if (sliderBar.changed() == true)
//        {
//            LogManager.recordMessage("Slider bar is at " + sliderBar.getVirtualValue());
//            int newOffset = (int) sliderBar.getVirtualValue();
//            
//            if (newOffset != offset)
//            {
//                this.offset = newOffset;
//                adjustScroller(true);
//            }
//        }
    }
    
}
