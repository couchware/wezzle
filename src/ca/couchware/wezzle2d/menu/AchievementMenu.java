/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.difficulty.GameDifficulty;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.Achievement;
import ca.couchware.wezzle2d.manager.Achievement.Level;
import ca.couchware.wezzle2d.manager.AchievementManager;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.Padding;
import ca.couchware.wezzle2d.ui.Scroller;
import ca.couchware.wezzle2d.util.CouchDate;
import ca.couchware.wezzle2d.util.CouchLogger;
import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;

/**
 * The play now group, which holds all the configuration options for playing
 * a Wezzle game.
 * 
 * @author cdmckay
 */
public class AchievementMenu extends AbstractMenu
{       
    
    /** The data formatter used for setting the status. */
    final private static DateFormat dateFormatter = 
            new SimpleDateFormat("MMM dd yyyy");        
    
    /** The colour of completed achievements. */
    final private Color COLOR_COMPLETED;    
    
    /** The colour of not completed achievements. */
    final private Color COLOR_NOT_COMPLETED;
   
    /** The slider bar used to scroll the achievements. */
    private Scroller scroller;
        
    /** The list of achievements. */
    private List<Achievement> achievementList;    
    
    /** The label for the title of the achievement. */
    private ITextLabel achievementTitle;
    
    /** The level of the achievement. */
    private ITextLabel achievementLevel;
    
    /** The array of labels for the description. */
    private ITextLabel[] achievementDescriptionArray;

    /** The label for the difficulty of the achievement. */
    private ITextLabel achievementStatus;

    /** The label for the required difficulty of the achievement. */
    private ITextLabel achievementDifficulty;
           
