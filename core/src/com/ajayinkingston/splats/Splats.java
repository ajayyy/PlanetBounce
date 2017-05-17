package com.ajayinkingston.splats;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
	float projectilesize = 5;
	float projectileSpeed = 250;

	int mapsize = 30000;

	public Splats(int device) {// 0 web 1 android 2 iOS 3 desktop
		this.device = device;
	}

	@Override
	public void create() {// TODO MAKE HTML RESIZE CONSTANTLY
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
		
		// planets[0] = new Planet(0, -200, 300);

		// ArrayList<Planet> planetlist = new ArrayList<>();
		// Planet center = new Planet(0, 0, random.nextInt(300)+200);
		//
		// planetlist.add(center);
		//// planetlist.add(new Planet((int) -center.radius*2, (int)
		// center.radius*2, random.nextInt(200)+100));
		// float x = center.x;
		//// float y = center.y;
		// float size = center.radius;//TODO SPAWN CENTERS
		// float bufferdist = 400;
		// for(float y=center.y;y<mapsize;y+=size*12){
		// x = center.x;
		// while(x > -mapsize){
		// float nsize = random.nextInt(200)+100;
		// x = -size*6-nsize + x;
		// // y = size*6-nsize + y;
		// planetlist.add(new Planet(x,y,nsize));
		// System.out.println("sdalsadmlkkldsa" + x + " " + mapsize);
		// }
		// x = center.x;
		//// y = center.y;
		// size = center.radius;
		// while(x < mapsize){
		// float nsize = random.nextInt(200)+100;
		// x = bufferdist*6-nsize + x;
		// // y = bufferdist*6-nsize + y;
		// planetlist.add(new Planet(x,y,nsize));
		// System.out.println("sdalsadmlkkldsa" + x + " " + mapsize);
		// }
		// size = center.radius;
		// x = center.y + bufferdist*3;
		// float ny = y + bufferdist*6;
		// while(x > -mapsize){
		// float nsize = random.nextInt(200)+100;
		// x = -bufferdist*6-nsize + x;
		// planetlist.add(new Planet(x,ny,nsize));
		// System.out.println("sdalsadmlkkldsa" + x + " " + mapsize);
		// }
		// size = center.radius;
		// x = center.y + bufferdist*3;
		// // y += size*6;
		// while(x < mapsize){
		// float nsize = random.nextInt(200)+100;
		// x = bufferdist*6-nsize + x;
		// planetlist.add(new Planet(x,ny,nsize));
		// System.out.println("sdalsadmlkkldsa" + x + " " + mapsize);
		// }
		// }
		//
		// planets = planetlist.toArray(new Planet[planetlist.size()]);
		//
		// arrow = new Arrow(planets[0]);
		messenger = new WebSocketClientMessenger("67.193.147.175", 2492, device, this);

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
		if (Gdx.graphics.getDeltaTime() > 0.1) {// TODO FIX THIS BECUASE IT WILL CAUSE A LOT OF OF SYNC ISSUES (check keep) (render isn't called while window moving)
			Gdx.graphics.setTitle("UNDER 10 FPS");
			return;
		}
		if (planets.length < 1)
			return;
		// messenger.sendMessage(clientplayer.x + " " + clientplayer.y + " " +
		// clientplayer.xspeed + " " + clientplayer.yspeed);

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

		clientplayer.render(this, false);

		for (Player player : new ArrayList<Player>(players)) {
			player.update(this, Gdx.graphics.getDeltaTime(), false);
			player.render(this);
		}

		for (int i = 0; i < planets.length; i++) {
			planets[i].render(this);
		}

		for (Projectile projectile : new ArrayList<>(projectiles)) {
			projectile.render(this, shapeRenderer);
			if(projectile.distance > 500){
				projectiles.remove(projectile);
			}
		}

		// arrow.render(this);

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
			Player player = new Player(Integer.parseInt(message.split(" ")[1]), Long.parseLong(message.split(" ")[6]), Integer.parseInt(message.split(" ")[7]), this);
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
					clientplayer.size = clientplayer.aniStartMass + clientplayer.aniMassChange;
				}
				clientplayer.aniMassChange = (int) (Integer.parseInt(message.split(" ")[2]) );
				clientplayer.aniStartMass = clientplayer.size;
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
			
		}else if (message.startsWith("SPEEDCHANGE")) {
//			if(true) return;
			int id = Integer.parseInt(message.split(" ")[1]);
			float xspeed = Float.parseFloat(message.split(" ")[2]);
			float yspeed = Float.parseFloat(message.split(" ")[3]);
			long time = Long.parseLong(message.split(" ")[4]);
			
			if(id == messenger.getId()){
				
				ArrayList<OldState> oldStates = new ArrayList<>(clientplayer.oldStates);
				OldState originalState = getOldStateAtTime(oldStates, time, new Player(-1, -1, clientplayer.size, this));
				int originalStateIndex = oldStates.indexOf(originalState);
				if (originalStateIndex == -1)
					originalStateIndex = 0;
				
				// reset as if we were there (like server)
				double timeDifference = (System.currentTimeMillis() - clientplayer.start - time) / 1000d;// difference between now, and when it was pressed

				int amountremoved = oldStates.size() - (originalStateIndex + 1);
				ArrayList<OldState> oldOldStates = new ArrayList<>();
				for (int i = 0; i < amountremoved; i++) {// remove all of the future ones
					oldOldStates.add(oldStates.get(originalStateIndex));
					oldStates.remove(originalStateIndex);
				}

				// make now like that old state
				if(clientplayer.transformationPlayer != null && clientplayer.transformationPlayerPercent < 100 && clientplayer.transformationPlayerPercent >= 0){
					Player newTransformationPlayer = new Player(-1, -1, clientplayer.size, this);
					newTransformationPlayer.x = clientplayer.transformationPlayer.x + (clientplayer.x - clientplayer.transformationPlayer.x) * (clientplayer.transformationPlayerPercent/100);
					newTransformationPlayer.y = clientplayer.transformationPlayer.y + (clientplayer.y - clientplayer.transformationPlayer.y) * (clientplayer.transformationPlayerPercent/100);
					newTransformationPlayer.xspeed = xspeed;
					newTransformationPlayer.yspeed = yspeed;
					clientplayer.transformationPlayer = newTransformationPlayer;
				}else{
					clientplayer.transformationPlayer = new Player(-1, -1, clientplayer.size, this);
					clientplayer.transformationPlayer.x = clientplayer.x;
					clientplayer.transformationPlayer.y = clientplayer.y;
					clientplayer.transformationPlayer.xspeed = xspeed;
					clientplayer.transformationPlayer.yspeed = yspeed;
				}
				clientplayer.transformationPlayerPercent = 0;
				clientplayer.transformationPlayer.left = clientplayer.left;
				clientplayer.transformationPlayer.right = clientplayer.right;
				clientplayer.x = originalState.x;
				clientplayer.y = originalState.y;
				clientplayer.xspeed = xspeed;
				clientplayer.yspeed = yspeed;

				clientplayer.oldStates = oldStates;
				for (int i = 0; i < amountremoved; i++) {// remove all of the future ones
					clientplayer.left = oldOldStates.get(i).left;
					clientplayer.right = oldOldStates.get(i).right;
					if(oldOldStates.get(i).shot){
						clientplayer.xspeed -= (float) (Math.cos(oldOldStates.get(i).projectileAngle) * projectileSpeed);
						clientplayer.yspeed -= (float) (Math.sin(oldOldStates.get(i).projectileAngle) * projectileSpeed);
					}

					double delta = timeDifference / amountremoved;
					clientplayer.render(this, true);
				}
				
			}else{
				Player player = getPlayer(id);
				
				ArrayList<OldState> oldStates = new ArrayList<>(player.oldStates);
				OldState originalState = getOldStateAtTime(oldStates, time, player);
				int originalStateIndex = oldStates.indexOf(originalState);
				if (originalStateIndex == -1)
					originalStateIndex = 0;
				
				// reset as if we were there (like server)
				double timeDifference = (System.currentTimeMillis() - (time)) / 1000d;// difference between now, and when it was pressed

				int amountremoved = oldStates.size() - (originalStateIndex + 1);
				ArrayList<OldState> oldOldStates = new ArrayList<>();
				for (int i = 0; i < amountremoved; i++) {// remove all of the future ones
					oldOldStates.add(oldStates.get(originalStateIndex));
					oldStates.remove(originalStateIndex);
				}

				// make now like that old state
				if(player.transformationPlayer != null && player.transformationPlayerPercent < 100 && player.transformationPlayerPercent >= 0){
					Player newTransformationPlayer = new Player(-1, -1, player.mass, this);
					newTransformationPlayer.x = player.transformationPlayer.x + ((player.x - player.transformationPlayer.x) * (player.transformationPlayerPercent/100));
					newTransformationPlayer.y = player.transformationPlayer.y + ((player.y - player.transformationPlayer.y) * (player.transformationPlayerPercent/100));
					newTransformationPlayer.xspeed = xspeed;
					newTransformationPlayer.yspeed = yspeed;
					player.transformationPlayer = newTransformationPlayer;
				}else{
					player.transformationPlayer = new Player(-1, -1, player.mass, this);
					player.transformationPlayer.x = player.x;
					player.transformationPlayer.y = player.y;
					player.transformationPlayer.xspeed = xspeed;
					player.transformationPlayer.yspeed = yspeed;
				}
				player.transformationPlayerPercent = 0;
				player.transformationPlayer.left = player.left;
				player.transformationPlayer.right = player.right;
				player.x = originalState.x;
				player.y = originalState.y;
				player.xspeed = xspeed;
				player.yspeed = yspeed;

				player.oldStates = oldStates;
				for (int i = 0; i < amountremoved; i++) {// remove all of the future ones
					player.left = oldOldStates.get(i).left;
					player.right = oldOldStates.get(i).right;
					if(oldOldStates.get(i).shot){
						player.xspeed -= (float) (Math.cos(oldOldStates.get(i).projectileAngle) * projectileSpeed);
						player.yspeed -= (float) (Math.sin(oldOldStates.get(i).projectileAngle) * projectileSpeed);
					}

					double delta = 1/60;
					if(i!=0){
						delta = (oldOldStates.get(i).when - oldOldStates.get(i-1).when) / 1000d;
					}
					player.update(this, delta, true);
				}
				
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
			long time = Long.parseLong(message.split(" ")[6]);

			if (id == messenger.getId()) {
//				if(true) return;
				if(clientplayer.uncheckedMovements>0) return; //if the server doesn't know everything, don't trust it

				// do it for clientplayer

				ArrayList<OldState> oldStates = new ArrayList<>(clientplayer.oldStates);
				OldState originalState = getOldStateAtTime(oldStates, time, new Player(-1, -1, clientplayer.size, this));
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
						isStateInBetween(currentState, beforeState, afterState)){
					// reset as if we were there (like server)
					double timeDifference = (System.currentTimeMillis() - clientplayer.start - time) / 1000d;// difference between now, and when it was pressed

					int amountremoved = oldStates.size() - (originalStateIndex + 1);
					ArrayList<OldState> oldOldStates = new ArrayList<>();
					for (int i = 0; i < amountremoved; i++) {// remove all of the future ones
						oldOldStates.add(oldStates.get(originalStateIndex));
						oldStates.remove(originalStateIndex);
					}

					// make now like that old state
					if(clientplayer.transformationPlayer != null && clientplayer.transformationPlayerPercent < 100 && clientplayer.transformationPlayerPercent >= 0){
						Player newTransformationPlayer = new Player(-1, -1, clientplayer.size, this);
						newTransformationPlayer.x = clientplayer.transformationPlayer.x + (clientplayer.x - clientplayer.transformationPlayer.x) * (clientplayer.transformationPlayerPercent/100);
						newTransformationPlayer.y = clientplayer.transformationPlayer.y + (clientplayer.y - clientplayer.transformationPlayer.y) * (clientplayer.transformationPlayerPercent/100);
						newTransformationPlayer.xspeed = xspeed;
						newTransformationPlayer.yspeed = yspeed;
						clientplayer.transformationPlayer = newTransformationPlayer;
					}else{
						clientplayer.transformationPlayer = new Player(-1, -1, clientplayer.size, this);
						clientplayer.transformationPlayer.x = clientplayer.x;
						clientplayer.transformationPlayer.y = clientplayer.y;
						clientplayer.transformationPlayer.xspeed = xspeed;
						clientplayer.transformationPlayer.yspeed = yspeed;
					}
					clientplayer.transformationPlayerPercent = 0;
					clientplayer.transformationPlayer.left = clientplayer.left;
					clientplayer.transformationPlayer.right = clientplayer.right;
					clientplayer.x = x;
					clientplayer.y = y;
					clientplayer.xspeed = xspeed;
					clientplayer.yspeed = yspeed;

					clientplayer.oldStates = oldStates;
					for (int i = 0; i < amountremoved; i++) {// remove all of the future ones
						clientplayer.left = oldOldStates.get(i).left;
						clientplayer.right = oldOldStates.get(i).right;
						if(oldOldStates.get(i).shot){
							clientplayer.xspeed -= (float) (Math.cos(oldOldStates.get(i).projectileAngle) * projectileSpeed);
							clientplayer.yspeed -= (float) (Math.sin(oldOldStates.get(i).projectileAngle) * projectileSpeed);
						}

						double delta = timeDifference / amountremoved;
						clientplayer.render(this, true);
					}
				}

			} else {
				Player player = getPlayer(id);
				ArrayList<OldState> oldStates = new ArrayList<>(player.oldStates);
				OldState originalState = getOldStateAtTime(oldStates, time, player);
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
					System.out.println("asdsadsadsadadsadsadsadslkjasdkljasdlksdlasd");
					System.out.println("asdsadsadsadadsadsadsadslkjasdkljasdlksdlasd");
					System.out.println("asdsadsadsadadsadsadsadslkjasdkljasdlksdlasd");

					// reset as if we were there (like server)
					double timeDifference = (System.currentTimeMillis() - (time)) / 1000d;// difference between now, and when it was pressed

					int amountremoved = oldStates.size() - (originalStateIndex + 1);
					ArrayList<OldState> oldOldStates = new ArrayList<>();
					for (int i = 0; i < amountremoved; i++) {// remove all of the future ones
						oldOldStates.add(oldStates.get(originalStateIndex));
						oldStates.remove(originalStateIndex);
					}

					// make now like that old state
					if(player.transformationPlayer != null && player.transformationPlayerPercent < 100 && player.transformationPlayerPercent >= 0){
						Player newTransformationPlayer = new Player(-1, -1, player.mass, this);
						newTransformationPlayer.x = player.transformationPlayer.x + ((player.x - player.transformationPlayer.x) * (player.transformationPlayerPercent/100));
						newTransformationPlayer.y = player.transformationPlayer.y + ((player.y - player.transformationPlayer.y) * (player.transformationPlayerPercent/100));
						newTransformationPlayer.xspeed = xspeed;
						newTransformationPlayer.yspeed = yspeed;
						player.transformationPlayer = newTransformationPlayer;
					}else{
						player.transformationPlayer = new Player(-1, -1, player.mass, this);
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
							player.xspeed -= (float) (Math.cos(oldOldStates.get(i).projectileAngle) * projectileSpeed);
							player.yspeed -= (float) (Math.sin(oldOldStates.get(i).projectileAngle) * projectileSpeed);
						}

						double delta = 1/60;
						if(i!=0){
							delta = (oldOldStates.get(i).when - oldOldStates.get(i-1).when) / 1000d;
						}
						player.update(this, delta, true);
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
			long currentTime = System.currentTimeMillis();
			long time = Long.parseLong(message.split(" ")[4]);// when the action
																// happened
			if (time > currentTime ) {
				// ok something went wrong here
				// fix it
				// player.start = (long) (currentTime - time);
				time = currentTime ;
				System.err.println("HOUSTON, WE HAVE A HACKED SEVER (server is in the future, or am I in the past?)");
			}
			double timeDifference = (currentTime - (time)) / 1000d;// difference between now, and when it was pressed
			
			OldState originalState = getOldStateAtTime(new ArrayList<>(player.oldStates), time, player);
			
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
					player.xspeed -= (float) (Math.cos(oldOldStates.get(i).projectileAngle) * projectileSpeed);
					player.yspeed -= (float) (Math.sin(oldOldStates.get(i).projectileAngle) * projectileSpeed);
				}
				
				double delta = 1/60;
				if(i!=0){
					delta = (oldOldStates.get(i).when - oldOldStates.get(i-1).when) / 1000d;
				}
				player.update(this, delta, true);
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
			long currentTime = System.currentTimeMillis();
			long time = Long.parseLong(message.split(" ")[1]);// when the action
																// happened
			if (time > currentTime ) {
				// ok something went wrong here
				// fix it
				// player.start = (long) (currentTime - time);
				time = currentTime ;
				System.err.println("HOUSTON, WE HAVE A HACKED SEVER (server is in the future, or am I in the past?)");
			}
			double timeDifference = (currentTime - (time)) / 1000d;// difference between now, and when it was pressed
			
			OldState originalState = getOldStateAtTime(new ArrayList<>(player.oldStates), time, player);
			
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
					player.xspeed -= (float) (Math.cos(oldOldStates.get(i).projectileAngle) * projectileSpeed);
					player.yspeed -= (float) (Math.sin(oldOldStates.get(i).projectileAngle) * projectileSpeed);
				}
				
				double delta = 1/60;
				if(i!=0){
					delta = (oldOldStates.get(i).when - oldOldStates.get(i-1).when) / 1000d;
				}
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
	public void onConnect(long difference) {
		clientplayer.start = System.currentTimeMillis() - difference;
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

	public OldState getOldStateAtTime(ArrayList<OldState> oldStates, long time, Player player) {
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
	}

	public Planet getClosestPlanet(Player player) {
		ArrayList<Planet> closestplanets = getClosestPlanets(player);
		// System.out.println("yioeuioruiouoetiuewiourio"+(closestplanets.get(0)==null));
		return closestplanets.get(0);
	}

	public ArrayList<Planet> getClosestPlanets(Player player) {
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
			if (Math.pow(Math.abs(player.x - planets[i].x), 2) + Math.pow(Math.abs(player.y - planets[i].y), 2) < Math
					.pow(clientplayer.size / 2 + (planets[i].radius * 3.5f), 2)) {
				// close
				closeplanets.add(planets[i]);
				if (closest == null || Math.pow(Math.abs(player.x - planets[i].x), 2)
						+ Math.pow(Math.abs(player.y - planets[i].y), 2) < closestdistance) {
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
			} else if (closest == null) {
				closest = planets[i];
				closestdistance = (float) (Math.pow(Math.abs(player.x - planets[i].x), 2)
						+ Math.pow(Math.abs(player.y - planets[i].y), 2));
			}
		}

		closeplanets.remove(closest);
		closeplanets.add(0, closest);// Put it at the back of the list
		// System.out.println("GHFJSWSHDGTFBDSDHSFDGHSGFDHTSGFDGSFSGFDHSGFDSGFDGSFG"+(closest==null));
		return closeplanets;
	}

	public double getClosestAngle(Player player) {
		Planet planet = getClosestPlanet(player);
		double angle = Math.atan2((player.y) - (planet.y), (player.x) - (planet.x));
		double closestangle = angle - Math.PI;
		return closestangle;
	}

	public boolean isTouchingPlanet(Player player, Planet planet) {
		return Math.pow(Math.abs(player.x - planet.x), 2) + Math.pow(Math.abs(player.y - planet.y), 2) < Math
				.pow(clientplayer.size / 2 + planet.radius, 2);
	}

	public double getAngleFromPlanet(Player player, Planet planet) {
		double angle = Math.atan2((player.y) - (planet.y), (player.x) - (planet.x));
		double closestangle = angle - Math.PI;
		return closestangle;
	}

}
