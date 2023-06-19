package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.mygdx.game.stages.GameScreen;
import com.mygdx.game.utils.GameManager;;

public class BombermanGame extends Game {

	@Override
	public void create() {
		setScreen(new GameScreen());
	}

	@Override
	public void dispose() {
		GameManager.getInstance().getAssetManager().dispose();
	}

}
