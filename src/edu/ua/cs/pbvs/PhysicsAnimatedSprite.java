package edu.ua.cs.pbvs;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class PhysicsAnimatedSprite extends AnimatedSprite{

	public Body body;
	public boolean jumping = true;

	public PhysicsAnimatedSprite(float pX, float pY, TiledTextureRegion pTiledTextureRegion, PhysicsWorld world ) {
		super(pX, pY, pTiledTextureRegion);
	    final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0f, 0.5f);
		body = PhysicsFactory.createBoxBody(world, this, BodyType.DynamicBody, FIXTURE_DEF);
        world.registerPhysicsConnector(new PhysicsConnector(this, body, true, true));
		this.setScaleCenterY((pTiledTextureRegion.getHeight()/3)-10);
		// TODO Auto-generated constructor stub
	}

	public void jump(){
		final Vector2 velocity = Vector2Pool.obtain(0, -40);
		if (jumping)
		{
			body.applyLinearImpulse (velocity, body.getLocalCenter());
			jumping = false;
		}
		Vector2Pool.recycle(velocity);
	}
	
	public void setJump()
	{
		jumping = true;
	}
}

