package no.ntnu.brickbreaker.models;

import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.BaseSprite;



public class TexturedBrick extends BaseSprite{
	private TiledTextureRegion texture;
	public TexturedBrick(float pX, float pY, float pWidth, float pHeight,  TiledTextureRegion positionTextureRegion) {
		super(pX, pY, pWidth, pHeight, positionTextureRegion);
		this.texture = positionTextureRegion;
		// TODO Auto-generated constructor stub
	}

	protected void onManagedUpdate(final float pSecondsElapsed) {
//			if(this.collidesWith(pOtherShape))

	}

}

