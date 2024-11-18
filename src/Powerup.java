import bagel.*;
import java.util.ArrayList;
import java.util.Properties;

public abstract class Powerup extends GameObject {
    private final double RADIUS;
    private int frameCount;
    private boolean wasUsed;
    private boolean isActive;

    // Constructor
    public Powerup(double x, double y, Properties gameProps) {
        super(x, y, gameProps);

        RADIUS = Double.parseDouble(gameProps.getProperty("gameObjects.coin.radius"));

        frameCount = 0;
        wasUsed = false;
        isActive = false;
    }

    // Getters and Setters

    public double getRadius() {
        return RADIUS;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public boolean wasUsed() {
        return wasUsed;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    public void setWasUsed(boolean wasUsed) {
        this.wasUsed = wasUsed;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    // PUBLIC METHODS

    public abstract void update(Input input, ArrayList<Playable> playableEntities);

    public abstract Powerup getActivePowerup();

    public abstract void setActivePowerup(Powerup powerup);

    /**
     * Verify if a power-up has collided with an entity.
     * @param entity An entity that is able to collect a power-up
     * @return True if power-up has collided with an entity. False otherwise.
     */
    public boolean hasCollidedWith(Playable entity) {
        double distance = getPosition().distanceTo(((GameObject) entity).getPosition());
        double range = getRadius() + entity.getCollisionRadius();

        return distance <= range;
    }

    /**
     * Deactivate the currently active power-up, and activate this power-up.
     */
    public void activate() {
        // Deactivate previously active power-up
        deactivate();

        // Set appropriate flags
        wasUsed = true;
        isActive = true;
        setActivePowerup(this);
    }

    /**
     * Deactivate previously active power-up
     */
    public void deactivate() {
        if (getActivePowerup() != null) {
            getActivePowerup().setIsActive(false);
        }
    }

}
