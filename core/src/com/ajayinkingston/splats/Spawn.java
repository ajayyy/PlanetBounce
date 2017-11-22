package com.ajayinkingston.splats;

/**
 * 
 * Holds data about new player spawns (incase they occur in the past of the future)
 *
 */
public class Spawn {
	float x,y,xspeed,yspeed;
	int id, mass;
	
	int spawnFrame, startFrame;
	
	public Spawn(int id, float x, float y, float xspeed, float yspeed, int mass, int startFrame, int spawnFrame){
		this.id = id;
		this.x = x;
		this.y = y;
		this.xspeed = xspeed;
		this.yspeed = yspeed;
		this.mass = mass;
		this.startFrame = startFrame;
		this.spawnFrame = spawnFrame;
	}
}
