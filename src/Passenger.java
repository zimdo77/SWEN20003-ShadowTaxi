import bagel.*;
import bagel.util.*;
import java.text.DecimalFormat;
import java.util.Properties;

public class Passenger extends Person implements WeatherObserver {
    private final Image SPRITE;
    private final int TAXI_DETECT_RADIUS;
    private final double TRIP_RATE;
    private final int PRIORITY_RATE_1;
    private final int PRIORITY_RATE_2;
    private final int PRIORITY_RATE_3;
    private final int PASSENGER_INFO_FONT_SIZE;

    private static final int PRIORITY_TEXT_SPACE = 30;
    private static final int EXP_EARNINGS_TEXT_SPACE = 100;
    private final int EJECTION_DISPLACEMENT = 100;
    private static final int PRIORITY_HIGH = 1;
    private static final int PRIORITY_MEDIUM = 2;
    private static final int PRIORITY_LOW = 3;

    // When checking if a passenger has arrived at their trip end flag, since the Point class in bagel uses a
    // double data type, comparing using "passengerPosition.equals(tripEndFlagPosition)" or
    // "passengerPosition.distance(tripEndFlagPosition) == 0" might not work because of floating point precision
    // error. So assume a passenger has arrived at the trip end flag if the distance is within a very small tolerance.
    private static final double EQUALITY_TOLERANCE = 1;

    private static double minHealth;

    private int priority;
    private int initialPriority;
    private TripEndFlag associatedTripEndFlag;
    private int tripDistance;
    private boolean hasUmbrella;
    private boolean tripCompleted;
    private boolean isAtFlag;
    private boolean isEjected;
    private boolean canReenter;

    // Property files
    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    // Utility/helper objects
    private final RenderText renderText;
    private final DecimalFormat df;

    // Constructor
    public Passenger(double x, double y, int priority, int tripDistance, int hasUmbrella,
                     Properties gameProps, Properties messageProps) {
        super(x, y, gameProps);

        SPRITE = new Image(gameProps.getProperty("gameObjects.passenger.image"));
        TAXI_DETECT_RADIUS = Integer.parseInt(gameProps.getProperty("gameObjects.passenger.taxiDetectRadius"));
        TRIP_RATE = Double.parseDouble(gameProps.getProperty("trip.rate.perY"));
        PRIORITY_RATE_1 = Integer.parseInt(gameProps.getProperty("trip.rate.priority1"));
        PRIORITY_RATE_2 = Integer.parseInt(gameProps.getProperty("trip.rate.priority2"));
        PRIORITY_RATE_3 = Integer.parseInt(gameProps.getProperty("trip.rate.priority3"));
        PASSENGER_INFO_FONT_SIZE = Integer.parseInt(gameProps.getProperty("gameObjects.passenger.fontSize"));

        initialPriority = priority;
        this.priority = initialPriority;
        this.tripDistance = tripDistance;
        this.hasUmbrella = hasUmbrella == 1;
        setIsInTaxi(false);
        tripCompleted = false;
        isAtFlag = false;
        isEjected = false;
        canReenter = false;

        renderText = new RenderText(gameProps);
        df = new DecimalFormat("0.0");

        GAME_PROPS = gameProps;
        MESSAGE_PROPS = messageProps;
    }


    // Getters and Setters
    public boolean isTripCompleted() {
        return tripCompleted;
    }

    public boolean isAtFlag() {
        return isAtFlag;
    }

    public static int getPriorityHigh() {
        return PRIORITY_HIGH;
    }

    public static int getPriorityMedium() {
        return PRIORITY_MEDIUM;
    }

    public static double getMinHealth() {
        return minHealth;
    }

    public void setCanReenter(boolean canReenter) {
        this.canReenter = canReenter;
    }

    public static void setMinHealth(double minHealth) {
        Passenger.minHealth = minHealth;
    }

    public void setAssociatedTripEndFlag(TripEndFlag associatedTripEndFlag) {
        this.associatedTripEndFlag = associatedTripEndFlag;
    }

    // PUBLIC METHODS

