/*
 *  Wezzle
 *  Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.group;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.graphics.EntityGroup;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.AnimationManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.Box;
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

    private Padding linePad;
    private Box lineBox;
    private HelpGroupLineLesson lineLesson;

    private Padding rotatePad;
    private Box rotateBox;
    private HelpGroupRotateLesson rotateLesson;

    private Padding itemPad;
    private Box itemBox;
    private HelpGroupItemLesson itemLesson;

    private int originalX;

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

        createLineLessonEntities(centerX, centerY, w, h, spacing);
        createRotateLessonEntities(centerX, centerY, w, h, spacing);
        createItemLessonEntities(centerX, centerY, w, h, spacing);

        originalX = centerX - w / 2;

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
            game.getUI().hideBarsUsingFade();
            game.getUI().hideBoardUsingFade();
            //game.getUI().hideTraditionalPieceBoxUsingFade();

            this.animationMan.remove( this.animation );
            this.animation = new MetaAnimation
                    .Builder()
                    .add( createSlideFadeIn() )
                    .add( this.lineLesson.createAnimation() )
                    .add( this.rotateLesson.createAnimation() )
                    .add( this.itemLesson.createAnimation() )
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
            game.getUI().showBarsUsingFade();
            game.getUI().showBoardUsingFade();
            //game.getUI().showTraditionalPieceBoxUsingFade();

            this.animationMan.remove( this.animation );            
            this.animation = createSlideFadeOut();            
            game.getUI().getUIAnimationManager().add( this.animation );

            this.lineLesson.resetEntities();
            this.rotateLesson.resetEntities();
            this.itemLesson.resetEntities();
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
    }

    private void createLineLessonEntities(
            int centerX, int centerY, int w, int h, int spacing)
    {        
        ImmutableRectangle lineRect = new ImmutableRectangle(
                centerX - w / 2,
                centerY - h / 2 - spacing - h,
                w, h );
        
        this.lineBox = new Box.Builder(
                        lineRect.getX(),
                        lineRect.getY() )
                    .width( lineRect.getWidth() )
                    .height( lineRect.getHeight() )
                    .alignment( EnumSet.of( Alignment.TOP, Alignment.LEFT ) )
                    .border( Box.Border.MEDIUM )
                    .opacity( 90 )
                    .build();

        entityList.add( this.lineBox );

        this.linePad = Padding.newInstance( 30 );
        this.lineLesson = new HelpGroupLineLesson( hub, entityList, lineRect, linePad );      
    }

    private void createRotateLessonEntities(
            int centerX, int centerY, int w, int h, int spacing)
    {
        ImmutableRectangle rotateRect = new ImmutableRectangle(
                centerX - w / 2,
                centerY - h / 2,
                w, h );

        this.rotateBox = new Box.Builder(
                        rotateRect.getX(),
                        rotateRect.getY() )
                    .width( rotateRect.getWidth() )
                    .height( rotateRect.getHeight() )
                    .alignment( EnumSet.of( Alignment.TOP, Alignment.LEFT ) )
                    .border( Box.Border.MEDIUM )
                    .opacity( 90 )
                    .build();

        entityList.add( this.rotateBox );

        this.rotatePad = Padding.newInstance( 30 );
        this.rotateLesson = new HelpGroupRotateLesson( hub, entityList, rotateRect, rotatePad );
    }

    private void createItemLessonEntities(
            int centerX, int centerY, int w, int h, int spacing)
    {
        ImmutableRectangle itemRect = new ImmutableRectangle(
                centerX - w / 2,
                centerY - h / 2 + h + spacing,
                w, h );

        this.itemBox = new Box.Builder(
                        itemRect.getX(),
                        itemRect.getY() )
                    .width( itemRect.getWidth() )
                    .height( itemRect.getHeight() )
                    .alignment( EnumSet.of( Alignment.TOP, Alignment.LEFT ) )
                    .border( Box.Border.MEDIUM )
                    .opacity( 90 )
                    .build();

        entityList.add( this.itemBox );

        this.itemPad = Padding.newInstance( 30 );
        this.itemLesson = new HelpGroupItemLesson( hub, entityList, itemRect, linePad );
    }

    private IAnimation createSlideFadeIn()
    {
        EntityGroup boxes = new EntityGroup( this.lineBox, this.rotateBox, this.itemBox );
        boxes.setOpacity( 0 );

        IAnimation fadeIn = new FadeAnimation
                .Builder( FadeAnimation.Type.IN, boxes )
                .duration( 500 )
                .maxOpacity( 90 )
                .build();       

        EntityGroup rightBoxes = new EntityGroup( this.lineBox, this.itemBox );
        EntityGroup leftBoxes  = new EntityGroup( this.rotateBox );

        rightBoxes.setX( originalX - 50 );
        leftBoxes.setX( originalX + 50 );

        IAnimation slideRight1 = new MoveAnimation
                .Builder( this.lineBox )
                .duration( 250 )
                .theta( 0 )
                .maxX( originalX )
                .speed( (50 * 1000) / 250 )
                .build();

        IAnimation slideLeft = new MoveAnimation
                .Builder( this.rotateBox )
                .duration( 250 )
                .theta( 180 )
                .minX( originalX )
                .speed( (50 * 1000) / 250 )
                .build();

        IAnimation slideRight2 = new MoveAnimation
                .Builder( this.itemBox )
                .duration( 250 )
                .theta( 0 )
                .maxX( originalX )
                .speed( (50 * 1000) / 250 )
                .build();

        IAnimation slideFade = new MetaAnimation
                .Builder()
                .add( fadeIn )
                .add( slideRight1 )
                .add( slideLeft )
                .add( slideRight2 )
                .runRule( MetaAnimation.RunRule.SIMULTANEOUS )
                .finishRule( MetaAnimation.FinishRule.ALL )
                .build();

        return slideFade;
    }

    private IAnimation createSlideFadeOut()
    {
        EntityGroup boxes = new EntityGroup( this.lineBox, this.rotateBox, this.itemBox );
        boxes.setOpacity( 0 );

        IAnimation fadeOut = new FadeAnimation
                .Builder( FadeAnimation.Type.OUT, boxes )
                .duration( 300 )
                .build();
        
        IAnimation slideLeft1 = new MoveAnimation
                .Builder( this.lineBox )
                .duration( 400 )
                .theta( 180 )
                .speed( (50 * 1000) / 250 )
                .build();

        IAnimation slideRight = new MoveAnimation
                .Builder( this.rotateBox )
                .duration( 400 )
                .theta( 0 )
                .speed( (50 * 1000) / 250 )
                .build();

        IAnimation slideLeft2 = new MoveAnimation
                .Builder( this.itemBox )
                .duration( 400 )
                .theta( 180 )
                .speed( (50 * 1000) / 250 )
                .build();

        IAnimation slideFade = new MetaAnimation
                .Builder()
                .add( fadeOut )
                .add( slideLeft1 )
                .add( slideRight )
                .add( slideLeft2 )
                .runRule( MetaAnimation.RunRule.SIMULTANEOUS )
                .finishRule( MetaAnimation.FinishRule.ALL )
                .build();

        return slideFade;
    }

}
