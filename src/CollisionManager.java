import java.util.Iterator;
import java.util.List;

public class CollisionManager {
    public void handleBulletAlienCollisions(
            List<Bullet> bullets,
            List<Alien> aliens,
            Runnable onAlienDestroyed) {
        Iterator<Bullet> bulletIterator = bullets.iterator();

        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();

            for (Alien alien : aliens) {
                if (alien.isAlive() && bullet.getBounds().intersects(alien.getBounds())) {
                    alien.destroy();
                    bulletIterator.remove();
                    onAlienDestroyed.run();
                    break;
                }
            }
        }
    }
}
