import bagel.*;
import java.util.Properties;

public class EnemyCar extends InvaderCar {
    private final Image SPRITE;
    private static final int SPAWN_RATE = 400;

    // Constructor
    public EnemyCar(Properties gameProps) {
        super(gameProps);
        SPRITE = new Image(gameProps.getProperty("gameObjects.enemyCar.image"));
    }

    // Getters and Setters

    public static int getSpawnRate() {
        return SPAWN_RATE;
    }

    // PUBLIC METHODS

    /**
     * Implement logic for rendering an 'EnemyCar' entity.
     * To be called in the main update() method.
     * @param input The current mouse/keyboard input.
     */
    @Override
    public void update(Input input) {
        move(input);

        // Implement collision timeout
        if (isInCollisionTimeout()){
            renderCollisionTimeout();
        }

        // Render EnemyCar
        if (!isTerminated()) {
            SPRITE.draw(getX(), getY());
        }
    }

}
