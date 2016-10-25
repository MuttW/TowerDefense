package nl.mutt.towerdefense.utils;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Timer;

import nl.mutt.towerdefense.objects.Enemy;
import nl.mutt.towerdefense.utils.data.Waves;

public class SpawnHandler {

	public ArrayList<Enemy> enemies;
	World world;
	Vector2[] path;
	Waves data;
	Timer timer;

	public SpawnHandler(ArrayList<Enemy> enemies, World world, Vector2[] path) {
		this.enemies = enemies;
		this.world = world;
		this.path = path;
	}

	public void spawn(String filePath) {
		getSpawnData(filePath);
		setEnemySpawnTimer();
	}

	public void getSpawnData(String path) {
		Json json = new Json();
		data = json.fromJson(Waves.class, Gdx.files.internal(path));
	}

	public void setEnemySpawnTimer() {
		timer = new Timer();
		for (int i = 0; i < data.waves.length; i++) {
			for (int j = 0; j < data.waves[i].amount; j++) {
				timer.scheduleTask(new Timer.Task() {
					@Override
					public void run() {
						enemies.add(new Enemy(world, path));
					}
				}, data.waves[i].delay + data.waves[i].interval*j);
			}
		}
	}

}
