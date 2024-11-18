import bagel.*;
import java.util.Properties;

public class HomeScreen {
    private final Image BACKGROUND;
    private final String TITLE;
    private final String INSTRUCTION;
    private final int TITLE_FONT_SIZE;
    private final int INSTRUCTION_FONT_SIZE;
    private final int TITLE_Y;
    private final int INSTRUCTION_Y;

    // Utility/helper objects
    private final RenderText renderText;

    // Constructor
    public HomeScreen(Properties gameProps, Properties messageProps) {
        BACKGROUND = new Image(gameProps.getProperty("backgroundImage.home"));
        TITLE = messageProps.getProperty("home.title");
        INSTRUCTION = messageProps.getProperty("home.instruction");
        TITLE_FONT_SIZE = Integer.parseInt(gameProps.getProperty("home.title.fontSize"));
        INSTRUCTION_FONT_SIZE = Integer.parseInt(gameProps.getProperty("home.instruction.fontSize"));
        TITLE_Y = Integer.parseInt(gameProps.getProperty("home.title.y"));
        INSTRUCTION_Y = Integer.parseInt(gameProps.getProperty("home.instruction.y"));
        renderText = new RenderText(gameProps);
    }

    // PUBLIC METHODS

    /**
     * Implement all home screen elements.
     * To be called in the main update() method.
     */
    public void update() {
        // Render home screen background
        BACKGROUND.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

        // Render home screen text
        renderText.drawCenteredText(TITLE, TITLE_FONT_SIZE, TITLE_Y);
        renderText.drawCenteredText(INSTRUCTION, INSTRUCTION_FONT_SIZE, INSTRUCTION_Y);

    }
}
