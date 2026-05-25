import java.awt.Rectangle;

public class Bullet {
    public static final int WIDTH = 5;
    public static final int HEIGHT = 14;

    private int x;
    private int y;
    private final int speed;

    public Bullet(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void update() {
        y -= speed;
    }

    public boolean isOffScreen() {
        return y + HEIGHT < 0;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
