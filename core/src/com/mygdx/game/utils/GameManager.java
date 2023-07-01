package com.mygdx.game.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.listeners.WorldListener;

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
        private World b2World;

        public static final String GAME_NAME = "Bomberman NES";

        public static final int APP_WIDTH = 29;
        public static final int APP_HEIGHT = 13;
        public static final float PPM = 16;
        public static final int FPS = 60;
        public static final int GAME_WIDTH = APP_WIDTH * (int) PPM * 10;
        public static final int GAME_HEIGHT = APP_HEIGHT * (int) PPM * 10;
        public static final float GAME_ZOOM = 0.25f;

        // Bomberman properties
        public static final float BOMBERMAN_SPAWN_X = 1.5f;
        public static final float BOMBERMAN_SPAWN_Y = 11.5f;
        public static final float BOMBERMAN_WIDTH = .7f;
        public static final float BOMBERMAN_HEIGHT = 1f;
        public static final float BOMBERMAN_B2D_WIDTH = 0.3f;
        public static final float BOMBERMAN_B2D_HEIGHT = 0.4f;
        public static final float BOMBERMAN_B2D_RADIUS = .15f;
        public static final float BOMBERMAN_VELOCITY = 6f;
        public static final float BOMBERMAN_DENSITY = 1f;

        // Tiled Maps properties
        public static final float TILES_WIDTH = 1f;
        public static final float TILES_HEIGHT = 1f;

        public static final String BOMBERMAN_ATLAS_PATH = "assets/bombermanAtlas/bombermanAtlas.atlas";
        public static final String[] BOMBERMAN_UP_REGION_NAMES = new String[] { "tile017", "tile018", "tile019" };
        public static final String[] BOMBERMAN_DOWN_REGION_NAMES = new String[] { "tile003", "tile004", "tile005" };
        public static final String[] BOMBERMAN_LEFT_REGION_NAMES = new String[] { "tile000", "tile001", "tile002" };
        public static final String[] BOMBERMAN_RIGHT_REGION_NAMES = new String[] { "tile014", "tile015", "tile016" };
        public static final String[] BOMBERMAN_DYING_REGION_NAMES = new String[] {
                        "playerDying01", "playerDying02", "playerDying03", "playerDying04", "playerDying05",
                        "playerDying06", "playerDying07"
        };

        public static final String[] BOMB = new String[] { "bomb01", "bomb02", "bomb03" };
        public static final String[] EXPLOSION_CENTER_REGION_NAMES = new String[] { "explosionCenter01",
                        "explosionCenter02", "explosionCenter03", "explosionCenter04" };

        public static final String[] EXPLOSION_DOWN_REGION_NAMES = new String[] { "explosionDown01", "explosionDown02",
                        "explosionDown03", "explosionDown04" };
        public static final String[] EXPLOSION_DOWN_CONTINUE_REGION_NAMES = new String[] { "explosionDownContinue01",
                        "explosionDownContinue02", "explosionDownContinue03", "explosionDownContinue04" };

        public static final String[] EXPLOSION_UP_REGION_NAMES = new String[] { "explosionUp01", "explosionUp02",
                        "explosionUp03", "explosionUp04" };
        public static final String[] EXPLOSION_UP_CONTINUE_REGION_NAMES = new String[] { "explosionUpContinue01",
                        "explosionUpContinue02",
                        "explosionUpContinue03", "explosionUpContinue04" };

        public static final String[] EXPLOSION_RIGHT_REGION_NAMES = new String[] { "explosionRight01",
                        "explosionRight02",
                        "explosionRight03", "explosionRight04" };
        public static final String[] EXPLOSION_RIGHT_CONTINUE_REGION_NAMES = new String[] {
                        "explosionRightContinue01", "explosionRightContinue02", "explosionRightContinue03",
                        "explosionRightContinue04" };

        public static final String[] EXPLOSION_LEFT_REGION_NAMES = new String[] { "explosionLeft01", "explosionLeft02",
                        "explosionLeft03", "explosionLeft04" };
        public static final String[] EXPLOSION_LEFT_CONTINUE_REGION_NAMES = new String[] { "explosionLeftContinue01",
                        "explosionLeftContinue02", "explosionLeftContinue03", "explosionLeftContinue04" };

        public static final String[] BRICK_BREAKING_REGION_NAMES = new String[] { "brickExplosion01",
                        "brickExplosion02", "brickExplosion03", "brickExplosion04", "brickExplosion05",
                        "brickExplosion06" };
        public static final String BRICK_TEXTURE = "brick";

        // Bomb properties
        public static final float BOMB_WIDTH = 1f;
        public static final float BOMB_HEIGHT = 1f;
        public static final float BOMB_B2D_WIDTH = .5f;
        public static final float BOMB_B2D_HEIGHT = .5f;

        // Explosion properties
        public static final float EXPLOSION_WIDTH = 1f;
        public static final float EXPLOSION_HEIGHT = 1f;
        public static final float EXPLOSION_B2D_WIDTH = .5f;
        public static final float EXPLOSION_B2D_HEIGHT = .5f;
        public static final float EXPLOSION_RADIUS_CENTER = .5f;
        public static final float GAP_EXPLOSION = 0.1f;

        // Brick properties
        public static final float BRICK_WIDTH = 1f;
        public static final float BRICK_HEIGHT = 1f;
        public static final float BRICK_B2D_WIDTH = .5f;
        public static final float BRICK_B2D_HEIGHT = .5f;

        // BOX 2D Collision Bits
        public static final short NOTHING_BIT = 0x0001;
        public static final short WALL_BIT = 0x0002;
        public static final short PLAYER_BIT = 0x0004;
        public static final short BOMB_BIT = 0x0008;
        public static final short EXPLOSION_BIT = 0x0010;
        public static final short BRICK_BIT = 0x0012;
        public static final short ENEMY_BIT = 0x0014;
        public static final short BITS[] = {NOTHING_BIT, WALL_BIT, PLAYER_BIT, BOMB_BIT, EXPLOSION_BIT, BRICK_BIT, ENEMY_BIT};

        // Ballon Enemy properties
        public static final int BALLON_HP = 1;
        public static final float BALLON_WIDTH = .8f;
        public static final float BALLON_HEIGHT = 1f;
        public static final float BALLON_B2D_RADIUS = 0.45f;
        public static final float BALLON_SPEED = 1.7f;
        public static final String[] BALLON_DYING_REGION_NAMES = new String[] { "ballomDying01", "ballomDying02",
                        "ballomDying03", "ballomDying04", "ballomDying05"};
        public static final String[] BALLON_RIGHT_REGION_NAMES = new String[] { "ballomRight01", "ballomRight02",
                        "ballomRight03" };
        public static final String[] BALLON_LEFT_REGION_NAMES = new String[] { "ballomLeft01", "ballomLeft02",
                        "ballomLeft03" };

        private GameManager() {
                // create box2d world
                b2World = WorldUtils.createWorld();
                b2World.setContactListener(new WorldListener());

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

        public World getWorld() {
                return b2World;
        }

        @Override
        public void dispose() {
                assetManager.dispose();
        }

}