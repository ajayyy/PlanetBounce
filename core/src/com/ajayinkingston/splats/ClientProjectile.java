package com.ajayinkingston.splats;

import java.util.ArrayList;

import com.ajayinkingston.planets.server.Main;
import com.ajayinkingston.planets.server.Planet;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

public class ClientProjectile extends com.ajayinkingston.planets.server.Projectile{
	
	public ClientProjectile(double x, double y, int radius, double angle, float speed){
		super(x, y, radius, angle, speed);
	}
	
	public void render(Splats splats){
		Vector3 pos = splats.cam.project(new Vector3((int)x*splats.batch.scaleFactor,(int)y*splats.batch.scaleFactor, 0));
		if(pos.x>0 && pos.x < Gdx.graphics.getWidth() && pos.y>0 && pos.y<Gdx.graphics.getHeight()){
			splats.shapeRenderer.begin(ShapeType.Filled);
			splats.shapeRenderer.setColor(Color.WHITE);
			splats.shapeRenderer.circle((float) x, (float) y, radius);
			splats.shapeRenderer.end();
		}
		
	}
	
}
