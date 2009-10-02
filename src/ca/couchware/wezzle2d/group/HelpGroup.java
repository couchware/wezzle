/*
 *  Wezzle
 *  Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.group;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.animation.AnimationAdapter;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.animation.FadeAnimation.Type;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.animation.ZoomAnimation;
import ca.couchware.wezzle2d.graphics.EntityGroup;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.AnimationManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.piece.PieceDot;
import ca.couchware.wezzle2d.piece.PieceGrid;
import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.tile.TileColor;
import ca.couchware.wezzle2d.tile.TileHelper;
import ca.couchware.wezzle2d.tile.TileType;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.Padding;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import ca.couchware.wezzle2d.util.ImmutableRectangle;
import java.awt.Color;
import java.util.EnumSet;
import java.util.List;

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

    public HelpGroup(ManagerHub hub)
    {
        if (hub == null)
            throw new IllegalArgumentException("Hub must not be null");

        this.hub = hub;

        this.animationMan = AnimationManager.newInstance();
       
        this.groupBox = createGroupBox();
        this.entityList.add( this.groupBox );

        this.lineQuadrantPad = Padding.newInstance( 40, 20 );
        this.lineQuadrant = new HelpGroupLineQuadrant(
                hub, entityList, GroupBoxRect, lineQuadrantPad );

        this.rotateQuadrantPad = Padding.newInstance( 20, 40 );
        this.rotateQuadrant = new HelpGroupRotateQuadrant(
                hub, entityList, GroupBoxRect, rotateQuadrantPad );
        
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
                .width(  GroupBoxRect.getWidth()  )
                .height( GroupBoxRect.getHeight() )                
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
        }

        // Invoke super.
        super.setActivated(activated);
    }

    public void updateLogic(Game game, ManagerHub hub)
    {
        this.animationMan.animate();
    }

}
