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
    private Game game;
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

//    private IButton closeButton;

    public HelpGroup(Game game, ManagerHub hub)
    {
        if (game == null)
            throw new IllegalArgumentException("Game must not be null");

        if (hub == null)
            throw new IllegalArgumentException("Hub must not be null");

        this.game = game;
        this.hub = hub;

        this.animationMan = AnimationManager.newInstance();             

        int centerX = Game.SCREEN_WIDTH  / 2;
        int centerY = Game.SCREEN_HEIGHT / 2;
        int w = 280;
        int h = 150;
        int spacing = 10;

        this.lineQuadrantPad = Padding.newInstance( 30 );
        ImmutableRectangle lineRect = new ImmutableRectangle(
                centerX - w / 2, 
                centerY - h / 2 - spacing - h,
                w, h);

        this.lineQuadrant = new HelpGroupLineQuadrant(
                hub, entityList, lineRect, lineQuadrantPad );

        this.rotateQuadrantPad = Padding.newInstance( 30 );
        ImmutableRectangle rotateRect = new ImmutableRectangle(
                centerX - w / 2,
                centerY - h / 2,
                w, h);

        this.rotateQuadrant = new HelpGroupRotateQuadrant(
                hub, entityList, rotateRect, rotateQuadrantPad );

        this.itemQuadrantPad = Padding.newInstance( 30 );
        ImmutableRectangle itemRect = new ImmutableRectangle(
                centerX - w / 2,
                centerY - h / 2 + h + spacing,
                w, h);

        this.itemQuadrant = new HelpGroupItemQuadrant(
                hub, entityList, itemRect, itemQuadrantPad );

        // Create close button.
//        this.closeButton = new Button.Builder(400, 460)
//                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
//                .text("Close")
//                .visible(false)
//                .build();
        
//        this.entityList.add(this.closeButton);
        
        for (IEntity entity : entityList)
        {
            entity.setVisible(false);
            hub.layerMan.add(entity, Layer.HELP);
        }
    }   

    @Override
    public void setActivated(final boolean activated)
    {
        if (activated)
        {
            hub.musicMan.fadeToGain(0.05);
            //hub.layerMan.hide( Layer.UI );
            //hub.layerMan.hide( Layer.BOARD );
            game.getUI().hideBarsUsingFade();
            game.getUI().hideBoardUsingFade();
            //game.getUI().hideTraditionalPieceBoxUsingFade();

            this.animation = new MetaAnimation
                    .Builder()
                    .add( this.lineQuadrant.createAnimation() )
                    .add( this.rotateQuadrant.createAnimation() )
                    .add( this.itemQuadrant.createAnimation() )         
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
            //hub.layerMan.show( Layer.UI );
            //hub.layerMan.show( Layer.BOARD );
            game.getUI().showBarsUsingFade();
            game.getUI().showBoardUsingFade();
            //game.getUI().showTraditionalPieceBoxUsingFade();

            this.animationMan.remove( this.animation );
            this.animation = null;
            this.lineQuadrant.resetEntities();
            this.rotateQuadrant.resetEntities();
            this.itemQuadrant.resetEntities();
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
//        if (closeButton.isActivated())
//        {
//            // Hide all side triggered menues.
//            closeButton.setActivated(false);
//            hub.groupMan.hideGroup(
//                        GroupManager.Type.OPTIONS,
//                        GroupManager.Layer.MIDDLE,
//                        !game.isCompletelyBusy());
//        }

        // Clear the change setting.
        this.clearChanged();
    }

}
