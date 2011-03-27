package edu.ua.cs.pbvs;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.anddev.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ParallaxBackground;
import org.anddev.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;


public class pbvs extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;

	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;

	private Texture spriteTexture;  //I think this is like a texture container.  or something.
	private TiledTextureRegion mPlayerTextureRegion; // player texture

	private Texture mAutoParallaxBackgroundTexture;

	private TextureRegion mParallaxLayerBack;  //parallax textures
	private TextureRegion mParallaxLayerMid;
	private TextureRegion mParallaxLayerFront;
	
	private Texture mOnScreenControlTexture;  //button thing
	private TextureRegion mOnScreenControlBaseTextureRegion;
	private TextureRegion mOnScreenControlKnobTextureRegion;

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
			this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT); 
			return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
			//I dont really know what can be done to these.
			//check the Tiled map thingy
	}

	public void onLoadResources() {
			this.spriteTexture = new Texture(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA); //inits the texture
			this.mPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(this.spriteTexture, this, "gfx/player_possible.png", 0, 0, 3, 4);
			/*
			 * createTiledFromAsset gets a asset, and cuts it into rows and columns.  in this case, 3 is the rows, 4 is the columns
			 */

			this.mAutoParallaxBackgroundTexture = new Texture(1024, 1024, TextureOptions.DEFAULT);  
			this.mParallaxLayerFront = TextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "gfx/parallax_background_layer_front.png", 0, 0);
			this.mParallaxLayerBack = TextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "gfx/parallax_background_layer_back.png", 0, 188);
			this.mParallaxLayerMid = TextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "gfx/parallax_background_layer_mid.png", 0, 669);
			
			TextureRegionFactory.setAssetBasePath("gfx/");


			this.mOnScreenControlTexture = new Texture(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			this.mOnScreenControlBaseTextureRegion = TextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "digital_control_base.png", 0, 0);
			this.mOnScreenControlKnobTextureRegion = TextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_knob.png", 128, 0);

			this.mEngine.getTextureManager().loadTextures(this.spriteTexture, 
					this.mOnScreenControlTexture , this.mAutoParallaxBackgroundTexture );
	}

	public Scene onLoadScene() {
			this.mEngine.registerUpdateHandler(new FPSLogger());

			final Scene scene = new Scene(1);
			
			/* Calculate the coordinates for the face(do you mean player?), so its centered on the camera. */
			final int playerX = (CAMERA_WIDTH - this.mPlayerTextureRegion.getTileWidth()) / 2;
			final int playerY = CAMERA_HEIGHT - this.mPlayerTextureRegion.getTileHeight() - 5;
			
			final AnimatedSprite player = this.makeSprite(playerX, playerY, this.mPlayerTextureRegion);
			//I made a makeSprite wrapper cause I thought it made sense.
			
			scene.getLastChild().attachChild(player);  // I haven't a clue what this is about,
			//scene.getLastChild().attachChild(enemy);   // but it appears to add things to the screen.
			
			final PhysicsHandler controlHandler = new PhysicsHandler(player);
			player.registerUpdateHandler(controlHandler);  //this is the thing that the controls control.
			
			final ParallaxBackground paraBack = this.loadmanualParallax();
			scene.setBackground( paraBack );  
			
			final DigitalOnScreenControl digitalOnScreenControl = this.loadControl(controlHandler, paraBack, player);
			//makes and configures the onscreen controls.

			scene.setChildScene(digitalOnScreenControl);
			//adds them to the scene

			return scene;
	}

	public void onLoadComplete() {

	}

	// ===========================================================
	// Methods
	// ===========================================================
	private AnimatedSprite makeSprite(int x, int y, TiledTextureRegion texture )
	{
			final AnimatedSprite nSprite = new AnimatedSprite(x, y, texture);
			nSprite.setScaleCenterY(texture.getTileHeight());
			nSprite.setScale(2);
			//nSprite.animate(100);
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
			
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float controlXval, final float controlYVal ) {
				//physicsHandler.setVelocity(controlXval * 0, controlYVal * 100); //when controls are idle the values = 0
				
				if(controlXval > 0)
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
				else if (controlXval < 0)
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
				paraBack.setParallaxValue((float)this.count);
			}
		});
		digitalOnScreenControl.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		digitalOnScreenControl.getControlBase().setAlpha(0.75f);
		digitalOnScreenControl.getControlBase().setScaleCenter(0, 32);
		digitalOnScreenControl.getControlBase().setScale(1.50f);
		digitalOnScreenControl.getControlKnob().setScale(00f);
		digitalOnScreenControl.refreshControlKnobPosition();
		return digitalOnScreenControl;
			
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================	
}
