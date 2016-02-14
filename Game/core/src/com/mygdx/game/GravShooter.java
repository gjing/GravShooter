package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GravShooter extends ApplicationAdapter {
	private SpriteBatch batch;
	private ShapeRenderer planet;
	private Defender defender;
	private OrthographicCamera camera;
	
	@Override
	public void create () {
	    camera = new OrthographicCamera();
	    camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		planet = new ShapeRenderer();
		planet.setAutoShapeType(true);
		defender = new Defender();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		planet.begin(ShapeRenderer.ShapeType.Filled);
		planet.setColor(0.5f, 1, 0.5f, 1);
		planet.circle(400, 240, 100);
		planet.end();
		
		defender.draw();

		batch.begin();
		batch.end();
		
		if(Gdx.input.isKeyPressed(Keys.LEFT)) defender.move(-5 * Gdx.graphics.getDeltaTime());
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) defender.move(5 * Gdx.graphics.getDeltaTime());
	}
	
	public class Defender{
		private float angle;
		private int x;
		private int y;

		private ShapeRenderer render;
		
		public Defender() {
			x = 375;
			y = 340;
			angle = 0;
			render = new ShapeRenderer();
			render.setAutoShapeType(true);
		}
		
		public void draw() {
			render.begin(ShapeRenderer.ShapeType.Filled);
			render.setColor(1, 1, 0, 1);
			render.rect(x, y, 50, 25);
			render.end();
		}
		
		public void move(float radians) {
			angle = angle + radians;
			if (angle > 2.0*Math.PI) {
				angle -= 2.0*Math.PI;
			}
			else if (angle < -2.0*Math.PI) {
				angle += 2*Math.PI;
			}
			Double x_pos = 100.0 * Math.sin(angle);
			Double y_pos = 100.0 * Math.cos(angle);
			x = x_pos.intValue() + 375;
			y = y_pos.intValue() + 240;
			
		}
		
		public String getAngle() {
			return Float.toString(angle);
		}
	}
}
