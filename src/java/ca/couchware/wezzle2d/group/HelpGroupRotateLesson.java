/*
 *  Wezzle
 *  Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.group;

import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory;
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
import ca.couchware.wezzle2d.piece.Piece;
import ca.couchware.wezzle2d.piece.PieceDash;
import ca.couchware.wezzle2d.piece.PieceGrid;
import ca.couchware.wezzle2d.tile.Tile;
import ca.couchware.wezzle2d.tile.TileColor;
import ca.couchware.wezzle2d.tile.TileHelper;
import ca.couchware.wezzle2d.tile.TileType;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.Padding;
import ca.couchware.wezzle2d.util.ImmutablePosition;
import ca.couchware.wezzle2d.util.ImmutableRectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Contains the logic and resources for the help quadrant teaching about
 * Wezzle lines.
 *
 * @author cdmckay
 */
class HelpGroupRotateLesson
{
    private final IWindow win;
    private final ManagerHub hub;
    private final List<IEntity> entities;
    private final List<IEntity> unmodifiableEntities;
    
    private final Padding quadrantPad;

    private final ImmutableRectangle rect;

    public HelpGroupRotateLesson(IWindow win, ManagerHub hub,
            List<IEntity> parentEntities,
            ImmutableRectangle rect, Padding quadrantPadding)
    {
        this.win = win;
        this.hub = hub;
        this.entities = new ArrayList<IEntity>();
        this.unmodifiableEntities = Collections.unmodifiableList( entities );
        this.quadrantPad = quadrantPadding;
        this.rect = rect;

        createEntities( this.entities );
        parentEntities.addAll( this.entities );
    }
            
    private final static int Columns = 4;
    private final static int Rows    = 2;

    // (0,0) is the top-left tile and
    // (2, 1) is the bottom-right.
    private ImmutablePosition[][] tileGridPositions = new ImmutablePosition[Columns][Rows];
    private Tile[][] tileGrid = new Tile[Columns][Rows];

    private Piece piece;
    private ImmutablePosition pieceGridPosition;
    private PieceGrid pieceGrid;    

    private void createEntities(List<IEntity> entityList)
    {
        final int totalWidth  = hub.boardMan.getCellWidth() * Columns;
        final int totalHeight = hub.boardMan.getCellWidth() * Rows;

        final int gridX = rect.getX() + (rect.getWidth() - totalWidth) / 2;
        final int gridY = rect.getY() + quadrantPad.getTop();      

        this.tileGrid[0][0] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.BLUE, gridX, gridY);

