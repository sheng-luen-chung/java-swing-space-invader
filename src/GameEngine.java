import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameEngine {
    private final int boardWidth;
    private final int boardHeight;
    private final GameState gameState;
    private final ScoreManager scoreManager;
    private final LevelManager levelManager;
    private final Player player;
    private final AlienFleet alienFleet;
    private final List<Bullet> bullets;
    private final List<Shield> shields;
    private final List<ExplosionEffect> explosions;
    private final CollisionManager collisionManager;
    private final SoundManager soundManager;
    private final HighScoreManager highScoreManager;
    private final Random random;

    private boolean movingLeft;
    private boolean movingRight;
    private int levelClearedTicks;
    private int playerHitFlashTicks;

    public GameEngine(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.gameState = new GameState();
        this.scoreManager = new ScoreManager();
        this.levelManager = new LevelManager();
        this.player = new Player(boardWidth / 2 - 25, boardHeight - GameConfig.PLAYER_BOTTOM_MARGIN, 50, 24);
        this.alienFleet = new AlienFleet();
        this.bullets = new ArrayList<>();
        this.shields = new ArrayList<>();
        this.explosions = new ArrayList<>();
        this.collisionManager = new CollisionManager();
        this.soundManager = new SoundManager();
        this.highScoreManager = new HighScoreManager();
        this.random = new Random();
        createShields();
    }

    public void update() {
        updateEffects();

        if (gameState.getStatus() == GameState.Status.LEVEL_CLEARED) {
            if (levelClearedTicks > 0) {
                levelClearedTicks--;
            }
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

        if (countPlayerBullets() < GameConfig.MAX_PLAYER_BULLETS) {
            int bulletX = player.getCenterX() - Bullet.WIDTH / 2;
            int bulletY = player.getY() - Bullet.HEIGHT;
            bullets.add(new Bullet(bulletX, bulletY, GameConfig.PLAYER_BULLET_SPEED, BulletType.PLAYER_BULLET));
            soundManager.play(GameConfig.SOUND_SHOOT);
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

    public void startNextLevel() {
        if (gameState.getStatus() == GameState.Status.LEVEL_CLEARED && levelClearedTicks <= 0) {
            levelManager.nextLevel();
            startCurrentLevel();
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
        explosions.clear();
        createShields();
        movingLeft = false;
        movingRight = false;
        levelClearedTicks = 0;
        playerHitFlashTicks = 0;
    }

    private void updatePlayer() {
        if (movingLeft) {
            player.moveLeft(GameConfig.PLAYER_SPEED);
        }

        if (movingRight) {
            player.moveRight(GameConfig.PLAYER_SPEED, boardWidth);
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
        if (random.nextInt(GameConfig.ENEMY_SHOT_ROLL_MAX) >= shotChance) {
            return;
        }

        Alien shooter = alienFleet.getRandomShooter(random);
        if (shooter == null) {
            return;
        }

        int bulletX = shooter.getX() + shooter.getWidth() / 2 - Bullet.WIDTH / 2;
        int bulletY = shooter.getY() + shooter.getHeight();
        bullets.add(new Bullet(bulletX, bulletY, GameConfig.ENEMY_BULLET_SPEED, BulletType.ENEMY_BULLET));
    }

    private void handleCollisions() {
        CollisionManager.CollisionResult result = collisionManager.handleCollisions(
                bullets,
                alienFleet.getAliens(),
                shields,
                player);

        for (Alien alien : result.getDestroyedAliens()) {
            scoreManager.addAlienScore(alien);
            createExplosion(alien);
            soundManager.play(GameConfig.SOUND_EXPLOSION);
        }

        if (result.isPlayerHit()) {
            gameState.loseLife();
            playerHitFlashTicks = GameConfig.PLAYER_HIT_FLASH_TICKS;
            soundManager.play(GameConfig.SOUND_PLAYER_HIT);
            if (gameState.hasNoLives()) {
                endGame();
            }
        }
    }

    private void checkEndConditions() {
        if (alienFleet.allAliensDestroyed()) {
            gameState.setStatus(GameState.Status.LEVEL_CLEARED);
            levelClearedTicks = GameConfig.LEVEL_CLEARED_MIN_TICKS;
            soundManager.play(GameConfig.SOUND_LEVEL_CLEAR);
            return;
        }

        int dangerLine = player.getY() - GameConfig.ALIEN_GAME_OVER_DISTANCE;
        if (alienFleet.hasReachedY(dangerLine) || alienFleet.hasReachedY(boardHeight)) {
            endGame();
        }
    }

    private void updateEffects() {
        if (playerHitFlashTicks > 0) {
            playerHitFlashTicks--;
        }

        Iterator<ExplosionEffect> iterator = explosions.iterator();
        while (iterator.hasNext()) {
            ExplosionEffect explosion = iterator.next();
            explosion.update();

            if (explosion.isFinished()) {
                iterator.remove();
            }
        }
    }

    private void createExplosion(Alien alien) {
        int x = alien.getX() + alien.getWidth() / 2;
        int y = alien.getY() + alien.getHeight() / 2;
        explosions.add(new ExplosionEffect(x, y, random));
    }

    private void endGame() {
        gameState.setFinalScore(scoreManager.getScore());
        highScoreManager.updateHighScore(scoreManager.getScore());
        gameState.setStatus(GameState.Status.GAME_OVER);
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

    public List<ExplosionEffect> getExplosions() {
        return explosions;
    }

    public HighScoreManager getHighScoreManager() {
        return highScoreManager;
    }

    public boolean isPlayerVisible() {
        return playerHitFlashTicks <= 0 || (playerHitFlashTicks / 5) % 2 == 0;
    }

    public boolean canAdvanceLevel() {
        return gameState.getStatus() == GameState.Status.LEVEL_CLEARED && levelClearedTicks <= 0;
    }
}
