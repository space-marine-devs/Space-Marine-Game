package edu.ua.cs.pbvs;

import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.level.LevelLoader;
import org.anddev.andengine.level.util.constants.LevelConstants;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.SAXUtils;
import org.xml.sax.Attributes;

public class LevelLoaderWrapper extends LevelLoader {
	
	
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
	LevelLoaderWrapper(final pbvs game, final Scene scene )
	{
		this.setAssetBasePath("level/");
		this.registerEntityLoader(LevelConstants.TAG_LEVEL, 
			new IEntityLoader() {
				public void onLoadEntity(final String pEntityName, final Attributes pAttributes) {
					final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
					final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
					//Toast.makeText(pbvs.this, "Loaded level with width=" + width + " and height=" + height + ".", Toast.LENGTH_LONG).show();
					
					game.ground = new Rectangle(-3200, height, width+3200, 3200);
					game.roof   = new Rectangle(-3200, -3200, width+3200, 3200);
					game.left   = new Rectangle(-3200, -3200, 3200, height+6400);
					game.right  = new Rectangle(width, -3200, 3200, 6400);
		            game.ground.setColor(.20f, .20f, .20f);
		            game.roof.setColor  (.20f, .20f, .20f);
		            game.roof.setAlpha(0);
		            game.left.setColor  (.20f, .20f, .20f);
		            game.right.setColor (.20f, .20f, .20f);
		            
					scene.getLastChild().attachChild(game.ground);
					scene.getLastChild().attachChild(game.roof);
					scene.getLastChild().attachChild(game.left);
					scene.getLastChild().attachChild(game.right);
		            
				}
			}
		);

		this.registerEntityLoader(TAG_ENTITY, 
			new IEntityLoader() {
				public void onLoadEntity(final String pEntityName, final Attributes pAttributes) {
					final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
					final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
					final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_WIDTH);
					final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_HEIGHT);
					final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
	
					TextureRegion temp;
					if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_METALBOX)) 
					{
						temp = game.metalBoxTextureRegion;
					}
					else
					{
						return;
					}
					scene.getLastChild().attachChild(new PhysicsSprite(x, y, width, height, temp, game.mPhysicsWorld));
				}
			}
		);
	}
	

}