    public AchievementMenu(IMenu parent, ManagerHub hub, LayerManager menuLayerMan)
    {
        // Invoke the super.
        super(parent, hub, menuLayerMan);

        // Make convenience variables for the managers used.
        final AchievementManager achievementMan = hub.achievementMan;
        final LayerManager layerMan = this.menuLayerMan;
        final SettingsManager settingsMan = hub.settingsMan;

        // Set the completed/not completed colors.
        this.COLOR_COMPLETED = settingsMan.getColor(Key.GAME_COLOR_SECONDARY);
        this.COLOR_NOT_COMPLETED = settingsMan.getColor(Key.GAME_COLOR_PRIMARY);
        
        // The label color.
        final Color LABEL_COLOR  = settingsMan.getColor(Key.GAME_COLOR_PRIMARY);                
        
        // Set the achievement list.
        achievementList = achievementMan.getAchievementList();                                                                                                 
             
        final int numberOfCompletedAchievements = achievementMan.getNumberOfCompletedAchievements();
        final int numberOfAchievements = achievementMan.getNumberOfAchievements();
        
        // The title label.
        ITextLabel titleLabel = new LabelBuilder(74, 97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR)
                .text(String.format("Achievements (%d/%d)", 
                        numberOfCompletedAchievements, numberOfAchievements))
                .size(20)                
                .visible(false).build();
        this.entityList.add(titleLabel);
        
        // The first box.
        Box listBox = new Box.Builder(68, 122)
                .width(400).height(214)
                .border(Box.Border.MEDIUM)
                .opacity(80)
                .visible(false)
                .build();
        this.entityList.add(listBox);
        
        // Create the list of titles for the first 5 achievements.
        Scroller.Builder builder = new Scroller.Builder(68, 229)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .padding(Padding.newInstance(12, 30, 12, 12))                
                .rows(4)
                .visible(false);
        for (Achievement ach : achievementList)
        {
            if (ach.getDateCompleted() != null)
               builder.add("* " + ach.getTitle());
            else
                builder.add(ach.getTitle());
        }         
        scroller = builder.selectedIndex(0).build();
        entityList.add(scroller);
        
        for (int i = 0; i < achievementList.size(); i++)
        {
            Achievement ach = achievementList.get(i);
            scroller.setColor(i, getColor(ach.getLevel().toString(), settingsMan));
        }
        
        // The first box.
        Box descriptionBox = new Box.Builder(68, 346)
                .width(400).height(174)
                .border(Box.Border.MEDIUM)
                .opacity(80)
                .visible(false)
                .build();
        this.entityList.add(descriptionBox);
        
        // The achievement description text.
        this.achievementTitle = new ResourceFactory.LabelBuilder(96, 381)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .visible(false)
                .text("Chain Gang I - Bronze").size(20)
                .build();
        this.entityList.add(this.achievementTitle);
        
        // The achievement description text.
        this.achievementLevel = new ResourceFactory.LabelBuilder(440, 381)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.RIGHT))
                .visible(false)
                .text("BRONZE").size(12)
                .build();
        this.entityList.add(this.achievementLevel);
               
        this.achievementDescriptionArray = new ITextLabel[3];
        
        for (int i = 0; i < achievementDescriptionArray.length; i++)
        {
            achievementDescriptionArray[i] = new ResourceFactory.LabelBuilder(0, 0)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .visible(false)
                .text("").size(12)
                .build();
            this.entityList.add(achievementDescriptionArray[i]);
        }
        
        achievementDescriptionArray[0].setPosition(96, 410);
        achievementDescriptionArray[1].setPosition(96, 430);
        achievementDescriptionArray[2].setPosition(96, 450);          

        this.achievementDifficulty = new ResourceFactory.LabelBuilder(96, 471)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .visible(false)
                .text("Achievement requires ??? difficulty.").size(12)
                .build();
        this.entityList.add(this.achievementDifficulty);

        this.achievementStatus = new ResourceFactory.LabelBuilder(96, 491)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .visible(false)
                .text("Achievement has not been completed.").size(12)
                .build();
        this.entityList.add(this.achievementStatus);
        
        // Show the first achievement in the list.
        updateAchievementText(achievementList.get(0));
        
        // Add them all to the layer manager.
        for (IEntity e : this.entityList)
        {
            layerMan.add(e, Layer.UI);        
        }                    
    }               
        
    final private void updateAchievementText(Achievement ach)
    {
        // Set the title.
        achievementTitle.setText(ach.getTitle());
        
        // Set the difficulty.
        Achievement.Level level = ach.getLevel();
        achievementLevel.setText(level.toString());
        achievementLevel.setColor(level.getColor());
        
        // Split description up.
        String[] lineArray;
        if (ach.getDescription().contains("\n"))
        {
            lineArray = ach.getDescription().split("\n");
        }
        else
        {
            lineArray = new String[] { ach.getDescription() };
        }   
        
        // Set the description.        
        for (int i = 0; i < achievementDescriptionArray.length; i++)
        {
            if (i < lineArray.length)
                achievementDescriptionArray[i].setText(lineArray[i]);
            else
                achievementDescriptionArray[i].setText("");
        }

        // Set the difficuly requirements.
        GameDifficulty difficulty = ach.getDifficulty();
        if (difficulty != GameDifficulty.NONE)
        {
            String text = String.format(
                    "Achievement requires %s difficulty mode.",
                    difficulty.toString());
            achievementDifficulty.setText( text );
        }
        else
        {
            achievementDifficulty.setText( "" );
        }

        // Set the status.
        CouchDate date = ach.getDateCompleted();
        if (date == null)
        {
            achievementStatus.setText("Achievement has not been completed.");
        }
        else
        {            
            achievementStatus.setText("Achievement was completed " + dateFormatter.format(date.getTime()) + ".");
        }
    }
    
    public void updateLogic(Game game, ManagerHub hub)
    {    
        if (scroller.changed() == true)
        {
            //LogManager.recordMessage("Scroller is at " + scroller.getSelectedIndex());            
            Achievement ach = achievementList.get(scroller.getSelectedIndex());
            updateAchievementText(ach);
        }
    }


    private Color getColor(String col, SettingsManager settingsMan){
        if(col.equals("BRONZE"))
            return settingsMan.getColor(Key.ACHIEVEMENT_COLOR_BRONZE);
        else if (col.equals("SILVER"))
            return settingsMan.getColor(Key.ACHIEVEMENT_COLOR_SILVER);
        else if (col.equals("GOLD"))
            return settingsMan.getColor(Key.ACHIEVEMENT_COLOR_GOLD);
        else if (col.equals("PLATINUM"))
            return settingsMan.getColor(Key.ACHIEVEMENT_COLOR_PLATINUM);

        CouchLogger.get().recordMessage(this.getClass(), "Colour undefined");
        return null;
    }
}
