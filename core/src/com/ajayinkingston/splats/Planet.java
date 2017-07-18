package com.ajayinkingston.splats;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class Planet {
	float x,y,radius;
	
	float gravityhelperconstant,bounceheight;//set based on radius
	
	Food[] food = new Food[0]; //all the food
	
	float anisize,heightchange;
	boolean anisizeflip,heightchangeflip;
	
	public Planet(float x, float y, float radius, int foodAmount){
		this.x = x;
		this.y = y;
		this.radius = radius;
//		gravity = -1200000;
//		gravity = -100000;
		gravityhelperconstant = -3000;//XXX IF YOU CHANGE THIS THEN MAKE SURE TO CHANGE IT SERVER SIDE TOO
		
		double actualgravity = gravityhelperconstant / 300 * 350;//what really controls everything. NOTE: BOUNCEHEIGHT IS NOT GRAVITY, IT IS USED FOR BOUNCING
		//this is made by itself in the respective classes in a longer form. 350 is the multiplier. 300 is distance from player to planetcenter - radius + 300
		
		
		//bounce height is the equation y = 0.00019941857907 * x + 0.320988385581  (y = mx+b)
		
//		bounceheight = -(float) (actualgravity * (0.000175*radius + 0.328));//XXX IF YOU CHANGE THIS THEN MAKE SURE TO CHANGE IT SERVER SIDE TOO
		
		
		//the currently used form is a parabola, but if on big planets it doesnt seem reasonable it can always be adjusted with if stateemens (if over this radius then use a different formula)
		
		if(radius>1500){
			radius = 1500;
			//only sets temp variable
		}
		bounceheight = -(float) (actualgravity * ((-0.00000020563305192)*Math.pow(radius - 1191.62314342, 2) + 0.51676174503));//XXX IF YOU CHANGE THIS THEN MAKE SURE TO CHANGE IT SERVER SIDE TOO
//		bounceheight = -(float) (actualgravity * ((-0.00000056432129312)*Math.pow(radius - 865.537564728, 2) + 0.490178722022));//XXX IF YOU CHANGE THIS THEN MAKE SURE TO CHANGE IT SERVER SIDE TOO

		//only use this linear one if were small (on reeeally big planets, we dont want 94% bounce rate
		if(radius<600) bounceheight = -(float) (actualgravity * (0.00019941857907*radius + 0.320988385581));//XXX IF YOU CHANGE THIS THEN MAKE SURE TO CHANGE IT SERVER SIDE TOO
		
		
		if(radius<400) bounceheight = -(float) (actualgravity * (0.000233139509302*radius + 0.292662804186));//XXX IF YOU CHANGE THIS THEN MAKE SURE TO CHANGE IT SERVER SIDE TOO

		
		
//		bounceheight = -(float) (actualgravity * (0.00019941857907*radius + 0.320988385581)) /2;//XXX IF YOU CHANGE THIS THEN MAKE SURE TO CHANGE IT SERVER SIDE TOO
//		bounceheight = -(float) (actualgravity * (0.0000995142728571*radius + 0.161152864571));//XXX IF YOU CHANGE THIS THEN MAKE SURE TO CHANGE IT SERVER SIDE TOO
//		bounceheight = -(float) (actualgravity * (0.5));//XXX IF YOU CHANGE THIS THEN MAKE SURE TO CHANGE IT SERVER SIDE TOO
//		bounceheight = -(float) (actualgravity * (0.4104));//XXX IF YOU CHANGE THIS THEN MAKE SURE TO CHANGE IT SERVER SIDE TOO
		//XXX IF YOU CHANGE THIS THEN MAKE SURE TO CHANGE IT SERVER SIDE TOO
		
		//0.42, 0.3897   0.0402   subtract last number from the solution number based on radius        try 0.4023 as a number, should be 0.8 percentage and 600 radius

		
//		System.out.println(bounceheight);
//		System.out.println(-(gravity / (radius) * 400 - 150) * 0.24f);
		
//		bounceheight = -(gravity / (radius) * 400 - 150) * 0.24f;
		
//		bounceheight = -(gravity / (radius) * 400 - 150) * 0.3f;
		
		//TODO MAKE ITEMS (JUMPS, MASS INCREASE, POWER UPS) ON PLANETS, AND IMPLEMENT ARROWS TO KNOW WHERE THE PLANETS/PEOPLE ARE
		
		food = new Food[foodAmount];
		for(int i=0;i<food.length;i++){
			food[i] = new Food(false, 0, 0, 0);
		}
	}
	
	public void renderGlow(Splats splats){
		Vector3 pos = splats.cam.project(new Vector3(x*splats.batch.scaleFactor,y*splats.batch.scaleFactor, 0));
		if(pos.x+radius>0 && pos.x-radius<Gdx.graphics.getWidth() && pos.y+radius>0 && pos.y-radius<Gdx.graphics.getHeight()){
			splats.batch.begin();
			float factor = 1.2f;
			splats.batch.draw(splats.planetglow, x-radius*(factor), y-radius*(factor), radius*2*(factor), radius*2*(factor));
			splats.batch.end();
		}
	}
	
	public void render(Splats splats){
//		if(x+radius/2>splats.cam.position.x-Gdx.graphics.getWidth()/2 && x-radius/2<splats.cam.position.x+Gdx.graphics.getWidth()/2
//				&& y+radius/2>splats.cam.position.y-Gdx.graphics.getHeight()/2 && y-radius/2<splats.cam.position.y+Gdx.graphics.getHeight()/2){
//			
//		}
		Vector3 pos = splats.cam.project(new Vector3(x*splats.batch.scaleFactor,y*splats.batch.scaleFactor, 0));
		if(pos.x+radius>0 && pos.x-radius<Gdx.graphics.getWidth() && pos.y+radius>0 && pos.y-radius<Gdx.graphics.getHeight()){
//		if(true){
			splats.batch.begin();
			splats.batch.draw(splats.planetimg, x-radius, y-radius, radius*2, radius*2);
			splats.batch.end();
			
			//TODO MAYBE CHECK IF IT IS VISIBLE?
			splats.batch.begin();
			for(int i=0;i<food.length;i++){
				if(!food[i].enabled) continue;
				float x = (float) (Math.cos(food[i].angle)*(radius+food[i].getSize()/2+5+heightchange)) + this.x;
				float y = (float) (Math.sin(food[i].angle)*(radius+food[i].getSize()/2+5+heightchange)) + this.y;
				splats.batch.draw(splats.pointImages[food[i].image], x-food[i].getSize()/2-anisize/2, y-food[i].getSize()/2-anisize/2, food[i].getSize()/2+anisize/2, food[i].getSize()/2+anisize/2, food[i].getSize()+anisize, food[i].getSize()+anisize, 1, 1, 0, 0, 0, splats.pointImages[food[i].image].getWidth(), splats.pointImages[food[i].image].getWidth(), false, false);

			}
			splats.batch.end();
//			rotation += 200*Gdx.graphics.getDeltaTime();
//			if(rotation >= 360) rotation = 0;
			if(!anisizeflip){
				anisize += 12*Gdx.graphics.getDeltaTime();
				if(anisize>20){
					anisize = 20;
					anisizeflip = true;
				}
			}else{
				anisize -= 12*Gdx.graphics.getDeltaTime();
				if(anisize<0){
					anisize = 0;
					anisizeflip = false;
				}
			}
			
			if(!heightchangeflip){
				heightchange += 10*Gdx.graphics.getDeltaTime();
				if(heightchange>15){
					heightchange = 15;
					heightchangeflip = true;
				}
			}else{
				heightchange -= 10*Gdx.graphics.getDeltaTime();
				if(heightchange<0){
					heightchange = 0;
					heightchangeflip = false;
				}
			}
			

//			splats.shapeRenderer.setColor(Color.GRAY);
//			splats.shapeRenderer.begin(ShapeType.Filled);
//			splats.shapeRenderer.circle(x, y, radius);
//			splats.shapeRenderer.end();
		}
//		System.out.println(pos.x+radius/2>0 && pos.x-radius/2>Gdx.graphics.getWidth() && pos.y+radius/2>0 && pos.y-radius/2>Gdx.graphics.getHeight());
//		System.out.println((x+radius/2>splats.cam.position.x-Gdx.graphics.getWidth()/2) + " " + (x-radius/2<splats.cam.position.x+Gdx.graphics.getWidth()/2) + " " + 
//				(y+radius/2>splats.cam.position.y-Gdx.graphics.getHeight()/2) + " " + (y-radius/2<splats.cam.position.y+Gdx.graphics.getHeight()/2));
	}
	
}
