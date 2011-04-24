package edu.ua.cs.pbvs;

import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;

public class PhysicsData {
	PhysicsAnimatedSprite sprite;
	PhysicsConnector connector;
	
	public PhysicsData(PhysicsAnimatedSprite sprite, PhysicsConnector connector) {
		this.sprite = sprite;
		this.connector = connector;
	}
}
