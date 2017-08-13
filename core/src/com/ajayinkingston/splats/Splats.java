package com.ajayinkingston.splats;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
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

	ArrayList<Player> players = new ArrayList<>();

	Arrow arrow;

	float xspeed, yspeed;

	// TODO: OBJECTIVE: MAKE SINGLEPLAYER GAME AND THEN TURN MULTIPLAYER (EASIER FOR TESTING)

	Planet[] planets = new Planet[0];

	ArrayList<Projectile> projectiles = new ArrayList<>();// TODO: MAKE THEM DESPAWN

	long projectilelast;
	int projectilesize = 5;
	float projectileSpeedChange = 250;
	float projectileSpeed = 1000;

	int mapsize = 30000;
	
	double leftoverdelta; //delta left over after the 40fps
	double futureleftoverdelta; //first this variable is set, then leftoverdelta becomes this by the end of the frame
	double fps = 40;//fps to update at
	ArrayList<Update> futureUpdates = new ArrayList<>();
	
	Test test;
	
	long testing;

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
		}
		for(int i=0;i<playerImages.length;i++){
			playerImages[i] = new Texture("player"+i+".png");
		}
		for(int i=0;i<playerGlowImages.length;i++){
			playerGlowImages[i] = new Texture("playerglow"+i+".png");
		}
		// cam = new OrthographicCamera(Gdx.graphics.getWidth()*50,
		// Gdx.graphics.getHeight()*50);
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
//		cam.position.x -= cam.viewportWidth / 2f;
//		cam.position.y -= cam.viewportHeight / 2f;
		cam.rotate(90);
		cam.update();
		
		clientplayer = new ClientPlayer(this);
		
//		test = new Test(this);
		test = new Test(0, 0, 0, 0, 0);
//		test = new Test(0, 0, 0, this);
		
		// arrow = new Arrow(planets[0]);
//		messenger = new WebSocketClientMessenger("ajay.ddns.net", 2492, device, this);
		messenger = new WebSocketClientMessenger("localhost", 2492, device, this);

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
		if (Gdx.graphics.getRawDeltaTime() > 0.1) {
			Gdx.graphics.setTitle("UNDER 10 FPS");
			return;
		}
		if (planets.length < 1)
			return;
		// messenger.sendMessage(clientplayer.x + " " + clientplayer.y + " " +
		// clientplayer.xspeed + " " + clientplayer.yspeed);
		
		System.out.println(clientplayer.start);

		Gdx.graphics.setTitle("" + Gdx.graphics.getFramesPerSecond() + " " + random.nextDouble());
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		shapeRenderer.setProjectionMatrix(cam.combined);

		// clientplayer.x = Gdx.input.getX();
		// clientplayer.y = Gdx.input.getY();

		Gdx.gl.glClearColor(0,0,0f, 1);// Make camera movement smoother
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
		
		for (int i = 0; i < planets.length; i++) {
			planets[i].renderGlow(this);
		}
//		System.out.println(planets[0].radius);
//		planets[0] = new Planet(0, 0, 25, 2);
//		for(int i=1;i<planets.length;i++){
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
//		players.get(0).x = 0;
//		players.get(0).y = 600;
//		players.get(0).yspeed = 0;
//		players.get(0).xspeed = 0;

		double delta = Gdx.graphics.getRawDeltaTime();
		
		clientplayer.render(this);
		
		for (Player player : new ArrayList<Player>(players)) {
			player.render(this, delta);
		}

		for (int i = 0; i < planets.length; i++) {
			planets[i].render(this);
		}
		
		for (Projectile projectile : new ArrayList<>(projectiles)) {
			projectile.render(this, shapeRenderer);
			if(System.currentTimeMillis() - projectile.start > 4500 || isTouchingPlanet(projectile, getClosestPlanet(projectile))){
				projectiles.remove(projectile);
			}
		}
