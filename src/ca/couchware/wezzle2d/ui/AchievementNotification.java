/*
 * Wezzle
 * Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.ui;

import ca.couchware.wezzle2d.util.IBuilder;
import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.graphics.EntityGroup;
import ca.couchware.wezzle2d.manager.Achievement;
import java.util.EnumSet;

/**
 * An class for creating achievements notification UI elements that look kinda
 * like this:
 * 
 * .-------------------------.
 * | Achievement!            |
 * | A Tale of Two Rockets   |
 * | SILVER                  |
 * `-------------------------'
 * 
 * @author cdmckay
 */
public class AchievementNotification extends AbstractNotification
{                   
    
    /** The achievement that we're creating the notification box for. */
    final private Achievement achievement;
       
    /** The notification title. */
    final private ITextLabel title;
    
    /** The achievement title. */
    final private ITextLabel achTitle;
    
    /** The achievement difficulty. */
    final private ITextLabel achDifficulty;
    
    /** The entity group representing all the entities in the notification. */
    final private EntityGroup entityGroup;
    
    private AchievementNotification(Builder builder)
    {
        super(builder.window, builder.x, builder.y,
                builder.opacity, builder.visible, builder.alignment);

        // Save the reference.       
        this.achievement = builder.achievement;                       
        
        // Create the title text.
        this.title = new ResourceFactory.LabelBuilder(
                    this.x + 15 + offsetX, 
                    this.y + 28 + offsetY)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .text("Achievement!").size(21)
                .build();
        
        this.achTitle = new ResourceFactory.LabelBuilder(
                    this.x + 15 + offsetX,
                    this.y + 57 + offsetY)                    
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .text(this.achievement.getTitle()).size(12)
                .build();
        
        this.achDifficulty = new ResourceFactory.LabelBuilder(
                    this.x + 15 + offsetX,
                    this.y + 81 + offsetY)                    
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .text(this.achievement.getDifficulty().toString()).size(12)
                .color(this.achievement.getDifficulty().getColor())
                .build();
        
        this.entityGroup = new EntityGroup(this.title, this.achTitle, this.achDifficulty);
        this.entityList.add(this.entityGroup);
    }
    
    public static class Builder implements IBuilder<AchievementNotification>
    {
        // Required values.  
        private final IWindow window;
        private final Achievement achievement;
        private int x;
        private int y;     
        
        // Optional values.
        private EnumSet<Alignment> alignment = EnumSet.of(Alignment.TOP, Alignment.LEFT);              
        private int opacity = 100;        
        private boolean visible = true;        
        
        public Builder(int x, int y, Achievement achievement)
        {            
            this.window = ResourceFactory.get().getWindow();
            this.achievement = achievement;
            this.x = x;
            this.y = y;
        }
        
        public Builder(AchievementNotification notif)
        {            
            this.window = notif.window;
            this.achievement = notif.achievement;
            this.x = notif.x;
            this.y = notif.y;
            this.alignment = notif.alignment.clone();                 
            this.opacity = notif.opacity;                                   
            this.visible = notif.visible;            
        }
        
        public Builder x(int val) { x = val; return this; }        
        public Builder y(int val) { y = val; return this; }
               
        public Builder alignment(EnumSet<Alignment> val) 
        { alignment = val; return this; }
                        
        public Builder opacity(int val)
        { opacity = val; return this; }               
               
        public Builder visible(boolean val) 
        { visible = val; return this; }
                
        public AchievementNotification build()
        {
            AchievementNotification notif = new AchievementNotification(this);                      
            return notif;
        }                
    }     

}
