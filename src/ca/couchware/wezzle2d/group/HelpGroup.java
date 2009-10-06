/*
 *  Wezzle
 *  Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.group;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.AnimationManager;
import ca.couchware.wezzle2d.manager.GroupManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.Padding;
import ca.couchware.wezzle2d.util.ImmutableRectangle;
import java.util.EnumSet;

/**
 * This group implements the help function, a dialog that shows the
 * user how to play Wezzle.
 *
 * @author cdmckay
 */
public class HelpGroup extends AbstractGroup
{
    private ManagerHub hub;
    private Box groupBox;

    private AnimationManager animationMan;
    private IAnimation animation;

    private Padding lineQuadrantPad;
    private HelpGroupLineQuadrant lineQuadrant;

    private Padding rotateQuadrantPad;
    private HelpGroupRotateQuadrant rotateQuadrant;

    private Padding itemQuadrantPad;
    private HelpGroupItemQuadrant itemQuadrant;

    private Padding timerQuadrantPad;
    private HelpGroupTimerQuadrant timerQuadrant;

    private IButton closeButton;

    public HelpGroup(ManagerHub hub)
    {
        if (hub == null)
            throw new IllegalArgumentException("Hub must not be null");

        this.hub = hub;

        this.animationMan = AnimationManager.newInstance();
       
        this.groupBox = createGroupBox();
        this.entityList.add( this.groupBox );

        this.lineQuadrantPad = Padding.newInstance( 24, 2, 25, 0 );
        this.lineQuadrant = new HelpGroupLineQuadrant(
                hub, entityList, GroupBoxRect, lineQuadrantPad );

        this.rotateQuadrantPad = Padding.newInstance( 2, 24, 25, 0 );
        this.rotateQuadrant = new HelpGroupRotateQuadrant(
                hub, entityList, GroupBoxRect, rotateQuadrantPad );

        this.itemQuadrantPad = Padding.newInstance( 24, 2, 25, 0 );
        this.itemQuadrant = new HelpGroupItemQuadrant(
                hub, entityList, GroupBoxRect, itemQuadrantPad );

        this.timerQuadrantPad = Padding.newInstance( 2, 24, 25, 0 );
        this.timerQuadrant = new HelpGroupTimerQuadrant(
                hub, entityList, GroupBoxRect, timerQuadrantPad );

        // Create close button.
        this.closeButton = new Button.Builder(400, 460)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .text("Close")                
                .visible(false)
                .build();
        
        this.entityList.add(this.closeButton);
        
        for (IEntity entity : entityList)
        {
            entity.setVisible(false);
            hub.layerMan.add(entity, Layer.UI);
        }
    }

    private static final ImmutableRectangle GroupBoxRect =
            new ImmutableRectangle(
                Game.SCREEN_WIDTH / 2 - 300,
                Game.SCREEN_HEIGHT / 2 - 200,
                600, 400);  

    /**
     * Creates the group box that surrounds the help dialog.
     * @return
     */
    private Box createGroupBox()
    {
        Box box = new Box.Builder( 
                    GroupBoxRect.getCenterX(),
                    GroupBoxRect.getCenterY())
                .alignment( EnumSet.of( Alignment.MIDDLE, Alignment.CENTER) )
                .width(  GroupBoxRect.getWidth() )
                .height( GroupBoxRect.getHeight() )
                .opacity( 95 )
                .build();

        return box;
    }

    @Override
    public void setActivated(final boolean activated)
    {
        if (activated)
        {
            hub.musicMan.fadeToGain(0.05);

            this.animation = new MetaAnimation
                    .Builder()
                    .add( this.lineQuadrant.createAnimation() )
                    .add( this.rotateQuadrant.createAnimation() )
                    .add( this.itemQuadrant.createAnimation() )
                    .add( this.timerQuadrant.createAnimation() )
                    .runRule( MetaAnimation.RunRule.SEQUENCE )
                    .finishRule( MetaAnimation.FinishRule.ALL )
                    .build();
             
            this.animationMan.add( this.animation );
        }
        else
        {
            int intGain = hub.settingsMan.getInt(Key.USER_MUSIC_VOLUME);
            double gain = (double) intGain / 100.0;
            hub.musicMan.fadeToGain(gain);

            this.animationMan.remove( this.animation );
            this.animation = null;
            this.lineQuadrant.resetEntities();
            this.rotateQuadrant.resetEntities();
            this.itemQuadrant.resetEntities();
            this.timerQuadrant.resetEntities();
        }

        // Invoke super.
        super.setActivated(activated);
    }

    public void updateLogic(Game game, ManagerHub hub)
    {
        // Sanity check.
        if (game == null)
            throw new IllegalArgumentException("Game must not be null");

        if (hub == null)
            throw new IllegalArgumentException("Hub must not be null");

        this.animationMan.animate();

        // Make sure something changed.
        if ( !this.controlChanged() ) return;

        // Check if the back button was pressed.
        if (closeButton.isActivated())
        {
            // Hide all side triggered menues.
            closeButton.setActivated(false);
            hub.groupMan.hideGroup(
                        GroupManager.Type.OPTIONS,
                        GroupManager.Layer.MIDDLE,
                        !game.isCompletelyBusy());
        }

        // Clear the change setting.
        this.clearChanged();
    }

}
