import bagel.*;
import bagel.util.*;
import java.util.Properties;

public class PlayerInfoScreen {
    private final Image BACKGROUND;
    private final int PLAYER_INFO_FONT_SIZE;
    private final String PLAYER_NAME_INSTRUCTION;
    private final String START_GAME_INSTRUCTION;
    private final int PLAYER_NAME_INSTRUCTION_Y;
    private final int START_GAME_INSTRUCTION_Y;
    private final int PLAYER_NAME_INPUT_Y;

    private static final Colour PLAYER_NAME_COLOUR = Colour.BLACK;

    private String playerName;

    // Utility/helper objects
    private final RenderText renderText;

    // Constructor
    public PlayerInfoScreen(Properties gameProps, Properties messageProps) {
        BACKGROUND = new Image(gameProps.getProperty("backgroundImage.playerInfo"));
        PLAYER_INFO_FONT_SIZE = Integer.parseInt(gameProps.getProperty("playerInfo.fontSize"));
        PLAYER_NAME_INSTRUCTION = messageProps.getProperty("playerInfo.playerName");
        START_GAME_INSTRUCTION = messageProps.getProperty("playerInfo.start");
        PLAYER_NAME_INSTRUCTION_Y = Integer.parseInt(gameProps.getProperty("playerInfo.playerName.y"));
        START_GAME_INSTRUCTION_Y = Integer.parseInt(gameProps.getProperty("playerInfo.start.y"));
        PLAYER_NAME_INPUT_Y = Integer.parseInt(gameProps.getProperty("playerInfo.playerNameInput.y"));
        playerName = "";
        renderText = new RenderText(gameProps);
    }

    // Getters and Setters
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Implement all player info screen elements.
     * To be called in the main update() method.
     * @param input The current mouse/keyboard input.
     */
    public void update(Input input) {
        // Render player info screen background
        BACKGROUND.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);

        // Render player info screen text
        renderText.drawCenteredText(PLAYER_NAME_INSTRUCTION, PLAYER_INFO_FONT_SIZE, PLAYER_NAME_INSTRUCTION_Y);
        renderText.drawCenteredText(START_GAME_INSTRUCTION, PLAYER_INFO_FONT_SIZE, START_GAME_INSTRUCTION_Y);

        // Get username input and render on screen
        String keyPress = MiscUtils.getKeyPress(input);
        if (keyPress != null) {
            playerName += keyPress;
        }
        if ((input.wasPressed(Keys.BACKSPACE) || input.wasPressed(Keys.DELETE)) && !playerName.isEmpty()) {
            playerName = playerName.substring(0, playerName.length() - 1);
        }
        renderText.drawCenteredText(playerName, PLAYER_INFO_FONT_SIZE, PLAYER_NAME_INPUT_Y, PLAYER_NAME_COLOUR);
    }

}
