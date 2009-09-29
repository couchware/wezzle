/*
 *  Wezzle
 *  Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.group;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ui.Box;
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

    public HelpGroup(ManagerHub hub)
    {
        if (hub == null)
            throw new IllegalArgumentException("Hub must not be null");
        
        this.hub = hub;
        this.groupBox = createGroupBox();
    }

    /**
     * Creates the group box that surrounds the help dialog.
     * @return
     */
    private Box createGroupBox()
    {
        Box box = new Box.Builder( Game.SCREEN_WIDTH / 2, Game.SCREEN_HEIGHT / 2 )
                .alignment( EnumSet.of( Alignment.MIDDLE, Alignment.CENTER) )
                .width( 600 ).height( 400 )
                .build();

        return box;
    }

    public void updateLogic(Game game, ManagerHub hub)
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

}
