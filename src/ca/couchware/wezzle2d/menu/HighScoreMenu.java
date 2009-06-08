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

    /** The high score label list. */
    final private List<ITextLabel> scoreLabelList;

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
                .visible(false).build();
        this.entityList.add(titleLabel);

         // The box.
        Box optionBox = new Box.Builder(68, 122)
                .width(400).height(398)
                .border(Box.Border.MEDIUM)
                .opacity(80)
                .visible(false)
                .build();
        this.entityList.add(optionBox);

        // Create the score labels.
        this.scoreLabelList = new ArrayList<ITextLabel>(HighScoreManager.NUMBER_OF_SCORES);

        // Create all the labels.
        for (int i = 0; i < HighScoreManager.NUMBER_OF_SCORES; i++)
        {
            ITextLabel label = new LabelBuilder(268, 186 + (45 * i))
                    .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                    .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                    .opacity(0).size(20).text(" ")
                    .visible(false).build();
            scoreLabelList.add(label);

            this.entityList.add(label);
        }

        // Update the labels.
        this.updateScoreLabels(scoreLabelList, hub.highScoreMan.getScoreList());

        // Create the no high score label.
        this.noHighScore = new ITextLabel[2];

        this.noHighScore[0] = new LabelBuilder(268, 306)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .size(20).text("There are no")
                .visible(false).build();

        this.noHighScore[1] = new LabelBuilder(268, 336)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .size(20).text("high scores yet.")
                .visible(false).build();

        // Create the reset button.
        this.resetButton = new Button.Builder(268, 445)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY))
                .normalOpacity(90)
                .visible(false)
                .text("Reset")
                .build();

        if (hub.highScoreMan.getScoreList().isEmpty())
        {
            // The false means don't add it to the layer manager
            // or change visibility.
            showNoHighScoreText(false);
        }
        else
        {
            showResetButton();
        }        

        // Add them all to the layer manager.
        for (IEntity entity : this.entityList)
        {
            this.menuLayerMan.add(entity, Layer.UI);
        }
    }

    public void updateLogic(Game game, ManagerHub hub)
    { 
        if (this.resetButton.clicked())
        {
            // Reset the scores.
            hub.highScoreMan.resetScoreList();
            
            // Show the text.
            this.showNoHighScoreText(true);
            this.hideResetButton();            

            // Update the labels.
            this.updateScoreLabels(this.scoreLabelList, hub.highScoreMan.getScoreList());            
        }
    }

    private void showNoHighScoreText(boolean addToLayerManager)
    {
        for (ITextLabel label : this.noHighScore)
        {
            this.entityList.add(label);
            if (addToLayerManager)
            {
                label.setVisible(true);
                this.menuLayerMan.add(label, Layer.UI);
            }
        }
    }

    private void showResetButton()
    {
        this.entityList.add(this.resetButton);
    }

    private void hideResetButton()
    {
        // Remove the button.
        this.menuLayerMan.remove(this.resetButton, Layer.UI);
        this.entityList.remove(this.resetButton);
    }

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

            if (i < scoreList.size())
            {
                HighScore highScore = scoreList.get(i);

                // Change the text.
                label.setText(format(i, highScore));
                label.setOpacity(100);
            }
            else
            {
                label.setText("");
            }
        } // end for      
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
