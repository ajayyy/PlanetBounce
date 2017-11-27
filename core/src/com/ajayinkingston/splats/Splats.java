package com.ajayinkingston.splats;

import java.util.ArrayList;
import java.util.Random;

import com.ajayinkingston.planets.server.Data;
import com.ajayinkingston.planets.server.Entity;
import com.ajayinkingston.planets.server.Movement;
import com.ajayinkingston.planets.server.OldState;
import com.ajayinkingston.planets.server.Planet;
import com.ajayinkingston.planets.server.Player;
import com.ajayinkingston.planets.server.Projectile;
import com.ajayinkingston.planets.server.Shot;
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
	
	ArrayList<Movement> movements = new ArrayList<>(); //movements made by clients, added to this list so that they can all be processed in the same frame;
	ArrayList<Shot> shots = new ArrayList<>();
	ArrayList<Spawn> spawns = new ArrayList<>(); //holds data for when a player is spawned

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
		
		clientplayer = new ClientPlayer(messenger.getId(), 0, 800, 0, 0, this);//defaults to startmass right now
		
		data = new Data();
		data.players.add(clientplayer);
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
		
		if(delta > 0.1f) delta = 1/fps;
		
		clientplayer.render(this);
		
		for (Player player : new ArrayList<Player>(data.players)) {
			if(player == clientplayer) continue;
			((com.ajayinkingston.splats.ClientPlayer) player).render(this);
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
				update(1/fps, false);
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
				update(1/fps, false);
			}
			if(update.includeLeftoverDelta) leftoverdelta = fulldelta % (1/fps);
			futureUpdates.remove(update);
			loops++;
		}
