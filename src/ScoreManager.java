public class ScoreManager {
    private int score;

    public void reset() {
        score = 0;
    }

    public void addAlienScore(Alien alien) {
        score += alien.getScoreValue();
    }

    public int getScore() {
        return score;
    }
}
