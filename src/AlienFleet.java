import java.util.ArrayList;
import java.util.List;

public class AlienFleet {
    private static final int ROWS = 4;
    private static final int COLUMNS = 9;
    private static final int ALIEN_WIDTH = 42;
    private static final int ALIEN_HEIGHT = 28;
    private static final int GAP_X = 22;
    private static final int GAP_Y = 18;
    private static final int START_X = 95;
    private static final int START_Y = 70;
    private static final int HORIZONTAL_SPEED = 2;
    private static final int DROP_DISTANCE = 24;

    private final List<Alien> aliens;
    private int direction;

    public AlienFleet() {
        aliens = new ArrayList<>();
        reset();
    }

    public void reset() {
        aliens.clear();
        direction = 1;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int x = START_X + col * (ALIEN_WIDTH + GAP_X);
                int y = START_Y + row * (ALIEN_HEIGHT + GAP_Y);
                aliens.add(new Alien(x, y, ALIEN_WIDTH, ALIEN_HEIGHT));
            }
        }
    }

    public void update(int boardWidth) {
        boolean shouldDrop = willHitSide(boardWidth);
        int dx = shouldDrop ? 0 : direction * HORIZONTAL_SPEED;
        int dy = shouldDrop ? DROP_DISTANCE : 0;

        if (shouldDrop) {
            direction *= -1;
        }

        for (Alien alien : aliens) {
            if (alien.isAlive()) {
                alien.move(dx, dy);
            }
        }
    }

    private boolean willHitSide(int boardWidth) {
        for (Alien alien : aliens) {
            if (!alien.isAlive()) {
                continue;
            }

            int nextX = alien.getX() + direction * HORIZONTAL_SPEED;
            if (nextX <= 0 || nextX + alien.getWidth() >= boardWidth) {
                return true;
            }
        }

        return false;
    }

    public boolean allAliensDestroyed() {
        for (Alien alien : aliens) {
            if (alien.isAlive()) {
                return false;
            }
        }

        return true;
    }

    public boolean hasReachedY(int y) {
        for (Alien alien : aliens) {
            if (alien.isAlive() && alien.getY() + alien.getHeight() >= y) {
                return true;
            }
        }

        return false;
    }

    public List<Alien> getAliens() {
        return aliens;
    }
}
