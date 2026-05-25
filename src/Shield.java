import java.awt.Rectangle;

public class Shield {
    private static final int MAX_HEALTH = 6;

    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private int health;

    public Shield(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.health = MAX_HEALTH;
    }

    public void damage() {
        if (!isDestroyed()) {
            health--;
        }
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public double getHealthRatio() {
        return Math.max(0, health) / (double) MAX_HEALTH;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
