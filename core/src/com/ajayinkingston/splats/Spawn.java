package com.ajayinkingston.splats;

/**
 * 
 * Holds data about new player spawns (incase they occur in the past of the future)
 *
 */
public class Spawn {
	float x,y,xspeed,yspeed;
	int id, mass;
	
	long spawnFrame, startFrame;
	
	int image = -1; //stores what image they use to keep it consistent if the player must be repawned for being too new
	//if -1 it wasn't set
	
	public Spawn(int id, float x, float y, float xspeed, float yspeed, int mass, long startFrame, long spawnFrame){
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
