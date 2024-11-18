import bagel.*;
import bagel.util.*;
import java.util.ArrayList;
import java.util.Properties;

public abstract class InvaderCar extends Vehicle {
    private final int MIN_SPEED;
    private final int MAX_SPEED;
    private static final int Y_1 = -50;
    private static final int Y_2 = 768;
    private int speed;

    // Constructor
    public InvaderCar(Properties gameProps) {
        super(0, 0, gameProps);

        MIN_SPEED = Integer.parseInt(gameProps.getProperty("gameObjects.otherCar.minSpeedY"));
        MAX_SPEED = Integer.parseInt(gameProps.getProperty("gameObjects.otherCar.maxSpeedY"));

        setRandomX();
        setRandomY();
        setRandomSpeed();
        setDamagePoints(Double.parseDouble(gameProps.getProperty("gameObjects.otherCar.damage")) *
                getHealthMultiplier());
    }

    // PUBLIC METHODS

    public abstract void update(Input input);

    /**
     * Movement logic for invader cars.
     * @param input The current mouse/keyboard input.
     */
    public void move(Input input) {
        moveRelativeToPlayer(input);
        if (!isInCollisionTimeout()) {
            setY(getY() - speed);
        }
    }

    /**
     * Check if the invader car has collided with any entities
     * @return The entity that the invader car has collided with. If none, return null.
     */
    @Override
    public Damageable checkForCollision(GamePlayScreen gamePlayScreen) {
        // Create a list of all possible targets the invader can cause damage to
        ArrayList<Damageable> targets = new ArrayList<>(gamePlayScreen.getOtherCars());
        targets.addAll(gamePlayScreen.getEnemyCars());
        targets.addAll(gamePlayScreen.getPassengers());
        targets.add(gamePlayScreen.getTaxi());
        targets.add(gamePlayScreen.getDriver());

        // Make sure it doesn't check for itself
        targets.remove(this);

        // Check for collision for all possible targets, and return the target that has collided with the invader car
        for (Damageable target : targets) {
            Point targetPosition = ((GameObject) target).getPosition();
            double distance = getPosition().distanceTo(targetPosition);
            double range = getCollisionRadius() + target.getCollisionRadius();

            if (distance < range && target.canTakeDamage()) {
                return target;
            }
        }

        return null;
    }

    /**
     * Randomly choose between the three possible spawn x-coordinates for the invader car
     */
    @Override
    public void setRandomX() {
        int lane = MiscUtils.getRandomInt(1, 3 + 1);
        if (lane == 1) {
            setX(getLane1X());
        } else if (lane == 2) {
            setX(getLane2X());
        } else {
            setX(getLane3X());
        }
    }

    /**
     * Randomly choose between the two possible spawn y-coordinates for the invader car
     */
    @Override
    public void setRandomY() {
        setY(MiscUtils.selectAValue(Y_1, Y_2));
    }

    /**
     * Randomly choose a speed for the invader car
     */
    public void setRandomSpeed() {
        speed = MiscUtils.selectAValue(MIN_SPEED, MAX_SPEED);
    }

}