    /**
     * Implement logic for rendering a passenger entity.
     * To be called in the main update() method.
     * @param input The current mouse/keyboard input.
     * @param taxi Associated Taxi object.
     */
    @Override
    public void update(Input input, Taxi taxi) {
        Trip associatedTrip = taxi.getTrip();
        Driver associatedDriver = taxi.getAssociatedDriver();
        Point taxiPosition = taxi.getPosition();
        Point passengerPosition = getPosition();
        Point tripEndFlagPosition = associatedTripEndFlag.getPosition();
        double distancePassengerToTaxi = passengerPosition.distanceTo(taxiPosition);
        double distanceFlagToTaxi = tripEndFlagPosition.distanceTo(taxiPosition);
        double distancePassengerToFlag = passengerPosition.distanceTo(tripEndFlagPosition);

        boolean taxiIsStationary = !input.isDown(Keys.UP) && !input.isDown(Keys.LEFT) && !input.isDown(Keys.UP);
        boolean taxiStoppedWithinRadius = distanceFlagToTaxi <= associatedTripEndFlag.getRadius();
        boolean taxiMovedPassedFlag = taxiPosition.y <= tripEndFlagPosition.y;

        // If passenger is ejected during a trip...
        if (isEjected) {
            if (canReenter) {
                // If driver got in taxi, then get in the taxi
                walkTowards(associatedDriver.getPosition());
                if (passengerPosition.distanceTo(associatedDriver.getPosition()) < EQUALITY_TOLERANCE) {
                    setIsInTaxi(true);
                    isEjected = false;
                    canReenter = false;
                }
            } else {
                // follow driver
                double displacement = EJECTION_DISPLACEMENT - associatedDriver.getEjectionDisplacement();
                walkTowards(new Point(associatedDriver.getX() - displacement, associatedDriver.getY()));
            }

        } else {
            // If passenger is in the taxi...
            if (isInTaxi()) {
                // Check conditions for passenger to be dropped off:
                if (taxiIsStationary && (taxiMovedPassedFlag || taxiStoppedWithinRadius)) {
                    setIsInTaxi(false);
                    tripCompleted = true;
                    taxi.setAssociatedPassenger(null);

                    // Check conditions for penalty to be set:
                    if (taxiMovedPassedFlag && !taxiStoppedWithinRadius) {
                        double penaltyDistance = tripEndFlagPosition.y - taxiPosition.y;
                        associatedTrip.assignPenalty(penaltyDistance);
                    }

                    // Add net earnings of the trip to the total score (expected fee minus penalty)
                    // If net earnings are negative, it gets set to 0 (i.e., only add earnings if they are positive)
                    double netEarnings = associatedTrip.getNetEarnings();
                    if (netEarnings > 0) {
                        taxi.setScore(taxi.getScore() + netEarnings);
                    }

                    // Dis-activate associated Trip object
                    associatedTrip.setTripActive(false);

                    // If not dropping off passenger...
                } else {
                    moveWithTaxi(taxi);
                }

                // If passenger is not in the taxi...
            } else {
                // If passenger just completed a trip (i.e. walking out the taxi)...
                if (tripCompleted) {
                    // If arrived at flag...
                    if (distancePassengerToFlag < EQUALITY_TOLERANCE) {
                        isAtFlag = true;

                        // Otherwise, keep walking towards flag
                    } else {
                        walkTowards(tripEndFlagPosition);
                    }

                    // If passenger has not done a trip yet (i.e. waiting to be picked up)...
                } else {
                    // Check conditions for passenger to be picked up:
                    // Taxi is stationary, has no current passenger, adjacent to passenger, and has a driver.
                    if (taxiIsStationary && taxi.getAssociatedPassenger() == null &&
                            distancePassengerToTaxi <= TAXI_DETECT_RADIUS && associatedDriver.isInTaxi()) {
                        // If arrived at taxi...
                        if (distancePassengerToTaxi <= EQUALITY_TOLERANCE) {
                            setIsInTaxi(true);
                            taxi.setAssociatedPassenger(this);

                            // Create a new trip object, and assign it to the taxi
                            taxi.setTrip(new Trip(calculateExpectedEarnings(), priority, tripDistance, GAME_PROPS,
                                    MESSAGE_PROPS));

                            // Otherwise, keep walking towards taxi
                        } else {
                            walkTowards(taxiPosition);
                        }
                    }

                    // Render passenger priority and expected earnings
                    displayTextInfo();
                }
            }
        }

        // Update the minimum passenger health
        if (getHealth() < minHealth) {
            setMinHealth(getHealth());
        }

        // Implement collision timeout
        if (isInCollisionTimeout()) {
            renderCollisionTimeout();
        }

        // Implement passenger movement relative to the player
        moveRelativeToPlayer(input);

        // Render passenger
        SPRITE.draw(getX(), getY());
    }

    /**
     * Logic for ejecting a passenger from the taxi.
     * @param taxi Associated taxi object.
     */
    @Override
    public void eject(Taxi taxi) {
        isEjected = true;
        setIsInTaxi(false);
        setX(taxi.getX() - EJECTION_DISPLACEMENT);
        setY(taxi.getY());
    }


    /**
     * Render initial passenger priority and expected earnings appropriately on screen
     */
    @Override
    public void displayTextInfo() {
        renderText.drawText(priority + "", PASSENGER_INFO_FONT_SIZE, getX() - PRIORITY_TEXT_SPACE,
                getY());
        renderText.drawText(df.format(calculateExpectedEarnings()),PASSENGER_INFO_FONT_SIZE,
                getX() - EXP_EARNINGS_TEXT_SPACE, getY());
    }

    /**
     * Update passenger priorities according to weather change.
     * @param weather New weather condition.
     */
    @Override
    public void updateWeatherChange(WeatherCondition.Weather weather) {
        if (weather == WeatherCondition.Weather.RAINING && !hasUmbrella) {
            priority = PRIORITY_HIGH;
        } else if (weather == WeatherCondition.Weather.SUNNY) {
            priority = initialPriority;
        }
    }

    // HELPER METHODS

    /**
     * Calculate the initial expected earnings of a passenger.
     * @return Initial expected earnings of a passenger.
     */
    private double calculateExpectedEarnings() {
        int priorityRate;

        if (priority == PRIORITY_HIGH) {
            priorityRate = PRIORITY_RATE_1;
        } else if (priority == PRIORITY_MEDIUM) {
            priorityRate = PRIORITY_RATE_2;
        } else {
            priorityRate = PRIORITY_RATE_3;
        }

        return TRIP_RATE * tripDistance + priorityRate;
    }

    /**
     * Walk towards a destination point.
     * @param destination Destination point.
     */
    private void walkTowards(Point destination) {
        // Set direction parameters to face destination point
        Vector2 direction = new Vector2(destination.x - getX(), destination.y - getY());
        double directionX = direction.normalised().x;
        double directionY = direction.normalised().y;

        // Walk towards destination
        if (getX() != destination.x) {
            setX(getX() + getWalkSpeedX() * directionX);
        }

        if (getY() != destination.y) {
            setY(getY() + getWalkSpeedY() * directionY);
        }

    }

}
