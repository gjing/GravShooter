package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GravShooter extends ApplicationAdapter {
	private SpriteBatch batch;
	private Defender defender;
	private Planet planet;
	private OrthographicCamera camera;
	private BitmapFont font;
	private boolean debug;
	private ShapeRenderer renderer;
	private Array<Bullet> bullets;
	private Array<Asteroid> asteroids;
	private static float v_bullet = 200f;
	private static double p_mass = 100.0;
	private long lastShotTime;
	private long lastAstTime;
    Circle circle2;
    Circle planet_circle;
    public int window_center_x = 400; 
    public int window_center_y = 300;
    public int score;
    public int ast_spawn_time;
	private boolean game_over;
	
	@Override
	public void create () {
	    camera = new OrthographicCamera();
	    camera.setToOrtho(false, window_center_x*2, window_center_y*2);
		batch = new SpriteBatch();
		renderer = new ShapeRenderer();
		renderer.setAutoShapeType(true);
		defender = new Defender();
		planet = new Planet();
	    bullets = new Array<Bullet>();
        planet_circle = planet.circle();
        score = 0;
        ast_spawn_time = 2130000000;
        game_over = false;
		
		font = new BitmapFont();
		debug = false;

	    asteroids = new Array<Asteroid>();
	    spawnAsteroid();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (!game_over) {
			
			renderer.begin(ShapeRenderer.ShapeType.Filled);
	
	        planet.draw(renderer);
			defender.draw(renderer);
			for(Bullet bullet: bullets) {
				bullet.draw(renderer);
		    	bullet.update();
			}
			for(Asteroid ast: asteroids) {
				ast.draw(renderer);
		    	ast.update();
			}
	
			/*
			 * removes bullets that have floated around for more than 60 seconds
			 */
			Iterator<Bullet> bullet_iter = bullets.iterator();
		    while(bullet_iter.hasNext()) {
		    	Bullet bullet = bullet_iter.next();
		    	if(bullet.isExpired(TimeUtils.nanoTime())) {
		    		bullet_iter.remove();
		    	}
		    }
		    
			/*
			 * destroys asteroids when they collide with planet or when they collide with bullet
			 */
			Iterator<Asteroid> ast_iter = asteroids.iterator();
		    while(ast_iter.hasNext()) {
		    	Asteroid ast = ast_iter.next();
		    	Circle ast_circle = ast.circle();
		    	
		    	Iterator<Bullet> bullet_hit = bullets.iterator();
			    while(bullet_hit.hasNext()) {
			    	Bullet bullet = bullet_hit.next();
			    	if(ast_circle.overlaps(bullet.circle())) {
			    		ast_iter.remove();
			    		bullet_hit.remove();
			    		/*Play a sound here*/
			    		score++;
			    		if (ast_spawn_time > 130000000) {
			    			ast_spawn_time -= 20000000;
			    		}
			    	}
			    }
		    	if(ast_circle.overlaps(planet_circle)) {
		    		ast_iter.remove();
		    		/*Play a sound here and put in an explosion or something*/
		    		planet.hurt(ast.get_radius());
		    	}
		    }
	
			renderer.end();
	
			if(Gdx.input.isKeyPressed(Keys.LEFT)) defender.aim(-5 * Gdx.graphics.getDeltaTime());
			if(Gdx.input.isKeyPressed(Keys.RIGHT)) defender.aim(5 * Gdx.graphics.getDeltaTime());
			if(Gdx.input.isKeyPressed(Keys.UP)) defender.move(-5 * Gdx.graphics.getDeltaTime());
			if(Gdx.input.isKeyPressed(Keys.DOWN)) defender.move(5 * Gdx.graphics.getDeltaTime());
			if(TimeUtils.nanoTime() - lastShotTime > 500000000 && Gdx.input.isKeyPressed(Keys.SPACE)) {
				bullets.add(defender.shoot());
			}
		    
		    if(TimeUtils.nanoTime() - lastAstTime > ast_spawn_time) spawnAsteroid();
		    batch.begin();
			if (debug) {
				font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				font.draw(batch, defender.getAngle(), 725, 90);
				font.draw(batch, defender.getAim(), 725, 110);
				font.draw(batch,  String.format("health %d", planet.get_health()), window_center_x, window_center_y);
			}
			if (planet.get_health() < 0) {
				game_over = true;
			}
			font.draw(batch, String.format("score %d", score), 10, window_center_y*2-10);
			batch.end();
		}
		else {
			batch.begin();
			font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			font.getData().setScale(5);
			font.draw(batch, "Game Over", 210, 350);
			font.draw(batch, String.format("Score: %d", score), 250, 250);
			font.getData().setScale(2);
			font.draw(batch, "Hit Enter to Restart", 260, 170);
			batch.end();
			if(Gdx.input.isKeyPressed(Keys.ENTER)) {
				score = 0;
				defender = new Defender();
				planet = new Planet();
			    bullets = new Array<Bullet>();
		        planet_circle = planet.circle();
		        score = 0;
		        game_over = false;
			    asteroids = new Array<Asteroid>();
			    spawnAsteroid();
				font.getData().setScale(1);
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
	
	public class Bullet{
		private Double x;
		private Double y;
		private Double vx;
		private Double vy;
		private int radius;
		private long spawnTime;
		
		public Bullet(int x, int y, float angle) {
			this.x = (double)(x-400);
			this.y = (double)(y-300);
			radius = 3;
			vx = v_bullet*Math.sin(angle);
			vy = v_bullet*Math.cos(angle);
			spawnTime = TimeUtils.nanoTime();
		}

		public void draw(ShapeRenderer renderer) {
			renderer.setColor(1, 1, 0, 1);
			renderer.circle(true_x(), true_y(), radius);
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

		public boolean isExpired(long time)
		{
			return time - spawnTime > 60000000000.0;
		}

		/*
		 * returns the absolute x position
		 */
		public int true_x() {
			return x.intValue() + window_center_x;
		}

		/*
		 * returns the absolute x position
		 */
		public int true_y() {
			return y.intValue() + window_center_y;
		}
		
		public Circle circle() {
			return new Circle(true_x(), true_y(), radius);
		}
	}

	public class Asteroid {
		private Double x;
		private Double y;
		private Double vx;
		private Double vy;
		private int radius;
		
		public Asteroid() {
			radius = MathUtils.random(10,20);
			x = (double)MathUtils.random(window_center_x + (radius*2), window_center_x + (radius*3));
			y = (double)MathUtils.random(window_center_y + (radius*2), window_center_y + (radius*3));
			if (MathUtils.randomBoolean()) {
				x = (double)MathUtils.random(-(window_center_x + (radius*3)), window_center_x + (radius*3));
			}
			else {
				y = (double)MathUtils.random(-(window_center_y + (radius*3)), window_center_y + (radius*3));
			}
			if (MathUtils.randomBoolean()) {
				x = -x;
			}
			if (MathUtils.randomBoolean()) {
				y = -y;
			}
			vx = (double)MathUtils.random(-(radius*3), (radius*3));
			vy = (double)MathUtils.random(-(radius*3), (radius*3));
		}

		public void draw(ShapeRenderer renderer) {
			renderer.setColor(Color.SLATE);
			renderer.circle(true_x(), true_y(), radius);
		}
		
		/*
		 * calculates the position of the asteroid, after taking in 'gravitational' effects from the 'planet'
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
		
		/*
		 * returns the absolute x position
		 */
		public int true_x() {
			return x.intValue() + window_center_x;
		}

		/*
		 * returns the absolute y position
		 */
		public int true_y() {
			return y.intValue() + window_center_y;
		}
		
		public int get_radius() {
			return radius;
		}
		
		public Circle circle() {
			return new Circle(true_x(), true_y(), 15);
		}
	}
	
	private void spawnAsteroid() {
	    Asteroid ast = new Asteroid();
	    asteroids.add(ast);
	    lastAstTime = TimeUtils.nanoTime();
	}

	public class Planet {
		private int health;
		private Color color;
		private float r;
		private float g;
		
		public Planet() {
			health = 1000;
			r = 0f;
			g = 1f;
			color = new Color(r, g, 0.2f, 0f);
		}

		public void draw(ShapeRenderer renderer) {
			color.set(r, g, 0.2f, 0f);
			renderer.setColor(color);
			renderer.circle(window_center_x, window_center_y, window_center_x/4);
		}
		
		public void hurt(int ast_rad) {
			health -= ast_rad;
			r = (float)(1.0 - health/1000.0);
			g = (float)health/1000;
			if (r > 1.0f) {
				r = 1f;
			}
			if (g < 0f) {
				g = 0f;
			}
		}
		
		public int get_health() {
			return health;
		}
		
		public Circle circle() {
			return new Circle(window_center_x, window_center_y, window_center_x/4);
		}
	}

	@Override
	public void dispose() {
		font.dispose();
		renderer.dispose();
		batch.dispose();
	}
}
