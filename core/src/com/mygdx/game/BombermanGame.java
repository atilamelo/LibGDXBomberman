package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.stages.GameScreen;
import com.mygdx.game.stages.MenuScreen;
import com.mygdx.game.utils.GameManager;;

public class BombermanGame extends Game {

	@Override
	public void create() {
		setScreen(new GameScreen(this));
	}

	@Override
	public void dispose() {
		GameManager.getInstance().getAssetManager().dispose();
	}

}
