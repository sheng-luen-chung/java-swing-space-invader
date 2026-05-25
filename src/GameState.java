public class GameState {
    public enum Status {
        START_SCREEN,
        PLAYING,
        PAUSED,
        LEVEL_CLEARED,
        GAME_OVER
    }

    private Status status;
    private int lives;

    public GameState() {
        reset();
    }

    public void reset() {
        status = Status.START_SCREEN;
        lives = 3;
    }

    public boolean isPlaying() {
        return status == Status.PLAYING;
    }

    public boolean isPaused() {
        return status == Status.PAUSED;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        if (lives > 0) {
            lives--;
        }
    }

    public boolean hasNoLives() {
        return lives <= 0;
    }
}
