package com.ajayinkingston.splats;

import java.util.ArrayList;
import java.util.Random;

import com.ajayinkingston.planets.server.Data;
import com.ajayinkingston.planets.server.Entity;
import com.ajayinkingston.planets.server.Main;
import com.ajayinkingston.planets.server.OldState;
import com.ajayinkingston.planets.server.Planet;
import com.ajayinkingston.planets.server.Player;
import com.ajayinkingston.planets.server.Projectile;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.jlcm.prototipo.ClientMessageReceiver;
import com.jlcm.prototipo.WebSocketClientMessenger;

public class Splats extends ApplicationAdapter implements ClientMessageReceiver {
	int device;

	Random random = new Random();

	WebSocketClientMessenger messenger;

	Texture planetimg,planetglow,shadow;
	Texture[] pointImages = new Texture[9];
	Texture[] playerImages = new Texture[8];
	Texture[] playerGlowImages = new Texture[8];

	ScalingSpriteBatch batch;
	ScalingShapeRenderer shapeRenderer;
	OrthographicCamera cam;
	Texture img;

	ClientPlayer clientplayer;
	boolean start;//starts false and when the server tells us it has started, this becomes true, to know when to start calculating

	Arrow arrow;

	float xspeed, yspeed;
	
	int mapsize = 30000;
	
	long projectilelast;
	
	double leftoverdelta; //delta left over after the 40fps
	double fps = 40;//fps to update at
	ArrayList<Update> futureUpdates = new ArrayList<>();
	long aheadUpdates = 0;
	
	Test test;
	
	long testing;
	
	Data data; //class thats stores everything (so that it can be the same on the sever and client)
	
	//ALWAYS USE CLIENT VERSION OF CLASSES IF POSSIBLE (it is a requirement when making a new instance of a class)
	
//	Texture grid;

	public Splats(int device) {// 0 web 1 android 2 iOS 3 desktop
		this.device = device;//this can be removed because there are built in methods for this. TODO remove this
	}

	@Override
	public void create() {
		batch = new ScalingSpriteBatch();// Make
		shapeRenderer = new ScalingShapeRenderer(batch.scaleFactor);
		img = new Texture("badlogic.jpg");
		planetimg = new Texture("planet.png");
		planetglow = new Texture("planetglow.png");
		shadow = new Texture("shadow.png");
		for(int i=0;i<pointImages.length;i++){
			pointImages[i] = new Texture("point"+i+".png");
			pointImages[i].setFilter(TextureFilter.Linear, TextureFilter.Linear); 
		}
		for(int i=0;i<playerImages.length;i++){
			playerImages[i] = new Texture("player"+i+".png");
			playerImages[i].setFilter(TextureFilter.Linear, TextureFilter.Linear); 
		}
		for(int i=0;i<playerGlowImages.length;i++){
			playerGlowImages[i] = new Texture("playerglow"+i+".png");
			playerGlowImages[i].setFilter(TextureFilter.Linear, TextureFilter.Linear); 
		}
//		grid = new Texture("grid.png");
//		img.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		// cam = new OrthographicCamera(Gdx.graphics.getWidth()*50,
		// Gdx.graphics.getHeight()*50);
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
//		cam.position.x -= cam.viewportWidth / 2f;
//		cam.position.y -= cam.viewportHeight / 2f;
//		cam.rotate(90);
//		cam.update();
		
//		test = new Test(this);
		test = new Test(0, 0, 800, 0);
//		test = new Test(0, 0, 0, this);
		
		// arrow = new Arrow(planets[0]);
//		messenger = new WebSocketClientMessenger("ajay.ddns.net", 2492, device, this);
		messenger = new WebSocketClientMessenger("localhost", 2492, device, this);
		
		System.out.println("ASdsadasd");
		
		clientplayer = new ClientPlayer(messenger.getId(), 0, 800, 0, this);//defaults to startmass right now
		
		data = new Data();
	}
	
	@Override
	public void resize(int width, int height) {
//        cam.viewportWidth = width*1.3f;
//        cam.viewportHeight = height*1.3f;
		cam.viewportWidth = width*1f;
        cam.viewportHeight = height*1f;
    }

