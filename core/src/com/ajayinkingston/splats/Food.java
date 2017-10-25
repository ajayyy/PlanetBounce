package com.ajayinkingston.splats;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class Food extends com.ajayinkingston.planets.server.Food{
	int image;//what image to use
	public Food(boolean enabled, double angle, int amount, int image){
		super(angle, amount);
		this.enabled = enabled;
		this.image = image;
	}
	
	public boolean onScreen(Splats splats, int x, int y){
		Vector3 pos = splats.cam.project(new Vector3(x,y, 0));
		return (pos.x+getSize()/2>0 && pos.x-getSize()/2<Gdx.graphics.getWidth() && pos.y+getSize()/2>0 && pos.y-getSize()/2<Gdx.graphics.getHeight());
	}
	
}