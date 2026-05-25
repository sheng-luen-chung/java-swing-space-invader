import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameEngine {
    private static final int PLAYER_SPEED = 6;
    private static final int PLAYER_BULLET_SPEED = 9;
    private static final int ENEMY_BULLET_SPEED = 5;
    private static final int PLAYER_BOTTOM_MARGIN = 45;
    private static final int ALIEN_GAME_OVER_DISTANCE = 35;
    private static final int MAX_PLAYER_BULLETS = 2;
    private static final int ENEMY_SHOT_ROLL_MAX = 1000;
    private static final int LEVEL_CLEARED_PAUSE_TICKS = 90;

    private final int boardWidth;
    private final int boardHeight;
    private final GameState gameState;
    private final ScoreManager scoreManager;
    private final LevelManager levelManager;
    private final Player player;
    private final AlienFleet alienFleet;
    private final List<Bullet> bullets;
    private final List<Shield> shields;
    private final CollisionManager collisionManager;
    private final Random random;

    private boolean movingLeft;
    private boolean movingRight;
    private int levelClearedTicks;

    public GameEngine(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.gameState = new GameState();
        this.scoreManager = new ScoreManager();
        this.levelManager = new LevelManager();
        this.player = new Player(boardWidth / 2 - 25, boardHeight - PLAYER_BOTTOM_MARGIN, 50, 24);
        this.alienFleet = new AlienFleet();
        this.bullets = new ArrayList<>();
        this.shields = new ArrayList<>();
        this.collisionManager = new CollisionManager();
        this.random = new Random();
        createShields();
    }

    public void update() {
        if (gameState.getStatus() == GameState.Status.LEVEL_CLEARED) {
            updateLevelTransition();
            return;
        }

        if (!gameState.isPlaying()) {
            return;
        }

        updatePlayer();
        updateBullets();
        alienFleet.update(boardWidth, levelManager.getAlienSpeed());
        maybeEnemyShoot();
        handleCollisions();

        if (!gameState.isPlaying()) {
            return;
        }

        checkEndConditions();
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    public void shoot() {
        if (!gameState.isPlaying()) {
            return;
        }

        if (countPlayerBullets() < MAX_PLAYER_BULLETS) {
            int bulletX = player.getCenterX() - Bullet.WIDTH / 2;
            int bulletY = player.getY() - Bullet.HEIGHT;
            bullets.add(new Bullet(bulletX, bulletY, PLAYER_BULLET_SPEED, BulletType.PLAYER_BULLET));
        }
    }

    public void restart() {
        gameState.reset();
        scoreManager.reset();
        levelManager.reset();
        startCurrentLevel();
        gameState.setStatus(GameState.Status.PLAYING);
    }

    public void startGame() {
        if (gameState.getStatus() == GameState.Status.START_SCREEN) {
            gameState.setStatus(GameState.Status.PLAYING);
        }
    }

    public void togglePause() {
        if (gameState.isPlaying()) {
            gameState.setStatus(GameState.Status.PAUSED);
        } else if (gameState.isPaused()) {
            gameState.setStatus(GameState.Status.PLAYING);
        }
    }

    private void startCurrentLevel() {
        player.setX(boardWidth / 2 - player.getWidth() / 2);
        alienFleet.reset();
        bullets.clear();
        createShields();
        movingLeft = false;
        movingRight = false;
        levelClearedTicks = 0;
    }

    private void updatePlayer() {
        if (movingLeft) {
            player.moveLeft(PLAYER_SPEED);
        }

        if (movingRight) {
            player.moveRight(PLAYER_SPEED, boardWidth);
        }
    }

    private void updateBullets() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.update();

            if (bullet.isOffScreen(boardHeight)) {
                iterator.remove();
            }
        }
    }

    private void maybeEnemyShoot() {
        int shotChance = levelManager.getEnemyShotChance();
        if (random.nextInt(ENEMY_SHOT_ROLL_MAX) >= shotChance) {
            return;
        }

        Alien shooter = alienFleet.getRandomShooter(random);
        if (shooter == null) {
            return;
        }

        int bulletX = shooter.getX() + shooter.getWidth() / 2 - Bullet.WIDTH / 2;
        int bulletY = shooter.getY() + shooter.getHeight();
        bullets.add(new Bullet(bulletX, bulletY, ENEMY_BULLET_SPEED, BulletType.ENEMY_BULLET));
    }

    private void handleCollisions() {
        CollisionManager.CollisionResult result = collisionManager.handleCollisions(
                bullets,
                alienFleet.getAliens(),
                shields,
                player);

        for (Alien alien : result.getDestroyedAliens()) {
            scoreManager.addAlienScore(alien);
        }

        if (result.isPlayerHit()) {
            gameState.loseLife();
            if (gameState.hasNoLives()) {
                gameState.setStatus(GameState.Status.GAME_OVER);
            }
        }
    }

    private void checkEndConditions() {
        if (alienFleet.allAliensDestroyed()) {
            gameState.setStatus(GameState.Status.LEVEL_CLEARED);
            levelClearedTicks = LEVEL_CLEARED_PAUSE_TICKS;
            return;
        }

        int dangerLine = player.getY() - ALIEN_GAME_OVER_DISTANCE;
        if (alienFleet.hasReachedY(dangerLine) || alienFleet.hasReachedY(boardHeight)) {
            gameState.setStatus(GameState.Status.GAME_OVER);
        }
    }

    private void updateLevelTransition() {
        levelClearedTicks--;

        if (levelClearedTicks <= 0) {
            levelManager.nextLevel();
            startCurrentLevel();
            gameState.setStatus(GameState.Status.PLAYING);
        }
    }

    private int countPlayerBullets() {
        int count = 0;

        for (Bullet bullet : bullets) {
            if (bullet.getType() == BulletType.PLAYER_BULLET) {
                count++;
            }
        }

        return count;
    }

    private void createShields() {
        shields.clear();

        int shieldWidth = 86;
        int shieldHeight = 34;
        int shieldY = boardHeight - 160;
        int gap = 78;
        int totalWidth = shieldWidth * 4 + gap * 3;
        int startX = (boardWidth - totalWidth) / 2;

        for (int i = 0; i < 4; i++) {
            shields.add(new Shield(startX + i * (shieldWidth + gap), shieldY, shieldWidth, shieldHeight));
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public Player getPlayer() {
        return player;
    }

    public AlienFleet getAlienFleet() {
        return alienFleet;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public List<Shield> getShields() {
        return shields;
    }
}
