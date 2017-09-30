package com.ajayinkingston.splats;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

public class ClientPlayer extends Entity{
	Random rand = new Random();
	
	double rotation = -1000000000; //might be needed for camera rotations
	
	Texture image;
	
	float speed = 500, maxspeed = 500;
	
//	float bounceheight = 1200; //Now set in planet class
	
	float gravityx,gravityy;//just for now
	
	int startmass = 50;
	boolean massChangeAni;
	int aniMass;
	int aniStartMass;
	int aniMassChange;
	
	boolean serverstateright,serverstateleft;//for making sure the server is up to date with information (right and left being button presses)
	
	long start;//current time millis of since connected
	long frames = 0;//amount of frames recorded
	
	ArrayList<OldState> oldStates = new ArrayList<>();
	
	boolean right,left;//used only when simulating frames
	
	int uncheckedMovements = 0;//amount of unchecked movements 
	ArrayList<OldState> uncheckedOldStates = new ArrayList<>();
	
	Player transformationPlayer;
	float transformationPlayerPercent = -1;
	
	int imagenum;
	
//	long rightstart;
	
//	final float xspeed,yspeed;
	
	public ClientPlayer(Splats splats){
		y = 800;
		
		mass = startmass;
		
		start = System.currentTimeMillis(); //incase somehow onconnect is not called
		
//		splats.cam.rotate(0);

		imagenum = rand.nextInt(splats.playerImages.length);
	}
	
	public void render(Splats splats){
		splats.batch.begin();
		float factor = 1.3f;
		splats.batch.draw(splats.shadow, x-getSize()/2*factor, y-getSize()/2*factor, getSize()*factor, getSize()*factor);
		splats.batch.draw(splats.playerImages[imagenum], x-getSize()/2, y-getSize()/2, getSize(), getSize());
		splats.batch.end();
	}
	
