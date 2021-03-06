package com.skeleton.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.skeleton.game.CromwellGame;
import com.skeleton.game.MazeGame;
import com.skeleton.game.SkeletonGame;
import com.skeleton.game.WizardGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.fullscreen = false;
		config.width = 1024;
		config.height = 768;
		new LwjglApplication(new MazeGame(), config);
	}
}
