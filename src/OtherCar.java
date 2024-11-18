import bagel.*;
import java.util.Properties;

public class OtherCar extends InvaderCar {
    private final Image SPRITE;
    private static final int SPAWN_RATE = 200;

    // Constructor
    public OtherCar(Properties gameProps) {
        super(gameProps);
        SPRITE = new Image(chooseSprite(gameProps));
    }

    // Getters and Setters

    public static int getSpawnRate() {
        return SPAWN_RATE;
    }

    // PUBLIC METHODS

    /**
     * Implement logic for rendering an 'OtherCar' entity.
     * To be called in the main update() method.
     * @param input The current mouse/keyboard input.
     */
    @Override
    public void update(Input input) {
        move(input);

        // Implement collision timeout
        if (isInCollisionTimeout()) {
            renderCollisionTimeout();
        }

        // Render OtherCar
        if (!isTerminated()) {
            SPRITE.draw(getX(), getY());
        }
    }

    // HELPER METHODS

    /**
     * Randomly choose between the two random OtherCar sprites.
     * @param gameProps Property file.
     * @return the name of the sprite resource file.
     */
    private String chooseSprite(Properties gameProps) {
        int randomInt = MiscUtils.selectAValue(1, 2);
        return String.format(gameProps.getProperty("gameObjects.otherCar.image"), randomInt);
    }

}
