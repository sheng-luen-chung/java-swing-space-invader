import java.nio.file.Path;
import java.nio.file.Paths;

public final class GameConfig {
    public static final int BOARD_WIDTH = 800;
    public static final int BOARD_HEIGHT = 600;
    public static final int FRAME_DELAY_MS = 16;

    public static final int INITIAL_LIVES = 3;
    public static final int PLAYER_SPEED = 6;
    public static final int PLAYER_BULLET_SPEED = 9;
    public static final int ENEMY_BULLET_SPEED = 5;
    public static final int PLAYER_BOTTOM_MARGIN = 45;
    public static final int ALIEN_GAME_OVER_DISTANCE = 35;
    public static final int MAX_PLAYER_BULLETS = 2;
    public static final int ENEMY_SHOT_ROLL_MAX = 1000;
    public static final int LEVEL_CLEARED_MIN_TICKS = 60;
    public static final int PLAYER_HIT_FLASH_TICKS = 80;

    public static final String SOUND_SHOOT = "shoot.wav";
    public static final String SOUND_EXPLOSION = "explosion.wav";
    public static final String SOUND_PLAYER_HIT = "player_hit.wav";
    public static final String SOUND_LEVEL_CLEAR = "level_clear.wav";

    public static final Path SOUND_DIR = Paths.get("assets", "sounds");
    public static final Path HIGH_SCORE_FILE = Paths.get("data", "highscore.txt");

    private GameConfig() {
    }
}
