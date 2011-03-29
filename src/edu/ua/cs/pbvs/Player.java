package edu.ua.cs.pbvs;

import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class Player extends PhysicsAnimatedSprite{
	public float health = 10;


	public Player(float pX, float pY, TiledTextureRegion pTiledTextureRegion, PhysicsWorld world ) {
		super(pX, pY, pTiledTextureRegion, world);
	    //final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0f, 0.5f);
		this.setScaleCenterY((pTiledTextureRegion.getHeight()/3)-10);
		this.setScale(3);
		body.setAngularDamping(2000000000);
		// TODO Auto-generated constructor stub
	}
}

