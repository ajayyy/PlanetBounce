package com.ajayinkingston.splats;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class Food {
	double angle;//the angle it is located on the planet
	int amount;//point amount
	boolean enabled; //if someone has collected it or something
	int image;//what image to use
	public Food(boolean enabled, double angle, int amount, int image){
		this.enabled = enabled;
		this.angle = angle;
		this.amount = amount;
		this.image = image;
	}
	
	public boolean onScreen(Splats splats, int x, int y){
		Vector3 pos = splats.cam.project(new Vector3(x,y, 0));
		return (pos.x+getSize()/2>0 && pos.x-getSize()/2<Gdx.graphics.getWidth() && pos.y+getSize()/2>0 && pos.y-getSize()/2<Gdx.graphics.getHeight());
	}
	
	public int getSize(){
		return (int) (amount * 4)+50;
	}
}