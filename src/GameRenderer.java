import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class GameRenderer {
    private static final Color BACKGROUND = new Color(12, 16, 28);
    private static final Color PLAYER_COLOR = new Color(86, 210, 132);
    private static final Color ALIEN_COLOR = new Color(236, 92, 99);
    private static final Color BULLET_COLOR = new Color(255, 230, 110);
    private static final Color TEXT_COLOR = new Color(235, 240, 248);
    private static final Font HUD_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font MESSAGE_FONT = new Font("SansSerif", Font.BOLD, 46);

    public void render(Graphics2D g, GameEngine engine) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g);
        drawHud(g, engine.getGameState());
        drawPlayer(g, engine.getPlayer());
        drawAliens(g, engine.getAlienFleet());
        drawBullets(g, engine);
        drawEndMessage(g, engine.getGameState());
    }

    private void drawBackground(Graphics2D g) {
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
    }

    private void drawHud(Graphics2D g, GameState gameState) {
        g.setColor(TEXT_COLOR);
        g.setFont(HUD_FONT);
        g.drawString("Score: " + gameState.getScore(), 18, 28);

        String controls = "Move: Left / Right   Shoot: Space   Restart: R";
        int controlsX = GamePanel.WIDTH - g.getFontMetrics().stringWidth(controls) - 18;
        g.drawString(controls, controlsX, 28);
    }

    private void drawPlayer(Graphics2D g, Player player) {
        int x = player.getX();
        int y = player.getY();
        int width = player.getWidth();
        int height = player.getHeight();

        g.setColor(PLAYER_COLOR);
        int[] shipX = { x, x + width / 2, x + width };
        int[] shipY = { y + height, y, y + height };
        g.fillPolygon(shipX, shipY, 3);

        g.setColor(TEXT_COLOR);
        g.setStroke(new BasicStroke(2));
        g.drawPolygon(shipX, shipY, 3);
    }

    private void drawAliens(Graphics2D g, AlienFleet fleet) {
        for (Alien alien : fleet.getAliens()) {
            if (!alien.isAlive()) {
                continue;
            }

            g.setColor(ALIEN_COLOR);
            g.fillRoundRect(alien.getX(), alien.getY(), alien.getWidth(), alien.getHeight(), 8, 8);

            // Small eyes make the simple Java2D aliens easier to recognize.
            g.setColor(BACKGROUND);
            g.fillOval(alien.getX() + 10, alien.getY() + 9, 6, 6);
            g.fillOval(alien.getX() + alien.getWidth() - 16, alien.getY() + 9, 6, 6);
        }
    }

    private void drawBullets(Graphics2D g, GameEngine engine) {
        g.setColor(BULLET_COLOR);
        for (Bullet bullet : engine.getBullets()) {
            g.fillRoundRect(bullet.getX(), bullet.getY(), Bullet.WIDTH, Bullet.HEIGHT, 4, 4);
        }
    }

    private void drawEndMessage(Graphics2D g, GameState gameState) {
        if (gameState.isPlaying()) {
            return;
        }

        String message = gameState.getStatus() == GameState.Status.YOU_WIN ? "YOU WIN" : "GAME OVER";

        g.setFont(MESSAGE_FONT);
        FontMetrics metrics = g.getFontMetrics();
        int x = (GamePanel.WIDTH - metrics.stringWidth(message)) / 2;
        int y = GamePanel.HEIGHT / 2;

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, y - 70, GamePanel.WIDTH, 120);

        g.setColor(TEXT_COLOR);
        g.drawString(message, x, y);

        g.setFont(HUD_FONT);
        String restart = "Press R to restart";
        int restartX = (GamePanel.WIDTH - g.getFontMetrics().stringWidth(restart)) / 2;
        g.drawString(restart, restartX, y + 38);
    }
}
