package edu.ua.cs.pbvs;

import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class Bullet extends PhysicsAnimatedSprite{
	public float health = 10;
	public int jump_count = 0;


	public Bullet(float pX, float pY, TiledTextureRegion pTiledTextureRegion, PhysicsWorld world ) {
		super(pX, pY, pTiledTextureRegion, world);
	    //final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0f, 0.5f);
		this.setScaleCenterY((pTiledTextureRegion.getHeight()/3)-10);
		this.setScale(3);
		this.body.setFixedRotation(true);
		// TODO Auto-generated constructor stub
	}
	
}

