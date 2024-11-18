import bagel.*;
import bagel.util.*;
import java.util.Properties;

public abstract class GameObject {
    private final int SPEED_RELATIVE_TO_PLAYER;
    private static final int HEALTH_MULTIPLIER = 100;
    private static final int COLLISION_TIMEOUT_FRAMES = 200;
    private static final int INITIAL_TIMEOUT_FRAMES = 10;

    private double x;
    private double y;

    // Constructor
    public GameObject(double x, double y, Properties gameProps)  {
        SPEED_RELATIVE_TO_PLAYER = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.speedY"));
        this.x = x;
        this.y = y;
    }

    // Getters and Setters

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Point getPosition() {
        return new Point(x, y);
    }

    public static int getHealthMultiplier() {
        return HEALTH_MULTIPLIER;
    }

    public static int getCollisionTimeoutFrames() {
        return COLLISION_TIMEOUT_FRAMES;
    }

    public static int getInitialTimeoutFrames() {
        return INITIAL_TIMEOUT_FRAMES;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    // PUBLIC METHODS

    /**
     * When player (taxi or driver) is moving, stationary objects move relative to the player
     * @param input The current mouse/keyboard input.
     */
    public void moveRelativeToPlayer(Input input) {
        if (input.isDown(Keys.UP)) {
            setY(getY() + SPEED_RELATIVE_TO_PLAYER);
        }
    }
}
