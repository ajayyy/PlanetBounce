package com.ajayinkingston.splats;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScalingSpriteBatch extends SpriteBatch{
//	float scaleFactor = 0.2f;
	float scaleFactor = 0.5f;

	@Override
	public void draw (Texture texture, float x, float y) {
		draw(texture, x, y, texture.getWidth(), texture.getHeight());
	}

	@Override
	public void draw (Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX,
			float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY){
		super.draw(texture, (int) (x*scaleFactor), (int) (y*scaleFactor), originX, originY, (int) (width*scaleFactor), (int) (height*scaleFactor), scaleX, scaleY, rotation, srcX, srcY, srcWidth, srcHeight, flipX, flipY);
	}

	@Override
	public void draw(Texture texture, float x, float y, float width, float height) {
		super.draw(texture, (int) (x*scaleFactor), (int) (y*scaleFactor), (int) (width*scaleFactor), (int) (height*scaleFactor));
	}
	
}
