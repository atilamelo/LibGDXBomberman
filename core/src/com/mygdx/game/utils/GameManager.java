package com.mygdx.game.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

/*
 * Responsável por gerenciar gerenciar recursos do jogo, como texturas, sons e músicas.
 * Mantém informações sobre o estado do jogo e métodos para reproduzir som
 * Utiliza a abordagem SingleTon (Somente uma instância em todo o programa)
 * Baseado na estrutura do repositório de réplica do Bomberman com LibGdx: 
 * https://github.com/yichen0831/Bomberman_libGdx/blob/master/core/src/com/ychstudio/gamesys/GameManager.java
 */
public class GameManager implements Disposable {
    private final AssetManager assetManager;
    private static final GameManager instance = new GameManager();

    public static final String GAME_NAME = "Bomberman NES";

    public static final int APP_WIDTH = 29;
    public static final int APP_HEIGHT = 13;
    public static final float PPM = 16;
    public static final int FPS = 60;
    public static final int GAME_WIDTH = APP_WIDTH * (int) PPM * 10;
    public static final int GAME_HEIGHT = APP_HEIGHT * (int) PPM * 10;
    public static final float GAME_ZOOM = 0.25f;

    // Bomberman properties
    public static final float BOMBERMAN_X = 2;
    public static final float BOMBERMAN_Y = 2;
    public static final float BOMBERMAN_WIDTH = 1f;
    public static final float BOMBERMAN_HEIGHT = 1f;
    public static final float BOMBERMAN_B2D_WIDTH = 0.3f;
    public static final float BOMBERMAN_B2D_HEIGHT = 0.4f;

    public static final float BOMBERMAN_VELOCITY = 2f;
    public static final float BOMBERMAN_DENSITY = 1f;

    // Tiled Maps properties
    public static final float TILES_WIDTH = 1f;
    public static final float TILES_HEIGHT = 1f;

    public static final String BOMBERMAN_ATLAS_PATH = "assets/bombermanAtlas/bombermanAtlas.atlas";
    public static final String[] BOMBERMAN_UP_REGION_NAMES = new String[] { "tile017", "tile018", "tile019" };
    public static final String[] BOMBERMAN_DOWN_REGION_NAMES = new String[] { "tile003", "tile004", "tile005" };
    public static final String[] BOMBERMAN_LEFT_REGION_NAMES = new String[] { "tile000", "tile001", "tile002" };
    public static final String[] BOMBERMAN_RIGHT_REGION_NAMES = new String[] { "tile014", "tile015", "tile016" };

    public static final String[] BOMB_ANIMATION = new String[] { "tile042", "tile043", "tile044" };
    public static final float BOMB_WIDTH = 1f;
    public static final float BOMB_HEIGHT = 1f;
    public static final float BOMB_B2D_WIDTH = .5f;
    public static final float BOMB_B2D_HEIGHT = .5f;

    private GameManager() {
        // load resources
        assetManager = new AssetManager();

        // load atlas
        assetManager.load(GameManager.BOMBERMAN_ATLAS_PATH, TextureAtlas.class);

        // load maps
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("maps/map_teste.tmx", TiledMap.class);

        assetManager.finishLoading();
    }

    public static GameManager getInstance() {
        return instance;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

}