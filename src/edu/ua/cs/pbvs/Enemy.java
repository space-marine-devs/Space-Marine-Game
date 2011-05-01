package edu.ua.cs.pbvs;

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
		move(dir);
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
		Vector2 vector = Vector2Pool.obtain(dir*20, 0);
		world.getPhysicsConnectorManager().findBodyByShape(this).setLinearVelocity(vector);
		 Vector2Pool.recycle(vector);
	}
	
	int pathLength = 15;
	public void onUpdate(int pSecondsElapsed)
	{
		if(!this.mIgnoreUpdate) {
			this.onManagedUpdate(pSecondsElapsed);
		}
		pathLength++;
		if(pathLength >= 15){
			pathLength = 0;
			dir *= -1;
			move(dir);
		}
	}
}
