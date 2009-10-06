/*
 *  Wezzle
 *  Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.group;

import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory;
import ca.couchware.wezzle2d.TileRemover;
import ca.couchware.wezzle2d.animation.AnimationHelper;
import ca.couchware.wezzle2d.animation.FadeAnimation;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.animation.MetaAnimation;
import ca.couchware.wezzle2d.animation.MoveAnimation;
import ca.couchware.wezzle2d.animation.WaitAnimation;
import ca.couchware.wezzle2d.graphics.EntityGroup;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.graphics.IPositionable.Alignment;
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
import java.util.EnumSet;
import java.util.List;

/**
 * Contains the logic and resources for the help quadrant teaching about
 * Wezzle lines.
 *
 * @author cdmckay
 */
class HelpGroupLineQuadrant
{
    private final ManagerHub hub;
    private final List<IEntity> entityList;

    private final ImmutableRectangle groupBoxRect;
    private final Padding quadrantPad;

    private final ImmutableRectangle rect;

    public HelpGroupLineQuadrant(ManagerHub hub, List<IEntity> entityList,
            ImmutableRectangle rect, Padding quadrantPadding)
    {
        this.hub = hub;
        this.entityList = entityList;
        this.groupBoxRect = rect;
        this.quadrantPad = quadrantPadding;

        this.rect = rect;

        createEntites(this.entityList);
    }
            
    private final static int Columns = 3;
    private final static int Rows    = 2;

    // (0,0) is the top-left tile and
    // (2, 1) is the bottom-right.
    private ImmutablePosition[][] tileGridPositions = new ImmutablePosition[Columns][Rows];
    private Tile[][] tileGrid = new Tile[Columns][Rows];

    private ImmutablePosition pieceGridPosition;
    private PieceGrid pieceGrid;

    private void createEntites(List<IEntity> entityList)
    {
        final int totalWidth  = hub.boardMan.getCellWidth() * Columns;
        //final int totalHeight = hub.boardMan.getCellWidth() * rows;

        final int gridX = rect.getX() + (rect.getWidth() - totalWidth) / 2;
        final int gridY = rect.getY() + quadrantPad.getTop();

        Box box = new Box
                .Builder( rect.getX(), rect.getY() )
                .width( rect.getWidth() )
                .height( rect.getHeight() )
                .alignment( EnumSet.of(Alignment.TOP, Alignment.LEFT) )
                .border( Box.Border.MEDIUM )
                .opacity( 90 )
                .build();

        entityList.add(box);

        this.tileGrid[0][0] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.BLUE, gridX, gridY);

