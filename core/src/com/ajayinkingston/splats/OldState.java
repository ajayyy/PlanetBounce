package com.ajayinkingston.splats;

public class OldState {
	float x,y,xspeed,yspeed,projectileAngle;
	boolean left,right,shot;
	long when;
	public OldState(float x, float y, float xspeed, float yspeed, long when, boolean left, boolean right){
		this.x = x;
		this.y = y;
		this.xspeed = xspeed;
		this.yspeed = yspeed;
		this.when = when;
		this.right = right;
		this.left = left;
	}
	
}
