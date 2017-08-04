package com.ajayinkingston.splats;

public class Entity {
	
	int id;
	
	int mass;//this is just the mass, to get the size use getSize()
	
	int score;//in future this will always stay high even if you lose mass
	float x,y;
	float xspeed,yspeed;
	
	float friction = 100;

	public int getSize(){
		return mass; //in the future it will be more complicated than just this
	}
	
	public float getRadius(){
		return getSize()/2f;
	}
	
	//util methods
	public static double getDotProduct(double x1, double y1, double x2, double y2){
		return x1 * x2 + y1 * y2;
	}
	
	public boolean collided(Position player){
		return collideDist(player) < getRadius() + player.radius;
	}
	
	public double collideDist(Position player){
		float xdist = x - player.x;
		float ydist = y - player.y;
		double dist = Math.sqrt(Math.pow(xdist, 2) + Math.pow(ydist, 2)); //distance between circles (xdist and ydist and a and b of triangle)
	
		return dist;
	}
}
