package nl.mutt.towerdefense;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import nl.mutt.towerdefense.screens.PlayScreen;

public class Main extends Game {
	
	public static int WIDTH, HEIGHT;
	public static final float PPM = 64;
	
	public SpriteBatch batch;
	
	private PlayScreen play;
	
	@Override
	public void create () {
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		
		batch = new SpriteBatch();
		
		play = new PlayScreen(this);
	}

	@Override
	public void render () {
		play.render(1/60f);
		batch.begin();
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		super.dispose();
	}
}
