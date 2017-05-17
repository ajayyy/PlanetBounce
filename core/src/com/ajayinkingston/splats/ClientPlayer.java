package com.ajayinkingston.splats;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

public class ClientPlayer {
	float x,y;
	
	Random rand = new Random();
	
	double rotation = -1000000000; //might be needed for camera rotations
	
	Texture image;
	
	float speed = 500, maxspeed = 500, friction = 100;
	float xspeed,yspeed;
	
//	float bounceheight = 1200; //Now set in planet class
	
	float gravityx,gravityy;//just for now
	
	int size = 50;
	int startsize = 50;
	
	double closestangle = Math.PI;
	
	boolean serverstateright,serverstateleft;//for making sure the server is up to date with information (right and left being button presses)
	
	long start;//current time millis of since connected
	
	ArrayList<OldState> oldStates = new ArrayList<>();
	
	boolean right,left;//used only when simulating frames
	
	int uncheckedMovements = 0;//amount of unchecked movements 
	ArrayList<OldState> uncheckedOldStates = new ArrayList<>();
	
	Player transformationPlayer;
	float transformationPlayerPercent = -1;
	
//	int mass;
	boolean massChangeAni;
	int aniMass;
	int aniStartMass;
	int aniMassChange;
	
	int imagenum;
	
	public ClientPlayer(Splats splats){
		y = 800;
		
		start = System.currentTimeMillis(); //incase somehow onconnect is not called
		
//		splats.cam.rotate(0);

		imagenum = rand.nextInt(splats.playerImages.length);
	}
	
