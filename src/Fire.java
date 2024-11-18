import bagel.*;
import java.util.Properties;

public class Fire extends VisualEffect {
    private final Image SPRITE;
    private Vehicle associatedVehicle;

    // Constructor
    public Fire(double x, double y, Vehicle vehicle, Properties gameProps) {
        super(x, y, gameProps);
        SPRITE = new Image(gameProps.getProperty("gameObjects.fire.image"));
        associatedVehicle = vehicle;
    }

    @Override
    public void update(Input input) {
        moveRelativeToPlayer(input);

        if (isActive()) {
            SPRITE.draw(getX(), getY());

            if (getFrameCount() > getFramesActive()) {
                associatedVehicle.setIsTerminated(true);
                deactivate();
            }
        }

        incrementFrameCount();
    }
}
