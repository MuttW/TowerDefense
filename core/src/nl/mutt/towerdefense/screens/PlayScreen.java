package nl.mutt.towerdefense.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import nl.mutt.towerdefense.Main;
import nl.mutt.towerdefense.objects.Bullet;
import nl.mutt.towerdefense.objects.Enemy;
import nl.mutt.towerdefense.objects.Player;
import nl.mutt.towerdefense.objects.Tower;
import nl.mutt.towerdefense.utils.CollisionHandler;
import nl.mutt.towerdefense.utils.SpawnHandler;

public class PlayScreen implements Screen {

	// Reference to main
	private Main main;

	// Camera
	private OrthographicCamera camera;
	private Viewport port;

	// Map variables
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;

	// Box2D variables
	private World world;
	private Box2DDebugRenderer b2dr;

	// Game Objects
	private SpawnHandler spawn;
	private ArrayList<Enemy> enemies;
	private ArrayList<Tower> towers;
	private CollisionHandler collision;
	private Player player;

	public PlayScreen(Main main) {
		this.main = main;

		camera = new OrthographicCamera(Main.WIDTH / Main.PPM, Main.HEIGHT / Main.PPM);

		port = new FitViewport(Main.WIDTH / Main.PPM, Main.HEIGHT / Main.PPM, camera);

		map = new TmxMapLoader().load("maps/map1.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1 / Main.PPM);

		camera.position.set(port.getWorldWidth() / 2, port.getWorldHeight() / 2, 0);
		camera.update();

		world = new World(new Vector2(0, 0), true);
		b2dr = new Box2DDebugRenderer();
		

		BodyDef bDef = new BodyDef();
		PolygonShape shape = new PolygonShape();
		FixtureDef fDef = new FixtureDef();
		Body body;

		for (int i = 0; i < 2; i++) {
			for (MapObject object : map.getLayers().get(4 + i).getObjects().getByType(RectangleMapObject.class)) {
				Rectangle rect = ((RectangleMapObject) object).getRectangle();

				bDef.type = BodyDef.BodyType.StaticBody;
				bDef.position.set((rect.getX() + rect.getWidth() / 2) / Main.PPM,
						(rect.getY() + rect.getHeight() / 2) / Main.PPM);

				body = world.createBody(bDef);

				shape.setAsBox(rect.getWidth() / 2 / Main.PPM, rect.getHeight() / 2 / Main.PPM);
				fDef.shape = shape;
				body.createFixture(fDef);
			}
		}

		Vector2[] path = null;
		for (MapObject object : map.getLayers().get(3).getObjects().getByType(PolylineMapObject.class)) {
			Polyline line = ((PolylineMapObject) object).getPolyline();

			float[] vertices = line.getVertices();
			path = new Vector2[vertices.length / 2];
			for (int i = 0; i < path.length; i++) {
				path[i] = new Vector2((vertices[i * 2] + line.getX()) / Main.PPM,
						(vertices[i * 2 + 1] + line.getY()) / Main.PPM);
			}
		}

		enemies = new ArrayList<Enemy>();

		towers = new ArrayList<Tower>();
		Tower tower = new Tower(world, 3, 2);
		Tower tower2 = new Tower(world, 4, 6);
		towers.add(tower);
		towers.add(tower2);

		spawn = new SpawnHandler(enemies, world, path);
		spawn.spawn("maps/map1.json");
		collision = new CollisionHandler();
		
		player = new Player();
	}

	@Override
	public void show() {

	}

	public void handleInput(float delta) {

	}

	public void update(float delta) {
		handleInput(delta);

		handleCollisions();

		enemyUpdates();
		towerUpdates(towers);

		world.step(1 / 60f, 6, 2);

		camera.update();
		renderer.setView(camera);
	}

	private void handleCollisions() {
		Enemy[] enemy = new Enemy[enemies.size()];
		Tower[] tower = new Tower[towers.size()];
		enemy = enemies.toArray(enemy);
		tower = towers.toArray(tower);
		collision.radiusIterationCheck(enemy, tower, 10);

		for (Tower t : tower) {
			ArrayList<Bullet> bullets = new ArrayList<Bullet>();
			for (Bullet b : t.bullets) {
				if (collision.bulletCollided(b)) {
					b.damageTarget();
					world.destroyBody(b.getBody());
				} else {
					bullets.add(b);
				}
			}
			t.bullets = bullets;
		}
	}

	public void enemyUpdates() {
		ArrayList<Enemy> list2 = new ArrayList<Enemy>();
		for (Enemy e : spawn.enemies) {
			e.update();
			if(e.isAlive()){
				list2.add(e);
			} else {
				if(e.isAttacking()){
					player.health -= 10;
					System.out.println("Attacking base");
				}
				world.destroyBody(e.getBody());
				player.money += 25;
			}
			
		}
		spawn.enemies = list2;
		enemies = list2;
	}

	public void towerUpdates(ArrayList<Tower> list) {
		for (Tower t : list) {
			t.update();
		}
	}

	@Override
	public void render(float delta) {
		update(delta);

		Gdx.gl.glClearColor(0.169f, 0.259f, 0.365f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Render OrthogonalTiledMapRenderer
		renderer.render();

		// Render Box2DDebugRenderer
		b2dr.render(world, camera.combined);

		main.batch.begin();
		main.batch.end();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		map.dispose();
		renderer.dispose();
		world.dispose();
		b2dr.dispose();
	}

}
