package nl.mutt.towerdefense.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class LiveObject extends Sprite{
	
	protected World world;
	protected Body b2body;
	
	public LiveObject(World world){
		this.world = world;
	}
	
	public void update() {
		
	}
	
	public Body getBody() {
		return b2body;
	}

}