	@Override
	public void render() {
		if (data.planets.length < 1)
			return;
		// messenger.sendMessage(clientplayer.x + " " + clientplayer.y + " " +
		// clientplayer.xspeed + " " + clientplayer.7yspeed);
		
		System.out.println(clientplayer.start);

		Gdx.graphics.setTitle("" + Gdx.graphics.getFramesPerSecond() + " " + random.nextDouble());
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		shapeRenderer.setProjectionMatrix(cam.combined);

		// clientplayer.x = Gdx.input.getX();
		// clientplayer.y = Gdx.input.getY();

		Gdx.gl.glClearColor(0,0,0f, 1);// Make camera movement smoother
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		
		batch.begin();
//		grid.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
//		batch.draw(grid, 500, 0, cam.viewportWidth, cam.viewportHeight);
		batch.draw(img, 0, 0);
		batch.end();
		
		for (int i = 0; i < data.planets.length; i++) {
			((com.ajayinkingston.splats.Planet) data.planets[i]).renderGlow(this);
		}
//		System.out.println(planets[0].radius);
//		planets[0] = new Planet(0, 0, 25, 2);
//		for(int i=1;i<data.planets.length;i++){
//			planets[i] = new Planet(55555,555555,100, 2);
//		}
//		clientplayer.mass = 120;
//		clientplayer.x = 0;
//		clientplayer.y = 800;
//		clientplayer.xspeed = 0;
//		clientplayer.yspeed = 0;
//		shapeRenderer.scaleFactor = 0.25f;
//		batch.scaleFactor = 0.25f;
//		System.out.println(((Math.sqrt(Math.pow(Math.abs((clientplayer.y) - (planets[0].y)), 2) + Math.pow(Math.abs((clientplayer.x) - (planets[0].x)), 2))) - clientplayer.size/2 - planets[0].radius) / planets[0].radius);
//		data.players.get(0).x = 0;
//		data.players.get(0).y = 600;
//		data.players.get(0).yspeed = 0;
//		data.players.get(0).xspeed = 0;

		double delta = Gdx.graphics.getRawDeltaTime();
		
		clientplayer.render(this);
		
		for (Player player : new ArrayList<Player>(data.players)) {
			((com.ajayinkingston.splats.Player) player).render(this, delta);
		}

		for (int i = 0; i < data.planets.length; i++) {
			((com.ajayinkingston.splats.Planet) data.planets[i]).render(this);
		}
		
		for (Projectile projectile : new ArrayList<>(data.projectiles)) {
			((com.ajayinkingston.splats.ClientProjectile) projectile).render(this);
		}
//		double fulldelta = delta + leftoverdelta;
//		for(double i=fulldelta;i>=1/fps;i-=1/fps){
//			clientplayer.update(this, 1/fps, false);
//		}
		
		double fulldelta = delta + leftoverdelta;
		for(double i=fulldelta;i>=1/fps;i-=1/fps){
			if(start){
				if(aheadUpdates > 0){
					aheadUpdates--;
					continue;
				}
				update(1/fps);
			}
		}
		leftoverdelta = fulldelta%(1/fps);
		int loops = 0;
		for(Update update: new ArrayList<>(futureUpdates)){
			fulldelta = update.delta;
			if(update.includeLeftoverDelta){
				fulldelta = update.delta + leftoverdelta;
			}
			for(double i=fulldelta;i>=1/fps;i-=1/fps){
				update(1/fps);
			}
			if(update.includeLeftoverDelta) leftoverdelta = fulldelta % (1/fps);
			futureUpdates.remove(update);
			loops++;
		}
//		System.out.println("LOOPS FOR FUTUREUPDATES: " + loops);
	}
	
