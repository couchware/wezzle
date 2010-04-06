/*
 *  Wezzle
 *  Copyright (c) 2007-2009 Couchware Inc.  All rights reserved.
 */

package ca.couchware.wezzle2d.menu;

import ca.couchware.wezzle2d.Game;
import ca.couchware.wezzle2d.IWindow;
import ca.couchware.wezzle2d.ManagerHub;
import ca.couchware.wezzle2d.ResourceFactory.LabelBuilder;
import ca.couchware.wezzle2d.TimeTrial;
import ca.couchware.wezzle2d.animation.IAnimation;
import ca.couchware.wezzle2d.audio.Music;
import ca.couchware.wezzle2d.audio.MusicPlayer;
import ca.couchware.wezzle2d.difficulty.GameDifficulty;
import ca.couchware.wezzle2d.event.GameEvent;
import ca.couchware.wezzle2d.graphics.IEntity;
import ca.couchware.wezzle2d.manager.IResettable;
import ca.couchware.wezzle2d.manager.LayerManager;
import ca.couchware.wezzle2d.manager.LayerManager.Layer;
import ca.couchware.wezzle2d.manager.MusicManager.Theme;
import ca.couchware.wezzle2d.manager.Settings;
import ca.couchware.wezzle2d.manager.Settings.Key;
import ca.couchware.wezzle2d.manager.SettingsManager;
import ca.couchware.wezzle2d.ui.IButton;
import ca.couchware.wezzle2d.ui.ITextLabel;
import ca.couchware.wezzle2d.ui.RadioGroup;
import ca.couchware.wezzle2d.ui.RadioItem;
import ca.couchware.wezzle2d.ui.Button;
import ca.couchware.wezzle2d.ui.Box;
import ca.couchware.wezzle2d.ui.IButton.IButtonListener;
import ca.couchware.wezzle2d.ui.SliderBar;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * The play now group, which holds all the configuration options for playing
 * a Wezzle game.
 * 
 * @author cdmckay
 */
public class PlayNowMenu extends AbstractMenu
{
    
    final private static int MIN_LEVEL = 1;
    final private static int MAX_LEVEL = 15;             

    private ManagerHub hub;
    private MusicPlayer menuPlayer;

    private int levelNumber;
    private ITextLabel levelLabel;
    private ITextLabel levelNumberLabel;    
    private SliderBar levelNumberSlider;   

    final private static int MIN_DIFFICULTY = 1;
    final private static int MAX_DIFFICULTY = GameDifficulty.values().length - 1;

    private int difficultyValue;
    private ITextLabel difficultyLabel;
    private ITextLabel difficultyValueLabel;
    private SliderBar difficultyValueSlider;
    
    private ITextLabel tutorialLabel;
    private RadioGroup tutorialRadio;

    private ITextLabel themeLabel;
    private RadioGroup themeRadio;
    private List<MusicPlayer> playerList;

    final private IButton startButton;

    final private static int TUTORIAL_ON = 0;
    final private static int TUTORIAL_OFF = 1;

    final private static int THEME_TRON = 0;
    final private static int THEME_ELECTRONIC = 1;
    final private static int THEME_HIPPOP = 2;

    final private static String THEME_TRON_KEY = "Tron";
    final private static String THEME_ELECTRONIC_KEY = "Electronic";
    final private static String THEME_HIPPOP_KEY = "HipPop";
    
