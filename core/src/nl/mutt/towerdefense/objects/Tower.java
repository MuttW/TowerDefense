package nl.mutt.towerdefense.objects;

import java.util.ArrayList;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import nl.mutt.towerdefense.Main;

public class Tower extends LiveObject {

	private int damage;
	public float range;
	private long speed;
	private long lastShot;

	public Enemy target;
	public ArrayList<Bullet> bullets;

	public Tower(World world, int column, int row) {
		super(world);

		init();

		// define body
		BodyDef bdef = new BodyDef();
		bdef.position.set((32 + column * 64) / Main.PPM, (32 + row * 64) / Main.PPM);
		bdef.type = BodyDef.BodyType.KinematicBody;
		b2body = world.createBody(bdef);

		// Define shape
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(64 / 2 / Main.PPM, 64 / 2 / Main.PPM);

		// Define fixture
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.friction = 1f;

		b2body.createFixture(fdef);
	}

	private void init() {
		damage = 50;
		range = 172f / Main.PPM;
		speed = 1000;

		bullets = new ArrayList<Bullet>();
	}

	public void update() {
		if (hasTarget()) {
			shootTarget(damage);
			targetAlive();
		}
	}

	public void targetAlive() {
		if (target.isAlive())
			return;
		else
			target = null;
	}

	public void shootTarget(int damage) {
		long time = System.currentTimeMillis();
		if (time - lastShot > speed && target != null) {
			bullets.add(new Bullet(world, b2body.getPosition(), damage, target));
			lastShot = time;
		}
	}

	public boolean hasTarget() {
		return target != null;
	}

}
