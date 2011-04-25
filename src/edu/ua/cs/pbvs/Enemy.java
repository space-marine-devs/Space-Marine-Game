package edu.ua.cs.pbvs;

import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import com.badlogic.gdx.math.Vector2;


public class Enemy extends PhysicsAnimatedSprite {
	
	private float health = 10;
	pbvs act;
	private int dir = 1;
	PhysicsWorld world;
	
	public Enemy(float pX, float pY, TiledTextureRegion pTiledTextureRegion, PhysicsWorld world, pbvs act ) {
		super(pX, pY, pTiledTextureRegion, world, act);
		this.world = world;
	    //final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0f, 0.5f);
		this.act = act;
		this.setScaleCenterY((pTiledTextureRegion.getHeight()/3)-10);
		this.setScale(3);
		this.body.setFixedRotation(true);
		createTimeHandler();
		// TODO Auto-generated constructor stub
	}
	
	public void hit() {
		health -= 2;
		if(health == 0) {
			act.removePhysicsSprite(this);
		}
	}
	
	public void move(int newDir) {
		dir = newDir;
		Vector2 vector = Vector2Pool.obtain(dir*10, 0);
		body.setLinearVelocity(vector);
	}
	
	private void createTimeHandler()
	{
	        TimerHandler timerHandler;
	       
	        act.getEngine().registerUpdateHandler(timerHandler = new TimerHandler(5, new ITimerCallback()
	        {                      
	            public void onTimePassed(final TimerHandler pTimerHandler)
	            {
	            	dir *= -1;
	            	move(dir);
	            	createTimeHandler();
	            }
	        }));
	}	

}