    public PlayNowMenu(IMenu parentMenu, IWindow win, ManagerHub hub, LayerManager menuLayerMan)
    {                
        // Invoke the super.
        super(parentMenu, win, hub, menuLayerMan);
        
        this.hub = hub;
        this.menuPlayer = ((MainMenu) this.parent).getPlayer();

        // The colors.
        final Color LABEL_COLOR  = hub.settingsMan.getColor(Key.GAME_COLOR_PRIMARY);        
        final Color OPTION_COLOR = hub.settingsMan.getColor(Key.GAME_COLOR_SECONDARY);       

        // The title label.
        ITextLabel titleLabel = new LabelBuilder(74, 97)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(LABEL_COLOR).text("Play Now").size(20)                
                .visible(false).build();

        this.entityList.add(titleLabel);
        
        // The first box.
        Box optionBox = new Box.Builder(win, 68, 122)
                .width(400).height(398)
                .border(Box.Border.MEDIUM)
                .opacity(80)
                .visible(false)
                .build();

        this.entityList.add(optionBox);

        createLevelNumberEntities(win, hub, LABEL_COLOR, OPTION_COLOR);
        createDifficultyEntities(win, hub, LABEL_COLOR, OPTION_COLOR);
        createTutorialEntities(win, hub, LABEL_COLOR, OPTION_COLOR);
        createThemeEntities(win, hub, LABEL_COLOR, OPTION_COLOR);

        // Create the start button.
        this.startButton = new Button.Builder(win, 268, 462)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .color(LABEL_COLOR)
                .normalOpacity(90)                
                .visible(false)                
                .text("Start")
                .build();

        this.entityList.add(this.startButton);
                
        // Add them all to the layer manager.
        for (IEntity entity : this.entityList)
        {
            this.menuLayerMan.add(entity, Layer.UI);
        }
    }

