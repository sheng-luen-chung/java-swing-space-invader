import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class GameRenderer {
    private static final Color BACKGROUND = new Color(12, 16, 28);
    private static final Color PLAYER_COLOR = new Color(86, 210, 132);
    private static final Color PLAYER_BULLET_COLOR = new Color(255, 230, 110);
    private static final Color ENEMY_BULLET_COLOR = new Color(104, 190, 255);
    private static final Color SHIELD_COLOR = new Color(88, 190, 172);
    private static final Color TEXT_COLOR = new Color(235, 240, 248);
    private static final Font HUD_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font MESSAGE_FONT = new Font("SansSerif", Font.BOLD, 46);

    public void render(Graphics2D g, GameEngine engine) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g);
        drawHud(g, engine);
        drawShields(g, engine);
        if (engine.isPlayerVisible()) {
            drawPlayer(g, engine.getPlayer());
        }
        drawAliens(g, engine.getAlienFleet());
        drawBullets(g, engine);
        drawExplosions(g, engine);
        drawOverlayMessage(g, engine);
    }

    private void drawBackground(Graphics2D g) {
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
    }

    private void drawHud(Graphics2D g, GameEngine engine) {
        GameState gameState = engine.getGameState();

        g.setColor(TEXT_COLOR);
        g.setFont(HUD_FONT);
        g.drawString("Score: " + engine.getScoreManager().getScore(), 18, 28);
        g.drawString("Level: " + engine.getLevelManager().getLevel(), 155, 28);
        g.drawString("Lives: " + gameState.getLives(), 260, 28);

        String controls = "Enter: Start/Restart   P: Pause   Space: Shoot";
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

            g.setColor(colorForAlien(alien));
            g.fillRoundRect(alien.getX(), alien.getY(), alien.getWidth(), alien.getHeight(), 8, 8);

            // Small eyes make the simple Java2D aliens easier to recognize.
            g.setColor(BACKGROUND);
            g.fillOval(alien.getX() + 10, alien.getY() + 9, 6, 6);
            g.fillOval(alien.getX() + alien.getWidth() - 16, alien.getY() + 9, 6, 6);
        }
    }

    private void drawBullets(Graphics2D g, GameEngine engine) {
        for (Bullet bullet : engine.getBullets()) {
            if (bullet.getType() == BulletType.PLAYER_BULLET) {
                g.setColor(PLAYER_BULLET_COLOR);
            } else {
                g.setColor(ENEMY_BULLET_COLOR);
            }

            g.fillRoundRect(bullet.getX(), bullet.getY(), Bullet.WIDTH, Bullet.HEIGHT, 4, 4);
        }
    }

    private void drawShields(Graphics2D g, GameEngine engine) {
        for (Shield shield : engine.getShields()) {
            if (shield.isDestroyed()) {
                continue;
            }

            int visibleHeight = (int) (shield.getHeight() * shield.getHealthRatio());
            int y = shield.getY() + shield.getHeight() - visibleHeight;

            g.setColor(SHIELD_COLOR);
            g.fillRoundRect(shield.getX(), y, shield.getWidth(), visibleHeight, 8, 8);

            // Cut a small notch so the wall reads like a bunker instead of a plain rectangle.
            g.setColor(BACKGROUND);
            g.fillRect(shield.getX() + shield.getWidth() / 2 - 12, y + visibleHeight - 12, 24, 12);
        }
    }

    private void drawExplosions(Graphics2D g, GameEngine engine) {
        for (ExplosionEffect explosion : engine.getExplosions()) {
            explosion.render(g);
        }
    }

    private void drawOverlayMessage(Graphics2D g, GameEngine engine) {
        GameState gameState = engine.getGameState();

        if (gameState.isPlaying()) {
            return;
        }

        if (gameState.getStatus() == GameState.Status.START_SCREEN) {
            drawStartScreen(g, engine);
            return;
        }

        String message = messageForStatus(gameState.getStatus());
        String hint = hintForStatus(gameState.getStatus());

        g.setFont(MESSAGE_FONT);
        FontMetrics metrics = g.getFontMetrics();
        int x = (GamePanel.WIDTH - metrics.stringWidth(message)) / 2;
        int y = GamePanel.HEIGHT / 2;

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, y - 70, GamePanel.WIDTH, 120);

        g.setColor(TEXT_COLOR);
        g.drawString(message, x, y);

        g.setFont(HUD_FONT);
        int hintX = (GamePanel.WIDTH - g.getFontMetrics().stringWidth(hint)) / 2;
        g.drawString(hint, hintX, y + 38);

        if (gameState.getStatus() == GameState.Status.GAME_OVER) {
            drawCenteredLine(g, "Final Score: " + gameState.getFinalScore(), y + 70);
            drawCenteredLine(g, "High Score: " + engine.getHighScoreManager().getHighScore(), y + 96);
        } else if (gameState.getStatus() == GameState.Status.LEVEL_CLEARED) {
            String nextText = engine.canAdvanceLevel() ? "Press Enter for level "
                    + (engine.getLevelManager().getLevel() + 1) : "Prepare for next level";
            drawCenteredLine(g, nextText, y + 70);
        }
    }

    private void drawStartScreen(Graphics2D g, GameEngine engine) {
        g.setColor(new Color(0, 0, 0, 175));
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        g.setFont(MESSAGE_FONT);
        g.setColor(TEXT_COLOR);
        drawCenteredLine(g, "SPACE INVADER", 155);

        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        drawCenteredLine(g, "High Score: " + engine.getHighScoreManager().getHighScore(), 205);

        g.setFont(HUD_FONT);
        drawCenteredLine(g, "Left / Right: Move", 270);
        drawCenteredLine(g, "Space: Shoot", 300);
        drawCenteredLine(g, "P: Pause", 330);
        drawCenteredLine(g, "Enter: Start", 390);
    }

    private void drawCenteredLine(Graphics2D g, String text, int y) {
        int x = (GamePanel.WIDTH - g.getFontMetrics().stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    private Color colorForAlien(Alien alien) {
        if (alien.getScoreValue() >= 40) {
            return new Color(255, 112, 112);
        }

        if (alien.getScoreValue() >= 30) {
            return new Color(255, 166, 94);
        }

        if (alien.getScoreValue() >= 20) {
            return new Color(232, 206, 86);
        }

        return new Color(167, 112, 255);
    }

    private String messageForStatus(GameState.Status status) {
        if (status == GameState.Status.START_SCREEN) {
            return "SPACE INVADER";
        }

        if (status == GameState.Status.PAUSED) {
            return "PAUSED";
        }

        if (status == GameState.Status.LEVEL_CLEARED) {
            return "LEVEL CLEARED";
        }

        return "GAME OVER";
    }

    private String hintForStatus(GameState.Status status) {
        if (status == GameState.Status.START_SCREEN) {
            return "Press Enter to start";
        }

        if (status == GameState.Status.PAUSED) {
            return "Press P to continue";
        }

        if (status == GameState.Status.LEVEL_CLEARED) {
            return "Wave destroyed";
        }

        return "Press Enter to restart";
    }
}
