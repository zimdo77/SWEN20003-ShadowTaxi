import bagel.*;

/**
 * Class to manage game states/screens.
 */

public class GameState {
    public enum State {
        HOME_SCREEN, PLAYER_INFO_SCREEN, GAME_PLAY_SCREEN, GAME_END_SCREEN
    }

    private State currentState;

    // Constructor
    public GameState() {
        currentState = State.HOME_SCREEN;
    }

    // Getters and Setters
    public State getCurrentState() {
        return currentState;
    }

    /**
     * Implement logic for transitioning game states/screens.
     * To be called in the main update() method.
     * @param input The current mouse/keyboard input.
     * @param game Associated Game object.
     */
    public void update(Input input, ShadowTaxi game) {
        if (shouldTransition(input, game)) {
            transitionState();
        }
    }

    // PUBLIC METHODS

    /**
     * Check if the current state/screen is the game end screen.
     * @return Returns true if it is. False otherwise.
     */
    public boolean isGameEndScreen() {
        return currentState == State.GAME_END_SCREEN;
    }

    // HELPER METHODS

    /**
     * Determine if a state transition should occur based on the current state.
     * @return True if transition should occur. False otherwise.
     */
    private boolean shouldTransition(Input input, ShadowTaxi game) {
        return switch (currentState) {
            case HOME_SCREEN, PLAYER_INFO_SCREEN -> input.wasPressed(Keys.ENTER);
            case GAME_PLAY_SCREEN -> game.getGamePlayScreen().hasFinishedGame();
            case GAME_END_SCREEN -> input.wasPressed(Keys.SPACE);
            default -> false;
        };
    }

    /**
     * Transition to the next game state/screen depending on the current state/screen.
     */
    private void transitionState() {
        switch (currentState) {
            case HOME_SCREEN:
                currentState = State.PLAYER_INFO_SCREEN;
                break;
            case PLAYER_INFO_SCREEN:
                currentState = State.GAME_PLAY_SCREEN;
                break;
            case GAME_PLAY_SCREEN:
                currentState = State.GAME_END_SCREEN;
                break;
            case GAME_END_SCREEN:
                currentState = State.HOME_SCREEN;

        }
    }

}
