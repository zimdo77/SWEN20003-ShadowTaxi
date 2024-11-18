import bagel.*;
import java.util.*;

public class TripEndFlag extends GameObject{
    private final Image SPRITE;
    private final double RADIUS;

    private Passenger associatedPassenger;

    // Constructor
    public TripEndFlag(double x, double y, Properties gameProps) {
        super(x, y, gameProps);

        // Property files
        SPRITE = new Image(gameProps.getProperty("gameObjects.tripEndFlag.image"));
        RADIUS = Double.parseDouble(gameProps.getProperty("gameObjects.tripEndFlag.radius"));
    }

    // Getters and Setters
    public double getRadius() {
        return RADIUS;
    }

    public void setAssociatedPassenger(Passenger associatedPassenger) {
        this.associatedPassenger = associatedPassenger;
    }

    // PUBLIC METHODS

    /**
     * Implement logic for rendering a trip end flag entity.
     * To be called in the main update() method.
     * @param input The current mouse/keyboard input.
     */
    public void update(Input input) {
        // Implement flag movement relative to the taxi
        moveRelativeToPlayer(input);

        // Only render flag when necessary
        if (associatedPassenger.isInTaxi() ||
                (associatedPassenger.isTripCompleted() && !associatedPassenger.isAtFlag())) {
            SPRITE.draw(getX(), getY());
        }
    }
}