    /**
     * Create the level number entites.
     * 
     * @param hub
     * @param labelColor
     * @param optionColor
     */
    private void createLevelNumberEntities(IWindow win, ManagerHub hub,
            Color labelColor, Color optionColor)
    {
        // Get the user set level and make sure it's within range.
        this.levelNumber = hub.settingsMan.getInt(Key.USER_LEVEL_DEFAULT);
        this.levelNumber = Math.max(MIN_LEVEL, this.levelNumber);
        this.levelNumber = Math.min(MAX_LEVEL, this.levelNumber);

        this.levelLabel = new LabelBuilder(110, 170)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(labelColor)
                .text("Level").size(20)
                .visible(false)
                .build();
        this.entityList.add(levelLabel);

        this.levelNumberLabel = new LabelBuilder(
                    levelLabel.getX() + levelLabel.getWidth() + 20,
                    levelLabel.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(optionColor)
                .text(String.valueOf(levelNumber)).size(20)
                .visible(false)
                .build();
        this.entityList.add(this.levelNumberLabel);

        this.levelNumberSlider = new SliderBar.Builder(
                    win, 268, levelLabel.getY() + 35)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .width(340)
                .virtualRange(MIN_LEVEL, MAX_LEVEL).
                virtualValue(this.levelNumber)
                .visible(false)
                .build();
        this.entityList.add(levelNumberSlider);
    }
    
    /**
     * Create the difficulty entities.
     * 
     * @param hub
     * @param labelColor
     * @param optionColor
     */
    private void createDifficultyEntities(IWindow win, ManagerHub hub,
            Color labelColor, Color optionColor)
    {
        // Get the user set difficulty and make sure it's within range.
        String difficultyStr = hub.settingsMan.getString(Key.USER_DIFFICULTY_DEFAULT);
        this.difficultyValue = GameDifficulty.valueOf(difficultyStr).ordinal();

        this.difficultyLabel = new LabelBuilder(
                    levelLabel.getX(),
                    levelLabel.getY() + 75)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(labelColor).text("Difficulty").size(20)
                .visible(false)
                .build();
        this.entityList.add(difficultyLabel);

        this.difficultyValueLabel = new LabelBuilder(
                    difficultyLabel.getX() + difficultyLabel.getWidth() + 20,
                    difficultyLabel.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(optionColor)
                .size(20).visible(false)
                .text(GameDifficulty.valueOf(difficultyStr).getDescription())
                .build();
        this.entityList.add(this.difficultyValueLabel);

        this.difficultyValueSlider = new SliderBar.Builder(
                    win, 268, difficultyLabel.getY() + 35)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .width(340)
                .virtualRange(MIN_DIFFICULTY, MAX_DIFFICULTY)
                .virtualValue(this.difficultyValue)
                .visible(false)
                .build();
        this.entityList.add(difficultyValueSlider);
    }

    /**
     * Create the tutorial entities.
     *
     * @param hub
     * @param labelColor
     * @param optionColor
     */
    private void createTutorialEntities(IWindow win, ManagerHub hub,
            Color labelColor, Color optionColor)
    {
        this.tutorialLabel = new LabelBuilder(
                    difficultyLabel.getX(),
                    difficultyLabel.getY() + 75)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(labelColor).text("Tutorial").size(20)
                .visible(false)
                .build();
        this.entityList.add(tutorialLabel);

        RadioItem tutorialOn = new RadioItem.Builder(win)
                .color(optionColor)
                .text("On").build();

        RadioItem tutorialOff = new RadioItem.Builder(win)
                .color(optionColor)
                .text("Off").build();

        final boolean tutorialDefault = hub.settingsMan.getBool(Key.USER_TUTORIAL_DEFAULT);
        this.tutorialRadio = new RadioGroup.Builder(
                    win, 310, tutorialLabel.getY())
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .add(tutorialOn,  tutorialDefault)
                .add(tutorialOff, !tutorialDefault)
                .visible(false)
                .build();
        this.entityList.add(tutorialRadio);
    }

    /**
     * Create the theme entities.
     * 
     * @param hub
     * @param labelColor
     * @param optionColor
     */
    private void createThemeEntities(IWindow win, ManagerHub hub,
            Color labelColor, Color optionColor)
    {
        this.themeLabel = new LabelBuilder(
                    tutorialLabel.getX(),
                    tutorialLabel.getY() + 47)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.LEFT))
                .color(labelColor).text("Music").size(20)
                .visible(false)
                .build();
        this.entityList.add(themeLabel);

        // Create the music players.
        if (!Game.isApplet())
        {
            createPlayers();
        }

        // Creat the level limit radio group.
        RadioItem themeItem1 = new RadioItem.Builder(win).color(optionColor)
                .text("Tron").build();
        themeItem1.addButtonListener(new IButtonListener()
        {
            public void buttonClicked()
            { playTheme(THEME_TRON); }
        });

        RadioItem themeItem2 = new RadioItem.Builder(win).color(optionColor)
                .text("Elec").build();
        themeItem2.addButtonListener(new IButtonListener()
        {
            public void buttonClicked()
            { playTheme(THEME_ELECTRONIC); }
        });

        RadioItem themeItem3 = new RadioItem.Builder(win).color(optionColor)
                .text("HipPop").build();
        themeItem3.addButtonListener(new IButtonListener()
        {
            public void buttonClicked()
            { playTheme(THEME_HIPPOP); }
        });

        Map<Theme, Boolean> themeMap = new EnumMap<Theme, Boolean>(Theme.class);
        themeMap.put(Theme.TRON, false);
        themeMap.put(Theme.ELECTRONIC, false);
        themeMap.put(Theme.HIPPOP, false);

        // Attempt to get the user's music preference.
        final String musicDefault = hub.settingsMan.getString(Key.USER_MUSIC_DEFAULT);
        Theme musicTheme = Theme.NONE;

        // Try to convert the string we got from the settings file into
        // an enum.
        try
        {
            musicTheme = Enum.valueOf(Theme.class, musicDefault);
        }
        catch (IllegalArgumentException e)
        {
            musicTheme = Theme.NONE;
        }

        // If the user set theme was successful, use it.
        if (musicTheme != Theme.NONE)
        {
            themeMap.put(musicTheme, true);
        }
        // Otherwise, use a random one.
        else
        {
            List<Theme> themeList = new ArrayList<Theme>(themeMap.keySet());
            Collections.shuffle(themeList);
            themeMap.put(themeList.get(0), true);
        }

        this.themeRadio = new RadioGroup.Builder(
                    win, 268, themeLabel.getY() + 35)
                .alignment(EnumSet.of(Alignment.MIDDLE, Alignment.CENTER))
                .add(themeItem1, themeMap.get(Theme.TRON))
                .add(themeItem2, themeMap.get(Theme.ELECTRONIC))
                .add(themeItem3, themeMap.get(Theme.HIPPOP))
                .itemSpacing(20).visible(false).build();
        this.entityList.add(themeRadio);
    }

