package edu.ua.cs.pbvs;

import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import com.badlogic.gdx.math.Vector2;

public class Bullet extends PhysicsAnimatedSprite{
	pbvs act;


	public Bullet(float pX, float pY, TiledTextureRegion pTiledTextureRegion, PhysicsWorld world, pbvs act ) {
		super(pX, pY, pTiledTextureRegion, world, act, 0.5f);
		this.act = act;
	    //final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0f, 0.5f);
		this.setScaleCenterY((pTiledTextureRegion.getHeight()/3)-10);
		this.setScale(3);
		this.body.setFixedRotation(true);
		this.body.setBullet(true);
		// TODO Auto-generated constructor stub
	}
	
	public void shoot(int dir) {
		final Vector2 velocity = Vector2Pool.obtain(dir*1000, 0);
		body.applyLinearImpulse(velocity, body.getLocalCenter());
		Vector2Pool.recycle(velocity);
	}
	
}

//force commit

