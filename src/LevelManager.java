public class LevelManager {
    private static final int STARTING_LEVEL = 1;
    private static final int BASE_ALIEN_SPEED = 2;
    private static final int MAX_ALIEN_SPEED = 7;
    private static final int BASE_ENEMY_SHOT_CHANCE = 8;
    private static final int SHOT_CHANCE_STEP = 4;
    private static final int MAX_ENEMY_SHOT_CHANCE = 40;

    private int level;

    public LevelManager() {
        reset();
    }

    public void reset() {
        level = STARTING_LEVEL;
    }

    public void nextLevel() {
        level++;
    }

    public int getLevel() {
        return level;
    }

    public int getAlienSpeed() {
        return Math.min(MAX_ALIEN_SPEED, BASE_ALIEN_SPEED + level - 1);
    }

    public int getEnemyShotChance() {
        return Math.min(MAX_ENEMY_SHOT_CHANCE, BASE_ENEMY_SHOT_CHANCE + (level - 1) * SHOT_CHANCE_STEP);
    }
}
