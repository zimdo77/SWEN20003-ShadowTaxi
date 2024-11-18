import bagel.*;
import java.util.ArrayList;
import java.util.Properties;

public abstract class Person extends GameObject implements Damageable, TextDisplayable {
    private final int WALK_SPEED_X;
    private final int WALK_SPEED_Y;
    private final int RADIUS;

    private static final int MOVE_AWAY_SPEED = 2;

    private boolean isInTaxi;
    private double health;
    private boolean inCollisionTimeout;
    private boolean isTerminated;
    private int collisionTimeoutFrameCount;
    private boolean isAbove;

    // Constructor
    public Person(double x, double y, Properties gameProps) {
        super(x, y, gameProps);

        WALK_SPEED_X = Integer.parseInt(gameProps.getProperty("gameObjects.passenger.walkSpeedX"));
        WALK_SPEED_Y = Integer.parseInt(gameProps.getProperty("gameObjects.passenger.walkSpeedY"));
        RADIUS = Integer.parseInt(gameProps.getProperty("gameObjects.passenger.radius"));

        health = Double.parseDouble(gameProps.getProperty("gameObjects.passenger.health")) * getHealthMultiplier();
        inCollisionTimeout = false;
        isTerminated = false;
        collisionTimeoutFrameCount = 0;
    }

    // Getters and Setters

    public int getWalkSpeedX() {
        return WALK_SPEED_X;
    }

    public int getWalkSpeedY() {
        return WALK_SPEED_Y;
    }

    public boolean isInTaxi() {
        return isInTaxi;
    }

    public void setIsInTaxi(boolean isInTaxi) {
        this.isInTaxi = isInTaxi;
    }

    public void setIsTerminated(boolean terminated) {
        isTerminated = terminated;
    }

    // PUBLIC METHODS

    public abstract void update(Input input, Taxi taxi);

    public abstract void eject(Taxi taxi);

    public abstract void displayTextInfo();

    /**
     * Set coordinates to move with taxi
     * @param taxi Associated taxi object.
     */
    public void moveWithTaxi(Taxi taxi) {
        setX(taxi.getX());
        setY(taxi.getY());
    }

    @Override
    public void addCollisionVFX(ArrayList<VisualEffect> visualEffects, Properties gameProps) {

    }

    /**
     * Check if person can take damage - cannot take damage if is in the taxi
     * @return True if person can take damage. False otherwise.
     */
    @Override
    public boolean canTakeDamage() {
        return !isInTaxi();
    }

    /**
     * Check if person is in collision timeout.
     * @return True if person is in collision timeout. False otherwise.
     */
    @Override
    public boolean isInCollisionTimeout() {
        return inCollisionTimeout;
    }

    /**
     * Set flag to enter collision timeout. And determine if person was the entity on top or on the bottom
     * during the collision.
     * @param isAbove True if the person was the entity on top. False otherwise.
     */
    @Override
    public void enterCollisionTimeout(boolean isAbove) {
        inCollisionTimeout = true;
        this.isAbove = isAbove;
    }


    /**
     * Set flag to exit collision timeout.
     */
    @Override
    public void deactivateCollisionTimeout() {
        inCollisionTimeout = false;
    }


    /**
     * Logic for rendering each passing frame of the collision timeout of a person
     */
    @Override
    public void renderCollisionTimeout() {
        if (collisionTimeoutFrameCount < getCollisionTimeoutFrames()) {
            // Move apart from collided entity for the initial part of the timeout
            if (collisionTimeoutFrameCount < getInitialTimeoutFrames()) {
                // Can either move up and down depending
                if (isAbove) {
                    setX(getX() - MOVE_AWAY_SPEED);
                    setY(getY() - MOVE_AWAY_SPEED);
                } else {
                    setX(getX() + MOVE_AWAY_SPEED);
                    setY(getY() + MOVE_AWAY_SPEED);
                }
            }
            collisionTimeoutFrameCount++;
        } else {
            // Exit collision timeout
            deactivateCollisionTimeout();
            collisionTimeoutFrameCount = 0;
        }
    }

    /**
     * Check if person is terminated,
     * @return True if person is terminated. False otherwise.
     */
    @Override
    public boolean isTerminated() {
        return isTerminated;
    }

    /**
     * Logic for terminating a person.
     * @param visualEffects List of all game visual effects.
     * @param damagedTaxis List of all damaged taxis in game.
     * @param gameProps Game properties
     */
    @Override
    public void terminate(ArrayList<VisualEffect> visualEffects, ArrayList<DamagedTaxi> damagedTaxis,
                          Properties gameProps) {
        visualEffects.add(new Blood(getX(), getY(), this, gameProps));
    }

    /**
     * Get person health.
     * @return Person health.
     */
    @Override
    public double getHealth() {
        return health;
    }

    /**
     * Set person health.
     * @param health Person health.
     */
    @Override
    public void setHealth(double health) {
        this.health = health;
    }

    /**
     * Get person collision radius.
     * @return Person collision radius.
     */
    @Override
    public double getCollisionRadius() {
        return RADIUS;
    }
}