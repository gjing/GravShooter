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
	private BitmapFont font;
	private boolean debug;
	
	@Override
	public void create () {
	    camera = new OrthographicCamera();
	    camera.setToOrtho(false, 800, 600);
		batch = new SpriteBatch();
		planet = new ShapeRenderer();
		planet.setAutoShapeType(true);
		defender = new Defender();
		
		font = new BitmapFont();
		debug = true;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		planet.begin(ShapeRenderer.ShapeType.Filled);
		planet.setColor(0.5f, 1, 0.5f, 1);
		planet.circle(400, 300, 100);
		planet.end();

		defender.draw();

		batch.begin();
		if (debug) {
			font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			font.draw(batch, defender.getAngle(), 725, 100);
		}
		batch.end();
		
		if(Gdx.input.isKeyPressed(Keys.LEFT)) defender.move(-5 * Gdx.graphics.getDeltaTime());
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) defender.move(5 * Gdx.graphics.getDeltaTime());
	}
	
	public class Defender{
		private float angle;
		private int x1;
		private int y1;
		private int x2;
		private int y2;

		private ShapeRenderer render;
		
		public Defender() {
			angle = 0;
			render = new ShapeRenderer();
			render.setAutoShapeType(true);
			x1 = 390;
			y1 = 400;
			x2 = 410;
			y2 = 400;
		}
		
		public void draw() {
			render.begin(ShapeRenderer.ShapeType.Filled);
			render.setColor(1, 1, 0, 1);
			render.triangle(400, 300, x1, y1, x2, y2);
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
			Double x1_pos = 100*Math.sin(angle + Math.atan(.1));
			Double y1_pos = 100*Math.cos(angle + Math.atan(.1));
			Double x2_pos = 100*Math.sin(angle - Math.atan(.1));
			Double y2_pos = 100*Math.cos(angle - Math.atan(.1));
			x1 = x1_pos.intValue() + 400;
			y1 = y1_pos.intValue() + 300;
			x2 = x2_pos.intValue() + 400;
			y2 = y2_pos.intValue() + 300;
		}
		
		public String getAngle() {
			return Float.toString(angle);
		}
	}
}
