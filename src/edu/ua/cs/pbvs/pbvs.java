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

    private Texture mTexture;
    private TiledTextureRegion mPlayerTextureRegion;
    private TiledTextureRegion mEnemyTextureRegion;

    private Texture mAutoParallaxBackgroundTexture;

    private TextureRegion mParallaxLayerBack;
    private TextureRegion mParallaxLayerMid;
    private TextureRegion mParallaxLayerFront;
    
    private Texture nTexture;
    private TextureRegion mFaceTextureRegion;
    
    private Texture mOnScreenControlTexture;
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

    @Override
    public Engine onLoadEngine() {
            this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
            return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
    }

    @Override
    public void onLoadResources() {
            this.mTexture = new Texture(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
            this.mPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "gfx/player.png", 0, 0, 3, 4);
            this.mEnemyTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "gfx/enemy.png", 73, 0, 3, 4);

            this.mAutoParallaxBackgroundTexture = new Texture(1024, 1024, TextureOptions.DEFAULT);
            this.mParallaxLayerFront = TextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "gfx/parallax_background_layer_front.png", 0, 0);
            this.mParallaxLayerBack = TextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "gfx/parallax_background_layer_back.png", 0, 188);
            this.mParallaxLayerMid = TextureRegionFactory.createFromAsset(this.mAutoParallaxBackgroundTexture, this, "gfx/parallax_background_layer_mid.png", 0, 669);
            
            TextureRegionFactory.setAssetBasePath("gfx/");

            this.nTexture = new Texture(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
            this.mFaceTextureRegion = TextureRegionFactory.createFromAsset(this.nTexture, this, "face_box.png", 0, 0);

            this.mOnScreenControlTexture = new Texture(256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
            this.mOnScreenControlBaseTextureRegion = TextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "digital_control_base.png", 0, 0);
            this.mOnScreenControlKnobTextureRegion = TextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_knob.png", 128, 0);

            this.mEngine.getTextureManager().loadTextures(this.mTexture, this.mOnScreenControlTexture , this.nTexture, this.mAutoParallaxBackgroundTexture );
    }

    @Override
    public Scene onLoadScene() {
            this.mEngine.registerUpdateHandler(new FPSLogger());

            final Scene scene = new Scene(1);
            
            /* Calculate the coordinates for the face, so its centered on the camera. */
            final int playerX = (CAMERA_WIDTH - this.mPlayerTextureRegion.getTileWidth()) / 2;
            final int playerY = CAMERA_HEIGHT - this.mPlayerTextureRegion.getTileHeight() - 5;
            
            final AnimatedSprite player = new AnimatedSprite(playerX, playerY, this.mPlayerTextureRegion);
            player.setScaleCenterY(this.mPlayerTextureRegion.getTileHeight());
            player.setScale(2);
            player.animate(new long[]{200, 200, 200}, 3, 5, true);

            final AnimatedSprite enemy = new AnimatedSprite(playerX - 80, playerY, this.mEnemyTextureRegion);
            enemy.setScaleCenterY(this.mEnemyTextureRegion.getTileHeight());
            enemy.setScale(2);
            enemy.animate(new long[]{200, 200, 200}, 3, 5, true);
            
            final PhysicsHandler physicsHandler = new PhysicsHandler(player);
            player.registerUpdateHandler(physicsHandler);
            
    		final ParallaxBackground paraBack = this.loadmanualParallax();
            scene.setBackground( paraBack );
            
            
            
            final DigitalOnScreenControl digitalOnScreenControl = this.loadControl(physicsHandler, paraBack);
            
            digitalOnScreenControl.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            digitalOnScreenControl.getControlBase().setAlpha(0.75f);
            digitalOnScreenControl.getControlBase().setScaleCenter(0, 128);
            digitalOnScreenControl.getControlBase().setScale(1.25f);
            digitalOnScreenControl.getControlKnob().setScale(00f);
            digitalOnScreenControl.refreshControlKnobPosition();

            scene.setChildScene(digitalOnScreenControl);


            /* Create two sprits and add it to the scene. */

            scene.getLastChild().attachChild(player);
            scene.getLastChild().attachChild(enemy);

            return scene;
    }

    @Override
    public void onLoadComplete() {

    }

    // ===========================================================
    // Methods
    // ===========================================================
    
    private ParallaxBackground loadmanualParallax()
    {
        final ParallaxBackground ParallaxBackground = new ParallaxBackground(0, 0, 0);
        ParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(0, CAMERA_HEIGHT - this.mParallaxLayerBack.getHeight(), this.mParallaxLayerBack)));
        ParallaxBackground.attachParallaxEntity(new ParallaxEntity(-5.0f, new Sprite(0, 80, this.mParallaxLayerMid)));
        ParallaxBackground.attachParallaxEntity(new ParallaxEntity(-10.0f, new Sprite(0, CAMERA_HEIGHT - this.mParallaxLayerFront.getHeight(), this.mParallaxLayerFront)));
        return ParallaxBackground;
    }
    
    private DigitalOnScreenControl loadControl( final PhysicsHandler physicsHandler, final ParallaxBackground paraBack)
    {

        final DigitalOnScreenControl digitalOnScreenControl = new DigitalOnScreenControl(0, CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight(), this.mCamera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, new IOnScreenControlListener() {
        	float count = 0;
            @Override
            public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
                physicsHandler.setVelocity(pValueX * 0, pValueY * 100);
                if(pValueX > 0)
                {
                	this.count+=0.75f;
                }
                else if (pValueX < 0)
                {
                	this.count-=0.75f;
                }
                paraBack.setParallaxValue((float)this.count);
            }
        });
        return digitalOnScreenControl;
        	
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================	
}