	public void render(final Splats splats, boolean simulation){
		start = 0;
		boolean moved = false;
		if(!simulation){
//			splats.shapeRenderer.setColor(Color.RED);
//			splats.shapeRenderer.begin(ShapeType.Filled);
			float x = this.x;
			float y = this.y;
			if(transformationPlayerPercent >= 0){
				x = transformationPlayer.x + (x - transformationPlayer.x) * (transformationPlayerPercent/100f);
				y = transformationPlayer.y + (y - transformationPlayer.y) * (transformationPlayerPercent/100f);
				transformationPlayerPercent += 200*Gdx.graphics.getDeltaTime();
				if(transformationPlayerPercent >= 100){
					transformationPlayerPercent = -1;
					transformationPlayer = null;
					x = this.x;
					y = this.y;
				}
			}
			
			splats.batch.begin();
			float factor = 1.3f;
			splats.batch.draw(splats.shadow, x-size/2*factor, y-size/2*factor, size*factor, size*factor);
			splats.batch.draw(splats.playerImages[imagenum], x-size/2, y-size/2, size, size);
			splats.batch.end();
			
//			splats.shapeRenderer.circle(x, y, size/2);
//			splats.shapeRenderer.setColor(Color.BROWN);
	//		double anegle = Math.atan2((y) - (splats.planets[i].y), (x) - (splats.planets[i].x));
	//		double boeunceangle = Math.toRadians(360)-Math.atan2(yspeed,xspeed);
	//		splats.shapeRenderer.line(new Vector2(x,y), new Vector2((float) (Math.cos(boeunceangle) * 10000 + x),(float) Math.sin(boeunceangle) * 10000 + y));
//			splats.shapeRenderer.end();
			
			if(massChangeAni){
//				 aniMassChange *= 1.5;
//				if(aniMass == 0){
//					float distance = (aniStartMass + aniMassChange) - size;
//					float speed = ((aniMassChange - distance) * 1+1)/10f;
//					size += 150*Gdx.graphics.getDeltaTime();
//					if(size - aniStartMass >=  + aniMassChange){
//						aniMass++;
//						size = (int) (aniStartMass + aniMassChange);
//
//					}
////				}
//				}else if(aniMass == 1){
//					float distance = size - (aniStartMass-aniMassChange);
//					float speed = (aniMassChange - distance) * 1;
//					size -= 150*Gdx.graphics.getDeltaTime();
//					if(size <= aniStartMass-aniMassChange){
//						aniMass++;
//						size = (int) (aniStartMass-aniMassChange);
//					}
//				}else{
					float distance = (aniStartMass + aniMassChange) - size;
					float speed = (aniMassChange - distance) * 1;
					size += 150*Gdx.graphics.getDeltaTime();
					if(size - aniStartMass >= aniMassChange){
						size = aniStartMass + aniMassChange;
						massChangeAni = false;
						aniMass = 0;
					}
//				}
			}
		}
		
		boolean left = false;
		boolean right = false;
		if((Gdx.input.isKeyPressed(Input.Keys.D) && !simulation) || (simulation && this.right)){
//			//act like 60 fps all the time
//			double delta = Gdx.graphics.getDeltaTime();
//			double gooddelta = 1/60d;//fps we want
//			double i = gooddelta;//for the loop
//			player.x = x;
//			player.y = y;
//			player.xspeed = xspeed;
//			player.yspeed = yspeed;
//			while(i<delta){
//				xspeed+=Math.cos(splats.getClosestAngle(player)+Math.toRadians(90)) * speed*gooddelta;
//				yspeed+=Math.sin(closestangle+Math.toRadians(90)) * speed*gooddelta;
//				if(i+gooddelta<delta){
//					i+=gooddelta;
//				}else{
//					xspeed+=Math.cos(closestangle+Math.toRadians(90)) * speed * (delta-i);
//					yspeed+=Math.sin(closestangle+Math.toRadians(90)) * speed * (delta-i);
//				}
//			}
//			splats.shapeRenderer.line(new Vector2(x,y), new Vector2((float) (Math.cos(closestangle+1.5708f) * 100 * speed*Gdx.graphics.getDeltaTime()),(float) Math.sin(closestangle+1.5708f) * 100 * speed*Gdx.graphics.getDeltaTime()));
//			splats.shapeRenderer.end();
//			yspeed-=speed*5*Gdx.graphics.getDeltaTime();
			xspeed+=Math.cos(closestangle+Math.toRadians(90)) * 1 * speed*Gdx.graphics.getDeltaTime();//1.5708 is 90 degrees in radians (half pi or quarter tau)
			yspeed+=Math.sin(closestangle+Math.toRadians(90)) * 1 * speed*Gdx.graphics.getDeltaTime();
//			splats.shapeRenderer.begin(ShapeType.Filled);
//			splats.shapeRenderer.setColor(Color.GREEN);
			if(!simulation){
				if(!serverstateright){
					final long now = System.currentTimeMillis();
//					final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
//					exec.schedule(new Runnable(){
//					    @Override
//					    public void run(){
							boolean success = splats.messenger.sendMessage("1 " + (now - start));//easily hackable (maybe change?)
//					    }
//					}, 10, TimeUnit.MILLISECONDS);
					uncheckedMovements++;
					moved = true;
	//				splats.messenger.sendMessage("1 " + (System.currentTimeMillis() - start - (long)(Gdx.graphics.getDeltaTime()*1000)));//sends when it happened minus delta time. Server uses this info later
				}
				serverstateright = true;
			}
			right = true;
		}else if(serverstateright){
			final long now = System.currentTimeMillis();
//			final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
//			exec.schedule(new Runnable(){
//			    @Override
//			    public void run(){
					splats.messenger.sendMessage("d1 " + (now - start));//easily hackable (maybe change?)
//			    }
//			}, 10, TimeUnit.MILLISECONDS);
			uncheckedMovements++;
			moved = true;
			serverstateright = false;
		}
		
		if((Gdx.input.isKeyPressed(Input.Keys.A) && !simulation) || (simulation && this.left)){
			xspeed+=Math.cos(closestangle-1.5708f) * speed*Gdx.graphics.getDeltaTime();
			yspeed+=Math.sin(closestangle-1.5708f) * speed*Gdx.graphics.getDeltaTime();
			
//			xspeed-=speed*Gdx.graphics.getDeltaTime();
			if(!simulation){
				if(!serverstateleft){
					final long now = System.currentTimeMillis();
//					final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
//					exec.schedule(new Runnable(){
//					    @Override
//					    public void run(){
							splats.messenger.sendMessage("-1 " + (now - start));//easily hackable (maybe change?)
//					    }
//					}, 10, TimeUnit.MILLISECONDS);
					uncheckedMovements++;
					moved = true;
	//				splats.messenger.sendMessage("-1 " + (System.currentTimeMillis() - start - (long)(Gdx.graphics.getDeltaTime()*1000)));//easily hackable (maybe change?)
				}
				serverstateleft = true;
			}
			left = true;
		}else if(serverstateleft){//d disable
			final long now = System.currentTimeMillis();
//			final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
//			exec.schedule(new Runnable(){
//			    @Override
//			    public void run(){
					splats.messenger.sendMessage("d-1 " + (now - start));//easily hackable (maybe change?)
//			    }
//			}, 10, TimeUnit.MILLISECONDS);
			uncheckedMovements++;
			moved = true;
//			splats.messenger.sendMessage("d-1 " + (System.currentTimeMillis() - start));//easily hackable (maybe change?)
			serverstateleft = false;
		}
		
		ArrayList<Planet> closeplanets = new ArrayList<>();
		Planet closest = null;
		float closestdistance = 0;
		for(int i=0;i<splats.planets.length;i++){
			if(splats.planets[i] == null){
				System.out.println("sadsdsadsadadsad " + i);
			}
			if(Math.pow(Math.abs(x - splats.planets[i].x), 2) + Math.pow(Math.abs(y - splats.planets[i].y), 2) < Math.pow(size/2 + splats.planets[i].radius, 2)){
				//collided
				double angle = Math.atan2((y) - (splats.planets[i].y), (x) - (splats.planets[i].x));
//				double angle = Math.atan2((splats.planets[i].y) - y, (splats.planets[i].x) - x);
				
//				double bounceangle = Math.toRadians(360)-Math.atan2(yspeed,xspeed);
//				double bounceangle = Math.toRadians(360) - (angle-Math.toRadians(90) + Math.atan2(yspeed,xspeed)) - 90 - angle + 90;
				double reflectionangle = (Math.atan2(yspeed, xspeed) - angle) % (Math.PI*2);
				double bounceangle = angle + (Math.toRadians(360) - reflectionangle) - Math.toRadians(180);

//				yspeed = (float) (Math.sin(angle) * splats.planets[i].bounceheight); //TODO BOUNCE HEIGHT A FORMULA FROM GRAVITY TO DIFFER IN EVERY PLANET AND FOR EVERY PLAYER WITH DIFFERENT MASS
//				xspeed = (float) (Math.cos(angle) * splats.planets[i].bounceheight);
				
				//new bad formula
//				xspeed = -xspeed;
//				yspeed = -yspeed;
				
				System.out.println(Math.toDegrees(angle));
				
//				double u = (getDotProduct(xspeed, yspeed, Math.cos(angle), Math.sin(angle)) / getDotProduct(Math.cos(angle), Math.sin(angle), Math.cos(angle), Math.sin(angle))) * 
				double ux = 2 * (getDotProduct(xspeed, yspeed, Math.cos(angle), Math.sin(angle))) * Math.cos(angle);
				double wx = xspeed - ux;
				double uy = 2 * (getDotProduct(xspeed, yspeed, Math.cos(angle), Math.sin(angle))) * Math.sin(angle);
				double wy = yspeed - uy;
				xspeed = (float) (wx - ux);
				yspeed = (float) (wy - uy);
				double finalangle = Math.atan2(yspeed, xspeed);
				xspeed = (float) (Math.cos(finalangle) * splats.planets[i].bounceheight);
				yspeed = (float) (Math.sin(finalangle) * splats.planets[i].bounceheight);
				
				//set x and y of player to most outer part of circle to make sure it is not detected again
				double newx = splats.planets[i].x + splats.planets[i].radius * ((x - splats.planets[i].x) / Math.sqrt(Math.pow(x - splats.planets[i].x, 2) + Math.pow(y - splats.planets[i].y, 2)));
				double newy = splats.planets[i].y + splats.planets[i].radius * ((y - splats.planets[i].y) / Math.sqrt(Math.pow(x - splats.planets[i].x, 2) + Math.pow(y - splats.planets[i].y, 2)));
				x = (float) (newx + Math.cos(angle) * (size/2+2));
				y = (float) (newy + Math.sin(angle) * (size/2+2));
				
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				
//				yspeed = -yspeed;
//				System.out.println((Math.cos(angle) * splats.planets[i].gravity / (Math.sqrt(Math.pow((y) - (splats.planets[i].y), 2) + Math.pow((x) - (splats.planets[i].x), 2))) * 400) + " " + (Math.sin(angle) * splats.planets[i].gravity / (Math.sqrt(Math.pow((y) - (splats.planets[i].y), 2) + Math.pow((x) - (splats.planets[i].x), 2))) * 400));
				closeplanets.add(splats.planets[i]);
//				if(closest == null || Math.pow(Math.abs(x - splats.planets[i].x), 2) + Math.pow(Math.abs(y - splats.planets[i].y), 2) < closestdistance){
//					closest = splats.planets[i];
//					closestdistance = (float) (Math.pow(Math.abs(x - splats.planets[i].x), 2) + Math.pow(Math.abs(y - splats.planets[i].y), 2));
//				}//TODO MAYBE MAKE THIS LAG A LITTLE LESS SOMEHOW
			}else if(Math.pow(Math.abs(x - splats.planets[i].x), 2) + Math.pow(Math.abs(y - splats.planets[i].y), 2) < Math.pow(size/2 + (splats.planets[i].radius*3.5f), 2)){
				//close
				closeplanets.add(splats.planets[i]);
//				if(closest == null || Math.pow(Math.abs(x - splats.planets[i].x), 2) + Math.pow(Math.abs(y - splats.planets[i].y), 2) < closestdistance){
//					closest = splats.planets[i];
//					closestdistance = (float) (Math.pow(Math.abs(x - splats.planets[i].x), 2) + Math.pow(Math.abs(y - splats.planets[i].y), 2));
//				}
			}
			if(closest == null || Math.pow(Math.abs(x - splats.planets[i].x), 2) + Math.pow(Math.abs(y - splats.planets[i].y), 2) < closestdistance){
				closest = splats.planets[i];
				closestdistance = (float) (Math.pow(Math.abs(x - splats.planets[i].x), 2) + Math.pow(Math.abs(y - splats.planets[i].y), 2));
			}
		}
		if(!closeplanets.contains(closest)) closeplanets.add(closest);
		
		gravityx = 0;
		gravityy = 0;
		closestangle = 0;
		for(Planet planet: closeplanets){
			double angle = Math.atan2((y) - (planet.y), (x) - (planet.x));
//			double angle = Math.atan2(((planet.y) - y), (planet.x) - x);
			if(planet == closest){
				closestangle = angle - Math.PI;
			}                                              //IT WORKS
			gravityx += Math.cos(angle) * planet.gravity / (Math.sqrt(Math.pow(Math.abs((y) - (planet.y)), 2) + Math.pow(Math.abs((x) - (planet.x)), 2))) * 350;//XXX: IF YOU CHANGE THIS CHANGE IT IN PLANET CLASS AND SERVER PROJECT TOO
			gravityy += Math.sin(angle) * planet.gravity / (Math.sqrt(Math.pow(Math.abs((y) - (planet.y)), 2) + Math.pow(Math.abs((x) - (planet.x)), 2))) * 350;
//			gravityx += Math.cos(angle) * planet.gravity ;//TODO MAKE GRAVITY LESS POWERFULL FOR LOWER MASS PLANETS 
//			gravityy += Math.sin(angle) * planet.gravity ;
		}
		yspeed+=gravityy*Gdx.graphics.getDeltaTime();
		xspeed+=gravityx*Gdx.graphics.getDeltaTime();
		
		x+=xspeed*Gdx.graphics.getDeltaTime();
		
//		if(xspeed>maxspeed) xspeed = maxspeed;
//		if(xspeed<-maxspeed) xspeed = -maxspeed;
//		if(Math.abs(xspeed)<=friction*Gdx.graphics.getDeltaTime()) xspeed = 0;
//		if(xspeed<0) xspeed+=friction*Gdx.graphics.getDeltaTime();
//		if(xspeed>0) xspeed-=friction*Gdx.graphics.getDeltaTime();
		
		y+=yspeed*Gdx.graphics.getDeltaTime();
		
		if(Math.abs(xspeed) < friction*Gdx.graphics.getDeltaTime()) xspeed = 0;
		else if(xspeed>0) xspeed-=friction*Gdx.graphics.getDeltaTime();
		else if(xspeed<0) xspeed+=friction*Gdx.graphics.getDeltaTime(); //XXX IF YOU CHANGE THIS CHANGE SERVER TOO
		if(Math.abs(yspeed) < friction*Gdx.graphics.getDeltaTime()) yspeed = 0;
		else if(yspeed>0) yspeed-=friction*Gdx.graphics.getDeltaTime();
		else if(yspeed<0) yspeed+=friction*Gdx.graphics.getDeltaTime();
		
		//update the transformation player
		if(transformationPlayerPercent != -1 && !simulation){
			transformationPlayer.left = left;
			transformationPlayer.right = right;
			transformationPlayer.update(splats, Gdx.graphics.getDeltaTime(), false);
		}
		
//		splats.cam.position.x = x; 
		
		if(!simulation){
			float x = this.x;
			float y = this.y;
			if(transformationPlayerPercent >= 0){
				x = transformationPlayer.x + (x - transformationPlayer.x) * (transformationPlayerPercent/100f);
				y = transformationPlayer.y + (y - transformationPlayer.y) * (transformationPlayerPercent/100f);
				transformationPlayerPercent += 300*Gdx.graphics.getDeltaTime();
				if(transformationPlayerPercent >= 100){
					transformationPlayerPercent = -1;
					transformationPlayer = null;
					x = this.x;
					y = this.y;
				}
			}
			float lerp = 2.5f;
			splats.cam.position.x += (((x* splats.batch.scaleFactor) - splats.cam.position.x) * lerp * Gdx.graphics.getDeltaTime()) ;
			splats.cam.position.y += (((y* splats.batch.scaleFactor) - splats.cam.position.y) * lerp * Gdx.graphics.getDeltaTime()) ;
//			while(x-size/2<splats.cam.position.x-splats.cam.viewportWidth/2){//TODO FIX THIS NOT WORKING PROPERLY (MOVING LEFT OFF SCREEN
//				splats.cam.position.x += (x - splats.cam.position.x) * lerp * Gdx.graphics.getDeltaTime();
//				splats.cam.position.y += (y - splats.cam.position.y) * lerp * Gdx.graphics.getDeltaTime();
//	//			splats.cam.position.x =+ x+size/2-splats.cam.viewportWidth/2;
//				System.out.println("lkajkilad");
//			}
//			while(x+size/2>splats.cam.position.x+splats.cam.viewportWidth/2){
//				splats.cam.position.x += (x - splats.cam.position.x) * lerp * Gdx.graphics.getDeltaTime();
//				splats.cam.position.y += (y - splats.cam.position.y) * lerp * Gdx.graphics.getDeltaTime();
//	//			splats.cam.position.x = x+splats.cam.viewportWidth/2-size/2;
//				System.out.println("jggfdfggfgf");
//			}
//			while(y-size/2<splats.cam.position.y-splats.cam.viewportHeight/2){
//				splats.cam.position.x += (x - splats.cam.position.x) * lerp * Gdx.graphics.getDeltaTime();
//				splats.cam.position.y += (y - splats.cam.position.y) * lerp * Gdx.graphics.getDeltaTime();
//	//			splats.cam.position.y = y+size/2-splats.cam.viewportHeight/2;
//				System.out.println("ccvx");
//			}
//			while(y+size/2>splats.cam.position.y+splats.cam.viewportHeight/2){
//				splats.cam.position.x += (x - splats.cam.position.x) * lerp * Gdx.graphics.getDeltaTime();
//				splats.cam.position.y += (y - splats.cam.position.y) * lerp * Gdx.graphics.getDeltaTime();
//	//			splats.cam.position.y = y-splats.cam.viewportHeight/2-size/2;
//				System.out.println("ti");
//			}
		}
		
		if(Gdx.input.isTouched() && System.currentTimeMillis() - splats.projectilelast >= 0.25 * 1000){
			Vector3 clickpos = splats.cam.unproject(new Vector3(Gdx.input.getX(),Gdx.input.getY(),0));
//			clickpos.x /= splats.batch.scaleFactor;
//			clickpos.y *= splats.batch.scaleFactor;
			final double projectileangle = Math.atan2(clickpos.y-(y*splats.batch.scaleFactor), clickpos.x-(x*splats.batch.scaleFactor));
			splats.projectiles.add(new Projectile(x, y, splats.projectilesize, projectileangle, 1000));
//			projectiles.add(new Projectile(clientplayer.x, clientplayer.y, projectilesize, Math.atan2((Gdx.graphics.getHeight()-Gdx.input.getY()) - (playerpos) + Gdx.graphics.getHeight()/2), Gdx.input.getX() - (Gdx.graphics.getWidth()/2+clientplayer.x))+clientplayer.rotation+Math.PI/2, 1000));
//			final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
//			exec.schedule(new Runnable(){
//			    @Override
//			    public void run(){
					splats.messenger.sendMessage("s " + projectileangle + " " + (System.currentTimeMillis() - start));//if removed remove unchecked movements too
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
				transformationPlayerPercent += 300*Gdx.graphics.getDeltaTime();
				if(transformationPlayerPercent >= 100){
					transformationPlayerPercent = -1;
					transformationPlayer = null;
					x = this.x;
					y = this.y;
				}
			}
			float lerp2 = 1.2f;
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
		
			if(closest != null){
	//			double angle1 = Math.abs((closestangle - rotation) % (Math.PI*2));
	//		    double angle2 = Math.abs((rotation - closestangle) % (Math.PI*2));
	//		    double difference = angle1 < angle2 ? -angle1 : angle2;
				double difference = getDifferenceBetweenAngles(rotation, closestangle);
	//			splats.cam.rotate((float) -Math.toDegrees((difference * lerp2 * Gdx.graphics.getDeltaTime())));
				rotation += (difference * lerp2 * Gdx.graphics.getDeltaTime());//THIS WORKS FOR SOME REASON TODO FIND OUT WHY
			}
	//		rotation = closestangle;
	//		System.out.println(rotation);
	//		System.out.println(Math.toDegrees(rotation) + " " + Math.toDegrees(rotation+1.5708f));
		}
		
