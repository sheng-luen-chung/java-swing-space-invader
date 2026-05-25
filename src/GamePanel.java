import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel {
    public static final int WIDTH = GameConfig.BOARD_WIDTH;
    public static final int HEIGHT = GameConfig.BOARD_HEIGHT;

    private final GameEngine engine;
    private final GameRenderer renderer;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);

        engine = new GameEngine(WIDTH, HEIGHT);
        renderer = new GameRenderer();

        InputHandler inputHandler = new InputHandler(this, engine);
        inputHandler.registerKeyBindings();

        // Swing Timer is the game loop. It updates game data, then asks Swing to repaint.
        Timer timer = new Timer(GameConfig.FRAME_DELAY_MS, event -> {
            engine.update();
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render((Graphics2D) g, engine);
    }
}
