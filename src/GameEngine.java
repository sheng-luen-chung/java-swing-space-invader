import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameEngine {
    private static final int PLAYER_SPEED = 6;
    private static final int BULLET_SPEED = 9;
    private static final int PLAYER_BOTTOM_MARGIN = 45;
    private static final int ALIEN_GAME_OVER_DISTANCE = 35;
    private static final int SCORE_PER_ALIEN = 10;

    private final int boardWidth;
    private final int boardHeight;
    private final GameState gameState;
    private final Player player;
    private final AlienFleet alienFleet;
    private final List<Bullet> bullets;
    private final CollisionManager collisionManager;

    private boolean movingLeft;
    private boolean movingRight;

    public GameEngine(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.gameState = new GameState();
        this.player = new Player(boardWidth / 2 - 25, boardHeight - PLAYER_BOTTOM_MARGIN, 50, 24);
        this.alienFleet = new AlienFleet();
        this.bullets = new ArrayList<>();
        this.collisionManager = new CollisionManager();
    }

    public void update() {
        if (!gameState.isPlaying()) {
            return;
        }

        updatePlayer();
        updateBullets();
        alienFleet.update(boardWidth);
        handleCollisions();
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

        // Limit one bullet on screen at a time to keep Version 1 simple.
        if (bullets.isEmpty()) {
            int bulletX = player.getCenterX() - Bullet.WIDTH / 2;
            int bulletY = player.getY() - Bullet.HEIGHT;
            bullets.add(new Bullet(bulletX, bulletY, BULLET_SPEED));
        }
    }

    public void restart() {
        gameState.reset();
        player.setX(boardWidth / 2 - player.getWidth() / 2);
        alienFleet.reset();
        bullets.clear();
        movingLeft = false;
        movingRight = false;
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

            if (bullet.isOffScreen()) {
                iterator.remove();
            }
        }
    }

    private void handleCollisions() {
        collisionManager.handleBulletAlienCollisions(
                bullets,
                alienFleet.getAliens(),
                () -> gameState.addScore(SCORE_PER_ALIEN));
    }

    private void checkEndConditions() {
        if (alienFleet.allAliensDestroyed()) {
            gameState.setStatus(GameState.Status.YOU_WIN);
            return;
        }

        int dangerLine = player.getY() - ALIEN_GAME_OVER_DISTANCE;
        if (alienFleet.hasReachedY(dangerLine) || alienFleet.hasReachedY(boardHeight)) {
            gameState.setStatus(GameState.Status.GAME_OVER);
        }
    }

    public GameState getGameState() {
        return gameState;
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
}
