package edu.ua.cs.pbvs;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;


public class Enemy extends PhysicsAnimatedSprite {
	
	private float health = 10;
	pbvs act;
	
	public Enemy(float pX, float pY, TiledTextureRegion pTiledTextureRegion, PhysicsWorld world, pbvs act ) {
		super(pX, pY, pTiledTextureRegion, world);
	    //final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0f, 0.5f);
		this.act = act;
		this.setScaleCenterY((pTiledTextureRegion.getHeight()/3)-10);
		this.setScale(3);
		this.body.setFixedRotation(true);
		// TODO Auto-generated constructor stub
	}
	
	public void hit() {
		health -= 2;
		if(health == 0) {
			act.removePhysicsSprite(this);
		}
	}

}
