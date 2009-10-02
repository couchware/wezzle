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
    private IAnimation lineAnimation;

    public HelpGroup(ManagerHub hub)
    {
        if (hub == null)
            throw new IllegalArgumentException("Hub must not be null");

        this.hub = hub;

        this.animationMan = AnimationManager.newInstance();

        this.groupBox = createGroupBox();
        this.entityList.add( this.groupBox );

        createLineAnimationEntites(this.entityList);       
        
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

    private static final Padding QuadrantPad = Padding.newInstance( 20 );

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

    private final static ImmutableRectangle LineRect =
            new ImmutableRectangle(
                GroupBoxRect.getX() + QuadrantPad.getLeft(),
                GroupBoxRect.getY() + QuadrantPad.getTop(),
                GroupBoxRect.getWidth()  / 2 - QuadrantPad.getLeft() - QuadrantPad.getRight(),
                GroupBoxRect.getHeight() / 2 - QuadrantPad.getTop()  - QuadrantPad.getBottom());

    private final static int LineColumns = 3;
    private final static int LineRows    = 2;
    
    // (0,0) is the top-left tile and 
    // (2, 1) is the bottom-right.
    private ImmutablePosition[][] lineTileGridPositions = new ImmutablePosition[LineColumns][LineRows];
    private Tile[][] lineTileGrid = new Tile[LineColumns][LineRows];

    private ImmutablePosition linePieceGridPosition;
    private PieceGrid linePieceGrid;

    private void createLineAnimationEntites(List<IEntity> entityList)
    {
        final int totalWidth  = hub.boardMan.getCellWidth() * LineColumns;
        final int totalHeight = hub.boardMan.getCellWidth() * LineRows;

        final int gridX = LineRect.getX() + (LineRect.getWidth()  - totalWidth)  / 2;
        final int gridY = LineRect.getY() + (LineRect.getHeight() - totalHeight) / 2;      

        this.lineTileGrid[0][0] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.BLUE, gridX, gridY);

        this.lineTileGrid[1][0] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.RED);
        TileHelper.toRightOf( this.lineTileGrid[0][0], this.lineTileGrid[1][0], 0);

        this.lineTileGrid[2][0] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.RED);
        TileHelper.toRightOf( this.lineTileGrid[1][0], this.lineTileGrid[2][0], 0);

        this.lineTileGrid[0][1] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.RED);
        TileHelper.toBottomOf( this.lineTileGrid[0][0], this.lineTileGrid[0][1], 0);

        this.lineTileGrid[1][1] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.BLUE);
        TileHelper.toRightOf( this.lineTileGrid[0][1], this.lineTileGrid[1][1], 0);

        this.lineTileGrid[2][1] = TileHelper.makeTile(
                TileType.NORMAL, TileColor.BLUE);
        TileHelper.toRightOf( this.lineTileGrid[1][1], this.lineTileGrid[2][1], 0);

        for (int i = 0; i < LineRows; i++)
        {
            for (int j = 0; j < LineColumns; j++)
            {
                entityList.add( this.lineTileGrid[j][i] );
                this.lineTileGridPositions[j][i] = this.lineTileGrid[j][i].getPosition();
            }
        }

        this.linePieceGrid = new PieceGrid
                .Builder(
                    gridX - hub.boardMan.getCellWidth(),
                    gridY + hub.boardMan.getCellHeight(),
                    PieceGrid.RenderMode.SPRITE )
                .alignment( EnumSet.of(Alignment.TOP, Alignment.LEFT ) )
                .alignmentMode( PieceGrid.AlignmentMode.TO_PIECE )
                .build();

        this.linePieceGrid.loadStructure( new PieceDot().getStructure() );
        this.linePieceGridPosition = this.linePieceGrid.getPosition();

        entityList.add(this.linePieceGrid);

        final ITextLabel label = new ResourceFactory
                .LabelBuilder(LineRect.getCenterX(), LineRect.getMaxY())
                .alignment( EnumSet.of(Alignment.CENTER, Alignment.BOTTOM) )
                .text( "Remove tiles to make lines." )
                .color( hub.settingsMan.getColor( Key.GAME_COLOR_PRIMARY) )
                .build();

        entityList.add(label);
    }

    private void resetLineAnimationEntities()
    {
        for (int i = 0; i < LineRows; i++)
        {
            for (int j = 0; j < LineColumns; j++)
            {
                this.lineTileGrid[j][i].setWidth( hub.boardMan.getCellWidth() );
                this.lineTileGrid[j][i].setHeight( hub.boardMan.getCellHeight() );
                this.lineTileGrid[j][i].setPosition( this.lineTileGridPositions[j][i] );
                this.lineTileGrid[j][i].setOpacity( 100 );
            }
        }

        this.linePieceGrid.setPosition( this.linePieceGridPosition );
        this.linePieceGrid.setOpacity( 100 );
    }

    private IAnimation createLineAnimation()
    {
        Tile clicked = this.lineTileGrid[0][1];
        Tile red1 = this.lineTileGrid[1][0];
        Tile red2 = this.lineTileGrid[2][0];
        Tile line1 = this.lineTileGrid[0][0];
        Tile line2 = this.lineTileGrid[1][1];
        Tile line3 = this.lineTileGrid[2][1];

        IAnimation moveGrid = new MoveAnimation
                .Builder( this.linePieceGrid )
                .wait( 1000 )
                .speed( 100 )
                .theta( 0 )
                .maxX( this.linePieceGrid.getX() + hub.boardMan.getCellWidth() )
                .build();      

        IAnimation fade = new FadeAnimation
                .Builder(FadeAnimation.Type.OUT, new EntityGroup(clicked, this.linePieceGrid))
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

        IAnimation zoom1 = new ZoomAnimation
                .Builder( ZoomAnimation.Type.IN, line1 )
                .speed( hub.settingsMan.getInt(Key.ANIMATION_LINE_REMOVE_ZOOM_SPEED) )
                .build();

        IAnimation zoom2 = new ZoomAnimation
                .Builder( ZoomAnimation.Type.IN, line2 )
                .speed( hub.settingsMan.getInt(Key.ANIMATION_LINE_REMOVE_ZOOM_SPEED) )
                .build();

        IAnimation zoom3 = new ZoomAnimation
                .Builder( ZoomAnimation.Type.IN, line3 )
                .speed( hub.settingsMan.getInt(Key.ANIMATION_LINE_REMOVE_ZOOM_SPEED) )
                .build();

        IAnimation removeBlue = new MetaAnimation
                .Builder()
                .add( zoom1 )
                .add( zoom2 )
                .add( zoom3 )
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
                .Builder( Type.OUT, red1 )
                .wait( 250 )
                .duration( 500 )
                .minOpacity( 30 )
                .build();

        IAnimation fadeRed2 = new FadeAnimation
                .Builder( Type.OUT, red2 )
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
                .add( removeBlue )
                .add( moveDownRed )
                .add( fadeRed )
                .runRule( MetaAnimation.RunRule.SEQUENCE )
                .finishRule( MetaAnimation.FinishRule.ALL )
                .build();

        return meta;
    }

    @Override
    public void setActivated(final boolean activated)
    {
        if (activated)
        {
            hub.musicMan.fadeToGain(0.05);

            this.lineAnimation = createLineAnimation();
            this.animationMan.add( this.lineAnimation );
        }
        else
        {
            int intGain = hub.settingsMan.getInt(Key.USER_MUSIC_VOLUME);
            double gain = (double) intGain / 100.0;
            hub.musicMan.fadeToGain(gain);

            this.animationMan.remove( this.lineAnimation );
            this.lineAnimation = null;
            resetLineAnimationEntities();
        }

        // Invoke super.
        super.setActivated(activated);
    }

    public void updateLogic(Game game, ManagerHub hub)
    {
        this.animationMan.animate();
    }

}
