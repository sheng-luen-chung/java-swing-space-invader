public class GameState {
    public enum Status {
        PLAYING,
        YOU_WIN,
        GAME_OVER
    }

    private Status status;
    private int score;

    public GameState() {
        reset();
    }

    public void reset() {
        status = Status.PLAYING;
        score = 0;
    }

    public boolean isPlaying() {
        return status == Status.PLAYING;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int amount) {
        score += amount;
    }
}
