import bagel.*;
import java.util.ArrayList;
import java.util.Properties;

public abstract class Vehicle extends GameObject implements DamageInflictable, Damageable {
    private final double RADIUS;
    private final int LANE_1_X;
    private final int LANE_2_X;
    private final int LANE_3_X;

    private static final int MOVE_AWAY_SPEED = 1;

    private double health;
    private double damagePoints;
    private boolean inCollisionTimeout;
    private int collisionTimeoutFrameCount;
    private boolean isTerminated;
    private boolean isAbove;

    // Constructor
    public Vehicle(double x, double y, Properties gameProps) {
        super(x, y, gameProps);

        RADIUS = Double.parseDouble(gameProps.getProperty("gameObjects.taxi.radius"));
        LANE_1_X = Integer.parseInt(gameProps.getProperty("roadLaneCenter1"));
        LANE_2_X = Integer.parseInt(gameProps.getProperty("roadLaneCenter2"));
        LANE_3_X = Integer.parseInt(gameProps.getProperty("roadLaneCenter3"));

        health = Double.parseDouble(gameProps.getProperty("gameObjects.taxi.health")) * getHealthMultiplier();
        inCollisionTimeout = false;
        collisionTimeoutFrameCount = 0;
        isTerminated = false;
    }

    // Getters and Setters

    public int getLane1X() {
        return LANE_1_X;
    }

    public int getLane2X() {
        return LANE_2_X;
    }

    public int getLane3X() {
        return LANE_3_X;
    }

    public void setIsTerminated(boolean terminated) {
        isTerminated = terminated;
    }

    // PUBLIC METHODS

    public abstract void update(Input input);

    public abstract void setRandomX();

    public abstract void setRandomY();

    @Override
    public void setDamagePoints(double damagePoints) {
        this.damagePoints = damagePoints;
    }

    /**
     * Invoke collision visual effect (smoke).
     */
    @Override
    public void addCollisionVFX(ArrayList<VisualEffect> visualEffects, Properties gameProps) {
        visualEffects.add(new Smoke(getX(), getY(), gameProps));
    }

    /**
     * Inflict damage toward a damageable target.
     * @param target entity that is taking damage.
     */
    @Override
    public void inflictDamage(Damageable target) {
        // If target is a playable entity (Driver or Taxi), it can only take damage if it is not invincible
        if (target instanceof Playable) {
            InvinciblePower activeInvinciblePower = InvinciblePower.getActiveInvinciblePower();
            if (activeInvinciblePower == null || !activeInvinciblePower.isActive()) {
                target.setHealth(target.getHealth() - damagePoints);
            }
        } else {
            target.setHealth(target.getHealth() - damagePoints);
        }
    }

    /**
     * Check if vehicle can take damage - cannot take damage if is terminated.
     * @return True if vehicle can take damage. False otherwise.
     */
    @Override
    public boolean canTakeDamage() {
        return !isTerminated;
    }

    /**
     * Check if vehicle is in collision timeout.
     * @return True if vehicle is in collision timeout. False otherwise.
     */
    @Override
    public boolean isInCollisionTimeout() {
        return inCollisionTimeout;
    }

    /**
     * Set flag to enter collision timeout. And determine if vehicle was the entity on top or on the bottom
     * during the collision.
     * @param isAbove True if the vehicle was the entity on top. False otherwise.
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
     * Logic for rendering each passing frame of the collision timeout of a vehicle
     */
    @Override
    public void renderCollisionTimeout() {
        if (collisionTimeoutFrameCount < getCollisionTimeoutFrames()) {
            // Move apart from collided entity for the initial part of the timeout
            if (collisionTimeoutFrameCount < getInitialTimeoutFrames()) {
                // Can either move up and down depending
                if (isAbove) {
                    setY(getY() - MOVE_AWAY_SPEED);
                } else {
                    setY(getY() + MOVE_AWAY_SPEED);
                }
            }

            collisionTimeoutFrameCount++;

        } else {
            // Once collision timeout ends, choose a new speed for invader cars
            if (this instanceof InvaderCar) {
                ((InvaderCar) this).setRandomSpeed();
            }

            deactivateCollisionTimeout();
            collisionTimeoutFrameCount = 0;
        }
    }

    /**
     * Check if vehicle is terminated,
     * @return True if vehicle is terminated. False otherwise.
     */
    @Override
    public boolean isTerminated() {
        return isTerminated;
    }

    /**
     * Logic for terminating a vehicle.
     * @param visualEffects List of all game visual effects.
     * @param damagedTaxis List of all damaged taxis in game.
     * @param gameProps Game properties
     */
    @Override
    public void terminate(ArrayList<VisualEffect> visualEffects, ArrayList<DamagedTaxi> damagedTaxis,
                          Properties gameProps) {
        // Invoke termination visual effect (fire)
        visualEffects.add(new Fire(getX(), getY(), this, gameProps));

        // If the vehicle is a taxi, generate a damaged taxi
        if (this instanceof Taxi) {
            damagedTaxis.add(new DamagedTaxi(getX(), getY(), gameProps));
        }
    }

    /**
     * Get vehicle health.
     * @return Vehicle health.
     */
    @Override
    public double getHealth() {
        return health;
    }

    /**
     * Set vehicle health.
     * @param health Vehicle health.
     */
    @Override
    public void setHealth(double health) {
        this.health = health;
    }

    /**
     * Get vehicle collision radius.
     * @return Vehicle collision radius.
     */
    @Override
    public double getCollisionRadius() {
        return RADIUS;
    }

}
