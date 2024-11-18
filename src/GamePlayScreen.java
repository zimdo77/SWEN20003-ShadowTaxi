import bagel.*;
import java.util.ArrayList;
import java.util.Properties;

public class GamePlayScreen implements TextDisplayable {
    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    private final String[][] GAME_OBJECTS;
    private final String[][] GAME_WEATHER;
    private final Image BACKGROUND_SUNNY_A;
    private final Image BACKGROUND_SUNNY_B;
    private final Image BACKGROUND_RAINING_A;
    private final Image BACKGROUND_RAINING_B;
    private final int GAMEPLAY_INFO_FONT_SIZE;
    private final int FRAMES_REMAINING_X;
    private final int FRAMES_REMAINING_Y;
    private final int PASSENGER_HEALTH_X;
    private final int PASSENGER_HEALTH_Y;
    private final int SCROLL_SPEED;
    private final int MAX_FRAMES;
    private final String FRAMES_REMAINING_TEXT;
    private final String PASSENGER_HEALTH_TEXT;
    private final String PLAYER_NAME;

    private static final int CSV_ENTITY_NAME_INDEX = 0;
    private static final int CSV_X_INDEX = 1;
    private static final int CSV_Y_INDEX = 2;
    private static final int CSV_PRIORITY_INDEX = 3;
    private static final int CSV_END_X_INDEX = 4;
    private static final int CSV_Y_DISTANCE_INDEX = 5;
    private static final int CSV_UMBRELLA_INDEX = 6;

    private static final String TAXI_KEY = "TAXI";
    private static final String DRIVER_KEY = "DRIVER";
    private static final String PASSENGER_KEY = "PASSENGER";
    private static final String COIN_KEY = "COIN";
    private static final String INVINCIBLE_POWER_KEY = "INVINCIBLE_POWER";

    private Taxi taxi;
    private Driver driver;
    private ArrayList<Passenger> passengers;
    private ArrayList<TripEndFlag> tripEndFlags;
    private ArrayList<Coin> coins;
    private ArrayList<InvinciblePower> invinciblePowers;
    private ArrayList<OtherCar> otherCars;
    private ArrayList<EnemyCar> enemyCars;
    private ArrayList<Fireball> fireballs;
    private ArrayList<WeatherCondition> weatherConditions;
    private ArrayList<DamageInflictable> damagers;
    private ArrayList<VisualEffect> visualEffects;
    private ArrayList<DamagedTaxi> damagedTaxis;
    private ArrayList<WeatherObserver> weatherObservers;
    private ArrayList<Playable> playableEntities;

    private int frameCount;
    private double yBackgroundA;
    private double yBackgroundB;
    private boolean finishedGame;
    private boolean wonGame;
    private WeatherCondition.Weather currentWeather;

    // Utility/helper objects
    private final RenderText renderText;

    // Constructor
    public GamePlayScreen(Properties gameProps, Properties messageProps, String playerName) {
        GAME_PROPS = gameProps;
        MESSAGE_PROPS = messageProps;

        GAME_OBJECTS = IOUtils.readCommaSeparatedFile(gameProps.getProperty("gamePlay.objectsFile"));
        GAME_WEATHER = IOUtils.readCommaSeparatedFile("res/gameWeather.csv");
        BACKGROUND_SUNNY_A = new Image(gameProps.getProperty("backgroundImage.sunny"));
        BACKGROUND_SUNNY_B = new Image(gameProps.getProperty("backgroundImage.sunny"));
        BACKGROUND_RAINING_A = new Image(gameProps.getProperty("backgroundImage.raining"));
        BACKGROUND_RAINING_B = new Image(gameProps.getProperty("backgroundImage.raining"));
        GAMEPLAY_INFO_FONT_SIZE = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.info.fontSize"));
        FRAMES_REMAINING_X = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.maxFrames.x"));
        FRAMES_REMAINING_Y = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.maxFrames.y"));
        PASSENGER_HEALTH_X = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.passengerHealth.x"));
        PASSENGER_HEALTH_Y = Integer.parseInt(GAME_PROPS.getProperty("gamePlay.passengerHealth.y"));
        SCROLL_SPEED = Integer.parseInt(gameProps.getProperty("gameObjects.taxi.speedY"));
        MAX_FRAMES = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames"));
        FRAMES_REMAINING_TEXT = MESSAGE_PROPS.getProperty("gamePlay.remFrames");
        PASSENGER_HEALTH_TEXT = MESSAGE_PROPS.getProperty("gamePlay.passengerHealth");
        PLAYER_NAME = playerName;

        frameCount = 0;
        yBackgroundA = Window.getHeight()/2.0;
        yBackgroundB = -Window.getHeight()/2.0;
        finishedGame = false;
        currentWeather = WeatherCondition.Weather.NONE;
        renderText = new RenderText(gameProps);

        createGameEntities();
        storeWeatherConditions();
    }

