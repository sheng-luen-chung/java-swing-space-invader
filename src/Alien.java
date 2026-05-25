import java.awt.Rectangle;

public class Alien {
    private int x;
    private int y;
    private final int width;
    private final int height;
    private final int scoreValue;
    private boolean alive;

    public Alien(int x, int y, int width, int height, int scoreValue) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.scoreValue = scoreValue;
        this.alive = true;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isAlive() {
        return alive;
    }

    public void destroy() {
        alive = false;
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

    public int getScoreValue() {
        return scoreValue;
    }
}
