public interface DamageInflictable extends Collidable {
    Damageable checkForCollision(GamePlayScreen gamePlayScreen);
    void inflictDamage(Damageable target);
    void setDamagePoints(double damagePoints);
}
