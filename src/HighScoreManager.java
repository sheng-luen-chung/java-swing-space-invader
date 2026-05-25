import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class HighScoreManager {
    private final Path highScoreFile;
    private int highScore;

    public HighScoreManager() {
        this.highScoreFile = GameConfig.HIGH_SCORE_FILE;
        load();
    }

    public int getHighScore() {
        return highScore;
    }

    public void updateHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            save();
        }
    }

    private void load() {
        try {
            Path parent = highScoreFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (!Files.exists(highScoreFile)) {
                highScore = 0;
                save();
                return;
            }

            String text = new String(Files.readAllBytes(highScoreFile), StandardCharsets.UTF_8).trim();
            highScore = text.isEmpty() ? 0 : Integer.parseInt(text);
        } catch (Exception ex) {
            highScore = 0;
            System.err.println("Warning: could not load high score: " + ex.getMessage());
        }
    }

    private void save() {
        try {
            Path parent = highScoreFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            byte[] bytes = Integer.toString(highScore).getBytes(StandardCharsets.UTF_8);
            Files.write(highScoreFile, bytes);
        } catch (IOException ex) {
            System.err.println("Warning: could not save high score: " + ex.getMessage());
        }
    }
}
