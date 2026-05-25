import java.util.Iterator;
import java.util.List;

public class CollisionManager {
    public CollisionResult handleCollisions(
            List<Bullet> bullets,
            List<Alien> aliens,
            List<Shield> shields,
            Player player) {
        CollisionResult result = new CollisionResult();

        handlePlayerBullets(bullets, aliens, shields, result);
        handleEnemyBullets(bullets, shields, player, result);

        return result;
    }

    private void handlePlayerBullets(
            List<Bullet> bullets,
            List<Alien> aliens,
            List<Shield> shields,
            CollisionResult result) {
        Iterator<Bullet> iterator = bullets.iterator();

        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();

            if (bullet.getType() != BulletType.PLAYER_BULLET) {
                continue;
            }

            if (hitShield(bullet, shields)) {
                iterator.remove();
                continue;
            }

            Alien hitAlien = findHitAlien(bullet, aliens);
            if (hitAlien != null) {
                hitAlien.destroy();
                iterator.remove();
                result.addDestroyedAlien(hitAlien);
            }
        }
    }

    private void handleEnemyBullets(
            List<Bullet> bullets,
            List<Shield> shields,
            Player player,
            CollisionResult result) {
        Iterator<Bullet> iterator = bullets.iterator();

        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();

            if (bullet.getType() != BulletType.ENEMY_BULLET) {
                continue;
            }

            if (hitShield(bullet, shields)) {
                iterator.remove();
                continue;
            }

            if (bullet.getBounds().intersects(player.getBounds())) {
                iterator.remove();
                result.setPlayerHit(true);
            }
        }
    }

    private Alien findHitAlien(Bullet bullet, List<Alien> aliens) {
        for (Alien alien : aliens) {
            if (alien.isAlive() && bullet.getBounds().intersects(alien.getBounds())) {
                return alien;
            }
        }

        return null;
    }

    private boolean hitShield(Bullet bullet, List<Shield> shields) {
        for (Shield shield : shields) {
            if (!shield.isDestroyed() && bullet.getBounds().intersects(shield.getBounds())) {
                shield.damage();
                return true;
            }
        }

        return false;
    }

    public static class CollisionResult {
        private final List<Alien> destroyedAliens = new java.util.ArrayList<>();
        private boolean playerHit;

        public void addDestroyedAlien(Alien alien) {
            destroyedAliens.add(alien);
        }

        public List<Alien> getDestroyedAliens() {
            return destroyedAliens;
        }

        public boolean isPlayerHit() {
            return playerHit;
        }

        public void setPlayerHit(boolean playerHit) {
            this.playerHit = playerHit;
        }
    }
}
