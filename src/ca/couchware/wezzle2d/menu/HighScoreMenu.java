/*
 *  Wezzle
 *  Copyright (c) 2007-2008 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.HighScore;
import ca.couchware.wezzle2d.manager.HighScoreManager;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ITextLabel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

/**
 * The high score menu, which holds all the high scores for the user to peruse
 * at his/her leisureee... so... leisurely.
 * 
 * @author cdmckay
 */
public class HighScoreMenu extends AbstractMenu
{

    /** The "no high score" array. */
    private ITextLabel[] noHighScore;

    /** The reset button. */
    final private IButton resetButton;

    public HighScoreMenu(IMenu parent, ManagerHub hub, LayerManager menuLayerMan)
    {
        // Invoke super.
        super(parent, hub, menuLayerMan);

        // The colors.
        final Color LABEL_COLOR  = hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY);
        final Color OPTION_COLOR = hub.settingsMan.getColor(Key.GAME_COLOR_SECONDARY);

        // The title label.
        ITextLabel titleLabel = new LabelBuilder(74, 97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR).text("High Scores").size(20)
                .visible(false).end();
        this.entityList.add(titleLabel);

         // The box.
        Box optionBox = new Box.Builder(68, 122)
                .width(400).height(398)
                .border(Box.Border.MEDIUM)
                .opacity(80)
                .visible(false)
                .end();
        this.entityList.add(optionBox);

        // Create the no high score label.
        this.noHighScore = new ITextLabel[2];

        this.noHighScore[0] = new LabelBuilder(268, 306)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .size(20).text("There are no")
                .visible(false).end();

        this.noHighScore[1] = new LabelBuilder(268, 336)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .size(20).text("high scores yet.")
                .visible(false).end();

        for (ITextLabel label : noHighScore)
        {
            this.entityList.add(label);
        }

        // Create the score labels.
        List<ITextLabel> labelList = new ArrayList<ITextLabel>(HighScoreManager.NUMBER_OF_SCORES);

        // Create all the labels.
        for (int i = 0; i < HighScoreManager.NUMBER_OF_SCORES; i++)
        {            
            ITextLabel label = new LabelBuilder(268, 186 + (45 * i))
                    .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                    .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                    .opacity(0).size(20).text(" ")
                    .visible(false).end();
            labelList.add(label);
           
            this.entityList.add(label);
        }

        // Update the labels.
        this.updateScoreLabels(labelList, hub.highScoreMan.getScoreList());

        // Create the reset button.
        this.resetButton = new Button.Builder(268, 445)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .normalOpacity(90)
                .visible(false)
                .text("Reset")
                .end();
        this.entityList.add(this.resetButton);

        // Add them all to the layer manager.
        for (IEntity entity : this.entityList)
        {
            this.menuLayerMan.add(entity, Layer.UI);
        }
    }

    public void updateLogic(Game game, ManagerHub hub)
    { }

    /**
     * Update a label list using a high score list.
     * @param labelList
     * @param scoreList
     */
    private void updateScoreLabels(List<ITextLabel> labelList, List<HighScore> scoreList)
    {
        for (int i = 0; i < labelList.size(); i++)
        {
            ITextLabel label = labelList.get(i);
            HighScore highScore = scoreList.get(i);

            // Change the text.
            label.setText(format(i, highScore));
            label.setOpacity(100);
        }

        // If no high scores exist, tell the user.
        final int op = scoreList.isEmpty() ? 100 : 0;
        for ( ITextLabel label : this.noHighScore )
        {
            label.setOpacity(op);
        }
    }

    /**
     * Format the high score instance is a human-readable manner.
     * @param rank
     * @param highScore
     * @return
     */
    private String format(int rank, HighScore highScore)
    {
        return String.format(Locale.CANADA, "%d. %,d points (Level %d)",
                rank + 1, 
                highScore.getScore(),
                highScore.getLevel());
    }

}
