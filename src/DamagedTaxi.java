import bagel.*;
import java.util.Properties;

public class DamagedTaxi extends GameObject{
    private final Image SPRITE;

    // Constructor
    public DamagedTaxi(double x, double y, Properties gameProps) {
        super(x, y, gameProps);
        SPRITE = new Image(gameProps.getProperty("gameObjects.taxi.damagedImage"));
    }

    public void update(Input input) {
        moveRelativeToPlayer(input);
        SPRITE.draw(getX(), getY());
    }
}
