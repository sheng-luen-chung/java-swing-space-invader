import java.awt.Rectangle;

public class Bullet {
    public static final int WIDTH = 5;
    public static final int HEIGHT = 14;

    private int x;
    private int y;
    private final int speed;
    private final BulletType type;

    public Bullet(int x, int y, int speed, BulletType type) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.type = type;
    }

    public void update() {
        if (type == BulletType.PLAYER_BULLET) {
            y -= speed;
        } else {
            y += speed;
        }
    }

    public boolean isOffScreen(int boardHeight) {
        return y + HEIGHT < 0 || y > boardHeight;
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

    public BulletType getType() {
        return type;
    }
}
