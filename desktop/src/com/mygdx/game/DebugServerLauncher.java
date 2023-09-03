package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.configs.BombermanConfig;
import com.mygdx.game.configs.LevelConfig;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.stages.ServerStage;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DebugServerLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("My GDX Game");
		config.setWindowedMode(GameManager.GAME_WIDTH, GameManager.GAME_HEIGHT);
		new Lwjgl3Application(new DebugGameServer(), config);
	}
}

class DebugGameServer extends Game{	

	@Override
	public void create() {
		setScreen(new DebugScreen(this));
	}

	@Override
	public void dispose() {
		GameManager.getInstance().getAssetManager().dispose();
	}
}

class DebugScreen extends GameScreen {
	ServerStage stage;

	public DebugScreen(Game game) {
		super(game);
	}

	@Override
	public void show() {
		stage = new ServerStage(this, LevelConfig.getLevelConfig(1), BombermanConfig.initialBombermanConfig);
	}

	@Override
	public void render(float delta) {
		// Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the stage
        stage.draw();
        stage.act(delta);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
        stage.dispose();
	}
	
}