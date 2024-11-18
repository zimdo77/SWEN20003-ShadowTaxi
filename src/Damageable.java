import java.util.ArrayList;
import java.util.Properties;

public interface Damageable extends Collidable {

    // Taking damage
    boolean canTakeDamage();

    // Collision
    void addCollisionVFX(ArrayList<VisualEffect> visualEffects, Properties gameProps);
    boolean isInCollisionTimeout();
    void enterCollisionTimeout(boolean isAbove);
    void deactivateCollisionTimeout();
    void renderCollisionTimeout();

    // Termination
    boolean isTerminated();
    void terminate(ArrayList<VisualEffect> visualEffects, ArrayList<DamagedTaxi> damagedTaxis, Properties gameProps);

    // Health
    double getHealth();
    void setHealth(double health);
}
