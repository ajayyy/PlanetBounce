package com.ajayinkingston.splats;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

public class Projectile {
	double x,y,xspeed,yspeed,distance;
	
	float radius;
	
	public Projectile(float x, float y, float radius, double angle, float speed){
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.xspeed = (Math.cos(angle) * speed);
		this.yspeed = (Math.sin(angle) * speed);
	}
	
	public void render(Splats splats, ShapeRenderer shapeRenderer){
		
		Vector3 pos = splats.cam.project(new Vector3((int)x*splats.batch.scaleFactor,(int)y*splats.batch.scaleFactor, 0));
		if(pos.x>0 && pos.x < Gdx.graphics.getWidth() && pos.y>0 && pos.y<Gdx.graphics.getHeight()){
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.circle((float) x, (float) y, radius);
			shapeRenderer.end();
		}
		
		x+=xspeed*Gdx.graphics.getDeltaTime();
		y+=yspeed*Gdx.graphics.getDeltaTime();
		distance += Math.abs(xspeed*Gdx.graphics.getDeltaTime()) + Math.abs(yspeed*Gdx.graphics.getDeltaTime());
	}
}
