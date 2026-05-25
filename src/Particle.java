import java.awt.Color;
import java.awt.Graphics2D;

public class Particle {
    private double x;
    private double y;
    private final double vx;
    private final double vy;
    private final int maxLife;
    private final Color color;
    private int life;

    public Particle(double x, double y, double vx, double vy, int life, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.life = life;
        this.maxLife = life;
        this.color = color;
    }

    public void update() {
        x += vx;
        y += vy;
        life--;
    }

    public boolean isAlive() {
        return life > 0;
    }

    public void render(Graphics2D g) {
        float alpha = Math.max(0.1f, life / (float) maxLife);
        int radius = 3 + (maxLife - life) / 6;
        Color faded = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255));

        g.setColor(faded);
        g.fillOval((int) x - radius, (int) y - radius, radius * 2, radius * 2);
    }
}
