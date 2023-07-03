package com.mygdx.game.utils;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.listeners.WorldListener;
import com.mygdx.game.systems.RandomPlacement.Position;

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

    public static final int MAP_WIDTH = 29;
    public static final int MAP_HEIGHT = 13;
    public static final float TILE_WIDTH = 1f;
    public static final float TILE_HEIGHT= 1f;
    public static final float PPM = 16;
    public static final int FPS = 60;
    public static final int GAME_WIDTH = MAP_WIDTH * (int) PPM * 10;
    public static final int GAME_HEIGHT = MAP_HEIGHT * (int) PPM * 10;
    public static final float GAME_ZOOM = 0.25f;

    // Bomberman properties
    public static final float BOMBERMAN_SPAWN_X = 1.5f;
    public static final float BOMBERMAN_SPAWN_Y = 11.5f;
    public static final float BOMBERMAN_WIDTH = .7f;
    public static final float BOMBERMAN_HEIGHT = 1f;
    public static final float BOMBERMAN_B2D_RADIUS = .4f;
    public static final float BOMBERMAN_INITIAL_SPEED = 1f;
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
    public static final short NOTHING_BIT = 0;
    public static final short WALL_BIT = 1;
    public static final short PLAYER_BIT = 1 << 1;
    public static final short BOMB_BIT = 1 << 2;
    public static final short EXPLOSION_BIT = 1 << 3;
    public static final short BRICK_BIT = 1 << 4;
    public static final short ENEMY_BIT = 1 << 5;
    public static final short DOOR_BIT = 1 << 6;
    public static final short POWER_UP_BIT = 1 << 7;
    public static final short BITS[] = { NOTHING_BIT, WALL_BIT, PLAYER_BIT, BOMB_BIT, EXPLOSION_BIT, BRICK_BIT,
            ENEMY_BIT, DOOR_BIT, POWER_UP_BIT };

    // Ballon Enemy properties
    public static final String[] BALLON_DYING_REGION_NAMES = new String[] { "ballomDying01", "ballomDying02",
            "ballomDying03", "ballomDying04", "ballomDying05" };
    public static final String[] BALLON_RIGHT_REGION_NAMES = new String[] { "ballomRight01", "ballomRight02",
            "ballomRight03" };
    public static final String[] BALLON_LEFT_REGION_NAMES = new String[] { "ballomLeft01", "ballomLeft02",
            "ballomLeft03" };

    /* Enemy properties (Box2D) */
    public static final float ENEMY_WIDTH = .8f;
    public static final float ENEMY_HEIGHT = 1f;
    public static final float ENEMY_B2D_RADIUS = 0.4f;
    public static final float ENEMY_DYING_TIME = 1.5f;
    /* Speed of each enemy */
    public static final float ENEMY_SLOWEST_SPEED = 0.5f;
    public static final float ENEMY_SLOW_SPEED = ENEMY_SLOWEST_SPEED * 2;
    public static final float ENEMY_NORMAL_SPEED = ENEMY_SLOW_SPEED * 2;
    public static final float ENEMY_FAST_SPEED = ENEMY_SLOW_SPEED * 3;
    /* Range that enemy will pursue the player */
    public static final int ENEMY_LOWQI_RANGE_PURSUE = 0;
    public static final int ENEMY_MEDIUMQI_RANGE_PURSUE = 2;
    public static final int ENEMY_HIGHQI_RANGE_PURSUE = 3;
    /* Chances of change randomly direction */
    public static final float ENEMY_LOWQI_INTERSECTION_CHANGE = 0;
    public static final float ENEMY_MEDIUMQI_INTERSECTION_CHANGE = 0.1f;
    public static final float ENEMY_HIGHQI_INTERSECTION_CHANGE = 0.5f;


    /*  */
    public static final String ONIL_DYING_TEXTURE = "onilDying";
    public static final String[] ONIL_RIGHT_REGION_NAMES = new String[] { "onilRight01", "onilRight02",
            "onilRight03" };
    public static final String[] ONIL_LEFT_REGION_NAMES = new String[] { "onilLeft01", "onilLeft02", "onilLeft03" };

    // Door properties
    public static final String DOOR_TEXTURE = "door";
    public static final float DOOR_WIDTH = 1f;
    public static final float DOOR_HEIGHT = 1f;

    // Power Up characteristics
    public static final float POWER_UP_WIDTH = 1f;
    public static final float POWER_UP_HEIGHT = 1f;
    public static final double POWER_UP_CHANCE = 0.1; // 20% of chance
    public static final float SPEED_UP_VALUE = 0.2f;
    public static final float INVENCIBLE_TIME = 30f;

    // Power Ups Texture Adress
    public static final String POWER_UP_BOMB_UP = "powerUpBombUp";
    public static final String POWER_UP_SPEED_UP = "powerUpSpeed";
    public static final String POWER_UP_BOMB_PASS = "powerUpBombPass";
    public static final String POWER_UP_BRICK_PASS = "powerUpbrickPass";
    public static final String POWER_UP_FIRE_UP = "powerUpFire";
    public static final String POWER_UP_FLAME_PASS = "powerUpFlamePass";
    public static final String POWER_UP_REMOTE_CONTROL = "powerUpRemoteControl";
    public static final String POWER_UP_INVENCIBLE = "powerUpInvencible";

    public int enemiesLeft;

    private GameManager() {
        // create box2d world
        b2World = WorldUtils.createWorld();
        b2World.setContactListener(new WorldListener());
        enemiesLeft = 0;

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

    public static List<Position> generateSpawnArea() {
        List<Position> spawnArea = new ArrayList<Position>();

        for(int x = 0; x < 4; x++){
            for(int y = 11; y > 9; y--){
                spawnArea.add(new Position(x, y));
            }
        }

        return spawnArea;
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

}