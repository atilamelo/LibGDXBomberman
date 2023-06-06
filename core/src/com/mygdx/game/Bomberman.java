package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.stages.GameScreen;

public class Bomberman extends Game {

	@Override
	public void create() {
		setScreen(new GameScreen());
	}
	
	
}
