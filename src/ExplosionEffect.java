import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ExplosionEffect {
    private static final int PARTICLE_COUNT = 18;
    private static final int MIN_LIFE = 18;
    private static final int LIFE_RANGE = 18;
    private static final double MIN_SPEED = 1.3;
    private static final double SPEED_RANGE = 2.8;

    private final List<Particle> particles = new ArrayList<>();

    public ExplosionEffect(int x, int y, Random random) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = MIN_SPEED + random.nextDouble() * SPEED_RANGE;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            int life = MIN_LIFE + random.nextInt(LIFE_RANGE);
            Color color = random.nextBoolean() ? new Color(255, 210, 90) : new Color(255, 92, 92);

            particles.add(new Particle(x, y, vx, vy, life, color));
        }
    }

    public void update() {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.update();

            if (!particle.isAlive()) {
                iterator.remove();
            }
        }
    }

    public void render(Graphics2D g) {
        for (Particle particle : particles) {
            particle.render(g);
        }
    }

    public boolean isFinished() {
        return particles.isEmpty();
    }
}
