package nl.mutt.towerdefense.utils;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import nl.mutt.towerdefense.objects.Bullet;
import nl.mutt.towerdefense.objects.Enemy;
import nl.mutt.towerdefense.objects.Tower;

public class CollisionHandler {

	public void radiusIterationCheck(Enemy[] enemies, Tower[] towers, int iterations) {
		for (Tower t : towers) {
			Circle shape = new Circle();
			shape.setPosition(t.getBody().getPosition());
			if(!t.hasTarget()){
				for (int i = 0; i < iterations; i++) {
					shape.setRadius(t.range / iterations * i);
					for (Enemy e : enemies) {
						if (shape.contains(e.getBody().getPosition())) {
							t.target = e;
						}
					}
				}
			} else {
				shape.setRadius(t.range);
				if(!shape.contains(t.target.getBody().getPosition()))
					t.target = null;
			}
		}
	}

	public boolean bulletCollided(Bullet bullet) {
		Vector2 enemyPosition = bullet.getTargetPosition();
		return enemyPosition.epsilonEquals(bullet.getBody().getPosition(), 0.25f);
	}

}
