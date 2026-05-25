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
                aliens.add(new Alien(x, y, ALIEN_WIDTH, ALIEN_HEIGHT, scoreForRow(row)));
            }
        }
    }

    public void update(int boardWidth, int horizontalSpeed) {
        boolean shouldDrop = willHitSide(boardWidth, horizontalSpeed);
        int dx = shouldDrop ? 0 : direction * horizontalSpeed;
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

    private boolean willHitSide(int boardWidth, int horizontalSpeed) {
        for (Alien alien : aliens) {
            if (!alien.isAlive()) {
                continue;
            }

            int nextX = alien.getX() + direction * horizontalSpeed;
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

    public Alien getRandomShooter(java.util.Random random) {
        List<Alien> candidates = new ArrayList<>();

        for (Alien alien : aliens) {
            if (alien.isAlive() && isFrontAlien(alien)) {
                candidates.add(alien);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get(random.nextInt(candidates.size()));
    }

    private boolean isFrontAlien(Alien candidate) {
        int candidateCenterX = candidate.getX() + candidate.getWidth() / 2;

        for (Alien alien : aliens) {
            if (!alien.isAlive() || alien == candidate) {
                continue;
            }

            int alienCenterX = alien.getX() + alien.getWidth() / 2;
            boolean sameColumn = Math.abs(alienCenterX - candidateCenterX) < ALIEN_WIDTH / 2;
            if (sameColumn && alien.getY() > candidate.getY()) {
                return false;
            }
        }

        return true;
    }

    private int scoreForRow(int row) {
        return (ROWS - row) * 10;
    }
}
