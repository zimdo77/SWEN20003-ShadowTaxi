import bagel.*;
import java.util.ArrayList;
import java.util.Properties;

public class Coin extends Powerup implements TextDisplayable {
    private final Image SPRITE;
    private final int MAX_FRAMES_IN_EFFECT;
    private final int FRAME_COUNT_FONT_SIZE;
    private final int FRAME_COUNT_X;
    private final int FRAME_COUNT_Y;

    // Track currently active coin
    private static Coin activeCoin;

    // Utility/helper objects
    private final RenderText renderText;

    // Constructor
    public Coin(double x, double y, Properties gameProps) {
        super(x, y, gameProps);

        SPRITE = new Image(gameProps.getProperty("gameObjects.coin.image"));
        MAX_FRAMES_IN_EFFECT = Integer.parseInt(gameProps.getProperty("gameObjects.coin.maxFrames"));
        FRAME_COUNT_FONT_SIZE = Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize"));
        FRAME_COUNT_X = Integer.parseInt(gameProps.getProperty("gameplay.coin.x"));
        FRAME_COUNT_Y = Integer.parseInt(gameProps.getProperty("gameplay.coin.y"));

        renderText = new RenderText(gameProps);
    }

    // Getters and Setters

    public static Coin getActiveCoin() {
        return activeCoin;
    }

    // PUBLIC METHODS

    /**
     * Implement logic for rendering a coin entity.
     * To be called in the main update() method.
     * @param input The current mouse/keyboard input.
     * @param playableEntities Entities that can collect the power-ups (Taxi and Driver)
     */
    @Override
    public void update(Input input, ArrayList<Playable> playableEntities) {
        // Activate the coin's power if it has collided with an entity
        for (Playable entity : playableEntities) {
            if (hasCollidedWith(entity)) {
                activate();
            }
        }

        // If the coin is still active, display the number of frames that the coin has been active for
        if (isActive()) {
            if (getFrameCount() <= MAX_FRAMES_IN_EFFECT) {
                displayTextInfo();
            } else {
                deactivate();
            }

            setFrameCount(getFrameCount() + 1);
        }

        // Implement coin movement relative to the player
        moveRelativeToPlayer(input);

        // Only render coin if it hasn't been used
        if (!wasUsed()) {
            SPRITE.draw(getX(), getY());
        }

    }

    /**
     * Display the number of frames that the coin has been active for
     */
    @Override
    public void displayTextInfo() {
        renderText.drawText(getFrameCount() + "", FRAME_COUNT_FONT_SIZE, FRAME_COUNT_X, FRAME_COUNT_Y);
    }

    @Override
    public Powerup getActivePowerup() {
        return activeCoin;
    }

    @Override
    public void setActivePowerup(Powerup powerup) {
        assert (powerup instanceof Coin);
        activeCoin = (Coin) powerup;
    }



}