		//save state to old states
		OldState oldState = new OldState(x, y, xspeed, yspeed, System.currentTimeMillis(), left, right);
		oldStates.add(oldState);
		while(oldStates.size() > 90){//1.5 seconds of old state data
			oldStates.remove(0);
		}
		if(moved){
			uncheckedOldStates.add(oldState);
		}
		
		Position position = new Position(x,y,size/2);
		for(Player player: splats.players){
			if(player.collided(position)){
				//collided with player
				Player player1 = player;
				ClientPlayer player2 = this;
			    //Calculate speeds
			    float factor = 1;
	    		float player1xspeed = (2 * (player2.size*factor) * player2.xspeed) / ((size*factor) + player2.size);
			    float player1yspeed = (2 * (player2.size*factor) * player2.yspeed) / ((size*factor) + player2.size);
			    float player2xspeed = (2 * (size*factor) * player1.xspeed) / ((size*factor) + player2.size);
			    float player2yspeed = (2 * (size*factor) * player1.yspeed) / ((size*factor) + player2.size);
			    
			    //set speeds (to make sure that when calculating it is using the master copy)
			    player1.xspeed = player1xspeed;
			    player1.yspeed = player1yspeed;
			    player2.xspeed = player2xspeed;
			    player2.yspeed = player2yspeed;
			    
//			    double dist = player.collideDist(position);//from player to position
			    
			    float xdist = player1.x - player2.x;
				float ydist = player1.y - player2.y;
				double dist = Math.sqrt(Math.pow(xdist, 2) + Math.pow(ydist, 2)); //distance between circles (xdist and ydist and a and b of triangle)
				
				double angle = Math.atan2(ydist, xdist);
				
				double amountofchange = ((size/2 + player2.size/2) - dist) /2;
				
				double newx1 = Math.cos(angle)*amountofchange;
				double newy1 = Math.sin(angle)*amountofchange;

				double newx2 = Math.cos(angle-Math.PI)*amountofchange;
				double newy2 = Math.sin(angle-Math.PI)*amountofchange;
			    
//			    double radialfactor = (size/2 + player2.size/2) / player.collideDist(position);
//
//	    		double singleradialfactor = (radialfactor -1) /2 +1;
//	    		
//	    		double factorpervariable = (singleradialfactor - 1) /2 + 1;//halves the resulting variable

	    		player1.x += newx1;
	    		player1.y += newy1;
	    		player2.x += newx2;
	    		player2.y += newy2;

			}
		}
	}
	
	public double getDotProduct(double x1, double y1, double x2, double y2){
		return x1 * x2 + y1 * y2;
	}
	
	public double getDifferenceBetweenAngles(double firstAngle, double secondAngle){
	        double difference = secondAngle - firstAngle;
	        while (difference < -Math.PI) difference += Math.PI*2;
	        while (difference > Math.PI) difference -= Math.PI*2;
	        return difference;
	 }
}