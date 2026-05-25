import javax.swing.JFrame;

public class GameWindow {
    private final JFrame frame;

    public GameWindow() {
        frame = new JFrame("Space Invader Version 2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel panel = new GamePanel();
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    public void showWindow() {
        frame.setVisible(true);
    }
}
