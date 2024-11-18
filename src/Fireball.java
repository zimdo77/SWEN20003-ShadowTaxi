import bagel.*;
import bagel.util.*;
import java.util.ArrayList;
import java.util.Properties;

public class Fireball extends GameObject implements DamageInflictable {
    private final Image SPRITE;
    private final double RADIUS;
    private final int SPEED;

    private static final int SPAWN_RATE = 300;

    private double damagePoints;
    private boolean isActive;
    private EnemyCar associatedEnemyCar;

    // Constructor
    public Fireball(double x, double y, EnemyCar associatedEnemyCar, Properties gameProps) {
        super(x, y, gameProps);

        SPRITE = new Image(gameProps.getProperty("gameObjects.fireball.image"));
        RADIUS = Double.parseDouble(gameProps.getProperty("gameObjects.fireball.radius"));
        SPEED = Integer.parseInt(gameProps.getProperty("gameObjects.fireball.shootSpeedY"));

        setDamagePoints(Double.parseDouble(gameProps.getProperty("gameObjects.fireball.damage")) *
                getHealthMultiplier());
        isActive = true;
        this.associatedEnemyCar = associatedEnemyCar;
    }

    // Getters and Setters

    public static int getSpawnRate() {
        return SPAWN_RATE;
    }

    // PUBLIC METHODS

    public void update(Input input) {
        setY(getY() - SPEED);
        moveRelativeToPlayer(input);

        // If a fireball reaches the top of the screen, set a flag to disable rendering
        if (getY() < 0) {
            isActive = false;
        }

        // Only render if fireball is active
        if (isActive) {
            SPRITE.draw(getX(), getY());
        }

    }

    /**
     * Check if the fireball has collided with any entities.
     * @return The entity that the fireball has collided with. If none, return null.
     */
    @Override
    public Damageable checkForCollision(GamePlayScreen gamePlayScreen) {
        // Create a list of all possible targets a fireball can cause damage to
        ArrayList<Damageable> targets = new ArrayList<>(gamePlayScreen.getOtherCars());
        targets.addAll(gamePlayScreen.getEnemyCars());
        targets.addAll(gamePlayScreen.getPassengers());
        targets.add(gamePlayScreen.getTaxi());
        targets.add(gamePlayScreen.getDriver());

        // Check for collision for all possible targets, and return the target that has collided with the fireball
        for (Damageable target : targets) {
            Point targetPosition = ((GameObject) target).getPosition();
            double distance = getPosition().distanceTo(targetPosition);
            double range = getCollisionRadius() + target.getCollisionRadius();

            if (distance < range && target.canTakeDamage() && target != associatedEnemyCar) {
                return target;
            }
        }
        return null;
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

        // Set a flag to stop rendering
        isActive = false;
    }

    @Override
    public double getCollisionRadius() {
        return RADIUS;
    }

    @Override
    public void setDamagePoints(double damagePoints) {
        this.damagePoints = damagePoints;
    }
}
