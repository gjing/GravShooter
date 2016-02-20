package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GravShooter extends ApplicationAdapter {
	private SpriteBatch batch;
	private Defender defender;
	private OrthographicCamera camera;
	private BitmapFont font;
	private boolean debug;
	private ShapeRenderer renderer;
	private Array<Bullet> bullets;
	private static float v_bullet = 200f;
	private static float v_ast = 100f;
	private static double p_mass = 100.0;
	private long lastShotTime;
	private long lastAstTime;
	
	@Override
	public void create () {
	    camera = new OrthographicCamera();
	    camera.setToOrtho(false, 800, 600);
		batch = new SpriteBatch();
		renderer = new ShapeRenderer();
		renderer.setAutoShapeType(true);
		defender = new Defender();
	    bullets = new Array<Bullet>();
		
		font = new BitmapFont();
		debug = true;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderer.begin(ShapeRenderer.ShapeType.Filled);
		renderer.setColor(0.5f, 1, 0.5f, 1);
		renderer.circle(400, 300, 100);
		defender.draw(renderer);
		for(Bullet bullet: bullets) {
			bullet.draw(renderer);
		}
		renderer.end();

		batch.begin();
		if (debug) {
			font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			font.draw(batch, defender.getAngle(), 725, 90);
			font.draw(batch, defender.getAim(), 725, 110);
		}
		batch.end();

		if(Gdx.input.isKeyPressed(Keys.LEFT)) defender.aim(-5 * Gdx.graphics.getDeltaTime());
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) defender.aim(5 * Gdx.graphics.getDeltaTime());
		if(Gdx.input.isKeyPressed(Keys.UP)) defender.move(-5 * Gdx.graphics.getDeltaTime());
		if(Gdx.input.isKeyPressed(Keys.DOWN)) defender.move(5 * Gdx.graphics.getDeltaTime());
		if(TimeUtils.nanoTime() - lastShotTime > 500000000 && Gdx.input.isKeyPressed(Keys.SPACE)) {
			bullets.add(defender.shoot());
		}
		
		Iterator<Bullet> iter = bullets.iterator();
	    while(iter.hasNext()) {
	    	Bullet bullet = iter.next();
	    	bullet.update();
	    	if(bullet.isOffscreen()) {
	    		iter.remove();
	    	}
	    }
	}
	
	public class Defender{
		private float angle;
		private float aim_angle;
		private int x1;
		private int y1;
		private int x2;
		private int y2;
		private int x3;
		private int y3;
		private int x4;
		private int y4;
		private int x_base;
		private int y_base;
		
		public Defender() {
			angle = 0;
			aim_angle = 0;
			x1 = 390;
			y1 = 400;
			x2 = 410;
			y2 = 400;
			x3 = 400;
			y3 = 380;
			x4 = 400;
			y4 = 405;
			x_base = 400;
			y_base = 400;
		}
		
		public void draw(ShapeRenderer renderer) {
			Double x_head = 15*Math.sin(aim_angle + angle);
			Double y_head = 15*Math.cos(aim_angle + angle);
			renderer.setColor(1, 1, 0, 1);
			renderer.triangle(x1, y1, x2, y2, x3, y3);
			renderer.triangle(x1, y1, x2, y2, x4, y4);
			
			renderer.rectLine(x_base, y_base, x_head.intValue() + x_base, y_head.intValue() + y_base, 3);
		}
		
		public void move(float radians) {
			angle = angle + radians;
			if (angle > 2.0*Math.PI) {
				angle -= 2.0*Math.PI;
			}
			else if (angle < 0) {
				angle += 2.0*Math.PI;
			}
			Double x1_pos = 100*Math.sin(angle + Math.atan(.1));
			Double y1_pos = 100*Math.cos(angle + Math.atan(.1));
			Double x2_pos = 100*Math.sin(angle - Math.atan(.1));
			Double y2_pos = 100*Math.cos(angle - Math.atan(.1));
			Double x3_pos = 80*Math.sin(angle);
			Double y3_pos = 80*Math.cos(angle);
			Double x4_pos = 105*Math.sin(angle);
			Double y4_pos = 105*Math.cos(angle);
			Double xb_pos = 100*Math.sin(angle);
			Double yb_pos = 100*Math.cos(angle);
			x1 = x1_pos.intValue() + 400;
			y1 = y1_pos.intValue() + 300;
			x2 = x2_pos.intValue() + 400;
			y2 = y2_pos.intValue() + 300;
			x3 = x3_pos.intValue() + 400;
			y3 = y3_pos.intValue() + 300;
			x4 = x4_pos.intValue() + 400;
			y4 = y4_pos.intValue() + 300;
			x_base = xb_pos.intValue() + 400;
			y_base = yb_pos.intValue() + 300;
		}
		
		public void aim(float radians) {
			aim_angle = aim_angle + radians;
			if (aim_angle >= 1.0) {
				aim_angle = 1f;
			}
			else if (aim_angle <= -1.0) {
				aim_angle = -1f;
			}
			
		}

		public String getAngle() {
			return Float.toString(angle);
		}
		public String getAim() {
			return Float.toString(aim_angle);
		}
		
		public Bullet shoot() {
			Double x_head = 15*Math.sin(aim_angle + angle);
			Double y_head = 15*Math.cos(aim_angle + angle);
			lastShotTime = TimeUtils.nanoTime();
			return new Bullet(x_head.intValue() + x_base, y_head.intValue() + y_base, angle+aim_angle);
		}
	}
	
	public class Bullet extends Circle {
		private Double x;
		private Double y;
		private Double vx;
		private Double vy;
		
		public Bullet(int x, int y, float angle) {
			this.x = (double)(x-400);
			this.y = (double)(y-300);
			vx = v_bullet*Math.sin(angle);
			vy = v_bullet*Math.cos(angle);
		}

		public void draw(ShapeRenderer renderer) {
			renderer.setColor(1, 1, 0, 1);
			renderer.circle(x.intValue() + 400, y.intValue() + 300, 3);
		}
		
		/*
		 * calculates the position of the bullet, after taking in 'gravitational' effects from the 'planet'
		 */
		public void update()
		{
			Double r = Math.pow(x, 2.0) + Math.pow(y, 2.0);
			Double dv = 1700000.0 * Gdx.graphics.getDeltaTime() * p_mass/r;
			Double dvx = Math.abs(dv*(x/Math.sqrt(r)));
			Double dvy = Math.abs(dv*(y/Math.sqrt(r)));
			if (x>0) {
				dvx = -dvx;
			}
			if (y>0) {
				dvy = -dvy;
			}
			vx += dvx*Gdx.graphics.getDeltaTime();
			vy += dvy*Gdx.graphics.getDeltaTime();
			Double dx = (vx)*Gdx.graphics.getDeltaTime();
			Double dy = (vy)*Gdx.graphics.getDeltaTime();
			x += dx;
			y += dy;
		}

		public boolean isOffscreen()
		{
			return 400<=x && x<=-400 && 300<=y && y<=-300;
		}
	}
	
	public class Asteroid extends Circle {
		private Double x;
		private Double y;
		private Double vx;
		private Double vy;
		
		public Asteroid(int x, int y, float angle) {
			this.x = (double)(x-400);
			this.y = (double)(y-300);
			vx = v_ast*Math.sin(angle);
			vy = v_ast*Math.cos(angle);
		}

		public void draw(ShapeRenderer renderer) {
			renderer.setColor(1, 0, 0, 1);
			renderer.circle(x.intValue() + 400, y.intValue() + 300, 30);
		}
		
		/*
		 * calculates the position of the bullet, after taking in 'gravitational' effects from the 'planet'
		 */
		public void update()
		{
			Double r = Math.pow(x, 2.0) + Math.pow(y, 2.0);
			Double dv = 1800000.0 * Gdx.graphics.getDeltaTime() * p_mass/r;
			Double dvx = Math.abs(dv*(x/Math.sqrt(r)));
			Double dvy = Math.abs(dv*(y/Math.sqrt(r)));
			if (x>0) {
				dvx = -dvx;
			}
			if (y>0) {
				dvy = -dvy;
			}
			vx += dvx*Gdx.graphics.getDeltaTime();
			vy += dvy*Gdx.graphics.getDeltaTime();
			Double dx = (vx)*Gdx.graphics.getDeltaTime();
			Double dy = (vy)*Gdx.graphics.getDeltaTime();
			x += dx;
			y += dy;
		}

		public boolean isOffscreen()
		{
			return 400<=x && x<=-400 && 300<=y && y<=-300;
		}
	}

	@Override
	public void dispose() {
		font.dispose();
		renderer.dispose();
		batch.dispose();
	}
}