//		double fulldelta = delta + leftoverdelta;
//		for(double i=fulldelta;i>=1/fps;i-=1/fps){
//			clientplayer.update(this, 1/fps, false);
//		}

		update(delta, true);
		for(Update update: new ArrayList<>(futureUpdates)){
			update(update.delta, update.includeLeftoverDelta);
			futureUpdates.remove(update);
		}
	}
	
	public void update(double delta, boolean includeLeftoverDelta) {
		double fulldelta = delta + leftoverdelta;
		if(!includeLeftoverDelta) fulldelta = delta;
		
		for(double i=fulldelta;i>=1/fps;i-=1/fps){
			clientplayer.update(this, 1/fps, false);
		}
		if(includeLeftoverDelta) futureleftoverdelta = fulldelta%(1/fps);
		
//		test.x = clientplayer.x;
//		test.y = clientplayer.y;
//		test.xspeed = clientplayer.xspeed;
//		test.yspeed = clientplayer.yspeed;
//		test.mass = clientplayer.mass;
		
//		if(players.size()==1){
//			players.add(new Player(3, 0, clientplayer.mass, this));
//			players.get(1).x = test.x;
//			players.get(1).y = test.y;
//			players.get(1).xspeed = test.xspeed;
//			players.get(1).yspeed = test.yspeed;
//			
////			players.add(new Player(4, 0, clientplayer.mass, this));
////			players.get(1).x = test.x;
////			players.get(1).y = test.y;
////			players.get(1).xspeed = test.xspeed;
////			players.get(1).yspeed = test.yspeed;
//
//		}
//		
//		players.get(0).x = test.x;
//		players.get(0).y = test.y;
//		players.get(0).yspeed = test.yspeed;
//		players.get(0).xspeed = test.xspeed;
		
//		players.get(1).x = test.x;
//		players.get(1).y = test.y;
//		players.get(1).yspeed = test.yspeed;
//		players.get(1).xspeed = test.xspeed;
		
//		test.x = players.get(0).x;
//		test.y = players.get(0).y;
//		test.yspeed = players.get(0).yspeed;
//		test.xspeed = players.get(0).xspeed;
//		
//		clientplayer.x = test.x;
//		clientplayer.y = test.y;
//		clientplayer.yspeed = test.yspeed;
//		clientplayer.xspeed = test.xspeed;
		
//		for(double i=fulldelta;i>=1/fps;i-=1/fps){
////			test.update(this, 1/fps);
//			test.update(this, 1/fps);
////			test.update(this, 1/fps, false);
//		}
		
		for (Player player : new ArrayList<Player>(players)) {
			for(double i=fulldelta;i>=1/fps;i-=1/fps){
				player.update(this, 1/fps, false);
			}
//			if(includeLeftoverDelta) futureleftoverdelta = fulldelta;
		}
		
		//collision detection
		Position position = new Position(clientplayer.x,clientplayer.y,clientplayer.getRadius());
//		for(Player player2: players){//for clientplayer to player
//			if(player2.collided(position)){
//				//collided with player
//				affectColidedPlayers(clientplayer, player2);
//			}
//		}
//		for(int i=0;i<players.size();i++){//for player to player
//			Position player1 = new Position(players.get(i).x, players.get(i).y, players.get(i).getRadius());
//			for(int s=i+1;s<players.size();s++){
//				if(players.get(s).collided(player1)){
//					//collided with player
//					affectColidedPlayers(players.get(i),players.get(s));
//				}
//			}
//		}
		
		//projectile collision detection
		Position clientplayerposition = new Position(clientplayer.x,clientplayer.y,clientplayer.getRadius());
		for(Projectile projectile: new ArrayList<>(projectiles)){
			for(Player player: players){
				Position projectile1 = new Position(projectile.x,projectile.y,projectile.radius);
				if(player.collided(projectile1)){
					affectColidedPlayers(player, projectile);
				}
			}
			if(projectile.collided(clientplayerposition)){
				//collided with player
				affectColidedPlayers(clientplayer, projectile);
			}
		}
		
		if(includeLeftoverDelta) leftoverdelta = futureleftoverdelta;
	}
	
	public void affectColidedPlayers(Entity player1, Entity player2){//once the players are collided, this function will deal with them
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
	    
	    player1.x += player1xspeed * Gdx.graphics.getRawDeltaTime();
	    player1.y += player1yspeed * Gdx.graphics.getRawDeltaTime();
	    player2.x += player2xspeed * Gdx.graphics.getRawDeltaTime();
	    player2.y += player2yspeed * Gdx.graphics.getRawDeltaTime();
	}

	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
	}

	public Player getPlayer(int id) {
		for (Player player : players) {
			if (player.id == id) {
				return player;
			}
		}
		System.out.println("Player doesn't exist when getting player");
		return null;
	}
	
	@Override
	public void onMessageRecieved(String message) {
		if (message.startsWith("CONNECTED")) {
			System.out.println("sdsdsadsda");
			Player player = new Player(Integer.parseInt(message.split(" ")[1]), Integer.parseInt(message.split(" ")[6]), this);
			player.x = Float.parseFloat(message.split(" ")[2]);
			player.y = Float.parseFloat(message.split(" ")[3]);
			player.xspeed = Float.parseFloat(message.split(" ")[4]);
			player.yspeed = Float.parseFloat(message.split(" ")[5]);

			players.add(player);
		} else if (message.startsWith("DISCONNECTED")) {
			players.remove(getPlayer(Integer.parseInt(message.split(" ")[1])));
		} else if (message.startsWith("PLANET")) {
			Planet[] planetslist = new Planet[planets.length + 1];
			for (int i = 0; i < planets.length; i++) {
				planetslist[i] = planets[i];
			}
			planetslist[planets.length] = new Planet(Float.parseFloat(message.split(" ")[1]),
					Float.parseFloat(message.split(" ")[2]), Float.parseFloat(message.split(" ")[3]), Integer.parseInt(message.split(" ")[4]));
			planets = planetslist;
		}else if(message.startsWith("FOOD")){
			String[] messageSplit = message.split(" ");
			
			Planet planet = planets[Integer.parseInt(messageSplit[1])];
			
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
			clientplayer.y = Float.parseFloat(message.split(" ")[2]);
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
				long currentFrame = clientplayer.frames;
				if(frame > currentFrame){
					for(long i=currentFrame;frame>i;i++){
						System.out.println("seriously -_-");
						futureUpdates.add(new Update(1/fps, false));
					}
				}
				
				if(players.isEmpty()){
					players.add(new Player(3, clientplayer.mass, this));
					players.get(0).x = clientplayer.x;
					players.get(0).y = clientplayer.y;
					players.get(0).xspeed = clientplayer.xspeed;
					players.get(0).yspeed = clientplayer.yspeed;

				}
				
				players.get(0).x = x;
				players.get(0).y = y;
				players.get(0).yspeed = yspeed;
				players.get(0).xspeed = xspeed;
				players.get(0).mass = clientplayer.mass;
				
//				testing = System.currentTimeMillis();
				
				if(System.currentTimeMillis() - testing <= 2500){
					clientplayer.x = x;
					clientplayer.y = y;
					clientplayer.yspeed = yspeed;
					clientplayer.xspeed = xspeed;
				}
				
//				test.x = x;
//				test.y = y;
//				test.yspeed = yspeed;
//				test.xspeed = xspeed;
//				
//				players.get(0).x = clientplayer.xy;
//				players.get(0).y = clientplayer.y;
//				players.get(0).yspeed = clientplayer.yspeed;
//				players.get(0).xspeed = clientplaer.xspeed;
				


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
				Player player = getPlayer(id);
				ArrayList<OldState> oldStates = new ArrayList<>(player.oldStates);
				OldState originalState = getOldStateAtFrame(oldStates, frame, player);
				int originalStateIndex = oldStates.indexOf(originalState);
				if (originalStateIndex == -1)
					originalStateIndex = 0;
				OldState beforeState = originalState;
				if (originalStateIndex > 0)
					beforeState = oldStates.get(originalStateIndex - 1);
				OldState afterState = originalState;
				if (originalStateIndex < oldStates.size() - 1)
					afterState = oldStates.get(originalStateIndex + 1);

				// int smallerx
				// if(smallerstate.x)

//				if (!isInBetween(x, beforeState.x, afterState.x) || !isInBetween(y, beforeState.y, afterState.y)
//						|| !isInBetween(xspeed, beforeState.xspeed, afterState.xspeed)
//						|| !isInBetween(yspeed, beforeState.yspeed, afterState.yspeed)) {
				OldState currentState = new OldState(x, y, xspeed, yspeed, -1, false, false);
				if(isStateInBetween(currentState, beforeState, originalState) && isStateInBetween(currentState, originalState, afterState) &&
						isStateInBetween(currentState, beforeState, afterState)){//TODO TEST THIS AND IMPLEMENT IT EVERYWHERE ELSE TOO

					// reset as if we were there (like server)
					long currentFrame = player.frames;

					int amountremoved = oldStates.size() - (originalStateIndex + 1);
					ArrayList<OldState> oldOldStates = new ArrayList<>();
					for (int i = 0; i < amountremoved; i++) {// remove all of the future ones
						oldOldStates.add(oldStates.get(originalStateIndex));
						oldStates.remove(originalStateIndex);
					}

					// make now like that old state
					if(player.transformationPlayer != null && player.transformationPlayerPercent < 100 && player.transformationPlayerPercent >= 0){
						Player newTransformationPlayer = new Player(-1, player.mass, this);
						newTransformationPlayer.x = player.transformationPlayer.x + ((player.x - player.transformationPlayer.x) * (player.transformationPlayerPercent/100));
						newTransformationPlayer.y = player.transformationPlayer.y + ((player.y - player.transformationPlayer.y) * (player.transformationPlayerPercent/100));
						newTransformationPlayer.xspeed = xspeed;
						newTransformationPlayer.yspeed = yspeed;
						player.transformationPlayer = newTransformationPlayer;
					}else{
						player.transformationPlayer = new Player(-1, player.mass, this);
						player.transformationPlayer.x = player.x;
						player.transformationPlayer.y = player.y;
						player.transformationPlayer.xspeed = xspeed;
						player.transformationPlayer.yspeed = yspeed;
					}
					player.transformationPlayerPercent = 0;
					player.transformationPlayer.left = player.left;
					player.transformationPlayer.right = player.right;
					player.x = x;
					player.y = y;
					player.xspeed = xspeed;
					player.yspeed = yspeed;

					player.oldStates = oldStates;
					for (int i = 0; i < amountremoved; i++) {// remove all of the future ones
						player.left = oldOldStates.get(i).left;
						player.right = oldOldStates.get(i).right;
						if(oldOldStates.get(i).shot){
							player.xspeed -= (float) (Math.cos(oldOldStates.get(i).projectileAngle) * projectileSpeedChange);
							player.yspeed -= (float) (Math.sin(oldOldStates.get(i).projectileAngle) * projectileSpeedChange);
						}

//						if(i!=0){
//							delta = (oldOldStates.get(i).when - oldOldStates.get(i-1).when) / 1000d;
//						}
						player.update(this, 1/fps, true);
					}
				}
			}
		} else if (message.startsWith("s")) {
//			if(true) return;
			while (getPlayer(Integer.parseInt(message.split(" ")[1])) == null) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {// wait for it to be set
					e.printStackTrace();
				}
			}
			//when other player shoots
			
			Player player = getPlayer(Integer.parseInt(message.split(" ")[1]));
			float projectileAngle = Float.parseFloat(message.split(" ")[3]);
			// double delta = Double.parseDouble(message.split(" ")[1]);
			long currentFrame = player.frames;
			long frame = Long.parseLong(message.split(" ")[4]);// when the action happened
			
			if (frame > currentFrame ) {
				// ok something went wrong here
				// fix it
				// player.start = (long) (currentTime - time);
				frame = currentFrame;
				System.err.println("HOUSTON, WE HAVE A HACKED SEVER (server is in the future, or am I in the past?)");
			}
			
			OldState originalState = getOldStateAtFrame(new ArrayList<>(player.oldStates), frame, player);
			
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
			player.shooting = true;
			player.projectileAngle = projectileAngle;
			//call player.update however many missed frames there were
			for(int i=0;i<amountremoved;i++){//remove all of the future ones
				player.left = oldOldStates.get(i).left;
				player.right = oldOldStates.get(i).right;
				if(oldOldStates.get(i).shot){
					player.xspeed -= (float) (Math.cos(oldOldStates.get(i).projectileAngle) * projectileSpeedChange);
					player.yspeed -= (float) (Math.sin(oldOldStates.get(i).projectileAngle) * projectileSpeedChange);
				}
				
				player.update(this, 1/fps, true);
			}
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
			
			OldState originalState = getOldStateAtFrame(new ArrayList<>(player.oldStates), frame, player);
			
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
					player.xspeed -= (float) (Math.cos(oldOldStates.get(i).projectileAngle) * projectileSpeedChange);
					player.yspeed -= (float) (Math.sin(oldOldStates.get(i).projectileAngle) * projectileSpeedChange);
				}
				
				double delta = 1/60.0;
