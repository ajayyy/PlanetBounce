package com.ajayinkingston.splats;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

/**                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
 * Arrows pointing at different objects
 */

public class Arrow {
	//grey planet (maybe draw planet icon beside) red player, yellow sun, black black hole
	
	float trianglex,circle1x,circle2x,triangley,circle1y,circle2y,rotation;
	
	float trianglewidth=40,triangleheight=40*0.868544601f;
	
	Planet target;//TODO MAKE THIS GIVEN FROM SUPER CLASS AND MAKE THIS TYPE AN ENTITY
	
	Texture triangle,circle1,circle2;//add a of lag to the movement
	//TODO MAKE ARROW WORK
	
	public Arrow(Planet target){//TODO PROBABLY MAKE THE IMAGES IMPORTED FROM SUPER CLASS TO HAVE AN ARROW ARRAY/LIST
		this.target = target;
		triangle = new Texture("badlogic.jpg");
		circle1 = new Texture("circleicon.png");
		circle2 = new Texture("circle.png");//work on basic arrow then make it point in a direction
		
	}
	
	public void render(Splats splats){
		triangley = splats.clientplayer.y;
		trianglex = splats.clientplayer.x;
		
		Vector3 targetcoords = splats.cam.project(new Vector3(Gdx.input.getX(),Gdx.input.getY(),0));
		rotation = (float) Math.atan2(targetcoords.y-Gdx.graphics.getHeight(), targetcoords.x-Gdx.graphics.getWidth()/2);
//		rotation+=0.1;
		
		splats.batch.begin();
		splats.batch.draw(triangle, trianglex-trianglewidth/2, triangley-triangleheight/2, trianglewidth/2, triangleheight/2, trianglewidth, triangleheight, 1, 1, (float) Math.toDegrees(rotation)-180, 0, 0, triangle.getWidth(), triangle.getHeight(), false, false);
		splats.batch.draw(triangle, trianglex-trianglewidth/2+20, triangley-triangleheight/2+20, trianglewidth/2, triangleheight/2, trianglewidth, triangleheight, 1, 1, (float) Math.toDegrees(rotation), 0, 0, triangle.getWidth(), triangle.getHeight(), false, false);
		splats.batch.draw(triangle, trianglex-trianglewidth/2+40, triangley-triangleheight/2+40, trianglewidth/2, triangleheight/2, trianglewidth, triangleheight, 1, 1, (float) Math.toDegrees(rotation), 0, 0, triangle.getWidth(), triangle.getHeight(), false, false);
	//		splats.batch.draw(circle1, trianglex-trianglewidth/2, triangley-trianglheight/2-70, trianglewidth, trianglheight);
		splats.batch.end();
	}
}

