import bagel.*;
import java.util.ArrayList;
import java.util.Properties;

public class InvinciblePower extends Powerup {
    private final Image SPRITE;
    private final int MAX_FRAMES_IN_EFFECT;

    // Track currently invincible power
    private static InvinciblePower activeInvinciblePower;

    // Constructor
    public InvinciblePower(double x, double y, Properties gameProps) {
        super(x, y, gameProps);
        SPRITE = new Image(gameProps.getProperty("gameObjects.invinciblePower.image"));
        MAX_FRAMES_IN_EFFECT = Integer.parseInt(gameProps.getProperty("gameObjects.invinciblePower.maxFrames"));
    }

    // Getters and Setters

    public static InvinciblePower getActiveInvinciblePower() {
        return activeInvinciblePower;
    }

    // PUBLIC METHODS

    /**
     * Implement logic for rendering an invincible power entity.
     * To be called in the main update() method.
     * @param input The current mouse/keyboard input.
     * @param playableEntities Entities that can collect the power-ups (Taxi and Driver)
     */
    @Override
    public void update(Input input, ArrayList<Playable> playableEntities) {

        for (Playable entity : playableEntities) {
            if (hasCollidedWith(entity)) {
                activate();
            }
        }

        if (isActive()) {
            if (getFrameCount() > MAX_FRAMES_IN_EFFECT) {
                deactivate();
            }
            setFrameCount(getFrameCount() + 1);
        }

        moveRelativeToPlayer(input);

        if (!wasUsed()) {
            SPRITE.draw(getX(), getY());
        }

    }

    @Override
    public Powerup getActivePowerup() {
        return activeInvinciblePower;
    }

    @Override
    public void setActivePowerup(Powerup powerup) {
        assert (powerup instanceof InvinciblePower);
        activeInvinciblePower = (InvinciblePower) powerup;
    }


}
