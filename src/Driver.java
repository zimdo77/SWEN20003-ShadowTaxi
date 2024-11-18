import bagel.*;
import bagel.util.*;
import java.text.DecimalFormat;
import java.util.Properties;

public class Driver extends Person implements Playable {
    private final Image SPRITE;
    private final int TAXI_GET_IN_RADIUS;
    private final int GAMEPLAY_INFO_FONT_SIZE;
    private final int HEALTH_TEXT_X;
    private final int HEALTH_TEXT_Y;
    private final String HEALTH_TEXT;

    private final int EJECTION_DISPLACEMENT = 50;

    // Utility/helper objects
    private final RenderText renderText;
    private final DecimalFormat df;

    // Constructor
    public Driver(double x, double y, Properties gameProps, Properties messageProps) {
        super(x, y, gameProps);

        SPRITE = new Image(gameProps.getProperty("gameObjects.driver.image"));
        TAXI_GET_IN_RADIUS = Integer.parseInt(gameProps.getProperty("gameObjects.driver.taxiGetInRadius"));
        GAMEPLAY_INFO_FONT_SIZE = Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize"));
        HEALTH_TEXT_X = Integer.parseInt(gameProps.getProperty("gamePlay.driverHealth.x"));
        HEALTH_TEXT_Y = Integer.parseInt(gameProps.getProperty("gamePlay.driverHealth.y"));
        HEALTH_TEXT = messageProps.getProperty("gamePlay.driverHealth");

        setIsInTaxi(false);

        renderText = new RenderText(gameProps);
        df = new DecimalFormat("0.00");
    }

    // Getters and Setters

    public int getEjectionDisplacement() {
        return EJECTION_DISPLACEMENT;
    }

    // PUBLIC METHODS

    /**
     * Implement logic for rendering a driver entity.
     * To be called in the main update() method.
     * @param input The current mouse/keyboard input.
     */
    @Override
    public void update(Input input, Taxi taxi) {
        Point taxiPosition = taxi.getPosition();
        Point driverPosition = getPosition();
        double distanceDriverToTaxi = taxiPosition.distanceTo(driverPosition);

        if (isInTaxi()) {
            moveWithTaxi(taxi);

        // If not in taxi...
        } else {
            // Check conditions to get in Taxi
            if (distanceDriverToTaxi <= TAXI_GET_IN_RADIUS) {
                setIsInTaxi(true);
                taxi.setAssociatedDriver(this);

                // If passenger is still on trip, let the passenger go back on the taxi
                if (taxi.getAssociatedPassenger() != null) {
                    taxi.getAssociatedPassenger().setCanReenter(true);
                }
            }

            moveViaInput(input);
            SPRITE.draw(getX(), getY());
        }

        // Implement collision timeout
        if (isInCollisionTimeout()) {
            renderCollisionTimeout();
        }

        // Display health
        displayTextInfo();
    }

    /**
     * Logic for ejecting a driver from the taxi.
     * @param taxi Associated taxi object.
     */
    @Override
    public void eject(Taxi taxi) {
        setIsInTaxi(false);
        setX(taxi.getX() - EJECTION_DISPLACEMENT);
        setY(taxi.getY());
    }

    /**
     * Render driver health on screen
     */
    @Override
    public void displayTextInfo() {
        renderText.drawText(HEALTH_TEXT + df.format(getHealth()), GAMEPLAY_INFO_FONT_SIZE, HEALTH_TEXT_X,
                HEALTH_TEXT_Y);
    }

    /**
     * Move driver according to keyboard input
     * @param input The current mouse/keyboard input.
     */
    @Override
    public void moveViaInput(Input input) {
        if (input.isDown(Keys.LEFT)) {
            setX(getX() - getWalkSpeedX());
        } else if (input.isDown(Keys.RIGHT)) {
            setX(getX() + getWalkSpeedX());
        } else if (input.isDown(Keys.UP)) {
            setY(getY() - getWalkSpeedY());
        } else if (input.isDown(Keys.DOWN)) {
            setY(getY() + getWalkSpeedY());
        }
    }

}
