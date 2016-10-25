package nl.mutt.towerdefense.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import nl.mutt.towerdefense.Main;

public class Enemy extends LiveObject {

	// Path variables
	private Vector2[] path;
	private int cPath;

	// Game variables
	private int baseHealth;
	private int health;
	private float walkSpeed;
	private float cornerBrake;
	private boolean attacking;
	
	public Enemy(World world, Vector2[] path) {
		super(world);
		this.path = path;

		init();

		Vector2 spawn = path[0];
		cPath = 1;

		// define body
		BodyDef bdef = new BodyDef();
		bdef.position.set(spawn.x, spawn.y);
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);

		//  Define shape
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(10 / 2 / Main.PPM, 10 / 2 / Main.PPM);
		
		// Define fixture
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.friction = 1f;
		
		b2body.createFixture(fdef);
	}
	
	private void init(){
		this.baseHealth = 100;
		this.health = baseHealth;
		this.walkSpeed = 2f;
		this.cornerBrake = -4 * walkSpeed;
	}

	public void update(){
		if(isAlive())
			handlePathWalking();
	}
	
	public void handlePathWalking() {
		// Check is Enemy body reached end of line, if not walk further, if so walk next line.
		if (!b2body.getPosition().epsilonEquals(path[cPath], 0.3f)) {
			
			// direction of current path
			float a = (path[cPath].y - path[cPath - 1].y) / (path[cPath].x - path[cPath - 1].x);
			
			float x = b2body.getPosition().x;
			float y = b2body.getPosition().y;
			
			// Y Directions
			if (a > 1) {
				if (b2body.getLinearVelocity().y < walkSpeed)
					b2body.applyLinearImpulse(0, walkSpeed, x, y, true);
			} else if (a < -1) {
				if (b2body.getLinearVelocity().y > -walkSpeed)
					b2body.applyLinearImpulse(0, -walkSpeed, x, y, true);
				
			} else { // If not moving in y, apply force in opposite direction to brake
				b2body.applyForceToCenter(b2body.getLinearVelocity().scl(new Vector2(0, cornerBrake)), true);
			}
			
			// X Directions
			if (a < 0 && a > -1) {
				if (b2body.getLinearVelocity().x < walkSpeed)
					b2body.applyLinearImpulse(walkSpeed, 0, x, y, true);
			} else if (a > 0 && a < 1) {
				if (b2body.getLinearVelocity().x > -walkSpeed)
					b2body.applyLinearImpulse(-walkSpeed, 0, x, y, true);
			} else { // If not moving in x, apply force in opposite direction to brake
				b2body.applyForceToCenter(b2body.getLinearVelocity().scl(new Vector2(cornerBrake, 0)), true);
			}
			
		} else { // Walk next path
			if (cPath < path.length - 1)
				cPath++;
			else { // No next path available
				health = 0;
				attacking = true;
			}

		}
	}
	
	public void damage(int damage){
		health -= damage;
		System.out.println(damage + " taken. " + health + " health left.");
	}
	
	public boolean isAlive(){
		return health > 0;
	}
	
	public boolean isAttacking(){
		return attacking;
	}

}
