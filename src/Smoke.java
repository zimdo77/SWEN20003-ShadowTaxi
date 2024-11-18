import bagel.*;
import java.util.Properties;

public class Smoke extends VisualEffect {
    private final Image SPRITE;

    // Constructor
    public Smoke(double x, double y, Properties gameProps) {
        super(x, y, gameProps);
        SPRITE = new Image(gameProps.getProperty("gameObjects.smoke.image"));
    }

    @Override
    public void update(Input input) {
        moveRelativeToPlayer(input);

        if (isActive()) {
            SPRITE.draw(getX(), getY());

            if (getFrameCount() > getFramesActive()) {
                deactivate();
            }
        }

        incrementFrameCount();
    }
}
