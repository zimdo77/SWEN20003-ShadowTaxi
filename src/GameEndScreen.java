import bagel.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Properties;

public class GameEndScreen {
    private final Image BACKGROUND;
    private final String SCORES_TITLE;
    private final String WIN_MESSAGE;
    private final String LOSS_MESSAGE;
    private final int SCORES_TITLE_FONT_SIZE;
    private final int SCORES_FONT_SIZE;
    private final int MESSAGE_FONT_SIZE;
    private final int SCORES_TITLE_Y;
    private final int SCORES_Y;
    private final int MESSAGE_Y;
    private final boolean WON_GAME;
    private final String[][] SCORES;


    private static final int SCORES_TEXT_SPACE = 40;
    private static final int MAX_SCORES_TO_DISPLAY = 5;
    private static final int CSV_NAME_INDEX =  0;
    private static final int CSV_SCORE_INDEX = 1;

    // Utility/helper objects
    private final RenderText renderText;
    private final DecimalFormat df;

    // Constructor
    public GameEndScreen(Properties gameProps, Properties messageProps, boolean wonGame) {
        BACKGROUND = new Image(gameProps.getProperty("backgroundImage.gameEnd"));
        SCORES_TITLE = messageProps.getProperty("gameEnd.highestScores");
        WIN_MESSAGE = messageProps.getProperty("gameEnd.won");
        LOSS_MESSAGE = messageProps.getProperty("gameEnd.lost");
        SCORES_TITLE_FONT_SIZE = Integer.parseInt(gameProps.getProperty("gameEnd.scores.fontSize"));
        SCORES_FONT_SIZE = Integer.parseInt(gameProps.getProperty("gameEnd.scores.fontSize"));
        MESSAGE_FONT_SIZE = Integer.parseInt(gameProps.getProperty("gameEnd.status.fontSize"));
        SCORES_TITLE_Y = Integer.parseInt(gameProps.getProperty("gameEnd.scores.y"));
        SCORES_Y = Integer.parseInt(gameProps.getProperty("gameEnd.scores.y"));
        MESSAGE_Y = Integer.parseInt(gameProps.getProperty("gameEnd.status.y"));
        SCORES = IOUtils.readCommaSeparatedFile(gameProps.getProperty("gameEnd.scoresFile"));
        WON_GAME = wonGame;

        renderText = new RenderText(gameProps);
        df = new DecimalFormat("0.00");
    }

    // PUBLIC METHODS

    /**
     * Implement all game end screen elements.
     * To be called in the main update() method.
     */
    public void update() {
        // Render game end screen background
        BACKGROUND.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

        // Render top 5 scores
        renderText.drawCenteredText(SCORES_TITLE, SCORES_TITLE_FONT_SIZE, SCORES_TITLE_Y);
        displayScores();

        // Render win/loss message
        if (WON_GAME) {
            renderText.drawCenteredText(WIN_MESSAGE, MESSAGE_FONT_SIZE, MESSAGE_Y);
        } else {
            renderText.drawCenteredText(LOSS_MESSAGE, MESSAGE_FONT_SIZE, MESSAGE_Y);
        }
    }

    // HELPER METHODS

    /**
     * Render the top scores on screen
     */
    private void displayScores() {
        sortScores(SCORES);

        int scoresToDisplay = Math.min(SCORES.length, MAX_SCORES_TO_DISPLAY);

        for (int i = 0; i < scoresToDisplay; i++) {
            String playerName = SCORES[i][CSV_NAME_INDEX];
            String playerScore = df.format(Double.parseDouble(SCORES[i][CSV_SCORE_INDEX]));
            renderText.drawCenteredText(playerName + " - " + playerScore, SCORES_FONT_SIZE,
                    SCORES_Y + (i + 1) * SCORES_TEXT_SPACE);
        }
    }

    /**
     * Sort a 2D array, where each element of the array is a sub-array of format [PLAYER, SCORE].
     * Sort by the second column "SCORE" in descending order.
     * @param scores The input 2D array.
     */
    private void sortScores(String[][] scores) {
        if (scores.length > 1) {
            Arrays.sort(scores, (a, b) -> Double.compare(Double.parseDouble(b[CSV_SCORE_INDEX]),
                    Double.parseDouble(a[CSV_SCORE_INDEX])));
        }
    }

}
