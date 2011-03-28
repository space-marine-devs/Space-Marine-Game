package edu.ua.cs.pbvs;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.anddev.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ParallaxBackground;
import org.anddev.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.level.LevelLoader;
import org.anddev.andengine.level.LevelLoader.IEntityLoader;
import org.anddev.andengine.level.util.constants.LevelConstants;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.SAXUtils;
import org.xml.sax.Attributes;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;


public class pbvs extends BaseGameActivity implements IAccelerometerListener, IOnSceneTouchListener {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;

	// ===========================================================
	// Fields
	// ===========================================================
    private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

	private BoundCamera mCamera;

	private Texture scaffoldTexture;  //I think this is like a texture container.  or something.
	private Texture playerTexture;  //I think this is like a texture container.  or something.
	private TextureRegion metalBoxTextureRegion; // player texture
	private TiledTextureRegion mPlayerTextureRegion; // player texture

	private Texture mAutoParallaxBackgroundTexture;

	private TextureRegion mParallaxLayerBack;  //parallax textures
	private TextureRegion mParallaxLayerMid;
	private TextureRegion mParallaxLayerFront;
	
	private Texture mOnScreenControlTexture;  //button thing
	private TextureRegion mOnScreenControlBaseTextureRegion;
	private TextureRegion mOnScreenControlKnobTextureRegion;
	
	private Texture mOnScreenButtonTexture;  //button thing
	private TextureRegion mOnScreenButtonBaseTextureRegion;
	private TextureRegion mOnScreenButtonKnobTextureRegion;
	
	private PhysicsWorld mPhysicsWorld;
	private PhysicsWorld mPlayerPhysicsWorld;
	
	private Shape ground;
	private Shape roof;
	private Shape left;
	private Shape right;
	
	private boolean jumping = true;
	
	private float mGravityX;
	private float mGravityY;
	
	
	
	/*
	 * Here are the xml attributes.
	 */
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_WIDTH = "width";
	private static final String TAG_ENTITY_ATTRIBUTE_HEIGHT = "height";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	
	/*
	 * Here are the XML object types
	 */
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_METALBOX = "metalbox";

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	public Engine onLoadEngine() {
			this.mCamera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT); 
			return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	public void onLoadResources() {
	        this.enableAccelerometerSensor(this);	
			TextureRegionFactory.setAssetBasePath("gfx/");
			this.prepSpriteTextures();
			this.prepParaBackground();
			this.prepControlTextures();
			this.mEngine.getTextureManager().loadTextures(this.scaffoldTexture, this.playerTexture,
					this.mOnScreenControlTexture , this.mAutoParallaxBackgroundTexture, this.mOnScreenButtonTexture );
			
	}