//				if(i!=0){
//					delta = (oldOldStates.get(i).when - oldOldStates.get(i-1).when) / 1000d;
//				}
				player.update(this, delta, true);
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
		clientplayer.start = time;
	}

	// utility methods

	public boolean isInBetween(float n, float n1, float n2) {
		if (n1 > n2) {
			float n3 = n1;
			n1 = n2;
			n2 = n3;
		}
		return n >= n1 && n <= n2;
	}
	
	public boolean isStateInBetween(OldState state1, OldState state2, OldState state3){
		return (!isInBetween(state1.x, state2.x, state3.x) || !isInBetween(state1.y, state2.y, state3.y)
				|| !isInBetween(state1.xspeed, state2.xspeed, state3.xspeed)
				|| !isInBetween(state1.yspeed, state2.yspeed, state3.yspeed));
	}

	public OldState getOldStateAtFrame(ArrayList<OldState> oldStates, long frame, Entity player) {
		OldState atFrame = null;
		oldStates = new ArrayList<>(oldStates);
		for (OldState oldState : oldStates) {
			if(oldState.frame == frame) atFrame = oldState;
		}
		if(atFrame == null) atFrame = oldStates.get(oldStates.size()-1);
		return atFrame;
	}
	
	/*public OldState getOldStateAtTime(ArrayList<OldState> oldStates, long time, Player player) {
		long smallest = System.currentTimeMillis();
		OldState smallestOldState = null;
		oldStates = new ArrayList<>(oldStates);
		for (OldState oldState : oldStates) {
			long difference = Math.abs(time - oldState.when);
			if (difference < smallest) {
				smallest = difference;
				smallestOldState = oldState;
			}
		}
		if (smallestOldState == null) {
			if(oldStates==null||oldStates.isEmpty()){
				smallestOldState = new OldState(player.x, player.y, player.xspeed, player.yspeed, smallest, player.left, player.right);
			}else{
				smallestOldState = oldStates.get(0);
			}
		}
		return smallestOldState;
	}*/

	public Planet getClosestPlanet(Entity player) {
		ArrayList<Planet> closestplanets = getClosestPlanets(player);
		// System.out.println("yioeuioruiouoetiuewiourio"+(closestplanets.get(0)==null));
		return closestplanets.get(0);
	}

	public ArrayList<Planet> getClosestPlanets(Entity player) {
		ArrayList<Planet> closeplanets = new ArrayList<>();
		Planet closest = null;
		float closestdistance = 0;
		for (int i = 0; i < planets.length; i++) {
			if (planets[i] == null) {
				System.out.println("Planet is null: " + i);
			}
			// if(Math.pow(Math.abs(player.x - planets[i].x), 2) +
			// Math.pow(Math.abs(player.y - planets[i].y), 2) <
			// Math.pow(playersize/2 + planets[i].radius, 2)){
			// collided
			// double angle = Math.atan2((player.y) - (planets[i].y), (player.x)
			// - (planets[i].x));
			//// double angle = Math.atan2((splats.planets[i].y) - y,
			// (splats.planets[i].x) - x);
			//
			// player.yspeed = (float) (Math.sin(angle) *
			// planets[i].bounceheight); //TODO BOUNCE HEIGHT A FORMULA FROM
			// GRAVITY TO DIFFER IN EVERY PLANET AND FOR EVERY PLAYER WITH
			// DIFFERENT MASS
			// player.xspeed = (float) (Math.cos(angle) *
			// planets[i].bounceheight);
			//// yspeed = -yspeed;
			//// System.out.println((Math.cos(angle) * splats.planets[i].gravity
			// / (Math.sqrt(Math.pow((y) - (splats.planets[i].y), 2) +
			// Math.pow((x) - (splats.planets[i].x), 2))) * 400) + " " +
			// (Math.sin(angle) * splats.planets[i].gravity /
			// (Math.sqrt(Math.pow((y) - (splats.planets[i].y), 2) +
			// Math.pow((x) - (splats.planets[i].x), 2))) * 400));
			// closeplanets.add(splats.planets[i]);
			// if(closest == null || Math.pow(Math.abs(x - splats.planets[i].x),
			// 2) + Math.pow(Math.abs(y - splats.planets[i].y), 2) <
			// closestdistance){
			// closest = splats.planets[i];
			// closestdistance = (float) (Math.pow(Math.abs(x -
			// splats.planets[i].x), 2) + Math.pow(Math.abs(y -
			// splats.planets[i].y), 2));
			// }
			if (Math.pow(Math.abs(player.x - planets[i].x), 2) + Math.pow(Math.abs(player.y - planets[i].y), 2) < Math.pow(player.getSize() / 2 + (planets[i].radius * 3.5f), 2)) {
				// close
				closeplanets.add(planets[i]);
				if (closest == null || Math.pow(Math.abs(player.x - planets[i].x), 2) + Math.pow(Math.abs(player.y - planets[i].y), 2) < closestdistance) {
					// System.out.println("DJASDJSADJKASDJKLSADJLKJADSLKDSJALKSJDALKSJADKJDKSADASJKSDAJKL"+(planets[i]==null));
					// System.out.println("DJASDJSADJKASDJKLSADJLKJADSLKDSJALKSJDALKSJADKJDKSADASJKSDAJKL"+(planets[i]==null));
					// System.out.println("DJASDJSADJKASDJKLSADJLKJADSLKDSJALKSJDALKSJADKJDKSADASJKSDAJKL"+(planets[i]==null));
					// System.out.println("DJASDJSADJKASDJKLSADJLKJADSLKDSJALKSJDALKSJADKJDKSADASJKSDAJKL"+(planets[i]==null));
					closest = planets[i];
					closestdistance = (float) (Math.pow(Math.abs(player.x - planets[i].x), 2)
							+ Math.pow(Math.abs(player.y - planets[i].y), 2));
				}
				if (planets[i] == null)
					System.out.print(i + "sdsadsadSADKLJAKLJADLKJDLKJADSKLoiurweiourweoi");
			} else if (closest == null || Math.pow(Math.abs(player.x - planets[i].x), 2) + Math.pow(Math.abs(player.y - planets[i].y), 2) < closestdistance) {
				closest = planets[i];
				closestdistance = (float) (Math.pow(Math.abs(player.x - planets[i].x), 2) + Math.pow(Math.abs(player.y - planets[i].y), 2));
			}
		}

		closeplanets.remove(closest);
		closeplanets.add(0, closest);// Put it at the back of the list
		// System.out.println("GHFJSWSHDGTFBDSDHSFDGHSGFDHTSGFDGSFSGFDHSGFDSGFDGSFG"+(closest==null));
		return closeplanets;
	}

	public double getClosestAngle(Entity player) {
		Planet planet = getClosestPlanet(player);
		double angle = Math.atan2((player.y) - (planet.y), (player.x) - (planet.x));
		double closestangle = angle - Math.PI;
		return closestangle;
	}

	public boolean isTouchingPlanet(Entity player, Planet planet) {
		return Math.pow(Math.abs(player.x - planet.x), 2) + Math.pow(Math.abs(player.y - planet.y), 2) < Math.pow(player.getSize() / 2 + planet.radius, 2);
	}

	public double getAngleFromPlanet(Player player, Planet planet) {
		double angle = Math.atan2((player.y) - (planet.y), (player.x) - (planet.x));
		double closestangle = angle - Math.PI;
		return closestangle;
	}

}
