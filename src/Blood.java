import bagel.*;
import java.util.Properties;

public class Blood extends VisualEffect {
    private final Image SPRITE;
    private Person associatedPerson;

    // Constructor
    public Blood(double x, double y, Person person, Properties gameProps) {
        super(x, y, gameProps);
        SPRITE = new Image(gameProps.getProperty("gameObjects.blood.image"));
        associatedPerson = person;
    }

    @Override
    public void update(Input input) {
        moveRelativeToPlayer(input);

        if (isActive()) {
            SPRITE.draw(getX(), getY());

            if (getFrameCount() > getFramesActive()) {
                associatedPerson.setIsTerminated(true);
                deactivate();
            }
        }

        incrementFrameCount();
    }
}
