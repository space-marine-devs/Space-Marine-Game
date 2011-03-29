package edu.ua.cs.pbvs;

import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class PhysicsSprite extends Sprite {
	final Body body;
	
	public PhysicsSprite(float pX, float pY, TextureRegion texture, PhysicsWorld world) {
		super(pX, pY, texture);
		setScaleCenterY(texture.getHeight());
		setScale(2);
	    final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0f, 0.5f);
		body = PhysicsFactory.createBoxBody(world, this, BodyType.DynamicBody, FIXTURE_DEF);
	    world.registerPhysicsConnector(new PhysicsConnector(this, body, true, true));
	}

	public PhysicsSprite(int pX, int pY, int width, int height, TextureRegion texture, PhysicsWorld world) {
		super(pX, pY, width, height, texture);
		setScaleCenterY(texture.getHeight());
		setScale(2);
	    final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0f, 0.5f);
		body = PhysicsFactory.createBoxBody(world, this, BodyType.DynamicBody, FIXTURE_DEF);
	    world.registerPhysicsConnector(new PhysicsConnector(this, body, true, true));
		// TODO Auto-generated constructor stub
	}
}
