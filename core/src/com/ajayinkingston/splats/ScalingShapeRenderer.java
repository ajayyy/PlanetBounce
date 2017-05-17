package com.ajayinkingston.splats;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ScalingShapeRenderer extends ShapeRenderer {
	
	float scaleFactor;
	
	public ScalingShapeRenderer(float scaleFactor){
		this.scaleFactor = scaleFactor;
	}
	
	@Override
	public void circle(float x, float y, float radius){
		super.circle(x*scaleFactor, y*scaleFactor, radius*scaleFactor);
	}
}
