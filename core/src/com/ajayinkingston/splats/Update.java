package com.ajayinkingston.splats;

public class Update {
	double delta;
	boolean includeLeftoverDelta;
	public Update(double delta, boolean includeLeftoverDelta){
		this.delta = delta;
		this.includeLeftoverDelta = includeLeftoverDelta;
	}
}
