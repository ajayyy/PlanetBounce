package com.ajayinkingston.splats;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Test extends Entity{
	int id;
	
	boolean left,right;//now going to be used
	
	long start;//current time millis of since connected
	long lastChecked = System.currentTimeMillis();//last time checked client if they have proper x
	
	ArrayList<OldState> oldStates = new ArrayList<>();
	
	boolean paused;
	int pausedStack;
	ArrayList<Double> pausedDeltaTimes = new ArrayList<>();
	
	double leftoverdelta; //delta left over after the 40fps
	double futureleftoverdelta; //first this variable is set, then leftoverdelta becomes this by the end of the frame
	double fps = 40;//fps to update at
	
	public Test(int id, float x, float y, int mass, long start){
		this.id = id;
		this.x = x;
		this.y = y;
		this.start = start;
		this.mass = mass;
		
		lastChecked = System.currentTimeMillis();
		start = 0;
	}
	
	public void update(Splats main, double delta){
		if(paused){
			pausedDeltaTimes.add(delta);
			pausedStack++;
			return;
		}
		
		//gravity
		ArrayList<Planet> closestplanets = main.getClosestPlanets(this);
		float gravityx = 0;
		float gravityy = 0;
		for(Planet planet: closestplanets){
//			System.out.println((player == null) + " " + (planet == null));
			double angle = Math.atan2((y) - (planet.y), (x) - (planet.x));
			
			gravityx += Math.cos(angle) * planet.gravityhelperconstant / ((Math.sqrt(Math.pow((y) - (planet.y), 2) + Math.pow((x) - (planet.x), 2))) - getRadius() - planet.radius + 300) * 350;//XXX: IF YOU CHANGE THIS CHANGE IT IN PLANET CLASS AND SERVER PROJECT TOO
			gravityy += Math.sin(angle) * planet.gravityhelperconstant / ((Math.sqrt(Math.pow((y) - (planet.y), 2) + Math.pow((x) - (planet.x), 2))) - getRadius() - planet.radius + 300) * 350;
		}
		
//		if(System.currentTimeMillis() - lastChecked >= 1000){//1 seconds since last checked?
//		if(true){
//			main.messenger.sendMessageToAll("CHECK " + id + " " + x + " " + y + " " + xspeed + " " + yspeed + " " + System.currentTimeMillis());
//			lastChecked = System.currentTimeMillis();
//		}
		
		//bouncing
		Planet planet = main.getClosestPlanet(this);
		if(main.isTouchingPlanet(this, planet)){
			double angle = Math.atan2((y) - (planet.y), (x) - (planet.x));
			
			double ux = 2 * (Entity.getDotProduct(xspeed, yspeed, Math.cos(angle), Math.sin(angle))) * Math.cos(angle);
			double wx = xspeed - ux;
			double uy = 2 * (Entity.getDotProduct(xspeed, yspeed, Math.cos(angle), Math.sin(angle))) * Math.sin(angle);
			double wy = yspeed - uy;
			xspeed = (float) (wx - ux);
			yspeed = (float) (wy - uy);
			double finalangle = Math.atan2(yspeed, xspeed);
			xspeed = (float) (Math.cos(finalangle) * planet.bounceheight);
			yspeed = (float) (Math.sin(finalangle) * planet.bounceheight);
			
			double newx = planet.x + planet.radius * ((x - planet.x) / Math.sqrt(Math.pow(x - planet.x, 2) + Math.pow(y - planet.y, 2)));
			double newy = planet.y + planet.radius * ((y - planet.y) / Math.sqrt(Math.pow(x - planet.x, 2) + Math.pow(y - planet.y, 2)));
			x = (float) (newx + Math.cos(angle) * (getRadius()+2));
			y = (float) (newy + Math.sin(angle) * (getRadius()+2));
		}
		
//		for(int i=0;i<planet.food.length;i++){
//			Food food = planet.food[i];
//			if(!food.enabled) continue;
//			int foodx = (int) (Math.cos(food.angle)*(planet.radius+food.getSize()/2+5) + planet.x);
//			int foody = (int) (Math.sin(food.angle)*(planet.radius+food.getSize()/2+5) + planet.y);
//			if(x+getSize()>foodx && x<foodx+food.getSize() && y+getSize()>foody && y<foody+food.getSize()){
//				food.enabled = false;
//				main.messenger.sendMessageToAll("COLLECT " + id + " " + food.getAmount());
//				main.messenger.sendMessageToAll("FOOD " + main.getIndexOf(planet, main.planets) + " " + i + " " + false + " " + 0 + " " + 0);
//				mass += food.getAmount();
//			}
//		}
		
		//add gravity speeds to speed
		xspeed += gravityx*delta;
		yspeed += gravityy*delta;
		
		//movement
		if(Gdx.input.isKeyPressed(Input.Keys.D)){
			xspeed += Math.cos(main.getClosestAngle(this)+1.5708)*main.clientplayer.speed * delta;//1.5708 is 90 degrees in radians (half pi or quarter tau)
			yspeed += Math.sin(main.getClosestAngle(this)+1.5708)*main.clientplayer.speed * delta;//1.5708 is 90 degrees in radians (half pi or quarter tau)
			
			if(!main.clientplayer.serverstateright){
				final long now = System.currentTimeMillis();
				main.messenger.sendMessage("1 " + (now - start));//easily hackable (maybe change?)
				main.clientplayer.serverstateright = true;
			}
		}else if(main.clientplayer.serverstateright){//d disable
			final long now = System.currentTimeMillis();
			main.messenger.sendMessage("d1 " + (now - start));//easily hackable (maybe change?)
			main.clientplayer.serverstateright = false;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)){
			xspeed += Math.cos(main.getClosestAngle(this)-1.5708)*main.clientplayer.speed * delta;//1.5708 is 90 degrees in radians (half pi or quarter tau)
			yspeed += Math.sin(main.getClosestAngle(this)-1.5708)*main.clientplayer.speed * delta;//1.5708 is 90 degrees in radians (half pi or quarter tau)

			if(!main.clientplayer.serverstateleft){
				final long now = System.currentTimeMillis();
				main.messenger.sendMessage("-1 " + (now - start));//easily hackable (maybe change?)
				main.clientplayer.serverstateleft = true;
			}
		}else if(main.clientplayer.serverstateleft){//d disable
			final long now = System.currentTimeMillis();
			main.messenger.sendMessage("d-1 " + (now - start));//easily hackable (maybe change?)
			main.clientplayer.serverstateleft = false;
		}
		
		//friction
		if(Math.abs(xspeed) < friction*delta) xspeed = 0;
		else if(xspeed>0) xspeed-=friction*delta;
		else if(xspeed<0) xspeed+=friction*delta;
		if(Math.abs(yspeed) < friction*delta) yspeed = 0;
		else if(yspeed>0) yspeed-=friction*delta;
		else if(yspeed<0) yspeed+=friction*delta;
		
		//add all speeds
		x += xspeed*delta;
		y += yspeed*delta;
		
		
//		System.out.println("X: " + x + " Y: " + y + " DELTA: " + delta);
		
		//save old states
		oldStates.add(new OldState(x, y, xspeed, yspeed, System.currentTimeMillis(), left, right));
		if(oldStates.size() > 200) oldStates.remove(0);
	}
	
	public double addAtFps(double original, double addition, double delta){ //adds, but only at 40 fps
		delta+=leftoverdelta;
		
		for(double i=1/fps;i<delta;delta-=1/fps){
			System.out.println("S");
			original += addition * 1/fps;
		}
		
		futureleftoverdelta = delta;
		return original;
	}
	
	public boolean collided(Entity player){
	   float xdist = x - player.x;
	   float ydist = y - player.y;
	   double dist = Math.sqrt(Math.pow(xdist, 2) + Math.pow(ydist, 2)); //distance between circles (xdist and ydist and a and b of triangle)
	
		if(dist < player.getRadius() + getRadius()){
		    //collided
		    return true;
		}
		return false;
	}
}
