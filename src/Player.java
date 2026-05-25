import java.awt.Rectangle;

public class Player {
    private int x;
    private final int y;
    private final int width;
    private final int height;

    public Player(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void moveLeft(int speed) {
        x = Math.max(0, x - speed);
    }

    public void moveRight(int speed, int boardWidth) {
        x = Math.min(boardWidth - width, x + speed);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getCenterX() {
        return x + width / 2;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
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
