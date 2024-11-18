import bagel.*;
import java.util.Properties;

/**
 * Skeleton Code for SWEN20003 Project 2, Semester 2, 2024
 * Please enter your name below
 * @author Quoc Khang Do
 */
public class ShadowTaxi extends AbstractGame {

    // Property files
    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    // Manage state of the game
    private final GameState gameState;

    // Screens
    private HomeScreen homeScreen;
    private PlayerInfoScreen playerInfoScreen;
    private GamePlayScreen gamePlayScreen;
    private GameEndScreen gameEndScreen;

    // Constructor
    public ShadowTaxi(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("home.title"));

        // Store input files
        GAME_PROPS = gameProps;
        MESSAGE_PROPS = messageProps;

        // Instantiate utility/helper objects
        gameState = new GameState();
    }

    // Getters & Setters

    public GamePlayScreen getGamePlayScreen() {
        return gamePlayScreen;
    }

    /**
     * Render the relevant screens and game objects for each game state (home, player info, gameplay, etc.)
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {

        // Terminate window if Escape key is pressed
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        // Update the game state depending on keyboard input
        gameState.update(input, this);

        // Render relevant screen depending on the game state
        switch(gameState.getCurrentState()) {
            case HOME_SCREEN:
                if (homeScreen == null) {
                    homeScreen = new HomeScreen(GAME_PROPS, MESSAGE_PROPS);
                    gameEndScreen = null;
                }
                homeScreen.update();
                break;

            case PLAYER_INFO_SCREEN:
                if (playerInfoScreen == null) {
                    playerInfoScreen = new PlayerInfoScreen(GAME_PROPS, MESSAGE_PROPS);
                    homeScreen = null;
                }
                playerInfoScreen.update(input);
                  break;

            case GAME_PLAY_SCREEN:
                if (gamePlayScreen == null) {
                    gamePlayScreen = new GamePlayScreen(GAME_PROPS, MESSAGE_PROPS, playerInfoScreen.getPlayerName());
                    playerInfoScreen = null;
                }
                gamePlayScreen.update(input);
                break;

            case GAME_END_SCREEN:
                if (gameEndScreen == null) {
                    gameEndScreen = new GameEndScreen(GAME_PROPS, MESSAGE_PROPS, gamePlayScreen.hasWonGame());
                    gamePlayScreen = null;
                }
                gameEndScreen.update();
                break;
        }
    }


    // MAIN
    public static void main(String[] args) {
        Properties game_props = IOUtils.readPropertiesFile("res/app.properties");
        Properties message_props = IOUtils.readPropertiesFile("res/message_en.properties");
        ShadowTaxi game = new ShadowTaxi(game_props, message_props);
        game.run();
    }

}
