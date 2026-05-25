import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class InputHandler {
    private final JComponent component;
    private final GameEngine engine;

    public InputHandler(JComponent component, GameEngine engine) {
        this.component = component;
        this.engine = engine;
    }

    public void registerKeyBindings() {
        bind("pressed LEFT", "leftPressed", () -> engine.setMovingLeft(true));
        bind("released LEFT", "leftReleased", () -> engine.setMovingLeft(false));
        bind("pressed RIGHT", "rightPressed", () -> engine.setMovingRight(true));
        bind("released RIGHT", "rightReleased", () -> engine.setMovingRight(false));
        bind("pressed SPACE", "shoot", engine::shoot);
        bind("pressed ENTER", "enter", this::handleEnter);
        bind("pressed P", "pause", engine::togglePause);
        bind("pressed R", "restart", engine::restart);
    }

    private void handleEnter() {
        if (engine.getGameState().getStatus() == GameState.Status.START_SCREEN) {
            engine.startGame();
        } else if (engine.getGameState().getStatus() == GameState.Status.GAME_OVER) {
            engine.restart();
        }
    }

    private void bind(String keyStrokeText, String actionName, Runnable action) {
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(keyStrokeText), actionName);

        component.getActionMap().put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                action.run();
            }
        });
    }
}
