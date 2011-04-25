package edu.ua.cs.pbvs;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public class Player extends PhysicsAnimatedSprite{
	public float health = 10;
	public int jump_count = 0;
	pbvs act;


	public Player(float pX, float pY, TiledTextureRegion pTiledTextureRegion, PhysicsWorld world, pbvs act ) {
		super(pX, pY, pTiledTextureRegion, world, act);
	    //final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0f, 0.5f);
		this.act = act;
		this.setScaleCenterY((pTiledTextureRegion.getHeight()/3)-10);
		this.setScale(3);
		this.body.setFixedRotation(true);
		// TODO Auto-generated constructor stub
	}
	
	public void onUpdate(int pSecondsElapsed)
	{
		if(!this.mIgnoreUpdate) {
			this.onManagedUpdate(pSecondsElapsed);
		}
		jump_count++;
		if (jump_count >= 10)
		{
			jump_count = 0;
			jumping = true;
		}
	}
	
	public void hit() {
		//health--;
	}

	//force commit!
	
}