    /**
     * Create the music players used by the theme selector.
     */
    private void createPlayers()
    {
        // Create the music player map.
        this.playerList = new ArrayList<MusicPlayer>();
       
        // Create three players, 1 for each theme.
        this.playerList.add(hub.musicMan.createPlayer(THEME_TRON_KEY, Music.TRON2));
        this.playerList.add(hub.musicMan.createPlayer(THEME_ELECTRONIC_KEY, Music.ELECTRONIC1));
        this.playerList.add(hub.musicMan.createPlayer(THEME_HIPPOP_KEY, Music.HIPPOP1));
                           
        for (MusicPlayer p : playerList)
        {
            p.setLooping(true);
            p.play();
            p.setNormalizedGain(0.0);
        }
    }

    private void playTheme(int theme)
    {
        final boolean isMusicOn = hub.settingsMan.getBool(Key.USER_MUSIC);
        if (!isMusicOn) return;

        for (int i = 0; i < playerList.size(); i++)
        {
            if (i == theme) continue;
            playerList.get(i).fadeToGain(0.0);
        }

        int intGain = SettingsManager.get().getInt(Settings.Key.USER_MUSIC_VOLUME);
        double userGain = (double) intGain / 100.0;
        
        playerList.get(theme).fadeToGain(userGain);
    }

    private void stopThemes()
    {
        for (MusicPlayer player : playerList)
        {
            player.fadeToGain(0.0);
        }
    }
        
    @Override
    public void updateLogic(Game game, ManagerHub hub)
    {
        super.updateLogic( game, hub );

        if ( this.levelNumberSlider.changed() )
        {
            levelNumber = (int) levelNumberSlider.getVirtualValue();
            this.levelNumberLabel.setText("" + levelNumber);
            hub.settingsMan.setInt(Key.USER_LEVEL_DEFAULT, this.levelNumber);
        }
        else if ( this.difficultyValueSlider.changed() )
        {
            this.difficultyValue = difficultyValueSlider.getVirtualValue();
            this.difficultyValueLabel.setText(
                    GameDifficulty.values()[this.difficultyValue].getDescription());
            hub.settingsMan.setString(Key.USER_DIFFICULTY_DEFAULT,
                    GameDifficulty.values()[this.difficultyValue].toString());
        }
        else if (this.startButton.clicked())
        {
            startGame(game, hub);
        }

        tutorialRadio.changed();
        themeRadio.changed();
    }