	public void update(final Splats splats, double delta, boolean simulation){
		boolean shot = false;
		
		if(frames > 60){
			right = true;
			simulation = true;
		}
//		if(frames > 150){
//			right = false;
//			simulation = false;
//		}
//		if(frames > 200){
//			left = true;
//			simulation = true;
//		}
//		if(frames > 250){
//			left = false;
//			simulation = false;
//		}
//		if(frames > 300){
//			right = true;	
//			simulation = true;
//		}
		if(frames > 350){
			right = false;
			simulation = false;
		}
		
		System.out.println(frames + " frames have passed for clientplayer");
//		start = 0;
		boolean moved = false;
		if(!simulation){
			float x = this.x;
			float y = this.y;
			if(transformationPlayerPercent >= 0){
				x = transformationPlayer.x + (x - transformationPlayer.x) * (transformationPlayerPercent/100f);
				y = transformationPlayer.y + (y - transformationPlayer.y) * (transformationPlayerPercent/100f);
				transformationPlayerPercent += 200*delta;
				if(transformationPlayerPercent >= 100){
					transformationPlayerPercent = -1;
					transformationPlayer = null;
					x = this.x;
					y = this.y;
				}
			}
			
			if(massChangeAni){
				float distance = (aniStartMass + aniMassChange) - getSize();
				float speed = (aniMassChange - distance) * 1;
				mass += 150*delta;
				if(mass - aniStartMass >= aniMassChange){
					mass = aniStartMass + aniMassChange;
					massChangeAni = false;
					aniMass = 0;
				}
			}
		}
		
		//gravity
		ArrayList<Planet> closestplanets = splats.getClosestPlanets(this);
		Planet closest = splats.getClosestPlanet(this);
		float gravityx = 0;
		float gravityy = 0;
		for(Planet planet: closestplanets){
//					System.out.println((player == null) + " " + (planet == null));
			double angle = Math.atan2((y) - (planet.y), (x) - (planet.x));
			
			gravityx += Math.cos(angle) * planet.gravityhelperconstant / ((Math.sqrt(Math.pow((y) - (planet.y), 2) + Math.pow((x) - (planet.x), 2))) - getRadius() - planet.radius + 300) * 350;//XXX: IF YOU CHANGE THIS CHANGE IT IN PLANET CLASS AND SERVER PROJECT TOO
			gravityy += Math.sin(angle) * planet.gravityhelperconstant / ((Math.sqrt(Math.pow((y) - (planet.y), 2) + Math.pow((x) - (planet.x), 2))) - getRadius() - planet.radius + 300) * 350;
		}
		
		//bouncing
		Planet planet = splats.getClosestPlanet(this);
		if(splats.isTouchingPlanet(this, planet)){
			System.out.println(frames + " frame bounced at");
//			System.out.println("COLLIDING");
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
			
			double newx = planet.x + planet.radius * ((x - planet.x) / Math.sqrt(Math.pow(x - planet.x, 2) + Math.pow(y - planet.y, 2)));
			double newy = planet.y + planet.radius * ((y - planet.y) / Math.sqrt(Math.pow(x - planet.x, 2) + Math.pow(y - planet.y, 2)));
			x = (float) (newx + Math.cos(angle) * (getRadius()+2));
			y = (float) (newy + Math.sin(angle) * (getRadius()+2));
		}
		
		//add gravity speeds to speed
		xspeed += gravityx * delta;
		yspeed += gravityy * delta;
		
		//movement
		boolean left = false;
		boolean right = false;
		if((Gdx.input.isKeyPressed(Input.Keys.D) && !simulation) || (simulation && this.right)){
			shot = true;
			
			xspeed += Math.cos(splats.getClosestAngle(this)+1.5708)*speed * delta;//1.5708 is 90 degrees in radians (half pi or quarter tau)
			yspeed += Math.sin(splats.getClosestAngle(this)+1.5708)*speed * delta;//1.5708 is 90 degrees in radians (half pi or quarter tau)
			System.out.println(frames + " MOVED BY X " + (Math.cos(splats.getClosestAngle(this)+1.5708)*speed * delta) + " MOVED BY Y " + Math.sin(splats.getClosestAngle(this)+1.5708)*speed * delta + " AT ANGLE " + splats.getClosestAngle(this) + " X " + x + " Y " + y);
			
//			x+=Math.cos(0) * 500*delta;
//			y+=Math.sin(0) * 500*delta;
			if(!simulation){
				if(!serverstateright){
					final long now = System.currentTimeMillis();
					boolean success = splats.messenger.sendMessage("1 " + (frames));//easily hackable (maybe change?)
					uncheckedMovements++;
					moved = true;
//					rightstart = frames;
				}
				serverstateright = true;
			}
			right = true;
		}else if(serverstateright){
			final long now = System.currentTimeMillis();
			splats.messenger.sendMessage("d1 " + (frames));//easily hackable (maybe change?)
			uncheckedMovements++;
			moved = true;
			serverstateright = false;
		}
		
		if((Gdx.input.isKeyPressed(Input.Keys.A) && !simulation) || (simulation && this.left)){
			xspeed += Math.cos(splats.getClosestAngle(this)-1.5708)*speed * delta;//1.5708 is 90 degrees in radians (half pi or quarter tau)
			yspeed += Math.sin(splats.getClosestAngle(this)-1.5708)*speed * delta;//1.5708 is 90 degrees in radians (half pi or quarter tau)
			
//			x+=Math.cos(-Math.PI) * 500*delta;
//			y+=Math.sin(-Math.PI) * 500*delta;
			
			if(!simulation){
				if(!serverstateleft){
					final long now = System.currentTimeMillis();
							splats.messenger.sendMessage("-1 " + (frames));//easily hackable (maybe change?)
					uncheckedMovements++;
					moved = true;
				}
				serverstateleft = true;
			}
			left = true;
		}else if(serverstateleft){//d disable
			final long now = System.currentTimeMillis();
					splats.messenger.sendMessage("d-1 " + (frames));//easily hackable (maybe change?)
			uncheckedMovements++;
			moved = true;
			serverstateleft = false;
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
		
		//update the transformation player
		if(transformationPlayerPercent != -1 && !simulation){
			transformationPlayer.left = left;
			transformationPlayer.right = right;
			transformationPlayer.update(splats, delta, false);
		}
		
		if(!simulation){
			float x = this.x;
			float y = this.y;
			if(transformationPlayerPercent >= 0){
				x = transformationPlayer.x + (x - transformationPlayer.x) * (transformationPlayerPercent/100f);
				y = transformationPlayer.y + (y - transformationPlayer.y) * (transformationPlayerPercent/100f);
				transformationPlayerPercent += 300*delta;
				if(transformationPlayerPercent >= 100){
					transformationPlayerPercent = -1;
					transformationPlayer = null;
					x = this.x;
					y = this.y;
				}
			}
			float lerp = 2.5f;
			splats.cam.position.x += (((x* splats.batch.scaleFactor) - splats.cam.position.x) * lerp * delta) ;
			splats.cam.position.y += (((y* splats.batch.scaleFactor) - splats.cam.position.y) * lerp * delta) ;
		}
		
		if(Gdx.input.isTouched() && System.currentTimeMillis() - splats.projectilelast >= 0.25 * 1000){
			Vector3 clickpos = splats.cam.unproject(new Vector3(Gdx.input.getX(),Gdx.input.getY(),0));
//			clickpos.x /= splats.batch.scaleFactor;
//			clickpos.y *= splats.batch.scaleFactor;
			final double projectileangle = Math.atan2(clickpos.y-(y*splats.batch.scaleFactor), clickpos.x-(x*splats.batch.scaleFactor));
			splats.projectiles.add(new Projectile(x + ((getSize() + splats.projectilesize/2) * Math.cos(projectileangle)), y + ((getSize() + splats.projectilesize/2) * Math.sin(projectileangle)), splats.projectilesize, projectileangle, splats.projectileSpeed));
//			projectiles.add(new Projectile(clientplayer.x, clientplayer.y, projectilesize, Math.atan2((Gdx.graphics.getHeight()-Gdx.input.getY()) - (playerpos) + Gdx.graphics.getHeight()/2), Gdx.input.getX() - (Gdx.graphics.getWidth()/2+clientplayer.x))+clientplayer.rotation+Math.PI/2, 1000));
//			final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
//			exec.schedule(new Runnable(){
//			    @Override
//			    public void run(){
					splats.messenger.sendMessage("s " + projectileangle + " " + (frames));//if removed remove unchecked movements too
//			    }
//			}, 100, TimeUnit.MILLISECONDS);
			if(transformationPlayerPercent != -1){
				transformationPlayer.shooting = true;
				transformationPlayer.projectileAngle = projectileangle;
			}
			uncheckedMovements++;
			moved = true;
			xspeed -= Math.cos(projectileangle) * 250;
			yspeed -= Math.sin(projectileangle) * 250;
			splats.projectilelast = System.currentTimeMillis();
		}
		
		if(!simulation){
			float x = this.x;
			float y = this.y;
			if(transformationPlayerPercent >= 0){
				x = transformationPlayer.x + (x - transformationPlayer.x) * (transformationPlayerPercent/100f);
				y = transformationPlayer.y + (y - transformationPlayer.y) * (transformationPlayerPercent/100f);
				transformationPlayerPercent += 300*delta;
				if(transformationPlayerPercent >= 100){
					transformationPlayerPercent = -1;
					transformationPlayer = null;
					x = this.x;
					y = this.y;
				}
			}
			float lerp2 = 1.2f;
			double closestangle = splats.getClosestAngle(this);
			if(rotation==-1000000000){
				rotation = closestangle;
				splats.cam.rotate((float) Math.toDegrees(closestangle));
			}
	//		if(Math.abs(closestangle - rotation) > Math.abs(closestangle +Math.PI*2 - rotation)){
	//			closestangle += Math.PI*2;//Prevent random rotations
	//		}
	//		if(Math.abs(closestangle - rotation) > Math.abs(closestangle -Math.PI*2 - rotation)){
	//			closestangle -= Math.PI*2;//Prevent random rotations
	//		}
		
//			double angle1 = Math.abs((closestangle - rotation) % (Math.PI*2));
//		    double angle2 = Math.abs((rotation - closestangle) % (Math.PI*2));
//		    double difference = angle1 < angle2 ? -angle1 : angle2;
			double difference = getDifferenceBetweenAngles(rotation, closestangle);
//			splats.cam.rotate((float) -Math.toDegrees((difference * lerp2 * delta)));
			rotation += (difference * lerp2 * delta);//THIS WORKS FOR SOME REASON TODO FIND OUT WHY
	//		rotation = closestangle;
	//		System.out.println(rotation);
	//		System.out.println(Math.toDegrees(rotation) + " " + Math.toDegrees(rotation+1.5708f));
		}
		
		//save state to old states
		OldState oldState = new OldState(x, y, xspeed, yspeed, frames, left, right, shot);
		oldStates.add(oldState);
		while(oldStates.size() > 90){//1.5 seconds of old state data
			oldStates.remove(0);
		}
		if(moved){
			uncheckedOldStates.add(oldState);
		}
		frames++;
		
	}
	
//	public double addAtFps(double original, double addition, double delta){ //adds, but only at 40 fps
//		delta+=leftoverdelta;
//		
//		for(double i=1/fps;i<delta;delta-=1/fps){
//			original += addition * 1/fps;
//		}
//		
//		futureleftoverdelta = delta;
//		return original;
//	}
	
	public double getDifferenceBetweenAngles(double firstAngle, double secondAngle){
	        double difference = secondAngle - firstAngle;
	        while (difference < -Math.PI) difference += Math.PI*2;
	        while (difference > Math.PI) difference -= Math.PI*2;
	        return difference;
	 }
}
