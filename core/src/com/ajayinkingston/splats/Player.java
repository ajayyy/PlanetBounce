package com.ajayinkingston.splats;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class Player extends Entity{
	public static Random rand = new Random();
	
	boolean real;//actually a player
	
	boolean left,right;//now going to be used
	boolean shooting;//this frame will be cleared next frame
	double projectileAngle = 0;
	
	long frames = 0;//amount of frames that passed;
	
	ArrayList<OldState> oldStates = new ArrayList<>();
	
	Player transformationPlayer;
	float transformationPlayerPercent = -1;
	
	boolean massChangeAni;
	int aniMass;
	
	int image;
	
	public Player(int id, int mass, Splats splats) {//TODO MAKE THIS CONSTRUCTOR ASK FOR X AND Y TOO (JUST LIKE SERVER)
		this(id, 0, 800, mass, splats);
	}
	
	public Player(int id, int x, int y, int mass, Splats splats) {//TODO MAKE THIS CONSTRUCTOR ASK FOR X AND Y TOO (JUST LIKE SERVER)
		this.id = id;
		this.mass = mass;
		this.x = x;
		this.y = y;
		
		if(id!=-1) real = true;
		
		image = rand.nextInt(splats.playerImages.length);
	}
	
	public void update(Splats splats, double delta, boolean simulation){
		
		//gravity
		ArrayList<Planet> closestplanets = splats.getClosestPlanets(this);
		float gravityx = 0;
		float gravityy = 0;
		for(Planet planet: closestplanets){
//			System.out.println((player == null) + " " + (planet == null));
			double angle = Math.atan2((y) - (planet.y), (x) - (planet.x));
			
			gravityx += Math.cos(angle) * planet.gravityhelperconstant / ((Math.sqrt(Math.pow((y) - (planet.y), 2) + Math.pow((x) - (planet.x), 2))) - getRadius() - planet.radius + 300) * 350;//XXX: IF YOU CHANGE THIS CHANGE IT IN PLANET CLASS AND SERVER PROJECT TOO
			gravityy += Math.sin(angle) * planet.gravityhelperconstant / ((Math.sqrt(Math.pow((y) - (planet.y), 2) + Math.pow((x) - (planet.x), 2))) - getRadius() - planet.radius + 300) * 350;
		}
		
		//bouncing
		Planet planet = splats.getClosestPlanet(this);
		if(splats.isTouchingPlanet(this, planet)){
			System.out.println("COLLIDING");
			double angle = Math.atan2((y) - (planet.y), (x) - (planet.x));
			
			double ux = 2 * (getDotProduct(xspeed, yspeed, Math.cos(angle), Math.sin(angle))) * Math.cos(angle);
			double wx = xspeed - ux;
			double uy = 2 * (getDotProduct(xspeed, yspeed, Math.cos(angle), Math.sin(angle))) * Math.sin(angle);
			double wy = yspeed - uy;
			xspeed = (float) (wx - ux);
			yspeed = (float) (wy - uy);
			double finalangle = Math.atan2(yspeed, xspeed);
			xspeed = (float) (Math.cos(finalangle) * planet.bounceheight);
			yspeed = (float) (Math.sin(finalangle) * planet.bounceheight);
			
//			double newx = planet.x + planet.radius * ((x - planet.x) / Math.sqrt(Math.pow(x - planet.x, 2) + Math.pow(y - planet.y, 2)));
//			double newy = planet.y + planet.radius * ((y - planet.y) / Math.sqrt(Math.pow(x - planet.x, 2) + Math.pow(y - planet.y, 2)));
//			x = (float) (newx + Math.cos(angle) * (getRadius()+2));
//			y = (float) (newy + Math.sin(angle) * (getRadius()+2));
		}
		
		//add gravity speeds to speed
		xspeed += gravityx * delta;
		yspeed += gravityy * delta;
		
		//movement
		if(right){
			xspeed += Math.cos(splats.getClosestAngle(this)+Math.toRadians(90)) * splats.clientplayer.speed * delta;
			yspeed += Math.sin(splats.getClosestAngle(this)+Math.toRadians(90)) * splats.clientplayer.speed * delta;
		}
		if(left){
			xspeed += Math.cos(splats.getClosestAngle(this)+Math.toRadians(-90)) * splats.clientplayer.speed * delta;
			yspeed += Math.sin(splats.getClosestAngle(this)+Math.toRadians(-90)) * splats.clientplayer.speed * delta;
		}
		
		//friction
		if(Math.abs(xspeed) < friction*delta) xspeed = 0;
		else if(xspeed>0) xspeed-=friction*delta;
		else if(xspeed<0) xspeed+=friction*delta;
		if(Math.abs(yspeed) < friction*delta) yspeed = 0;
		else if(yspeed>0) yspeed-=friction*delta;
		else if(yspeed<0) yspeed+=friction*delta;
		
		//add all speeds
		x += xspeed * delta;
		y += yspeed * delta;
		
		//shooting
		if(shooting){
			xspeed -= Math.cos(projectileAngle) * splats.projectileSpeedChange;
			yspeed -= Math.sin(projectileAngle) * splats.projectileSpeedChange;
			if(real){
				splats.projectiles.add(new Projectile(x + ((getRadius() + 1 + splats.projectilesize/2) * Math.cos(projectileAngle)), y + ((getRadius() + splats.projectilesize/2) * Math.sin(projectileAngle)), splats.projectilesize, projectileAngle, splats.projectileSpeed));
				if(transformationPlayerPercent != -1){
					transformationPlayer.shooting = true;
					transformationPlayer.projectileAngle = projectileAngle;
				}
			}
			shooting = false;
		}
		
		if(transformationPlayerPercent != -1 && real && !simulation){
			transformationPlayer.left = left;
			transformationPlayer.right = right;
			transformationPlayer.update(splats, Gdx.graphics.getDeltaTime(), false);
		}
		
//		System.out.println(xspeed + " " + yspeed);
		
		if(real){
			OldState oldState = new OldState(x, y, xspeed, yspeed, frames, left, right);
			oldStates.add(oldState);
			while(oldStates.size() > 90){//1.5 seconds of old state data
				oldStates.remove(0);
			}
		}
		frames++;
	}
	
	public void render(Splats splats, double delta){
		float x = this.x;
		float y = this.y;
		if(transformationPlayerPercent >= 0){
			x = transformationPlayer.x + ((x - transformationPlayer.x) * (transformationPlayerPercent/100f));
			y = transformationPlayer.y + ((y - transformationPlayer.y) * (transformationPlayerPercent/100f));
			transformationPlayerPercent += 200*delta;
			if(transformationPlayerPercent >= 100){
				transformationPlayerPercent = -1;
				transformationPlayer = null;
				x = this.x;
				y = this.y;
			}
		}
		
//		System.out.println("KJSDFLKSDFJLJDFSLKJSFDLKJSFDLKSFDJLKSDFJKLSDFJKLSDJF");
		Vector3 newthingya = splats.cam.project(new Vector3(x,y,0));
		if(newthingya.x > splats.cam.position.x && newthingya.x < splats.cam.position.x+Gdx.graphics.getWidth()
			&& newthingya.y > splats.cam.position.y && newthingya.y < splats.cam.position.y+Gdx.graphics.getHeight()){
			System.out.print("POUIEYPOIYUOREIUETIOUERIOUEIOUREIOUTIOREUIOUTEIOU");
		}else{
//			System.out.println((newthingya.x > splats.cam.position.x) + " " + (newthingya.x < splats.cam.position.x+Gdx.graphics.getWidth())
//					 + " " +  (newthingya.y < splats.cam.position.y) + " " +  (newthingya.y > splats.cam.position.y+Gdx.graphics.getHeight()));

		}
		
//		splats.shapeRenderer.setColor(Color.RED);
//		splats.shapeRenderer.begin(ShapeType.Filled);
//		splats.shapeRenderer.circle(x, y, mass/2);
//		splats.shapeRenderer.end();
		
		splats.batch.begin();
		float factor = 1.4f;
		splats.batch.draw(splats.shadow, x-getSize()/2*factor, y-getSize()/2*factor, getSize()*factor, getSize()*factor);
		splats.batch.draw(splats.playerImages[image], x-getSize()/2, y-getSize()/2, getSize(), getSize());
		splats.batch.end();
	}
	
}