        this.tileGrid[1][0] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.RED);
        TileHelper.toRightOf( this.tileGrid[0][0], this.tileGrid[1][0], 0);

        this.tileGrid[2][0] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.RED);
        TileHelper.toRightOf( this.tileGrid[1][0], this.tileGrid[2][0], 0);

        this.tileGrid[0][1] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.YELLOW);
        TileHelper.toBottomOf( this.tileGrid[0][0], this.tileGrid[0][1], 0);

        this.tileGrid[1][1] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.BLUE);
        TileHelper.toRightOf( this.tileGrid[0][1], this.tileGrid[1][1], 0);

        this.tileGrid[2][1] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.BLUE);
        TileHelper.toRightOf( this.tileGrid[1][1], this.tileGrid[2][1], 0);

        for (int i = 0; i < Rows; i++)
        {
            for (int j = 0; j < Columns; j++)
            {
                entityList.add( this.tileGrid[j][i] );
                this.tileGridPositions[j][i] = this.tileGrid[j][i].getPosition();
            }
        }

        this.pieceGrid = new PieceGrid
                .Builder(
                    gridX - hub.boardMan.getCellWidth(),
                    gridY + hub.boardMan.getCellHeight(),
                    PieceGrid.RenderMode.SPRITE )
                .alignment( EnumSet.of(Alignment.TOP, Alignment.LEFT ) )
                .alignmentMode( PieceGrid.AlignmentMode.TO_PIECE )
                .build();

        this.pieceGrid.loadStructure( new PieceDot().getStructure() );
        this.pieceGridPosition = this.pieceGrid.getPosition();

        entityList.add(this.pieceGrid);

        final ITextLabel label = new ResourceFactory
                .LabelBuilder(rect.getCenterX(), rect.getMaxY() - quadrantPad.getBottom())
                .alignment( EnumSet.of(Alignment.CENTER, Alignment.BOTTOM) )
                .text( "Remove tiles to make lines." )
                .color( hub.settingsMan.getColor( Key.GAME_COLOR_PRIMARY) )
                .build();

        entityList.add(label);
    }

    public void resetEntities()
    {
        for (int i = 0; i < Rows; i++)
        {
            for (int j = 0; j < Columns; j++)
            {
                this.tileGrid[j][i].setWidth( hub.boardMan.getCellWidth() );
                this.tileGrid[j][i].setHeight( hub.boardMan.getCellHeight() );
                this.tileGrid[j][i].setPosition( this.tileGridPositions[j][i] );
                this.tileGrid[j][i].setOpacity( 100 );
            }
        }

        this.pieceGrid.setPosition( this.pieceGridPosition );
        this.pieceGrid.setOpacity( 100 );
    }

    public IAnimation createAnimation()
    {
        Tile clicked = this.tileGrid[0][1];
        Tile red1 = this.tileGrid[1][0];
        Tile red2 = this.tileGrid[2][0];
        Tile line1 = this.tileGrid[0][0];
        Tile line2 = this.tileGrid[1][1];
        Tile line3 = this.tileGrid[2][1];

        IAnimation moveGrid = new MoveAnimation
                .Builder( this.pieceGrid )
                .wait( 1000 )
                .speed( 100 )
                .theta( 0 )
                .maxX( this.pieceGrid.getX() + hub.boardMan.getCellWidth() )
                .build();

        IAnimation fade = new FadeAnimation
                .Builder(FadeAnimation.Type.OUT, new EntityGroup(clicked, this.pieceGrid))
                .wait( 500 )
                .duration( 500 )
                .build();

        IAnimation moveDownBlue = new MoveAnimation
                .Builder( line1 )
                .wait( 500 )
                .speed( hub.settingsMan.getInt( Key.REFACTOR_SLOWER_SPEED_Y ))
                //.gravity( hub.settingsMan.getInt( Key.REFACTOR_SLOWER_GRAVITY ) )
                .theta( -90 )
                .maxY( clicked.getY() )
                .build();

        IAnimation moveLeftRed1 = new MoveAnimation
                .Builder( red1 )
                .wait( 250 )
                .speed( hub.settingsMan.getInt( Key.REFACTOR_SLOWER_SPEED_X ))
                .theta( 180 )
                .minX( clicked.getX() )
                .build();

        IAnimation moveLeftRed2 = new MoveAnimation
                .Builder( red2 )
                .wait( 250 )
                .speed( hub.settingsMan.getInt( Key.REFACTOR_SLOWER_SPEED_X ))
                .theta( 180 )
                .minX( clicked.getX() )
                .build();

        IAnimation moveLeftRed = new MetaAnimation
                .Builder()
                .add( moveLeftRed1 )
                .add( moveLeftRed2 )
                .runRule( MetaAnimation.RunRule.SIMULTANEOUS )
                .build();

//        IAnimation fadeOut = new FadeAnimation
//                .Builder( FadeAnimation.Type.OUT, t )
//                .duration( duration )
//                .minOpacity( minOpacity )
//                .build();
//
//        IAnimation fadeIn = new FadeAnimation
//                .Builder( FadeAnimation.Type.IN, t )
//                .duration( duration )
//                .minOpacity( minOpacity )
//                .build();
//
//        IAnimation fadeOut2 = new FadeAnimation
//                .Builder( FadeAnimation.Type.OUT, t )
//                .duration( duration )
//                .minOpacity( minOpacity )
//                .build();
//
//        IAnimation fadeIn2 = new FadeAnimation
//                .Builder( FadeAnimation.Type.IN, t )
//                .duration( duration )
//                .minOpacity( minOpacity )
//                .build();

//        IAnimation zoom1 = new ZoomAnimation
//                .Builder( ZoomAnimation.Type.IN, line1 )
//                .lateInitialization( true )
//                .speed( hub.settingsMan.getInt(Key.ANIMATION_LINE_REMOVE_ZOOM_SPEED) )
//                .build();
//
//        IAnimation zoom2 = new ZoomAnimation
//                .Builder( ZoomAnimation.Type.IN, line2 )
//                .lateInitialization( true )
//                .speed( hub.settingsMan.getInt(Key.ANIMATION_LINE_REMOVE_ZOOM_SPEED) )
//                .build();
//
//        IAnimation zoom3 = new ZoomAnimation
//                .Builder( ZoomAnimation.Type.IN, line3 )
//                .lateInitialization( true )
//                .speed( hub.settingsMan.getInt(Key.ANIMATION_LINE_REMOVE_ZOOM_SPEED) )
//                .build();

        IAnimation remove1 = AnimationHelper.animateRemove( hub, line1 );
        IAnimation remove2 = AnimationHelper.animateRemove( hub, line2 );
        IAnimation remove3 = AnimationHelper.animateRemove( hub, line3 );

        IAnimation removeBlue = new MetaAnimation
                .Builder()                
                .add( remove1 )
                .add( remove2 )
                .add( remove3 )
                .runRule( MetaAnimation.RunRule.SIMULTANEOUS )
                .finishRule( MetaAnimation.FinishRule.ALL )
                .build();

        IAnimation moveDownRed1 = new MoveAnimation
                .Builder( red1 )
                .wait( 250 )
                .speed( hub.settingsMan.getInt( Key.REFACTOR_SLOWER_SPEED_Y ))
                .theta( -90 )
                .maxY( clicked.getY() )
                .build();

        IAnimation moveDownRed2 = new MoveAnimation
                .Builder( red2 )
                .wait( 250 )
                .speed( hub.settingsMan.getInt( Key.REFACTOR_SLOWER_SPEED_Y ))
                .theta( -90 )
                .maxY( clicked.getY() )
                .build();

        IAnimation moveDownRed = new MetaAnimation
                .Builder()
                .add( moveDownRed1 )
                .add( moveDownRed2 )
                .runRule( MetaAnimation.RunRule.SIMULTANEOUS )
                .finishRule( MetaAnimation.FinishRule.ALL )
                .build();

        IAnimation fadeRed1 = new FadeAnimation
                .Builder( FadeAnimation.Type.OUT, red1 )
                .wait( 250 )
                .duration( 500 )
                .minOpacity( 30 )
                .build();

        IAnimation fadeRed2 = new FadeAnimation
                .Builder( FadeAnimation.Type.OUT, red2 )
                .wait( 250 )
                .duration( 500 )
                .minOpacity( 30 )
                .build();

        IAnimation fadeRed = new MetaAnimation
                .Builder()
                .add( fadeRed1 )
                .add( fadeRed2 )
                .runRule( MetaAnimation.RunRule.SIMULTANEOUS )
                .finishRule( MetaAnimation.FinishRule.ALL )
                .build();

        IAnimation meta = new MetaAnimation
                .Builder()
                .add( moveGrid )
                .add( fade )
                .add( moveDownBlue )
                .add( moveLeftRed )
                .add( new WaitAnimation(500) )
                .add( removeBlue )
                .add( moveDownRed )
                .add( fadeRed )
                .runRule( MetaAnimation.RunRule.SEQUENCE )
                .finishRule( MetaAnimation.FinishRule.ALL )
                .build();

        return meta;
    }

}