    public void startGame(Game game, ManagerHub hub)
    {
        // Make sure no groups are showing if we've come back to the menu
        // from a previous game.
        hub.groupMan.hideAllGroups(!game.isCompletelyBusy());       

         // Set the difficulty.
        GameDifficulty difficulty = GameDifficulty.values()[this.difficultyValue];
        game.setDifficulty( difficulty );

        // Reset the core managers.
        IResettable coreManagers[] = new IResettable[]
        {
            game.getRefactorer(),
            game.getTileDropper(),
            game.getTileRemover()
        };

        for (IResettable core : coreManagers)
        {
            core.resetState();
        }

        // Reset the board manager and remove any tiles (and their effects)
        // from the layer manager.
        hub.boardMan.resetState();
        hub.boardMan.setVisible(false); // This is done for the fade in.
        hub.layerMan.clearLayer(Layer.TILE);
        hub.layerMan.clearLayer(Layer.EFFECT);

        // Reset the various other managers.
        IResettable[] managers = new IResettable[]
        {
            hub.levelMan,
            hub.pieceMan,
            hub.soundMan,
            hub.scoreMan,
            hub.statMan,
            hub.timerMan,
            hub.tutorialMan
        };

        for (IResettable man : managers)
        {
            man.resetState();
        }

        // Set the music.
        Theme theme = null;

        if (Game.isApplet())
        {
            theme = Theme.DEMO;
        }
        else
        {
            switch (themeRadio.getSelectedIndex())
            {
                case THEME_TRON:
                    theme = Theme.TRON;
                    break;
                case THEME_ELECTRONIC:
                    theme = Theme.ELECTRONIC;
                    break;
                case THEME_HIPPOP:
                    theme = Theme.HIPPOP;
                    break;

                default: throw new AssertionError();
            }
        }

        // Save the theme preference and set it in the music manager.
        hub.settingsMan.setString(Key.USER_MUSIC_DEFAULT, theme.toString());
        hub.musicMan.setTheme(theme);

        // Make sure the latest sound volume and
        // music volume settings are being used.
        hub.soundMan.importSettings();
        hub.musicMan.importSettings();

        // Set the target score.
        hub.levelMan.setLevel(levelNumber, false);
        hub.scoreMan.setTargetTotalScore(hub.scoreMan.generateTargetLevelScore(levelNumber));
        hub.scoreMan.setTargetLevelScore(hub.scoreMan.generateTargetLevelScore(levelNumber));

        // Notify that the game started.
        hub.listenerMan.notifyGameStarted(new GameEvent(this,
                game.getDifficulty(),
                hub.levelMan.getLevel(),
                hub.scoreMan.getTotalScore()));

        // Turn off the tutorials if necessary.
        if (this.tutorialRadio.getSelectedIndex() == TUTORIAL_ON)
        {
            hub.settingsMan.setBool(Key.USER_TUTORIAL_DEFAULT, true);
            game.initializeTutorials(true);
        }
        else
        {
            hub.settingsMan.setBool(Key.USER_TUTORIAL_DEFAULT, false);
            game.initializeTutorials(false);
        }

        // Stop all the players.
        if (!Game.isApplet())
        {
            hub.musicMan.destroyPlayerWithFade(THEME_TRON_KEY, 0.0);
            hub.musicMan.destroyPlayerWithFade(THEME_ELECTRONIC_KEY, 0.0);
            hub.musicMan.destroyPlayerWithFade(THEME_HIPPOP_KEY, 0.0);
            hub.musicMan.destroyPlayerWithFade(MainMenu.MENU_PLAYER_KEY, 0.0);
        }

        game.startBoard();
        hub.pieceMan.nextPiece();

        // Notify the main menu.
        this.parent.setActivated(false);

        if(Game.isTimeTrial())
            TimeTrial.start();
    }

    @Override
    public IAnimation animateShow()
    {
        boolean isMusicOn = hub.settingsMan.getBool(Key.USER_MUSIC);        
        if (isMusicOn)
        {
            playTheme(themeRadio.getSelectedIndex());            
        }

        if (!Game.isApplet())
        {
            this.menuPlayer.fadeToGain( 0.0 );
        }
        
        return super.animateShow();
    }

    @Override
    public IAnimation animateHide()
    {        
        boolean isMusicOn = hub.settingsMan.getBool(Key.USER_MUSIC);
        if (isMusicOn)
        {
            stopThemes();
        }

        if (!Game.isApplet())
        {
            int intGain = hub.settingsMan.getInt(Key.USER_MUSIC_VOLUME);
            double gain = (double) intGain / 100.0;
            this.menuPlayer.fadeToGain( gain );
        }
                        
        return super.animateHide();
    }

}
