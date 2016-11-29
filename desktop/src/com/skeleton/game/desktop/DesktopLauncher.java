package com.skeleton.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.skeleton.game.CromwellGame;
import com.skeleton.game.SkeletonGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.fullscreen = false;
        config.height = 768;
        config.width = 1024;
		new LwjglApplication(new CromwellGame(), config);
	}
}
