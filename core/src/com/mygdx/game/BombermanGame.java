package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.mygdx.game.stages.GameScreen;
import com.mygdx.game.utils.Constants;;

public class BombermanGame extends Game {

	public AssetManager assetManager;

	@Override
	public void create() {
		assetManager = new AssetManager();
		assetManager.load(Constants.BOMBERMAN_ATLAS_PATH, TextureAtlas.class);
		
		// Loading maps
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		assetManager.load("maps/map_teste.tmx", TiledMap.class);
		
		assetManager.finishLoading();

		setScreen(new GameScreen(this));
	}

}
