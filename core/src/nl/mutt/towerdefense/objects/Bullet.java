package nl.mutt.towerdefense.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import nl.mutt.towerdefense.Main;

public class Bullet extends LiveObject {

	private int damage;
	private Enemy target;
	private Vector2 velocity;
	
	public Bullet(World world, Vector2 position, int damage, Enemy target){
		super(world);
		
		this.damage = damage;
		this.target = target;
		this.velocity = target.getBody().getPosition().sub(position).setLength(25f);
		
		// define body
		BodyDef bdef = new BodyDef();
		bdef.position.set(position);
		bdef.type = BodyDef.BodyType.KinematicBody;
		b2body = world.createBody(bdef);

		// Define shape
		CircleShape shape = new CircleShape();
		shape.setRadius(6 / 2 / Main.PPM);

		// Define fixture
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.isSensor = true;

		b2body.createFixture(fdef);
		b2body.setLinearVelocity(velocity);
	}
	
	public Vector2 getTargetPosition(){
		return target.getBody().getPosition();
	}
	
	public void damageTarget(){
		target.damage(damage);
	}

}
