package com.ajayinkingston.bouncee.desktop;

import com.ajayinkingston.splats.Splats;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1000;
		config.height = 600;
		new LwjglApplication(new Splats(3), config);
	}
}