	public void update(double delta) {
		
		//make sure these local variables are updated with the real ones
		
		for (Projectile projectile : new ArrayList<>(data.projectiles)) {
			projectile.update(data, delta);
			if(System.currentTimeMillis() - projectile.start > 4500 || Main.isTouchingPlanet(projectile, Main.getClosestPlanet(projectile, data.planets))){
				data.projectiles.remove(projectile);
			}
		}
		
		clientplayer.checkInput(this, data);
		clientplayer.update(data, delta);
		clientplayer.postUpdate(this, data);
		
//		test.x = clientplayer.x;
//		test.y = clientplayer.y;
//		test.xspeed = clientplayer.xspeed;
//		test.yspeed = clientplayer.yspeed;
//		test.mass = clientplayer.mass;
		
//		if(data.players.size()==1){
//			data.players.add(new Player(3, 0, clientplayer.mass, this));
//			data.players.get(1).x = test.x;
//			data.players.get(1).y = test.y;
//			data.players.get(1).xspeed = test.xspeed;
//			data.players.get(1).yspeed = test.yspeed;
//			
////			data.players.add(new Player(4, 0, clientplayer.mass, this));
////			data.players.get(1).x = test.x;
////			data.players.get(1).y = test.y;
////			data.players.get(1).xspeed = test.xspeed;
////			data.players.get(1).yspeed = test.yspeed;
//
//		}
//		
//		data.players.get(0).x = test.x;
//		data.players.get(0).y = test.y;
//		data.players.get(0).yspeed = test.yspeed;
//		data.players.get(0).xspeed = test.xspeed;
		
//		data.players.get(1).x = test.x;
//		data.players.get(1).y = test.y;
//		data.players.get(1).yspeed = test.yspeed;
//		data.players.get(1).xspeed = test.xspeed;
		
//		test.x = data.players.get(0).x;
//		test.y = data.players.get(0).y;
//		test.yspeed = data.players.get(0).yspeed;
//		test.xspeed = data.players.get(0).xspeed;
//		
//		clientplayer.x = test.x;
//		clientplayer.y = test.y;
//		clientplayer.yspeed = test.yspeed;
//		clientplayer.xspeed = test.xspeed;
		
//		test.update(this, 1/fps);
//		test.update(this, delta);
//		test.update(this, 1/fps, false);
		
		for (Player player : new ArrayList<Player>(data.players)) {
			player.update(data, 1/fps);
		}
		
		//collision detection
		for(Player player2: data.players){//for clientplayer to player
			if(player2.collided(clientplayer)){
				//collided with player
				affectColidedPlayers(clientplayer, player2);
			}
		}
		for(int i=0;i<data.players.size();i++){//for player to player
			for(int s=i+1;s<data.players.size();s++){
				if(data.players.get(s).collided(data.players.get(i))){
					//collided with player
					affectColidedPlayers(data.players.get(i),data.players.get(s));
				}
			}
		}
		
		//projectile collision detection
		for(Projectile projectile: new ArrayList<>(data.projectiles)){
//			for(Player player: data.players){
//				Position projectile1 = new Position(projectile.x,projectile.y,projectile.radius);
//				if(player.collided(projectile1)){
//					affectColideddata.players(player, projectile);
//				}
//			}
			if(projectile.collided(clientplayer)){
				//collided with player
				affectColidedPlayers(clientplayer, projectile);
			}
		}
		
	}
	