	public Scene onLoadScene() {
			final LevelLoader levelLoaderObj = new LevelLoader() ;
			levelLoaderObj.setAssetBasePath("level/");
			this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);	
			this.mPlayerPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);	


			
			this.mEngine.registerUpdateHandler(new FPSLogger());
			

			final Scene scene = new Scene(1);
			
			this.loadLevel(levelLoaderObj, scene);
			
			/* Calculate the coordinates for the face(do you mean player?), so its centered on the camera. */
			final int playerX = (CAMERA_WIDTH - this.mPlayerTextureRegion.getTileWidth()) / 2;
			final int playerY = CAMERA_HEIGHT - this.mPlayerTextureRegion.getTileHeight() - 5;
			
			final AnimatedSprite player = this.makePlayer(playerX, playerY, this.mPlayerTextureRegion, scene);
			this.mCamera.setChaseEntity(player);
			
			this.makeSprite(playerX+200, playerY+200, this.metalBoxTextureRegion, scene);
			this.makeSprite(playerX-200, playerY+200, this.metalBoxTextureRegion, scene);
			this.makeSprite(playerX+200, playerY-200, this.metalBoxTextureRegion, scene);
			this.makeSprite(playerX-200, playerY-200, this.metalBoxTextureRegion, scene);
			
			final PhysicsHandler controlHandler = new PhysicsHandler(player);
			player.registerUpdateHandler(controlHandler);  //this is the thing that the controls control.
			
			final ParallaxBackground paraBack = this.loadmanualParallax();
			scene.setBackground( paraBack );  
			
			
			final DigitalOnScreenControl digitalOnScreenControl = this.loadControl(controlHandler, paraBack, player);
			//makes and configures the onscreen controls.

			scene.setChildScene(digitalOnScreenControl);
			//adds them to the scene
			
			try {
				levelLoaderObj.loadLevelFromAsset(this, "example2.lvl");
			} catch (final IOException e) {
				Debug.e(e);
			}
			
			final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
            PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
            PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
            PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
            PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);
            PhysicsFactory.createBoxBody(this.mPlayerPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
            PhysicsFactory.createBoxBody(this.mPlayerPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
            PhysicsFactory.createBoxBody(this.mPlayerPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
            PhysicsFactory.createBoxBody(this.mPlayerPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);
            scene.registerUpdateHandler(this.mPhysicsWorld);
            scene.registerUpdateHandler(this.mPlayerPhysicsWorld);
			

			return scene;
	}

	public void onLoadComplete() {

	}

	// ===========================================================
	// Methods
	// ===========================================================
	private AnimatedSprite makeAnimatedSprite(int x, int y, TiledTextureRegion texture, Scene scene )
	{
			final AnimatedSprite nSprite = new AnimatedSprite(x, y, texture);
			nSprite.setScaleCenterY(texture.getTileHeight());
			nSprite.setScale(2);
			return nSprite;
	}
	private AnimatedSprite makePlayer(int x, int y, TiledTextureRegion texture, Scene scene )
	{
		final Body body;
		final AnimatedSprite nSprite = this.makeAnimatedSprite(x, y, texture, scene);
		
		
		body = PhysicsFactory.createBoxBody(this.mPlayerPhysicsWorld, nSprite, BodyType.DynamicBody, FIXTURE_DEF);
        this.mPlayerPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(nSprite, body, true, true));
		scene.getLastChild().attachChild(nSprite);
		return nSprite;
	}
	
	private AnimatedSprite makeAS(int x, int y, TiledTextureRegion texture, Scene scene )
	{
		final AnimatedSprite nSprite = this.makeAnimatedSprite(x, y, texture, scene);
		final Body body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, nSprite, BodyType.DynamicBody, FIXTURE_DEF);
        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(nSprite, body, true, true));
		scene.getLastChild().attachChild(nSprite);
		return nSprite;
	}
	
	private Sprite makeSprite(int x, int y, TextureRegion texture, Scene scene )
	{
			final Body body;
			final Sprite nSprite = new Sprite(x, y, texture);
			nSprite.setScaleCenterY(texture.getHeight());
			nSprite.setScale(2);
			body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, nSprite, BodyType.DynamicBody, FIXTURE_DEF);
			scene.getLastChild().attachChild(nSprite);
            this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(nSprite, body, true, true));
			return nSprite;
	}
	
	private ParallaxBackground loadmanualParallax()
	{
		final ParallaxBackground ParallaxBackground = new ParallaxBackground(0, 0, 0);
		ParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(0, CAMERA_HEIGHT - this.mParallaxLayerBack.getHeight(), this.mParallaxLayerBack)));
		ParallaxBackground.attachParallaxEntity(new ParallaxEntity(-5.0f, new Sprite(0, 80, this.mParallaxLayerMid)));
		ParallaxBackground.attachParallaxEntity(new ParallaxEntity(-10.0f, new Sprite(0, CAMERA_HEIGHT - this.mParallaxLayerFront.getHeight(), this.mParallaxLayerFront)));
		//I dont know the specifics here.
		return ParallaxBackground;
	}
	
	private DigitalOnScreenControl loadControl( final PhysicsHandler physicsHandler, final ParallaxBackground paraBack, final AnimatedSprite player)
	{

		final DigitalOnScreenControl digitalOnScreenControl = new DigitalOnScreenControl(0, CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight(), this.mCamera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, new IOnScreenControlListener() {
			float count = 0;
			int runCount = 0;
			int dir = 0;
			boolean run = true;
			
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float controlXVal, final float controlYVal ) {
				physicsHandler.setVelocity(controlXVal * 100, controlYVal * 100); //when controls are idle the values = 0
				
				if(controlXVal > 0)
				{
					if (!(dir > 0))
					{
						player.stopAnimation();
					}
					if (run)
					{
						dir = 1;
						player.animate(new long[]{150, 150, 150}, 3, 5, 1);
						runCount = 0;
						run = false;
					}
					else
					{
						if (runCount > 2)
						{
							run = true;
						}
						runCount++;
					}
					this.count+=1.50f;
				}
				else if (controlXVal < 0)
				{
					if (!(dir < 0))
					{
						player.stopAnimation();
					}
					if (run)
					{
						dir = -1;
						player.animate(new long[]{150, 150, 150}, 9, 11, 1);
						runCount = 0;
						run = false;
					}
					else
					{
						if (runCount > 2)
						{
							dir = 0;
							run = true;
						}
						runCount++;
					}
					this.count-=1.50f;
				}
				else
				{
					player.stopAnimation();
				}
				paraBack.setParallaxValue((float)this.count/4);
			}
		});
		final DigitalOnScreenControl rightControl = new DigitalOnScreenControl(CAMERA_WIDTH - (this.mOnScreenButtonBaseTextureRegion.getWidth()+135), CAMERA_HEIGHT - this.mOnScreenButtonBaseTextureRegion.getHeight(), this.mCamera, this.mOnScreenButtonBaseTextureRegion, this.mOnScreenButtonKnobTextureRegion, 0.1f, new IOnScreenControlListener() {
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float controlXVal, final float controlYVal ) {
				physicsHandler.setVelocity(controlXVal * 1000, controlYVal * 1000); //when controls are idle the values = 0
					//if (controlXVal != 0)
						//player.animate(new long[]{1, 1, 1}, 9, 11, 1);
				
			}
		});
		rightControl.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		rightControl.getControlBase().setAlpha(0.75f);
		rightControl.getControlBase().setScaleCenter(0, 48);
		rightControl.getControlBase().setScale(2.0f);
		rightControl.getControlKnob().setScale(00f);
		rightControl.refreshControlKnobPosition();	
		
		digitalOnScreenControl.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		digitalOnScreenControl.getControlBase().setAlpha(0.75f);
		digitalOnScreenControl.getControlBase().setScaleCenter(0, 48);
		digitalOnScreenControl.getControlBase().setScale(2.0f);
		digitalOnScreenControl.getControlKnob().setScale(00f);
		digitalOnScreenControl.refreshControlKnobPosition();
		
		digitalOnScreenControl.setChildScene(rightControl);
		
		return digitalOnScreenControl;
			
	}
	
	private void prepSpriteTextures()
	{
		this.playerTexture = new Texture(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA); //inits the texture
		this.scaffoldTexture = new Texture(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA); //inits the texture
		this.mPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(this.playerTexture, this, "player_possible.png", 0, 0, 3, 4);
		this.metalBoxTextureRegion = TextureRegionFactory.createFromAsset(this.scaffoldTexture, this, "metal_block.png", 0, 0);
	}
	private void prepParaBackground()
	{
		this.mAutoParallaxBackgroundTexture = new Texture(1024, 1024, TextureOptions.DEFAULT);  
		this.mParallaxLayerFront = TextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "parallax_background_layer_front.png", 0, 0);
		this.mParallaxLayerBack = TextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "parallax_background_layer_back.png", 0, 188);
		this.mParallaxLayerMid = TextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "parallax_background_layer_mid.png", 0, 669);
	}
	private void prepControlTextures()
	{
		this.mOnScreenControlTexture = new Texture(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mOnScreenControlBaseTextureRegion = TextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "digital_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = TextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_knob.png", 128, 0);
		
		this.mOnScreenButtonTexture = new Texture(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mOnScreenButtonBaseTextureRegion = TextureRegionFactory.createFromAsset(this.mOnScreenButtonTexture, this, "control_button.png", 0, 0);
		this.mOnScreenButtonKnobTextureRegion = TextureRegionFactory.createFromAsset(this.mOnScreenButtonTexture, this, "onscreen_control_knob.png", 128, 0);
	}
	
	private void loadLevel(LevelLoader levelLoaderObj, final Scene scene )
	{
		levelLoaderObj.registerEntityLoader(LevelConstants.TAG_LEVEL, 
			new IEntityLoader() {
				@Override
				public void onLoadEntity(final String pEntityName, final Attributes pAttributes) {
					final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
					final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
					//Toast.makeText(pbvs.this, "Loaded level with width=" + width + " and height=" + height + ".", Toast.LENGTH_LONG).show();
					
					ground = new Rectangle(-3200, height, width+3200, 3200);
					roof   = new Rectangle(-3200, -3200, width+3200, 3200);
					left   = new Rectangle(-3200, -3200, 3200, height+6400);
					right  = new Rectangle(width, -3200, 3200, 6400);
		            ground.setColor(.20f, .20f, .20f);
		            roof.setColor  (.20f, .20f, .20f);
		            left.setColor  (.20f, .20f, .20f);
		            right.setColor (.20f, .20f, .20f);
		            
					scene.getLastChild().attachChild(ground);
					scene.getLastChild().attachChild(roof);
					scene.getLastChild().attachChild(left);
					scene.getLastChild().attachChild(right);
		            
				}
			}
		);

		levelLoaderObj.registerEntityLoader(TAG_ENTITY, 
			new IEntityLoader() {
				@Override
				public void onLoadEntity(final String pEntityName, final Attributes pAttributes) {
					final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
					final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
					final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_WIDTH);
					final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_HEIGHT);
					final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
	
							
					Sprite spr;
					if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_METALBOX)) 
					{
						spr = new Sprite(x, y, width, height, pbvs.this.metalBoxTextureRegion);
						scene.getLastChild().attachChild(spr);
					}
				}
			}
		);
	}
	
	public void jump(AnimatedSprite sprite){
		 final Body faceBody = this.mPhysicsWorld.getPhysicsConnectorManager().findBodyByShape(sprite);

         final Vector2 velocity = Vector2Pool.obtain(this.mGravityX * -50, this.mGravityY * -50);
         faceBody.setLinearVelocity(velocity);
         Vector2Pool.recycle(velocity);
        }
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================	

	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onAccelerometerChanged(AccelerometerData pAccelerometerData) {
		// TODO Auto-generated method stub
		 this.mGravityX = pAccelerometerData.getY();
         this.mGravityY = pAccelerometerData.getX();

         final Vector2 gravity = Vector2Pool.obtain(this.mGravityX, this.mGravityY);
         this.mPhysicsWorld.setGravity(gravity);
         Vector2Pool.recycle(gravity);
		
	}
}