    // Getters and Setters
    public boolean hasFinishedGame() {
        return finishedGame;
    }

    public boolean hasWonGame() {
        return wonGame;
    }

    public Taxi getTaxi() {
        return taxi;
    }

    public Driver getDriver() {
        return driver;
    }

    public ArrayList<OtherCar> getOtherCars() {
        return otherCars;
    }

    public ArrayList<EnemyCar> getEnemyCars() {
        return enemyCars;
    }

    public ArrayList<Passenger> getPassengers() {
        return passengers;
    }

    // PUBLIC METHODS

    /**
     * Implement all gameplay screen elements.
     * To be called in the main update() method.
     * @param input The current mouse/keyboard input.
     */
    public void update(Input input) {
        setCurrentWeather();
        renderBackground();
        scrollBackground(input);
        generateInvaderCars();
        updateEntities(input);
        updateCollisions();
        displayTextInfo();
        checkEndGame();
        frameCount++;
    }

    /**
     * Display frames remaining, and passenger health.
     */
    @Override
    public void displayTextInfo() {
        double health;

        if (taxi.getAssociatedPassenger() == null) {
            health = Passenger.getMinHealth();
        } else {
            health = taxi.getAssociatedPassenger().getHealth();
        }

        renderText.drawText(PASSENGER_HEALTH_TEXT + health, GAMEPLAY_INFO_FONT_SIZE, PASSENGER_HEALTH_X,
                PASSENGER_HEALTH_Y);
        renderText.drawText(FRAMES_REMAINING_TEXT + getFramesRemaining(), GAMEPLAY_INFO_FONT_SIZE,
                FRAMES_REMAINING_X, FRAMES_REMAINING_Y);
    }


    // HELPER METHODS

    /**
     * Instantiate and store game entities and set their attributes.
     */
    private void createGameEntities() {
        passengers = new ArrayList<>();
        tripEndFlags = new ArrayList<>();
        coins = new ArrayList<>();
        invinciblePowers = new ArrayList<>();
        otherCars = new ArrayList<>();
        enemyCars = new ArrayList<>();
        fireballs = new ArrayList<>();
        damagers = new ArrayList<>();
        visualEffects = new ArrayList<>();
        damagedTaxis = new ArrayList<>();
        weatherObservers = new ArrayList<>();
        playableEntities = new ArrayList<>();

        for (String[] entity : GAME_OBJECTS) {
            String entityKey = entity[CSV_ENTITY_NAME_INDEX];
            switch(entityKey) {
                case (TAXI_KEY):
                    taxi = new Taxi(Integer.parseInt(entity[CSV_X_INDEX]), Integer.parseInt(entity[CSV_Y_INDEX]),
                            GAME_PROPS, MESSAGE_PROPS);
                    damagers.add(taxi);
                    playableEntities.add(taxi);
                    break;

                case (DRIVER_KEY):
                    driver = new Driver(Integer.parseInt(entity[CSV_X_INDEX]), Integer.parseInt(entity[CSV_Y_INDEX]),
                            GAME_PROPS, MESSAGE_PROPS);
                    playableEntities.add(driver);
                    break;

                case (PASSENGER_KEY):
                    Passenger passenger = new Passenger(Integer.parseInt(entity[CSV_X_INDEX]),
                            Integer.parseInt(entity[CSV_Y_INDEX]), Integer.parseInt(entity[CSV_PRIORITY_INDEX]),
                            Integer.parseInt(entity[CSV_Y_DISTANCE_INDEX]),
                            Integer.parseInt(entity[CSV_UMBRELLA_INDEX]), GAME_PROPS, MESSAGE_PROPS);
                    TripEndFlag tripEndFlag = new TripEndFlag(Integer.parseInt(entity[CSV_END_X_INDEX]),
                            Integer.parseInt(entity[CSV_Y_INDEX]) - Integer.parseInt(entity[CSV_Y_DISTANCE_INDEX]),
                            GAME_PROPS);

                    passengers.add(passenger);
                    weatherObservers.add(passenger);
                    tripEndFlags.add(tripEndFlag);

                    passenger.setAssociatedTripEndFlag(tripEndFlag);
                    tripEndFlag.setAssociatedPassenger(passenger);
                    Passenger.setMinHealth(Double.parseDouble(GAME_PROPS.getProperty("gameObjects.passenger.health"))
                            * GameObject.getHealthMultiplier());
                    break;

                case (COIN_KEY):
                    Coin coin = new Coin(Integer.parseInt(entity[CSV_X_INDEX]), Integer.parseInt(entity[CSV_Y_INDEX]),
                            GAME_PROPS);
                    coins.add(coin);
                    break;

                case (INVINCIBLE_POWER_KEY):
                    InvinciblePower invinciblePower = new InvinciblePower(Integer.parseInt(entity[CSV_X_INDEX]),
                            Integer.parseInt(entity[CSV_Y_INDEX]), GAME_PROPS);
                    invinciblePowers.add(invinciblePower);

            }
        }
    }

