import bagel.*;
import bagel.util.*;
import java.util.Properties;

/**
 * Class for rendering text on screen.
 */

public class RenderText {
    private final int WINDOW_WIDTH;
    private final String FONT;

    // Utility/helper objects
    private final DrawOptions drawOptions;

    // Constructor
    public RenderText(Properties gameProps) {
        WINDOW_WIDTH = Integer.parseInt(gameProps.getProperty("window.width"));
        FONT = gameProps.getProperty("font");
        drawOptions = new DrawOptions();
    }

    /**
     * Find the x coordinate that will center a rendered string/text horizontally on a window.
     * @param font Font object.
     * @param text String/text to be displayed.
     * @return The x coordinate that will center the rendered string/text.
     */
    public double getCenteredX(Font font, String text) {
        return (WINDOW_WIDTH - font.getWidth(text)) / 2.0;
    }

    /**
     * Render text on screen that is horizontally centered on the window.
     * @param message String (text) to be displayed
     * @param fontSize Font size
     * @param y Y-coordinate of bottom left of String
     */
    public void drawCenteredText(String message, int fontSize, int y) {
        Font font = new Font(FONT, fontSize);
        font.drawString(message, getCenteredX(font, message), y);
    }

    /**
     * Overloaded drawCenteredText() with an extra parameter to choose colour.
     * @param message String (text) to be displayed
     * @param fontSize Font size
     * @param y Y-coordinate of bottom left of String
     * @param colour Colour of rendered text
     */
    public void drawCenteredText(String message, int fontSize, double y, Colour colour) {
        Font font = new Font(FONT, fontSize);
        font.drawString(message, getCenteredX(font, message), y, drawOptions.setBlendColour(colour));
    }

    /**
     * A more general version of drawCenteredText(). Takes in an additional parameter for the x coordinate instead
     * of centering it horizontally on the window.
     * @param message String (text) to be displayed
     * @param fontSize Font size
     * @param x X-coordinate of bottom left of String
     * @param y Y-coordinate of bottom left of String
     */
    public void drawText(String message, int fontSize, double x, double y) {
        Font font = new Font(FONT, fontSize);
        font.drawString(message, x, y);
    }


}
