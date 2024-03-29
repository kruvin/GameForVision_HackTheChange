package no.ntnu.brickbreaker.game;

import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.Lock;

import no.ntnu.brickbreaker.GameHolder;
import no.ntnu.brickbreaker.R;
import no.ntnu.brickbreaker.models.Ball;
import no.ntnu.brickbreaker.models.Brick;
import no.ntnu.brickbreaker.models.Paddle;
import no.ntnu.brickbreaker.models.TexturedBrick;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

public class Game extends BaseGameActivity implements IOnSceneTouchListener, Observer {
	private static int CAMERA_HEIGHT = 480;
	private static int CAMERA_WIDTH = 800;
	private Camera mCamera;

	private TiledTextureRegion ballTextureRegion;
	private TiledTextureRegion brickTextureRegion;

	private Texture paddleTexture;
	private Texture ballTexture;
	private Texture brickTexture;
	private Paddle paddle;
	private GameHolder gameHolder;
	private Object mCurViewMode;

	public Engine onLoadEngine() {

		final Display defaultDisplay = getWindow().getWindowManager().getDefaultDisplay();
		CAMERA_WIDTH = defaultDisplay.getWidth();
		CAMERA_HEIGHT = defaultDisplay.getHeight();
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {

		gameHolder = GameHolder.getInstance();
		gameHolder.addObserver(this);
		gameHolder.setGameActivity(this);

		this.ballTexture = new Texture(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.paddleTexture = new Texture(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.brickTexture = new Texture(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		TextureRegionFactory.setAssetBasePath("gfx/");

		this.ballTextureRegion = TextureRegionFactory.createTiledFromAsset(this.ballTexture, this,
				"ball.png", 0, 0, 1, 1);
		this.brickTextureRegion = TextureRegionFactory.createTiledFromAsset(this.brickTexture,
				this, "brick.png", 0, 0, 1, 1);

		this.mEngine.getTextureManager().loadTexture(this.paddleTexture);
		this.mEngine.getTextureManager().loadTexture(this.ballTexture);
		this.mEngine.getTextureManager().loadTexture(this.brickTexture);
	}

	@Override
	public Scene onLoadScene() {

		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene(1);
		scene.setBackground(new ColorBackground(0f, 0f, 0f));
		scene.setOnSceneTouchListener(this);

		final Ball ball = new Ball(CAMERA_WIDTH / 2, CAMERA_HEIGHT - 30 - 16,
				this.ballTextureRegion, mEngine);
		ball.setVelocity(100.0f, 100.0f);

		scene.getTopLayer().addEntity(ball);

		paddle = new Paddle(CAMERA_WIDTH / 2, CAMERA_HEIGHT - 50, CAMERA_HEIGHT / 8,
				CAMERA_WIDTH / 34);

		final TexturedBrick[][] bricks = new TexturedBrick[420][50];
		Log.e("debug", "XXX1111111");
		AssetManager assetManager = this.getAssets();

		InputStream istr;
		Bitmap floorMapping = null;
		try {
			istr = assetManager.open(getString(R.string.gfx_room_col_png));
			floorMapping = BitmapFactory.decodeStream(istr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int count = bricks.length * bricks[0].length - 1;
		float widthRatio = CAMERA_WIDTH / (float) (floorMapping.getWidth());
		float heightRatio = CAMERA_HEIGHT / (float) (floorMapping.getHeight());
		try {
		for (int i = 0; i < floorMapping.getWidth(); i+=2) {
			for (int j = 0; j < floorMapping.getHeight(); j+=2) {
				if (count >= 0 && floorMapping.getPixel(i, j) == 0xffff0000) {
					//bricks[count/50][count%50] = new TexturedBrick(i * widthRatio, j * heightRatio,
					//		CAMERA_HEIGHT / 16, CAMERA_WIDTH / 32, this.brickTextureRegion);
					bricks[count/50][count%50] = new TexturedBrick(i * widthRatio, j * heightRatio,
							10, 10, this.brickTextureRegion);
					scene.getTopLayer().addEntity(bricks[count/50][count%50]);
					j += 5;
					count--;
				}
			}
		}}
		catch(Exception e) {
			Log.e("debug", "COUNT " + count);
			Log.e("dbeug", (widthRatio) + " - " + CAMERA_WIDTH);
			Log.e("debug", "EXCEPTION: " + e.getMessage());
		}

/*		for (int i = 0; i < bricks.length; i++) {
			for (int j = 0; j < bricks[0].length; j++) {
				// bricks[i][j]= new Brick(10+j*CAMERA_WIDTH/5, 10+i*CAMERA_HEIGHT/15 ,
				// CAMERA_HEIGHT/16, CAMERA_WIDTH/32);
				// scene.getTopLayer().addEntity(bricks[i][j]);
				
				  bricks[i][j] = new TexturedBrick(10 + j * CAMERA_WIDTH / 5, 10 + i *
				  CAMERA_HEIGHT
				  / 15, CAMERA_HEIGHT / 16, CAMERA_WIDTH / 32, this.brickTextureRegion);
				  scene.getTopLayer().addEntity(bricks[i][j]);
				 
			}
		}
*/
		scene.getTopLayer().addEntity(paddle);
		scene.getTopLayer().addEntity(ball);
		scene.registerTouchArea(paddle);

		/* The actual collision-checking. */
		scene.registerUpdateHandler(new IUpdateHandler() {
			public void reset() {
			}

			public void onUpdate(final float pSecondsElapsed) {
				if (ball.collidesWith(paddle)) {
					ball.bounceWithRectangle(paddle);
				} else if (ball.getY() >= Game.getCAMERA_HEIGHT() - 30) {
					scene.setBackground(new ColorBackground(255f, 0f, 0f));
				} else {
					for (int i = 0; i < bricks.length; i++) {
						for (int j = 0; j < bricks[0].length; j++) {
							scene.setBackground(new ColorBackground(0f, 0f, 0f));
							if (ball.collidesWith(bricks[i][j])) {
								bricks[i][j].setPosition(CAMERA_HEIGHT + 20, CAMERA_WIDTH + 20);
								scene.getTopLayer().removeEntity(bricks[i][j]);
								ball.bounceWithBrick(bricks[i][j]);

							}
						}
					}
				}
			}
		});

		return scene;
	}

	public static int getCAMERA_HEIGHT() {
		return CAMERA_HEIGHT;
	}

	public static int getCAMERA_WIDTH() {
		return CAMERA_WIDTH;
	}

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		paddle.setPosition(pSceneTouchEvent.getX() - paddle.getWidth() / 2,
				Game.getCAMERA_HEIGHT() - 30);
		return true;
	}

	protected void onPause() {
		super.onPause();
		gameHolder.setGameState(gameHolder.getPausedGameState());
	}

	@Override
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub

	}
}