    /**
     * Store weather conditions in a list of 'WeatherCondition' objects.
     */
    private void storeWeatherConditions() {
        weatherConditions = new ArrayList<>();

        for (String[] line : GAME_WEATHER) {
            WeatherCondition.Weather condition = WeatherCondition.Weather.valueOf(line[0]);
            int startFrame = Integer.parseInt(line[1]);
            int endFrame = Integer.parseInt(line[2]);

            weatherConditions.add(new WeatherCondition(condition, startFrame, endFrame));
        }
    }

    /**
     * Render screen background (sunny and raining).
     */
    private void renderBackground() {
        BACKGROUND_SUNNY_A.draw(Window.getWidth()/2.0, yBackgroundA);
        BACKGROUND_SUNNY_B.draw(Window.getWidth()/2.0, yBackgroundB);

        if (currentWeather == WeatherCondition.Weather.RAINING) {
            BACKGROUND_RAINING_A.draw(Window.getWidth()/2.0, yBackgroundA);
            BACKGROUND_RAINING_B.draw(Window.getWidth()/2.0, yBackgroundB);
        }
    }

    /**
     * Scrolling logic for the gameplay background moving relative to the taxi.
     * @param input The current mouse/keyboard input.
     */
    private void scrollBackground(Input input) {
        if (input.isDown(Keys.UP)) {
            yBackgroundA += SCROLL_SPEED;
            yBackgroundB += SCROLL_SPEED;
        }

        if (yBackgroundA >= Window.getHeight() + Window.getHeight()/2.0) {
            yBackgroundA = yBackgroundB - Window.getHeight();
        } else if (yBackgroundB >= Window.getHeight() + Window.getHeight()/2.0) {
            yBackgroundB = yBackgroundA - Window.getHeight();
        }
    }


    /**
     * Randomly generate invader cars (other cars and enemy cars).
     */
    private void generateInvaderCars() {
        if (MiscUtils.canSpawn(OtherCar.getSpawnRate())) {
            OtherCar otherCar = new OtherCar(GAME_PROPS);
            otherCars.add(otherCar);
            damagers.add(otherCar);
        }

        if (MiscUtils.canSpawn(EnemyCar.getSpawnRate())) {
            EnemyCar enemyCar = new EnemyCar(GAME_PROPS);
            enemyCars.add(enemyCar);
            damagers.add(enemyCar);
        }
    }

    /**
     * Call update methods for each game entity.
     * @param input The current mouse/keyboard input.
     */
    private void updateEntities(Input input) {
        driver.update(input, taxi);

        for (Passenger passenger : passengers) {
            passenger.update(input, taxi);
        }

        for (Coin coin : coins) {
            coin.update(input, playableEntities);
        }

        for (InvinciblePower invinciblePower : invinciblePowers) {
            invinciblePower.update(input, playableEntities);
        }

        for (TripEndFlag tripEndFlag : tripEndFlags) {
            tripEndFlag.update(input);
        }

        for (Fireball fireball:  fireballs) {
            fireball.update(input);
        }

        for (DamagedTaxi damagedTaxi : damagedTaxis) {
            damagedTaxi.update(input);
        }

        for (OtherCar otherCar : otherCars) {
            otherCar.update(input);
        }

        for (EnemyCar enemyCar : enemyCars) {
            enemyCar.update(input);
            if (!enemyCar.isTerminated()) {
                generateFireballs(enemyCar);
            }
        }

        taxi.update(input);

        for (VisualEffect visualEffect : visualEffects) {
            visualEffect.update(input);
        }

        if (taxi.getTrip() != null) {
            taxi.getTrip().update();
        }

    }