	public void affectColidedPlayers(Entity player1, Entity player2){ //once the data.players are collided, this function will deal with them
	    //Calculate speeds
//	    float player1xspeed = (player1.xspeed * (player1.getSize() - player1.getSize()) + (2 * clientplayer.startmass * player2.xspeed)) / (clientplayer.startmass +clientplayer.startmass);
//	    float player1yspeed = (player1.yspeed * (player1.getSize() - player1.getSize()) + (2 * clientplayer.startmass * player2.yspeed)) / (clientplayer.startmass + clientplayer.startmass);
//	    float player2xspeed = (player2.xspeed * (player1.getSize() - player1.getSize()) + (2 * clientplayer.startmass * player1.xspeed)) / (clientplayer.startmass + clientplayer.startmass);  ////https://gamedevelopment.tutsplus.com/tutorials/when-worlds-collide-simulating-circle-circle-collisions--gamedev-769
//	    float player2yspeed = (player2.yspeed * (player1.getSize() - player1.getSize()) + (2 * clientplayer.startmass * player1.yspeed)) / (clientplayer.startmass + clientplayer.startmass);
	    
	    float player1xspeed = player2.xspeed;
	    float player1yspeed = player2.yspeed;
	    float player2xspeed = player1.xspeed;  //https://gamedevelopment.tutsplus.com/tutorials/when-worlds-collide-simulating-circle-circle-collisions--gamedev-769
	    float player2yspeed = player1.yspeed;
	    
	    //set speeds (to make sure that when calculating it is using the master copy)
	    player1.xspeed = player1xspeed;
	    player1.yspeed = player1yspeed;
	    player2.xspeed = player2xspeed;
	    player2.yspeed = player2yspeed;
	    
	    player1.x += player1xspeed * 1/fps;
	    player1.y += player1yspeed * 1/fps;
	    player2.x += player2xspeed * 1/fps;
	    player2.y += player2yspeed * 1/fps;
	    
//	    if(player1 instanceof Projectile){
//			Position projectile1 = new Position(player1.x,player1.y,player1.getRadius());
//	    	if(( player2).collided(projectile1)){
//	    		projectiles.remove(player1);
//	    	}
//	    }else if(player2 instanceof Projectile){
//			Position projectile1 = new Position(player2.x,player2.y,player2.getRadius());
//	    	if(( player1).collided(projectile1)){
//	    		projectiles.remove(player2);
//	    	}
//	    }
	}

	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
	}

	@Override
	public void onMessageRecieved(String message) {
		if (message.startsWith("CONNECTED")) {

			Player player = new com.ajayinkingston.splats.Player(Integer.parseInt(message.split(" ")[1]), Integer.parseInt(message.split(" ")[6]), this);
			player.x = Float.parseFloat(message.split(" ")[2]);
			player.y = Float.parseFloat(message.split(" ")[3]);
			player.xspeed = Float.parseFloat(message.split(" ")[4]);
			player.yspeed = Float.parseFloat(message.split(" ")[5]);

			data.players.add(player);
		} else if (message.startsWith("DISCONNECTED")) {
			data.players.remove(getPlayer(Integer.parseInt(message.split(" ")[1])));
		} else if (message.startsWith("PLANET")) {
			System.out.println("recieved data for a planet");
			Planet[] planetslist = new Planet[data.planets.length + 1];
			for (int i = 0; i < data.planets.length; i++) {
				planetslist[i] = data.planets[i];
			}
			planetslist[data.planets.length] = new com.ajayinkingston.splats.Planet(Float.parseFloat(message.split(" ")[1]), Float.parseFloat(message.split(" ")[2]), Float.parseFloat(message.split(" ")[3]));
			((com.ajayinkingston.splats.Planet) planetslist[data.planets.length]).setupFood(Integer.parseInt(message.split(" ")[4]));
			data.planets = planetslist;
		}else if(message.startsWith("FOOD")){
			String[] messageSplit = message.split(" ");
			
			Planet planet = data.planets[Integer.parseInt(messageSplit[1])];
			
			planet.food[Integer.parseInt(messageSplit[2])] = new Food(Boolean.parseBoolean(message.split(" ")[3]), Float.parseFloat(message.split(" ")[4]), Integer.parseInt(message.split(" ")[5]), random.nextInt(pointImages.length));
			
		}else if(message.startsWith("COLLECT")){
			int id = Integer.parseInt(message.split(" ")[1]);

			if(id == messenger.getId()){
				if(clientplayer.massChangeAni){
					clientplayer.mass = clientplayer.aniStartMass + clientplayer.aniMassChange;
				}
				clientplayer.aniMassChange = (int) (Integer.parseInt(message.split(" ")[2]) );
				clientplayer.aniStartMass = clientplayer.mass;
				clientplayer.massChangeAni = true;
				clientplayer.aniMass = 0;
			}else{
				Player player = getPlayer(id);
				
				player.mass += Integer.parseInt(message.split(" ")[2]);
				
//				if(player.massChangeAni){
//					player.mass = player.aniStartMass + player.aniMassChange;
//				}
//				player.aniMassChange = (int) (Integer.parseInt(message.split(" ")[2]) * 1.5);
//				player.aniStartMass = clientplayer.size;
//				player.massChangeAni = true;
//				player.aniMass = 0;
			}
			
		} else if (message.startsWith("POSCHANGE")) {
//			clientplayer.y = Float.parseFloat(message.split(" ")[2]);
		} else if (message.startsWith("START")) {
			start = true;
		} else if (message.startsWith("CHECK")) {
//			if(true) return;
			int id = Integer.parseInt(message.split(" ")[1]);
			float x = Float.parseFloat(message.split(" ")[2]);
			float y = Float.parseFloat(message.split(" ")[3]);
			float xspeed = Float.parseFloat(message.split(" ")[4]);
			float yspeed = Float.parseFloat(message.split(" ")[5]);
			long frame = Long.parseLong(message.split(" ")[6]);

			if (id == messenger.getId()) {
				System.out.println(frame + " real");
//				long currentFrame = clientplayer.frames;
//				if(frame > currentFrame + futureUpdates.size() - aheadUpdates){
//					for(long i=currentFrame + futureUpdates.size() - aheadUpdates;frame>i;i++){
//						System.out.println("seriously -_-");
//						futureUpdates.add(new Update(1/fps, false));
//					}
//				}
////				if(frame < currentFrame + futureUpdates.size() - aheadUpdates){
////					aheadUpdates = (currentFrame + futureUpdates.size() - aheadUpdates) - frame;
////				}
//				
//				if(data.players.isEmpty()){
//					data.players.add(new com.ajayinkingston.splats.Player(3, clientplayer.mass, this));
//					data.players.get(0).x = clientplayer.x;
//					data.players.get(0).y = clientplayer.y;
//					data.players.get(0).xspeed = clientplayer.xspeed;
//					data.players.get(0).yspeed = clientplayer.yspeed;
//
//				}
//				
//				data.players.get(0).x = x;
//				data.players.get(0).y = y;
//				data.players.get(0).yspeed = yspeed;
//				data.players.get(0).xspeed = xspeed;
//				data.players.get(0).mass = clientplayer.mass;
//				
////				testing = System.currentTimeMillis();
//				
//				if(System.currentTimeMillis() - testing <= 2500){
////					clientplayer.x = x;
////					clientplayer.y = y;
////					clientplayer.yspeed = yspeed;
////					clientplayer.xspeed = xspeed;
//				}
				
//				test.x = x;
//				test.y = y;
//				test.yspeed = yspeed;
//				test.xspeed = xspeed;
//				
//				data.players.get(0).x = clientplayer.xy;
//				data.players.get(0).y = clientplayer.y;
//				data.players.get(0).yspeed = clientplayer.yspeed;
//				data.players.get(0).xspeed = clientplaer.xspeed;
				
//				if(clientplayer.frames < 50){
//					clientplayer.x = x;
//					clientplayer.y = y;
//					clientplayer.xspeed = xspeed;
//					clientplayer.yspeed = yspeed;
//				}

//				ArrayList<OldState> oldStates = new ArrayList<>(clientplayer.oldStates);
//				OldState originalState = getOldStateAtFrame(oldStates, frame, clientplayer);
//				int originalStateIndex = oldStates.indexOf(originalState);
//				if (originalStateIndex == -1)
//					originalStateIndex = 0;
//				OldState beforeState = originalState;
//				if (originalStateIndex > 0)
//					beforeState = oldStates.get(originalStateIndex - 1);
//				OldState afterState = originalState;
//				if (originalStateIndex < oldStates.size() - 1)
//					afterState = oldStates.get(originalStateIndex + 1);
//
//				// int smallerx
//				// if(smallerstate.x)
//
////				if (!isInBetween(x, beforeState.x, afterState.x) || !isInBetween(y, beforeState.y, afterState.y)
////						|| !isInBetween(xspeed, beforeState.xspeed, afterState.xspeed)
////						|| !isInBetween(yspeed, beforeState.yspeed, afterState.yspeed)) {
//				OldState currentState = new OldState(x, y, xspeed, yspeed, -1, false, false);
//				if(isStateInBetween(currentState, beforeState, originalState) && isStateInBetween(currentState, originalState, afterState) &&
//						isStateInBetween(currentState, beforeState, afterState)){//TODO TEST THIS AND IMPLEMENT IT EVERYWHERE ELSE TOO
//
//					// reset as if we were there (like server)
////					long currentFrame = clientplayer.frames;
//
//					int amountremoved = oldStates.size() - (originalStateIndex + 1);
//					ArrayList<OldState> oldOldStates = new ArrayList<>();
//					for (int i = 0; i < amountremoved; i++) {// remove all of the future ones
//						oldOldStates.add(oldStates.get(originalStateIndex));
//						oldStates.remove(originalStateIndex);
//					}
//
//				//	 make now like that old state
//					if(clientplayer.transformationPlayer != null && clientplayer.transformationPlayerPercent < 100 && clientplayer.transformationPlayerPercent >= 0){
//						Player newTransformationPlayer = new Player(-1, clientplayer.mass, this);
//						newTransformationPlayer.x = clientplayer.transformationPlayer.x + ((clientplayer.x - clientplayer.transformationPlayer.x) * (clientplayer.transformationPlayerPercent/100));
//						newTransformationPlayer.y = clientplayer.transformationPlayer.y + ((clientplayer.y - clientplayer.transformationPlayer.y) * (clientplayer.transformationPlayerPercent/100));
//						newTransformationPlayer.xspeed = xspeed;
//						newTransformationPlayer.yspeed = yspeed;
//						clientplayer.transformationPlayer = newTransformationPlayer;
//					}else{
//						clientplayer.transformationPlayer = new Player(-1, clientplayer.mass, this);
//						clientplayer.transformationPlayer.x = clientplayer.x;
//						clientplayer.transformationPlayer.y = clientplayer.y;
//						clientplayer.transformationPlayer.xspeed = xspeed;
//						clientplayer.transformationPlayer.yspeed = yspeed;
//					}
//					clientplayer.transformationPlayerPercent = 0;
//					clientplayer.transformationPlayer.left = clientplayer.left;
//					clientplayer.transformationPlayer.right = clientplayer.right;
//					clientplayer.x = x;
//					clientplayer.y = y;
//					clientplayer.xspeed = xspeed;
//					clientplayer.yspeed = yspeed;
//
//					clientplayer.oldStates = oldStates;
//					for (int i = 0; i < amountremoved; i++) {// remove all of the future ones
//						clientplayer.left = oldOldStates.get(i).left;
//						clientplayer.right = oldOldStates.get(i).right;
//						if(oldOldStates.get(i).shot){
//							clientplayer.xspeed -= (float) (Math.cos(oldOldStates.get(i).projectileAngle) * projectileSpeedChange);
//							clientplayer.yspeed -= (float) (Math.sin(oldOldStates.get(i).projectileAngle) * projectileSpeedChange);
//						}
//
////						if(i!=0){
////							delta = (oldOldStates.get(i).when - oldOldStates.get(i-1).when) / 1000d;
////						}
//						clientplayer.update(this, 1/fps, true);
//					}
//				}
				
				
			} else {
				if(true){
					System.err.println("You need to fix this remember???");
					return;
				}
//				Player player = getPlayer(id);
//				ArrayList<OldState> oldStates = new ArrayList<>(player.oldStates);
//				OldState originalState = getOldStateAtFrame(oldStates, frame, player);
//				int originalStateIndex = oldStates.indexOf(originalState);
//				if (originalStateIndex == -1)
//					originalStateIndex = 0;
//				OldState beforeState = originalState;
//				if (originalStateIndex > 0)
//					beforeState = oldStates.get(originalStateIndex - 1);
//				OldState afterState = originalState;
//				if (originalStateIndex < oldStates.size() - 1)
//					afterState = oldStates.get(originalStateIndex + 1);
//
//				// int smallerx
//				// if(smallerstate.x)
//
////				if (!isInBetween(x, beforeState.x, afterState.x) || !isInBetween(y, beforeState.y, afterState.y)
////						|| !isInBetween(xspeed, beforeState.xspeed, afterState.xspeed)
////						|| !isInBetween(yspeed, beforeState.yspeed, afterState.yspeed)) {
//				OldState currentState = new OldState(x, y, xspeed, yspeed, -1, false, false, false, 0);
//				if(isStateInBetween(currentState, beforeState, originalState) && isStateInBetween(currentState, originalState, afterState) &&
//						isStateInBetween(currentState, beforeState, afterState)){//TODO TEST THIS AND IMPLEMENT IT EVERYWHERE ELSE TOO
//
//					// reset as if we were there (like server)
//					long currentFrame = player.frames;
//
//					int amountremoved = oldStates.size() - (originalStateIndex + 1);
//					ArrayList<OldState> oldOldStates = new ArrayList<>();
//					for (int i = 0; i < amountremoved; i++) {// remove all of the future ones
//						oldOldStates.add(oldStates.get(originalStateIndex));
//						oldStates.remove(originalStateIndex);
//					}
//
//					// make now like that old state
//					if(player.transformationPlayer != null && player.transformationPlayerPercent < 100 && player.transformationPlayerPercent >= 0){
//						Player newTransformationPlayer = new Player(-1, player.mass, this);
//						newTransformationPlayer.x = player.transformationPlayer.x + ((player.x - player.transformationPlayer.x) * (player.transformationPlayerPercent/100));
//						newTransformationPlayer.y = player.transformationPlayer.y + ((player.y - player.transformationPlayer.y) * (player.transformationPlayerPercent/100));
//						newTransformationPlayer.xspeed = xspeed;
//						newTransformationPlayer.yspeed = yspeed;
//						player.transformationPlayer = newTransformationPlayer;
//					}else{
//						player.transformationPlayer = new Player(-1, player.mass, this);
//						player.transformationPlayer.x = player.x;
//						player.transformationPlayer.y = player.y;
//						player.transformationPlayer.xspeed = xspeed;
//						player.transformationPlayer.yspeed = yspeed;
//					}
//					player.transformationPlayerPercent = 0;
//					player.transformationPlayer.left = player.left;
//					player.transformationPlayer.right = player.right;
//					player.x = x;
//					player.y = y;
//					player.xspeed = xspeed;
//					player.yspeed = yspeed;
//
//					player.oldStates = oldStates;
//					for (int i = 0; i < amountremoved; i++) {// remove all of the future ones
//						player.left = oldOldStates.get(i).left;
//						player.right = oldOldStates.get(i).right;
//						if(oldOldStates.get(i).shot){
//							player.xspeed -= (float) (Math.cos(oldOldStates.get(i).projectileAngle) * projectileSpeedChange);
//							player.yspeed -= (float) (Math.sin(oldOldStates.get(i).projectileAngle) * projectileSpeedChange);
//						}
//
////						if(i!=0){
////							delta = (oldOldStates.get(i).when - oldOldStates.get(i-1).when) / 1000d;
////						}
//						player.update(this, 1/fps, true);
//					}
//				}
			}
		} else if (message.startsWith("s")) {
//			if(true) return;
			if(true){
				System.err.println("You need to fix this remember???");
				return;
			}
//			while (getPlayer(Integer.parseInt(message.split(" ")[1])) == null) {
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {// wait for it to be set
//					e.printStackTrace();
//				}
//			}
//			//when other player shoots
//			
//			Player player = getPlayer(Integer.parseInt(message.split(" ")[1]));
//			float projectileAngle = Float.parseFloat(message.split(" ")[3]);
//			// double delta = Double.parseDouble(message.split(" ")[1]);
//			long currentFrame = player.frames;
//			long frame = Long.parseLong(message.split(" ")[4]);// when the action happened
//			
//			if (frame > currentFrame ) {
//				// ok something went wrong here
//				// fix it
//				// player.start = (long) (currentTime - time);
//				frame = currentFrame;
//				System.err.println("HOUSTON, WE HAVE A HACKED SEVER (server is in the future, or am I in the past?)");
//			}
//			
//			OldState originalState = Main.getOldStateAtFrame(new ArrayList<>(player.oldStates), frame, player);
//			
//			//make now like that old state
//			player.x = originalState.x;
//			player.y = originalState.y;
//			player.xspeed = originalState.xspeed;
//			player.yspeed = originalState.yspeed;
//	
//			
//			//count the difference
//			int amountremoved = player.oldStates.size() - (player.oldStates.indexOf(originalState) + 1);
//			int index = player.oldStates.indexOf(originalState);
//			if(index==-1) index = 0;
//			ArrayList<OldState> oldOldStates = new ArrayList<>();
//			for(int i=0;i<amountremoved;i++){//remove all of the future ones
//				oldOldStates.add(player.oldStates.get(index));
//				player.oldStates.remove(index);
//			}
//			//insert new data
//			player.shooting = true;
//			player.projectileAngle = projectileAngle;
//			//call player.update however many missed frames there were
//			for(int i=0;i<amountremoved;i++){//remove all of the future ones
//				player.left = oldOldStates.get(i).left;
//				player.right = oldOldStates.get(i).right;
//				if(oldOldStates.get(i).shot){
//					player.xspeed -= (float) (Math.cos(oldOldStates.get(i).projectileAngle) * projectileSpeedChange);
//					player.yspeed -= (float) (Math.sin(oldOldStates.get(i).projectileAngle) * projectileSpeedChange);
//				}
//				
//				player.update(this, 1/fps, true);
//			}
		}else if(message.startsWith("r")){
			clientplayer.uncheckedMovements--;
		}else {
			while (getPlayer(Integer.parseInt(message.split(" ")[0])) == null) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {// wait for it to be set
					e.printStackTrace();
				}
			}//TODO MAKE THIS LIKE SERVER
			Player player = getPlayer(Integer.parseInt(message.split(" ")[0]));
			message = message.replaceFirst(Integer.parseInt(message.split(" ")[0]) + " ", "");
			boolean disable = false;
			if (message.startsWith("d")) {
				disable = true;
				message = message.substring(1);
			}
			int direction = Integer.parseInt(message.split(" ")[0]);
			// double delta = Double.parseDouble(message.split(" ")[1]);
			long currentFrame = player.frames;
			long frame = Long.parseLong(message.split(" ")[1]);// when the action
																// happened
			if (frame > currentFrame ) {
				// ok something went wrong here
				// fix it
				// player.start = (long) (currentTime - time);
				frame = currentFrame ;
				System.err.println("HOUSTON, WE HAVE A HACKED SEVER (server is in the future, or am I in the past?)");
			}
			
			OldState originalState = Main.getOldStateAtFrame(new ArrayList<>(player.oldStates), frame);
			
			//make now like that old state
			player.x = originalState.x;
			player.y = originalState.y;
			player.xspeed = originalState.xspeed;
			player.yspeed = originalState.yspeed;
	
			
			//count the difference
			int amountremoved = player.oldStates.size() - (player.oldStates.indexOf(originalState) + 1);
			int index = player.oldStates.indexOf(originalState);
			if(index==-1) index = 0;
			ArrayList<OldState> oldOldStates = new ArrayList<>();
			for(int i=0;i<amountremoved;i++){//remove all of the future ones
				oldOldStates.add(player.oldStates.get(index));
				player.oldStates.remove(index);
			}
			//insert new data
			boolean rightchange = false;
			boolean leftchange = false;
			if (direction > 0) {
				player.right = true;
				if (disable) {
					player.right = false;
				}
				rightchange = true;
			} else {
				player.left = true;
				if (disable) {
					player.left = false;
				}
				leftchange = true;
			}
			//call player.update however many missed frames there were
			for(int i=0;i<amountremoved;i++){//remove all of the future ones
				if(!leftchange){
					player.left = oldOldStates.get(i).left;
				}
				if(!rightchange){
					player.right = oldOldStates.get(i).right;
				}
				if(oldOldStates.get(i).shot){
					player.xspeed -= (float) (Math.cos(oldOldStates.get(i).projectileAngle) * Data.projectileSpeedChange);
					player.yspeed -= (float) (Math.sin(oldOldStates.get(i).projectileAngle) * Data.projectileSpeedChange);
				}
				
				double delta = 1/60.0;
//				if(i!=0){
//					delta = (oldOldStates.get(i).when - oldOldStates.get(i-1).when) / 1000d;
//				}
				player.update(data, delta);
			}
			
//			if (!disable) {// ignore delta for now when disable true
//				player.xspeed += Math.cos(getClosestAngle(player) + Math.toRadians(direction * 90)) * clientplayer.speed
//						* timeDifference;
//				player.yspeed += Math.sin(getClosestAngle(player) + Math.toRadians(direction * 90)) * clientplayer.speed
//						* timeDifference;
//			} else {
//				// compensate for ping
//				player.xspeed -= Math.cos(getClosestAngle(player) + Math.toRadians(direction * 90)) * clientplayer.speed
//						* timeDifference;
//				player.yspeed -= Math.sin(getClosestAngle(player) + Math.toRadians(direction * 90)) * clientplayer.speed
//						* timeDifference;
//			}
		}
	}

	@Override
	public void onConnect(long time) {
		System.out.println("Connected!");
		clientplayer.start = time;
	}

	// utility methods
	
	public double getAngleFromPlanet(Player player, Planet planet) {
		double angle = Math.atan2((player.y) - (planet.y), (player.x) - (planet.x));
		double closestangle = angle - Math.PI;
		return closestangle;
	}
	
	public Player getPlayer(int id) {
		for (Player player : data.players) {
			if (player.id == id) {
				return player;
			}
		}
		System.out.println("Player doesn't exist when getting player");
		return null;
	}
}
