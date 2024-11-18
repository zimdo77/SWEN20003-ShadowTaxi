import java.text.DecimalFormat;
import java.util.Properties;

public class Trip implements TextDisplayable {
    private final int TRIP_INFO_FONT_SIZE;
    private final int TRIP_INFO_X;
    private final int TRIP_INFO_Y;
    private final double TRIP_RATE;
    private final int PRIORITY_RATE_1;
    private final int PRIORITY_RATE_2;
    private final int PRIORITY_RATE_3;
    private final double PENALTY_RATE;
    private final String ON_TRIP_TITLE;
    private final String COMPLETED_TRIP_TITLE;
    private final String PENALTY_TEXT;
    private final String EXPECTED_EARNINGS_TEXT;
    private final String PRIORITY_TEXT;

    private final int TRIP_INFO_TEXT_SPACE = 30;

    private double expectedFee;
    private double penalty = 0;
    private int priority;
    private int tripDistance;
    private boolean tripActive = true;
    private boolean usedCoin = false;

    // Utility/helper objects
    private final RenderText renderText;
    private final DecimalFormat df1place;
    private final DecimalFormat df2place;

    // Constructor
    public Trip(double expectedFee, int priority, int tripDistance,
                Properties gameProps, Properties messageProps) {
        TRIP_INFO_FONT_SIZE = Integer.parseInt(gameProps.getProperty("gamePlay.info.fontSize"));
        TRIP_INFO_X = Integer.parseInt(gameProps.getProperty("gamePlay.tripInfo.x"));
        TRIP_INFO_Y = Integer.parseInt(gameProps.getProperty("gamePlay.tripInfo.y"));
        TRIP_RATE = Double.parseDouble(gameProps.getProperty("trip.rate.perY"));
        PRIORITY_RATE_1 = Integer.parseInt(gameProps.getProperty("trip.rate.priority1"));
        PRIORITY_RATE_2 = Integer.parseInt(gameProps.getProperty("trip.rate.priority2"));
        PRIORITY_RATE_3 = Integer.parseInt(gameProps.getProperty("trip.rate.priority3"));
        PENALTY_RATE = Double.parseDouble(gameProps.getProperty("trip.penalty.perY"));
        ON_TRIP_TITLE = messageProps.getProperty("gamePlay.onGoingTrip.title");
        COMPLETED_TRIP_TITLE = messageProps.getProperty("gamePlay.completedTrip.title");
        PENALTY_TEXT = messageProps.getProperty("gamePlay.trip.penalty");
        EXPECTED_EARNINGS_TEXT = messageProps.getProperty("gamePlay.trip.expectedEarning");
        PRIORITY_TEXT = messageProps.getProperty("gamePlay.trip.priority");

        this.expectedFee = expectedFee;
        this.priority = priority;
        this.tripDistance = tripDistance;

        renderText = new RenderText(gameProps);
        df1place = new DecimalFormat("0.0");
        df2place = new DecimalFormat("0.00");
    }

    // Getters and Setters
    public double getNetEarnings() {
        return expectedFee - penalty;
    }

    public void setTripActive(boolean tripActive) {
        this.tripActive = tripActive;
    }

    // PUBLIC METHODS

    /**
     * Implement logic for a trip object.
     * To be called in the main update() method.
     */
    public void update() {

        // Check the effect of the coin on the trip/passenger priority
        checkCoinEffect();

        // Calculate expected fees (not including penalty)
        expectedFee = calculateExpectedFees();

        // Render trip info
        displayTextInfo();
    }

    /**
     * Display the trip information on the bottom left of the game window.
     */
    @Override
    public void displayTextInfo() {
        // Only render during an active trip
        if (tripActive) {
            renderText.drawText(ON_TRIP_TITLE, TRIP_INFO_FONT_SIZE, TRIP_INFO_X, TRIP_INFO_Y);

            // Only render when trip is not active
        } else {
            renderText.drawText(COMPLETED_TRIP_TITLE, TRIP_INFO_FONT_SIZE, TRIP_INFO_X, TRIP_INFO_Y);
            renderText.drawText(PENALTY_TEXT + df2place.format(penalty), TRIP_INFO_FONT_SIZE, TRIP_INFO_X,
                    TRIP_INFO_Y + 3 * TRIP_INFO_TEXT_SPACE);
        }

        renderText.drawText(EXPECTED_EARNINGS_TEXT + df1place.format(expectedFee), TRIP_INFO_FONT_SIZE,
                TRIP_INFO_X, TRIP_INFO_Y + TRIP_INFO_TEXT_SPACE);
        renderText.drawText(PRIORITY_TEXT + priority, TRIP_INFO_FONT_SIZE, TRIP_INFO_X,
                TRIP_INFO_Y + 2 * TRIP_INFO_TEXT_SPACE);
    }

    /**
     * Calculate and set the penalty amount for the trip
     * @param penaltyDistance The y-distance from where the taxi stopped beyond the flag, which affects the penalty.
     */
    public void assignPenalty(double penaltyDistance) {
        penalty = PENALTY_RATE * penaltyDistance;
    }

    // HELPER METHODS

    /**
     * Check if passenger priority value to be decreased (i.e., passenger gains priority) due to the effect
     * of a coin.
     */
    private void checkCoinEffect() {
        Coin activeCoin = Coin.getActiveCoin();
        if (activeCoin != null) {
            if (activeCoin.isActive() && tripActive && !usedCoin && priority > Passenger.getPriorityHigh()) {
                priority--;
                usedCoin = true;
            }
        }
    }

    /**
     * Calculate the expected fees of a trip.
     * @return Expected fees of a trip.
     */
    private double calculateExpectedFees() {
        int priorityRate;

        if (priority == Passenger.getPriorityHigh()) {
            priorityRate = PRIORITY_RATE_1;
        } else if (priority == Passenger.getPriorityMedium()) {
            priorityRate = PRIORITY_RATE_2;
        } else {
            priorityRate = PRIORITY_RATE_3;
        }

        return TRIP_RATE * tripDistance + priorityRate;
    }

}