    /**
     * Logic for game entity collisions.
     */
    private void updateCollisions() {
        ArrayList<DamageInflictable> entitiesToRemove = new ArrayList<>();

        // Check the list of all possible 'damager' entities (i.e. entities which implement DamageInflictable)
        for (DamageInflictable entityA : damagers) {
            // Check if the damager (entity A) has collided with any entities
            Damageable entityB = entityA.checkForCollision(this);

            // If there's a confirmed collision with entityB...
            if (entityB != null) {

                // Determine if entityB is above entityA
                boolean isAbove = ((GameObject) entityB).getY() < ((GameObject) entityA).getY();

                // Implement collision logic
                if (!entityB.isInCollisionTimeout()) {
                    entityA.inflictDamage(entityB);
                    entityB.addCollisionVFX(visualEffects, GAME_PROPS);
                    entityB.enterCollisionTimeout(isAbove);
                    // Terminate entity if health goes below zero
                    if (entityB.getHealth() <= 0) {
                        entityB.terminate(visualEffects, damagedTaxis, GAME_PROPS);
                        if (entityB instanceof DamageInflictable) {
                            entitiesToRemove.add((DamageInflictable) entityB);
                        }
                    }
                }

                // If entity B can cause damage to entity A, implement the collision the other way round
                if (entityA instanceof Damageable) {
                    if (!((Damageable) entityA).isInCollisionTimeout()) {
                        if (entityB instanceof DamageInflictable) {
                            ((DamageInflictable) entityB).inflictDamage((Damageable) entityA);
                        }
                        ((Damageable) entityA).addCollisionVFX(visualEffects, GAME_PROPS);
                        ((Damageable) entityA).enterCollisionTimeout(!isAbove);
                        if (((Damageable) entityA).getHealth() <= 0) {
                            ((Damageable) entityA).terminate(visualEffects, damagedTaxis, GAME_PROPS);
                            entitiesToRemove.add(entityA);
                        }
                    }
                }
            }
        }

        // Remove the damagers that have been terminated, so they don't get checked again
        damagers.removeAll(entitiesToRemove);
    }

    /**
     * Set the current weather condition based on the game weather file, and notify weather observers (passengers).
     */
    private void setCurrentWeather() {
        for (WeatherCondition weatherCondition : weatherConditions) {
            if (frameCount >= weatherCondition.getStartFrame() && frameCount <= weatherCondition.getEndFrame()) {
                if (weatherCondition.getCondition() != currentWeather) {
                    currentWeather = weatherCondition.getCondition();
                    notifyWeatherObservers();
                }
            }
        }
    }

    /**
     * Get the number of frames remaining of the game.
     * @return number of frames remaining.
     */
    private int getFramesRemaining() {
        return MAX_FRAMES - frameCount;
    }

    /**
     * Randomly generate fireballs shot from an enemy car.
     * @param enemyCar The enemy car that the fireball is shot from.
     */
    private void generateFireballs(EnemyCar enemyCar) {
        if (MiscUtils.canSpawn(Fireball.getSpawnRate())) {
            Fireball fireball = new Fireball(enemyCar.getX(), enemyCar.getY(), enemyCar, GAME_PROPS);
            fireballs.add(fireball);
            damagers.add(fireball);
        }
    }

    /**
     * Check conditions to end the game.
     */
    private void checkEndGame() {
        boolean targetScoreReached = taxi.getScore() >= taxi.getTargetScore();
        boolean ranOutOfFrames = getFramesRemaining() < 0;
        boolean taxiOutOfBounds = taxi.getY() > Window.getHeight();

        if (targetScoreReached || ranOutOfFrames || taxiOutOfBounds || deathOccurred()) {
            endGame(targetScoreReached);
        }
    }

    /**
     * Set flags to indicate game has ended, and to indicate if game was a win or a loss.
     * And write player score to the leaderboard.
     * @param wonGame True in player won the game. False otherwise.
     */
    private void endGame(boolean wonGame) {
        finishedGame = true;
        this.wonGame = wonGame;
        IOUtils.writeScoreToFile(GAME_PROPS.getProperty("gameEnd.scoresFile"),
                PLAYER_NAME + ", " + taxi.getScore());
    }

    /**
     * Check if any passenger or driver deaths have occurred.
     * @return True if driver or any passenger is dead. False otherwise.
     */
    private boolean deathOccurred() {
        if (driver.isTerminated()) {
            return true;
        }

        for (Passenger passenger : passengers) {
            if (passenger.isTerminated()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Notify weather observers of weather change.
     */
    private void notifyWeatherObservers() {
        for (WeatherObserver observer : weatherObservers) {
            observer.updateWeatherChange(currentWeather);
        }
    }

}
