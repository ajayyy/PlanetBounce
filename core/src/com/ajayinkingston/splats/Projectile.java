package com.ajayinkingston.splats;

import java.util.ArrayList;

import com.ajayinkingston.planets.server.Entity;
import com.ajayinkingston.planets.server.Main;
import com.ajayinkingston.planets.server.Planet;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

public class Projectile extends Entity{
	double distance;
	
	long start;
	
	int radius;
	
	public Projectile(double x, double y, int radius, double angle, float speed){
		this.x = (float) x;
		this.y = (float) y;
		this.radius = radius;
		this.xspeed = (float) (Math.cos(angle) * speed);
		this.yspeed = (float) (Math.sin(angle) * speed);
		
		friction = 150;
		
		start = System.currentTimeMillis();
	}
	
	public void render(Splats splats){
		Vector3 pos = splats.cam.project(new Vector3((int)x*splats.batch.scaleFactor,(int)y*splats.batch.scaleFactor, 0));
		if(pos.x>0 && pos.x < Gdx.graphics.getWidth() && pos.y>0 && pos.y<Gdx.graphics.getHeight()){
			splats.shapeRenderer.begin(ShapeType.Filled);
			splats.shapeRenderer.setColor(Color.WHITE);
			splats.shapeRenderer.circle((float) x, (float) y, radius);
			splats.shapeRenderer.end();
		}
		
	}
	
	public void update(Splats splats, double delta){
		ArrayList<Planet> closestplanets = Main.getClosestPlanets(this, splats.planets);
		float gravityx = 0;
		float gravityy = 0;
		for(Planet planet:closestplanets){
//			System.out.println((this == null) + " " + (planet == null));
			double angle = Math.atan2((y) - (planet.y), (x) - (planet.x));
			
			float multiplier = 350;
			
			if (distance > 3000 || System.currentTimeMillis() - start > 3500) friction = 400;
			
			gravityx += Math.cos(angle) * planet.gravityhelperconstant / ((Math.sqrt(Math.pow((y) - (planet.y), 2) + Math.pow((x) - (planet.x), 2))) - radius - planet.radius + 300) * multiplier;//XXX: IF YOU CHANGE THIS CHANGE IT IN PLANET CLASS AND SERVER PROJECT TOO
			gravityy += Math.sin(angle) * planet.gravityhelperconstant / ((Math.sqrt(Math.pow((y) - (planet.y), 2) + Math.pow((x) - (planet.x), 2))) - radius - planet.radius + 300) * multiplier;
		}
		xspeed += gravityx * delta;
		yspeed += gravityy * delta;
		
		if(Math.abs(xspeed) < friction*delta) xspeed = 0;
		else if(xspeed>0) xspeed-=friction*delta;
		else if(xspeed<0) xspeed+=friction*delta; //XXX IF YOU CHANGE THIS CHANGE SERVER TOO
		if(Math.abs(yspeed) < friction*delta) yspeed = 0;
		else if(yspeed>0) yspeed-=friction*delta;
		else if(yspeed<0) yspeed+=friction*delta;
		
		x+=xspeed*delta;
		y+=yspeed*delta;
		distance += Math.abs(xspeed*delta) + Math.abs(yspeed*delta);
	}
	
}