        this.tileGrid[1][0] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.BLUE);
        TileHelper.toRightOf( this.tileGrid[0][0], this.tileGrid[1][0], 0);

        this.tileGrid[2][0] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.RED);
        TileHelper.toRightOf( this.tileGrid[1][0], this.tileGrid[2][0], 0);

        this.tileGrid[3][0] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.PURPLE);
        TileHelper.toRightOf( this.tileGrid[2][0], this.tileGrid[3][0], 0);

        this.tileGrid[0][1] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.RED);
        TileHelper.toBottomOf( this.tileGrid[0][0], this.tileGrid[0][1], 0);

        this.tileGrid[1][1] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.RED);
        TileHelper.toRightOf( this.tileGrid[0][1], this.tileGrid[1][1], 0);

        this.tileGrid[2][1] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.YELLOW);
        TileHelper.toRightOf( this.tileGrid[1][1], this.tileGrid[2][1], 0);

        this.tileGrid[3][1] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.PURPLE);
        TileHelper.toRightOf( this.tileGrid[2][1], this.tileGrid[3][1], 0);

        for (int i = 0; i < Rows; i++)
        {
            for (int j = 0; j < Columns; j++)
            {
                entityList.add( this.tileGrid[j][i] );
                this.tileGridPositions[j][i] = this.tileGrid[j][i].getPosition();
            }
        }

        this.piece = new PieceDash();
        this.pieceGrid = new PieceGrid
                .Builder(
                    win,
                    gridX + hub.boardMan.getCellWidth() * 3,
                    gridY - hub.boardMan.getCellHeight(),
                    PieceGrid.RenderMode.SPRITE_LIGHT )
                .alignment( EnumSet.of(Alignment.TOP, Alignment.LEFT ) )
                .alignmentMode( PieceGrid.AlignmentMode.TO_FULL_GRID )
                .build();

        this.pieceGrid.loadStructure( this.piece.getStructure() );
        this.pieceGridPosition = this.pieceGrid.getPosition();

        entityList.add(this.pieceGrid);

        final ITextLabel label = new ResourceFactory
                .LabelBuilder(rect.getCenterX(), rect.getMaxY() - quadrantPad.getBottom())
                .alignment( EnumSet.of(Alignment.CENTER, Alignment.BOTTOM) )
                .text( "Rotate pieces to make lines." )
                .color( hub.settingsMan.getColor( Key.GAME_COLOR_PRIMARY) )
                .build();

        entityList.add(label);
    }

    public List<IEntity> getEntities()
    {
        return unmodifiableEntities;
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

        this.piece = new PieceDash();
        this.pieceGrid.loadStructure( piece.getStructure() );
        this.pieceGrid.setPosition( this.pieceGridPosition );
        this.pieceGrid.setOpacity( 100 );
        this.pieceGrid.setRotation( 0.0 );
    }

    public IAnimation createAnimation()
    {
        Tile clicked1 = this.tileGrid[2][1];
        Tile clicked2 = this.tileGrid[3][1];
        Tile purple1 = this.tileGrid[3][0];
        Tile blue1 = this.tileGrid[0][0];
        Tile blue2 = this.tileGrid[1][0];
        Tile line1 = this.tileGrid[0][1];
        Tile line2 = this.tileGrid[1][1];
        Tile line3 = this.tileGrid[2][0];       

        IAnimation rotate = new MoveAnimation                
                .Builder( pieceGrid )
                .wait( 1000 )
                .omega( Math.PI )
                .duration( 500 )
                .build();

        IAnimation moveGrid1 = new MoveAnimation
                .Builder( pieceGrid )
                .wait( 800 )
                .speed( 100 )
                .theta( -90 )
                .maxY( pieceGrid.getY() + hub.boardMan.getCellHeight())
                .build();

        IAnimation moveGrid2 = new MoveAnimation
                .Builder( pieceGrid )
                .wait( 1000 )
                .speed( 100 )
                .theta( 180 )
                .minX( pieceGrid.getX() - hub.boardMan.getCellWidth())
                .build();

        IAnimation fade = new FadeAnimation
                .Builder(FadeAnimation.Type.OUT, 
                    new EntityGroup(clicked1, clicked2, this.pieceGrid))
                .wait( 500 )
                .duration( 500 )
                .build();

        IAnimation moveDownRed = new MoveAnimation
                .Builder( line3 )
                .wait( 500 )
                .speed( hub.settingsMan.getInt( Key.REFACTOR_SLOWER_SPEED_Y ))
                //.gravity( hub.settingsMan.getInt( Key.REFACTOR_SLOWER_GRAVITY ) )
                .theta( -90 )
                .maxY( clicked1.getY() )
                .build();

        IAnimation moveDownPurple = new MoveAnimation
                .Builder( purple1 )
                .wait( 500 )
                .speed( hub.settingsMan.getInt( Key.REFACTOR_SLOWER_SPEED_Y ))
                //.gravity( hub.settingsMan.getInt( Key.REFACTOR_SLOWER_GRAVITY ) )
                .theta( -90 )
                .maxY( clicked1.getY() )
                .build();

        IAnimation moveDown = new MetaAnimation
                .Builder()
                .add(moveDownRed)
                .add(moveDownPurple)
                .runRule( MetaAnimation.RunRule.SIMULTANEOUS )
                .finishRule( MetaAnimation.FinishRule.ALL )
                .build();

        IAnimation remove1 = AnimationHelper.animateRemove( hub, line1 );
        IAnimation remove2 = AnimationHelper.animateRemove( hub, line2 );
        IAnimation remove3 = AnimationHelper.animateRemove( hub, line3 );

        IAnimation removeRed = new MetaAnimation
                .Builder()                
                .add( remove1 )
                .add( remove2 )
                .add( remove3 )
                .runRule( MetaAnimation.RunRule.SIMULTANEOUS )
                .finishRule( MetaAnimation.FinishRule.ALL )
                .build();

        IAnimation moveDownBlue1 = new MoveAnimation
                .Builder( blue1 )
                .wait( 250 )
                .speed( hub.settingsMan.getInt( Key.REFACTOR_SLOWER_SPEED_Y ))
                .theta( -90 )
                .maxY( clicked1.getY() )
                .build();

        IAnimation moveDownBlue2 = new MoveAnimation
                .Builder( blue2 )
                .wait( 250 )
                .speed( hub.settingsMan.getInt( Key.REFACTOR_SLOWER_SPEED_Y ))
                .theta( -90 )
                .maxY( clicked1.getY() )
                .build();

        IAnimation moveDownBlue = new MetaAnimation
                .Builder()
                .add( moveDownBlue1 )
                .add( moveDownBlue2 )
                .runRule( MetaAnimation.RunRule.SIMULTANEOUS )
                .finishRule( MetaAnimation.FinishRule.ALL )
                .build();

        IAnimation moveLeftPurple = new MoveAnimation
                .Builder( purple1 )
                .wait( 250 )
                .speed( hub.settingsMan.getInt( Key.REFACTOR_SLOWER_SPEED_X ))
                .theta( 180 )
                .minX( clicked1.getX() )
                .build();

        IAnimation fadeRemainder = new FadeAnimation
                .Builder( FadeAnimation.Type.OUT, new EntityGroup(blue1, blue2, purple1))
                .wait( 250 )
                .duration( 500 )
                .minOpacity( 30 )
                .build();       
      
        IAnimation meta = new MetaAnimation
                .Builder()
                .add( rotate )                
                .add( moveGrid1 )
                .add( moveGrid2 )
                .add( fade )
                .add( moveDown )
                .add( new WaitAnimation(500) )
                .add( removeRed )
                .add( moveDownBlue )
                .add( moveLeftPurple )
                .add( fadeRemainder )
                .runRule( MetaAnimation.RunRule.SEQUENCE )
                .finishRule( MetaAnimation.FinishRule.ALL )
                .build();

        return meta;
    }

}
