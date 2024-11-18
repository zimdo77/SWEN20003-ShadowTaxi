public class WeatherCondition {
    public enum Weather {
        SUNNY, RAINING, NONE
    }

    private final Weather condition;
    private final int startFrame;
    private final int endFrame;

    // Constructor
    public WeatherCondition(Weather condition, int startFrame, int endFrame) {
        this.condition = condition;
        this.startFrame = startFrame;
        this.endFrame = endFrame;
    }

    // Getters and Setters
    public Weather getCondition() {
        return condition;
    }

    public int getStartFrame() {
        return startFrame;
    }

    public int getEndFrame() {
        return endFrame;
    }
}
