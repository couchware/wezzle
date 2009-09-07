package ca.couchware.wezzle2d.difficulty;

/**
 * An enum containing all the difficulties.
 * @author cdmckay
 */
public enum GameDifficulty
{
    NONE("None", null),
    EASY("Easy", new EasyDifficulty()),
    NORMAL("Normal", new NormalDifficulty()),
    HARD("Hard", new HardDifficulty());

    private String description;
    private IDifficultyStrategy difficulty;

    GameDifficulty(String description, IDifficultyStrategy difficulty)
    {
        this.description = description;
        this.difficulty = difficulty;
    }

    public String getDescription()
    {
        return description;
    }

    public IDifficultyStrategy getStrategy()
    {
        return difficulty;
    }
}