//		System.out.println("LOOPS FOR FUTUREUPDATES: " + loops);
	}
	
	public void update(double delta, boolean simulation) {
		
		//make sure these local variables are updated with the real ones
		
		for (Projectile projectile : new ArrayList<>(data.projectiles)) {
			projectile.update(data, delta);
			if(System.currentTimeMillis() - projectile.start > 4500 || Data.isTouchingPlanet(projectile, Data.getClosestPlanet(projectile, data.planets))){
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
			if(player == clientplayer) continue;
			player.update(data, 1/fps);
		}
		
		if(!simulation){
//			for(Movement movement: new ArrayList<>(movements)){
//				if(handleMovement(movement.player, movement.disabled, movement.direction, movement.frame)) movements.remove(movement);
//			}
			
			for(Shot shot: new ArrayList<>(shots)){
				if(handleShot(shot.player, shot.projectileangle, shot.frame)) shots.remove(shot);
			}
		}
		
		//collision detection
//		for(int i=0;i<data.players.size();i++){//for player to player
//			for(int s=i+1;s<data.players.size();s++){
//				if(data.players.get(s).collided(data.players.get(i))){
//					//collided with player
//					affectColidedPlayers(data.players.get(i),data.players.get(s));
//				}
//			}
//		}
//		
//		//projectile collision detection
		for(Projectile projectile: new ArrayList<>(data.projectiles)){
//			for(Player player: data.players){
//				if(player.collided(projectile)){
//					affectColidedPlayers(player, projectile);
//				}
//			}
			if(projectile.collided(clientplayer)){
				//collided with player
				affectColidedPlayers(clientplayer, projectile);
			}
		}
		
	}
	
	public void affectColidedPlayers(Entity player1, Entity player2) { //once the data.players are collided, this function will deal with them
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
	
	public boolean handleShot(Player player, float projectileangle, long frame) { //returns true if dealt with
		long currentFrame = player.frames;
		
		//now do the thing
		if(frame > currentFrame){
			// that's ok, the client is probably behind on purpose (still need to fix the bug where the client gets incredibly behind)
			System.out.println("aslkjdsalkjasjjjdjdsajasldjdas");
			return false;
		}

		//count the difference
		long amountremoved = currentFrame - frame;
		if(frame == currentFrame) amountremoved = 0;
		
		//make projectile and player oldOldState variables
		ArrayList<ArrayList<OldState>> playerOldOldStates = new ArrayList<>();
		
		//check for any projectiles created after this frame
		for(Projectile projectile: new ArrayList<>(data.projectiles)){
			if(projectile.frame < amountremoved){//because amount removed would be the amount of frames that have happened since, if this was created on that frame, then the frame - amount removed would be 0
				data.projectiles.remove(projectile);
			}
		}
		
		System.out.println((currentFrame - frame) + " asaasfasasdasfliuioelpo");
		
		//set all projectiles to proper values
		for(Projectile projectile: data.projectiles){
			OldState state = Data.getOldStateAtFrame(projectile.oldstates, projectile.frame - (currentFrame - frame));
			projectile.x = state.x;
			projectile.y = state.y;
			projectile.xspeed = state.xspeed;
			projectile.yspeed = state.yspeed;
			projectile.frame = state.frame;
			if(projectile.dead && state.frame <= projectile.deadframe){
				projectile.dead = false;
			}
			
			projectile.oldstates = Data.removeFutureOldStatesFromOldState(projectile.oldstates, state);
		}
		
		//set all players to proper values
		for(Player player2: data.players){
			OldState state = Data.getOldStateAtFrame(player2.oldStates, player2.frames - (currentFrame - frame));
			player2.x = state.x;
			player2.y = state.y;
			player2.xspeed = state.xspeed;
			player2.yspeed = state.yspeed;
			player2.frames = state.frame;
			
			playerOldOldStates.add(Data.getOldStatesAfterOldState(player2.oldStates, state));
			player2.oldStates = Data.removeFutureOldStatesFromOldState(player2.oldStates, state);
		}
		
		player.shoot(projectileangle, data.projectiles, ClientProjectile.class);
		
		ArrayList<Player> nonSpawnedPlayers = new ArrayList<>();
		
		for(Player player2: new ArrayList<>(data.players)){
			if(player2.frames < amountremoved){//because amount removed would be the amount of frames that have happened since, if this was created on that frame, then the frame - amount removed would be 0
				nonSpawnedPlayers.add(player2);
				data.players.remove(player2);
			}
		}
		
		player.frames = frame;
		
		//call update however many missed frames there were
		for(int i=0;i<amountremoved;i++){//remove all of the future ones
			System.out.println("isthisevenrunning????");
			
			for(Player player2: new ArrayList<>(nonSpawnedPlayers)){
				if(player.frames < amountremoved-i){
					data.players.add(player2);
					nonSpawnedPlayers.remove(player2);
				}
			}
			
			//iterate through players to make sure all events from oldstate are recalculated
			for(Player player2: data.players){
				ArrayList<OldState> oldOldStates = playerOldOldStates.get(data.players.indexOf(player2));
				player2.left = oldOldStates.get(i).left;
				player2.right = oldOldStates.get(i).right;
				if(oldOldStates.get(i).shot){
					player2.shoot(oldOldStates.get(i).projectileAngle, data.projectiles, Projectile.class);
				}
			}
			
			update(1/fps, true);
		}
		
		return true;
	}
	
	public boolean handleSpawns(Player newPlayer, Spawn spawn, Player player, long frame) { //returns true if dealt with
		
		/*The frame that is being recieved is the frame of clientplayer that the player spawned at

		The variable also includes what frame it was at when spawned (because "spawning" includes players connected before this player connected) **/
		
		long currentFrame = clientplayer.frames; //when clientplayer frames hit this frame count, then spawn the player
		
		if(spawn.spawnFrame > currentFrame){
			// that's ok, the client is probably behind on purpose (still need to fix the bug where the client gets incredibly behind)
			return false;
		}

		//count the difference
		long amountremoved = currentFrame - frame;
		if(spawn.spawnFrame == currentFrame) amountremoved = 0;
		
		//make projectile and player oldOldState variables
		ArrayList<ArrayList<OldState>> playerOldOldStates = new ArrayList<>();
		
		//check for any projectiles created after this frame
		for(Projectile projectile: new ArrayList<>(data.projectiles)){
			if(projectile.frame < amountremoved){//because amount removed would be the amount of frames that have happened since, if this was created on that frame, then the frame - amount removed would be 0
				data.projectiles.remove(projectile);
			}
		}
		
		//set all projectiles to proper values
		for(Projectile projectile: data.projectiles){
			OldState state = Data.getOldStateAtFrame(projectile.oldstates, projectile.frame - (currentFrame - frame));
			projectile.x = state.x;
			projectile.y = state.y;
			projectile.xspeed = state.xspeed;
			projectile.yspeed = state.yspeed;
			projectile.frame = state.frame;
			if(projectile.dead && state.frame <= projectile.deadframe){
				projectile.dead = false;
			}
			
			projectile.oldstates = Data.removeFutureOldStatesFromOldState(projectile.oldstates, state);
		}
		
		//set all players to proper values
		for(Player player2: data.players){
			OldState state = Data.getOldStateAtFrame(player2.oldStates, player2.frames - (currentFrame - frame));
			player2.x = state.x;
			player2.y = state.y;
			player2.xspeed = state.xspeed;
			player2.yspeed = state.yspeed;
			player2.frames = state.frame;
			
			playerOldOldStates.add(Data.getOldStatesAfterOldState(player2.oldStates, state));
			player2.oldStates = Data.removeFutureOldStatesFromOldState(player2.oldStates, state);
		}
		
		ArrayList<Player> nonSpawnedPlayers = new ArrayList<>();
		
		for(Player player2: new ArrayList<>(data.players)){
			if(player2.frames < amountremoved){//because amount removed would be the amount of frames that have happened since, if this was created on that frame, then the frame - amount removed would be 0
				nonSpawnedPlayers.add(player2);
				data.players.remove(player2);
			}
		}
		
		player.frames = frame;
		
		//call update however many missed frames there were
		for(int i=0;i<amountremoved;i++){//remove all of the future ones
			System.out.println("isthisevenrunning????");
			
			for(Player player2: new ArrayList<>(nonSpawnedPlayers)){
				if(player.frames < amountremoved-i){
					data.players.add(player2);
					nonSpawnedPlayers.remove(player2);
				}
			}
			
			//iterate through players to make sure all events from oldstate are recalculated
			for(Player player2: data.players){
				ArrayList<OldState> oldOldStates = playerOldOldStates.get(data.players.indexOf(player2));
				player2.left = oldOldStates.get(i).left;
				player2.right = oldOldStates.get(i).right;
				if(oldOldStates.get(i).shot){
					player2.shoot(oldOldStates.get(i).projectileAngle, data.projectiles, Projectile.class);
				}
			}
			
			update(1/fps, true);
		}
		
		return true;
	}

	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
	}

	@Override
	public void onMessageRecieved(String message) {
		if (message.startsWith("CONNECTED")) {
			
//			String[] messageData = message.split(" ");
//
//			Spawn spawn = new Spawn(Integer.parseInt(messageData[1]), Float.parseFloat(messageData[2]), Float.parseFloat(messageData[3]), Float.parseFloat(messageData[4]), Float.parseFloat(messageData[5]), Integer.parseInt(messageData[6]), 0, 0);
//			
//			spawns.add(spawn);
			
			Player player = new ClientPlayer(Integer.parseInt(message.split(" ")[1]), Float.parseFloat(message.split(" ")[2]), Float.parseFloat(message.split(" ")[3]), Integer.parseInt(message.split(" ")[6]), 0, this);
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
				long currentFrame = clientplayer.frames;
				if(frame > currentFrame + futureUpdates.size() - aheadUpdates){
					for(long i=currentFrame + futureUpdates.size() - aheadUpdates;frame>i;i++){
						futureUpdates.add(new Update(1/fps, false));
					}
				}
//				if(frame < currentFrame + futureUpdates.size() - aheadUpdates){
//					aheadUpdates = (currentFrame + futureUpdates.size() - aheadUpdates) - frame;
//				}
				
//				if(data.players.size() < 2){
//					data.players.add(new ClientPlayer(3, 0, 0, clientplayer.mass, 0, this));
//					data.players.get(1).x = clientplayer.x;
//					data.players.get(1).y = clientplayer.y;
//					data.players.get(1).xspeed = clientplayer.xspeed;
//					data.players.get(1).yspeed = clientplayer.yspeed;
//
//				}
//				
//				data.players.get(1).x = x;
//				data.players.get(1).y = y;
//				data.players.get(1).yspeed = yspeed;
//				data.players.get(1).xspeed = xspeed;
//				data.players.get(1).mass = clientplayer.mass;
				
			}

		} else if (message.startsWith("s")) {
			
			//a player has shot
			
			String[] messageData = message.split(" ");
			
			//click (shoot)
			Player player = Data.getPlayer(Integer.parseInt(messageData[1]), data.players);
			if(player==null) return;
			float projectileangle = Float.parseFloat(messageData[3]);
			long frame = Long.parseLong(messageData[4]);// when the action happened
			
			shots.add(new Shot(player, projectileangle, frame));
			
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
			
			OldState originalState = Data.getOldStateAtFrame(new ArrayList<>(player.oldStates), frame);
			
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
