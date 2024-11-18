import bagel.*;
import java.util.Properties;

public abstract class VisualEffect extends GameObject {
    private final int FRAMES_ACTIVE;

    private int frameCount;
    private boolean isActive;

    // Constructor
    public VisualEffect(double x, double y, Properties gameProps) {
        super(x, y, gameProps);
        FRAMES_ACTIVE = Integer.parseInt(gameProps.getProperty("gameObjects.smoke.ttl"));

        frameCount = 0;
        isActive = true;
    }

    // Getters and Setters

    public boolean isActive() {
        return isActive;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public int getFramesActive() {
        return FRAMES_ACTIVE;
    }

    // PUBLIC METHODS

    public abstract void update(Input input);

    public void deactivate() {
        isActive = false;
    }

    public void incrementFrameCount() {
        frameCount++;
    }

}
