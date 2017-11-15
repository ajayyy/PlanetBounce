package com.ajayinkingston.splats;

import java.util.ArrayList;
import java.util.Random;

import com.ajayinkingston.planets.server.Data;
import com.ajayinkingston.planets.server.OldState;
import com.ajayinkingston.planets.server.Shot;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

public class ClientPlayer extends com.ajayinkingston.planets.server.Player{
	Random rand = new Random();
	
	double rotation = -1000000000; //might be needed for camera rotations
	
	Texture image;
	
	float maxspeed = 500;
	
//	float bounceheight = 1200; //Now set in planet class
	
	float gravityx,gravityy;//just for now
	
	int startmass = 50;
	boolean massChangeAni;
	int aniMass;
	int aniStartMass;
	int aniMassChange;
	
	boolean serverstateright,serverstateleft;//for making sure the server is up to date with information (right and left being button presses)
	
	long start;//current time millis of since connected
	
	int uncheckedMovements = 0;//amount of unchecked movements    //TODO DELETE
	ArrayList<OldState> uncheckedOldStates = new ArrayList<>();
	
	Player transformationPlayer;
	float transformationPlayerPercent = -1;
	
	int imagenum;
	
//	long rightstart;
	
//	final float xspeed,yspeed;
	
	Shot shot = null; // null if no shot this frame
	
	public ClientPlayer(int id, float x, float y, int mass, Splats splats){
		super(id, x, y, mass);
		
		this.mass = startmass;
		
		start = System.currentTimeMillis(); //incase somehow onconnect is not called
		
		imagenum = rand.nextInt(splats.playerImages.length);
	}
	
	public void render(Splats splats){ //also does camera movement 
		splats.batch.begin();
		float factor = 1.3f;
		splats.batch.draw(splats.shadow, x-getSize()/2*factor, y-getSize()/2*factor, getSize()*factor, getSize()*factor);
		splats.batch.draw(splats.playerImages[imagenum], x-getSize()/2, y-getSize()/2, getSize(), getSize());
		splats.batch.end();
		
		//move camera
		float delta = Gdx.graphics.getDeltaTime(); //use gdx.deltatime because this is not physics
		
		float lerp = 2.5f;
		splats.cam.position.x += (((x * splats.batch.scaleFactor) - splats.cam.position.x) * lerp * delta) ;
		splats.cam.position.y += (((y * splats.batch.scaleFactor) - splats.cam.position.y) * lerp * delta) ;
		
//		float lerp2 = 1.2f;
//		double closestangle = Main.getClosestAngle(this, data.planets);
//		if(rotation==-1000000000){
//			rotation = closestangle;
//			splats.cam.rotate((float) Math.toDegrees(closestangle));
//		}
//		if(Math.abs(closestangle - rotation) > Math.abs(closestangle +Math.PI*2 - rotation)){
//			closestangle += Math.PI*2;//Prevent random rotations
//		}
//		if(Math.abs(closestangle - rotation) > Math.abs(closestangle -Math.PI*2 - rotation)){
//			closestangle -= Math.PI*2;//Prevent random rotations
//		}
	
//		double angle1 = Math.abs((closestangle - rotation) % (Math.PI*2));
//	    double angle2 = Math.abs((rotation - closestangle) % (Math.PI*2));
//	    double difference = angle1 < angle2 ? -angle1 : angle2;
//		double difference = getDifferenceBetweenAngles(rotation, closestangle);
//		splats.cam.rotate((float) -Math.toDegrees((difference * lerp2 * delta)));
//		rotation += (difference * lerp2 * delta);//THIS WORKS FOR SOME REASON TODO FIND OUT WHY
//		rotation = closestangle;
//		System.out.println(rotation);
//		System.out.println(Math.toDegrees(rotation) + " " + Math.toDegrees(rotation+1.5708f));
	}
	
	public void checkInput(Splats splats, Data data){
		System.out.println(frames + " frames have passed for clientplayer");
		
		if(Gdx.input.isKeyPressed(Input.Keys.D)){
			right = true;
		}else{
			right = false;
		}
		
		if(Gdx.input.isKeyPressed(Input.Keys.A)){
			left = true;
		}else{
			left = false;
		}
		
		if(Gdx.input.isTouched() && System.currentTimeMillis() - splats.projectilelast >= 0.25 * 1000){
			Vector3 clickpos = splats.cam.unproject(new Vector3(Gdx.input.getX(),Gdx.input.getY(),0));
			
			float projectileangle = (float) Math.atan2(clickpos.y-(y*splats.batch.scaleFactor), clickpos.x-(x*splats.batch.scaleFactor));
			
			shot = new Shot(this, projectileangle, frames+1); //the frames and player don't really matter right now, but once Player and ClientPlayer are merged it will matter TODO make it matter
		
			splats.projectilelast = System.currentTimeMillis();
		}
		
	}
	
	public void postUpdate(Splats splats, Data data){ //sends messages and does the shooting
		if(right && !serverstateright){
			splats.messenger.sendMessage("1 " + (frames));//easily hackable (maybe change?)
			uncheckedMovements++;
			serverstateright = true;
		}else if(!right && serverstateright){
			splats.messenger.sendMessage("d1 " + (frames));//easily hackable (maybe change?)
			serverstateright = false;
		}
		
		if(left && !serverstateleft){
			splats.messenger.sendMessage("-1 " + (frames));//easily hackable (maybe change?)
			uncheckedMovements++;
			serverstateleft = true;
		}else if(!left && serverstateleft){
			splats.messenger.sendMessage("d-1 " + (frames));//easily hackable (maybe change?)
			serverstateleft = false;
		}
		
		if(shot != null){
			shoot(shot.projectileangle, data.projectiles, ClientProjectile.class);
			splats.messenger.sendMessage("s " + shot.projectileangle + " " + shot.frame);//if removed remove unchecked movements too
			
			shot = null;
		}
	}
	
	public double getDifferenceBetweenAngles(double firstAngle, double secondAngle){
	        double difference = secondAngle - firstAngle;
	        while (difference < -Math.PI) difference += Math.PI*2;
	        while (difference > Math.PI) difference -= Math.PI*2;
	        return difference;
	 }
}
