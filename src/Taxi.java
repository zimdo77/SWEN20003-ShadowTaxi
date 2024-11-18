import bagel.*;
import bagel.util.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Properties;

public class Taxi extends Vehicle implements Playable, TextDisplayable {
    private final Image SPRITE;
    private final int SPEED_X;
    private final double TARGET_SCORE;
    private final double MAX_HEALTH;
    private final int MIN_SPAWN_Y;
    private final int MAX_SPAWN_Y;
    private final int GAMEPLAY_INFO_FONT_SIZE;
    private final int SCORE_TEXT_X;
    private final int SCORE_TEXT_Y;
    private final int TARGET_SCORE_X;
    private final int TARGET_SCORE_Y;
    private final int HEALTH_TEXT_X;
    private final int HEALTH_TEXT_Y;
    private final String SCORE_TEXT;
    private final String TARGET_SCORE_TEXT;
    private final String HEALTH_TEXT;

    private double score;
    private Trip trip;
    private Passenger associatedPassenger;
    private Driver associatedDriver;


    // Utility/helper objects
    private final RenderText renderText;
    private final DecimalFormat df;

    // Constructor
    public Taxi(double x, double y, Properties gameProps, Properties messageProps) {
        super(x, y, gameProps);

        SPRITE = new Image((gameProps).getProperty("gameObjects.taxi.image"));
        SPEED_X = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.speedX"));
        TARGET_SCORE = Double.parseDouble(gameProps.getProperty("gamePlay.target"));
        MAX_HEALTH = Double.parseDouble(gameProps.getProperty("gameObjects.taxi.health")) * getHealthMultiplier();
        MIN_SPAWN_Y = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.nextSpawnMinY"));
        MAX_SPAWN_Y = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.nextSpawnMaxY"));
        GAMEPLAY_INFO_FONT_SIZE = Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize"));
        SCORE_TEXT_X = Integer.parseInt(gameProps.getProperty("gamePlay.earnings.x"));
        SCORE_TEXT_Y = Integer.parseInt(gameProps.getProperty("gamePlay.earnings.y"));
        TARGET_SCORE_X = Integer.parseInt(gameProps.getProperty("gamePlay.target.x"));
        TARGET_SCORE_Y = Integer.parseInt(gameProps.getProperty("gamePlay.target.y"));
        HEALTH_TEXT_X = Integer.parseInt(gameProps.getProperty("gamePlay.taxiHealth.x"));
        HEALTH_TEXT_Y = Integer.parseInt(gameProps.getProperty("gamePlay.taxiHealth.y"));
        SCORE_TEXT = messageProps.getProperty("gamePlay.earnings");
        TARGET_SCORE_TEXT = messageProps.getProperty("gamePlay.target");
        HEALTH_TEXT = messageProps.getProperty("gamePlay.taxiHealth");

        score = 0.0;
        setDamagePoints(Double.parseDouble(gameProps.getProperty("gameObjects.taxi.damage")) * getHealthMultiplier());

        renderText = new RenderText(gameProps);
        df = new DecimalFormat("0.00");

    }

    // Getters and Setters
    public Trip getTrip() {
        return trip;
    }

    public double getScore() {
        return score;
    }

    public double getTargetScore() {
        return TARGET_SCORE;
    }

    public Passenger getAssociatedPassenger() {
        return associatedPassenger;
    }

    public Driver getAssociatedDriver() {
        return associatedDriver;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setAssociatedDriver(Driver associatedDriver) {
        this.associatedDriver = associatedDriver;
    }

    public void setAssociatedPassenger(Passenger associatedPassenger) {
        this.associatedPassenger = associatedPassenger;
    }

    // PUBLIC METHODS

    /**
     * Implement logic for rendering a taxi entity.
     * To be called in the main update() method.
     * @param input The current mouse/keyboard input.
     */
    @Override
    public void update(Input input) {
        // Only movable via input if the driver is inside taxi. Remain stationary otherwise.
        if (associatedDriver.isInTaxi()) {
            moveViaInput(input);
        } else {
            moveRelativeToPlayer(input);
        }

        // If taxi is destroyed, eject driver and passenger if either of them are inside. Then respawn.
        if (isTerminated()) {

            if (associatedPassenger != null) {
                if (associatedPassenger.isInTaxi()) {
                    associatedPassenger.eject(this);
                }
            }

            if (associatedDriver != null) {
                if (associatedDriver.isInTaxi()) {
                    associatedDriver.eject(this);
                }
            }

            setRandomX();
            setRandomY();
            setHealth(MAX_HEALTH);
            setIsTerminated(false);
        }

        // Implement collision timeout
        if (isInCollisionTimeout()) {
            renderCollisionTimeout();
        }

        // Render taxi
        SPRITE.draw(getX(), getY());

        // Display score and taxi health
        displayTextInfo();
    }

    /**
     * Check if the taxi has collided with any entities.
     * @return The entity that the taxi has collided with. If none, return null.
     */
    @Override
    public Damageable checkForCollision(GamePlayScreen gamePlayScreen) {
        // Create a list of all possible targets the taxi can cause damage to
        ArrayList<Damageable> targets = new ArrayList<>(gamePlayScreen.getOtherCars());
        targets.addAll(gamePlayScreen.getEnemyCars());

        // Check for collision for all possible targets, and return the target that has collided with the taxi
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
     * Display current and target taxi score on the top left of the game window, and taxi health on the top right
     * of the game window.
     */
    @Override
    public void displayTextInfo() {
        renderText.drawText(SCORE_TEXT + df.format(score), GAMEPLAY_INFO_FONT_SIZE, SCORE_TEXT_X,
                SCORE_TEXT_Y);
        renderText.drawText(TARGET_SCORE_TEXT + df.format(TARGET_SCORE), GAMEPLAY_INFO_FONT_SIZE,
                TARGET_SCORE_X, TARGET_SCORE_Y);
        renderText.drawText(HEALTH_TEXT + df.format(getHealth()), GAMEPLAY_INFO_FONT_SIZE, HEALTH_TEXT_X,
                HEALTH_TEXT_Y);
    }

    /**
     * Randomly choose between the two possible spawn x-coordinates for the taxi
     */
    @Override
    public void setRandomX() {
        setX(MiscUtils.selectAValue(getLane1X(), getLane3X()));
    }


    /**
     * Randomly choose between the two possible spawn y-coordinates for the taxi
     */
    @Override
    public void setRandomY() {
        setY(MiscUtils.getRandomInt(MIN_SPAWN_Y, MAX_SPAWN_Y + 1));
    }

    /**
     * Move taxi according to the keyboard input
     * @param input The current mouse/keyboard input.
     */
    @Override
    public void moveViaInput(Input input) {
        if (input.isDown(Keys.LEFT)) {
            setX(getX() - SPEED_X);
        } else if (input.isDown(Keys.RIGHT)) {
            setX(getX() + SPEED_X);
        }
    }

}