package com.example.scene1;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;


public class MainActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener {

	private static final int CAMERA_WIDTH = 1200;
	private static final int CAMERA_HEIGHT = 800;
	
	private BitmapTextureAtlas playerTextureAtlas;
	private TiledTextureRegion playerTextureRegion;
	
	private BitmapTextureAtlas backgroundTextureAtlas;
	private ITextureRegion backgroundTextureRegion;
	
	private AnimatedSprite player, background;
	
	private PhysicsWorld physicsWorld;
	final static FixtureDef PLAYER_FIX = PhysicsFactory.createFixtureDef(10.0f, 0.1f, 0.0f);
	final static FixtureDef WALL_FIX = PhysicsFactory.createFixtureDef(0.0f, 0.0f, 0.0f);
	
	private Scene scene;
	private Body playerBody;
	private boolean firstTouch = false;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
	}

	@Override
	protected void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		playerTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 128, TextureOptions.BILINEAR);
		playerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerTextureAtlas, this, "nyan_cat_sprite.png", 0, 0, 6, 1);
		playerTextureAtlas.load();
		
		backgroundTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 2048, 1024, TextureOptions.BILINEAR);
		backgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgroundTextureAtlas, this, "background.png", 0, 0);
		backgroundTextureAtlas.load();		
	}

	@Override
	protected Scene onCreateScene() {
		mEngine.registerUpdateHandler(new FPSLogger());
		physicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		
		
		scene = new Scene();
		scene.setOnSceneTouchListener(this);
		
		Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, this.getVertexBufferObjectManager());
		PhysicsFactory.createBoxBody(physicsWorld, ground, BodyType.StaticBody, WALL_FIX);
		
		scene.registerUpdateHandler(physicsWorld);
		
		AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-50.0f, new Sprite(0, CAMERA_HEIGHT - backgroundTextureRegion.getHeight(), backgroundTextureRegion, this.getVertexBufferObjectManager())));
		scene.setBackground(autoParallaxBackground);
		
		final float playerX = (CAMERA_WIDTH - playerTextureRegion.getWidth()) / 2;
		final float playerY = CAMERA_HEIGHT - playerTextureRegion.getHeight() - 5;
		
		this.player = new AnimatedSprite(playerX, playerY, playerTextureRegion, this.getVertexBufferObjectManager());
		this.player.animate(100);
		this.playerBody = PhysicsFactory.createBoxBody(physicsWorld, player, BodyType.DynamicBody, PLAYER_FIX);
		scene.attachChild(player);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(player, this.playerBody, true, false));
		
		
		
		
		return scene;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(this.physicsWorld != null) {
			if (pSceneTouchEvent.isActionDown())
		    {
		        	this.playerBody.setLinearVelocity(0.0f, 100.0f); 
		        	System.out.println();
		    }
		}
		return false;
	}
}
