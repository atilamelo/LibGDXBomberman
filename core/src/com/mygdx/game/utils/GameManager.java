package com.mygdx.game.utils;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
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

    public static final int MAP_WIDTH = 31;
    public static final int MAP_HEIGHT = 13;
    public static final float TILE_WIDTH = 1f;
    public static final float TILE_HEIGHT = 1f;
    public static final float PPM = 16;
    public static final int FPS = 60;
    public static final int GAME_WIDTH = MAP_WIDTH * (int) PPM * 10;
    public static final int GAME_HEIGHT = MAP_HEIGHT * (int) PPM * 10;
    public static final float GAME_ZOOM = 0.2f;

    // Bomberman properties
    public static final float BOMBERMAN_SPAWN_X = 1.5f;
    public static final float BOMBERMAN_SPAWN_Y = 11.5f;
    public static final float BOMBERMAN_WIDTH = .7f;
    public static final float BOMBERMAN_HEIGHT = 1f;
    public static final float BOMBERMAN_B2D_RADIUS = .4f;
    public static final float BOMBERMAN_INITIAL_SPEED = 1f;
    public static final float BOMBERMAN_DENSITY = 1f;
    public static final short BOMBERMAN_MASK_BITS = GameManager.WALL_BIT | GameManager.ENEMY_BIT | GameManager.BOMB_BIT
    | GameManager.EXPLOSION_BIT | GameManager.POWER_UP_BIT | GameManager.BRICK_BIT | GameManager.DOOR_BIT;

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

    // Enemys dying animation
    public static final String[] pinkDying = { "pinkDying01", "pinkDying02", "pinkDying03", "pinkDying04" };
    public static final String[] blueDying = { "blueDying01", "blueDying02", "blueDying03", "blueDying04" };
    public static final String[] purpleDying = { "purpleDying01", "purpleDying02", "purpleDying03",
            "purpleDying04" };

    // Ballon Enemy Render
    public static final String[] BALLON_DYING_REGION_NAMES = new String[] { "ballomDying", pinkDying[0],
            pinkDying[1],
            pinkDying[2], pinkDying[3] };
    public static final String[] BALLON_RIGHT_REGION_NAMES = new String[] { "ballomRight01", "ballomRight02",
            "ballomRight03" };
    public static final String[] BALLON_LEFT_REGION_NAMES = new String[] { "ballomLeft01", "ballomLeft02",
            "ballomLeft03" };

    // Onion Enemy Render
    public static final String[] ONIL_DYING_REGION_NAMES = new String[] { "onilDying", blueDying[0], blueDying[1],
            blueDying[2], blueDying[3] };
    public static final String[] ONIL_RIGHT_REGION_NAMES = new String[] { "onilRight01", "onilRight02",
            "onilRight03" };
    public static final String[] ONIL_LEFT_REGION_NAMES = new String[] { "onilLeft01", "onilLeft02", "onilLeft03" };

    // Doll Enemy Render
    public static final String[] DOLL_DYING_REGION_NAMES = new String[] { "dollDying", purpleDying[0],
            purpleDying[1],
            purpleDying[2], purpleDying[3] };
    public static final String[] DOLL_RIGHT_REGION_NAMES = new String[] { "dollRight01", "dollRight02",
            "dollRight03" };
    public static final String[] DOLL_LEFT_REGION_NAMES = new String[] { "dollLeft01", "dollLeft02",
            "dollLeft03" };

    // Minvo Enemy Render
    public static final String[] MINVO_DYING_REGION_NAMES = new String[] { "minvoDying", pinkDying[0], pinkDying[1],
            pinkDying[2], pinkDying[3] };
    public static final String[] MINVO_RIGHT_REGION_NAMES = new String[] { "minvoRight01", "minvoRight02",
            "minvoRight03" };
    public static final String[] MINVO_LEFT_REGION_NAMES = new String[] { "minvoLeft01", "minvoLeft02",
            "minvoLeft03" };

    // Kondoria Enemy Render
    public static final String[] KONDORIA_DYING_REGION_NAMES = new String[] { "kondoriaDying", blueDying[0],
            blueDying[1],
            blueDying[2], blueDying[3] };
    public static final String[] KONDORIA_RIGHT_REGION_NAMES = new String[] { "kondoriaRight01", "kondoriaRight02",
            "kondoriaRight03" };
    public static final String[] KONDORIA_LEFT_REGION_NAMES = new String[] { "kondoriaLeft01", "kondoriaLeft02",
            "kondoriaLeft03" };

    // Ovapi Enemy Render
    public static final String[] OVAPI_DYING_REGION_NAMES = new String[] { "ovapiDying", purpleDying[0],
            purpleDying[1],
            purpleDying[2], purpleDying[3] };
    public static final String[] OVAPI_RIGHT_REGION_NAMES = new String[] { "ovapiRight01", "ovapiRight02",
            "ovapiRight03" };
    public static final String[] OVAPI_LEFT_REGION_NAMES = new String[] { "ovapiLeft01", "ovapiLeft02",
            "ovapiLeft03" };

    // Pass Enemy Render
    public static final String[] PASS_DYING_REGION_NAMES = new String[] { "passDying", pinkDying[0], pinkDying[1],
            pinkDying[2], pinkDying[3] };
    public static final String[] PASS_RIGHT_REGION_NAMES = new String[] { "passRight01", "passRight02",
            "passRight03" };
    public static final String[] PASS_LEFT_REGION_NAMES = new String[] { "passLeft01", "passLeft02",
            "passLeft03" };

    // Pontan Enemy Render
    public static final String[] PONTAN_DYING_REGION_NAMES = new String[] { "pontanDying", pinkDying[0],
            pinkDying[1],
            pinkDying[2], pinkDying[3] };
    public static final String[] PONTAN_RIGHT_REGION_NAMES = new String[] { "pontanMoviment01", "pontanMoviment02",
            "pontanMoviment03", "pontanMoviment04" };
    public static final String[] PONTAN_LEFT_REGION_NAMES = new String[] { "pontanMoviment01", "pontanMoviment02",
            "pontanMoviment03", "pontanMoviment04" };

    /* Enemy properties (Box2D) */
    public static final float ENEMY_WIDTH = .9f;
    public static final float ENEMY_HEIGHT = .9f;
    public static final float ENEMY_B2D_RADIUS = 0.4f;
    public static final float ENEMY_DYING_TIME = 1.5f;
    /* Mask Bits for the enemies */
    public static final short[] MASK_BITS_DEFAULT_ARRAY = new short[] { GameManager.WALL_BIT, GameManager.BRICK_BIT,
            GameManager.BOMB_BIT, GameManager.PLAYER_BIT };
    public static final short MASK_BITS_DEFAULT = GameManager.WALL_BIT | GameManager.BRICK_BIT | GameManager.EXPLOSION_BIT
            | GameManager.BOMB_BIT | GameManager.PLAYER_BIT;
    public static final short[] MASK_BITS_PASS_BRICK_ARRAY = new short[] { GameManager.WALL_BIT,
            GameManager.BOMB_BIT, GameManager.PLAYER_BIT };
    public static final short MASK_BITS_PASS_BRICK = GameManager.WALL_BIT | GameManager.BOMB_BIT
            | GameManager.PLAYER_BIT | GameManager.EXPLOSION_BIT;
    /* Speed of each enemy */
    public static final float ENEMY_SLOWEST_SPEED = 1f;
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

    // Door properties
    public static final String DOOR_TEXTURE = "door";
    public static final float DOOR_WIDTH = 1f;
    public static final float DOOR_HEIGHT = 1f;

    // Power Up characteristics
    public static final float POWER_UP_WIDTH = 1f;
    public static final float POWER_UP_HEIGHT = 1f;
    public static final double POWER_UP_CHANCE = 0.0; // 20% of chance
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

    /* Sounds Path */
    public static final String MUSIC_PATH = "audio/musics";
    public static final String EFFECTS_PATH = "audio/effects";

    // Musics Path
    public static final String MUSIC_TITLE_SCREEN = MUSIC_PATH + "/title_screen.ogg";
    public static final String MUSIC_MAIN = MUSIC_PATH + "/main_BGM_2.ogg";
    public static final String MUSIC_ENDING = MUSIC_PATH + "/ending.ogg";
    public static final String MUSIC_GAME_OVER = MUSIC_PATH + "/game_over.ogg";
    public static final String MUSIC_BONUS_STAGE = MUSIC_PATH + "/bonus_stage.ogg";

    // Efects Path
    public static final String SOUND_STAGE_START = EFFECTS_PATH + "/stage_start.mp3";
    public static final String SOUND_PICK_POWER_UP = EFFECTS_PATH + "/power_up.mp3";
    public static final String SOUND_STAGE_CLEAR = EFFECTS_PATH + "/stage_clear.mp3";
    public static final String SOUND_MISS = EFFECTS_PATH + "/miss.mp3";
    public static final String SOUND_LEFT_RIGHT_WALK = EFFECTS_PATH + "/left_right_walk.wav";
    public static final String SOUND_DOWN_UP_WALK = EFFECTS_PATH + "/down_up_walk.wav";
    public static final String SOUND_PUT_BOMB = EFFECTS_PATH + "/put_bomb.wav";
    public static final String SOUND_CLEAR_ENEMIES = EFFECTS_PATH + "/all_enemies_clear.mp3";
    public static final String SOUND_BOMB_EXPLODES = EFFECTS_PATH + "/bomb_explodes.wav";

    public int enemiesLeft;
    private String currentMusic = "";

    private GameManager() {
        // create box2d world
        enemiesLeft = 0;

        assetManager = new AssetManager();

        /* Assets loads */

        // Atlas
        assetManager.load(GameManager.BOMBERMAN_ATLAS_PATH, TextureAtlas.class);

        // Tmx Map
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("maps/map_teste.tmx", TiledMap.class);

        // Load all sounds
        assetManager.load(SOUND_STAGE_START, Sound.class);
        assetManager.load(SOUND_PICK_POWER_UP, Sound.class);
        assetManager.load(SOUND_STAGE_CLEAR, Sound.class);
        assetManager.load(SOUND_MISS, Sound.class);
        assetManager.load(SOUND_LEFT_RIGHT_WALK, Sound.class);
        assetManager.load(SOUND_DOWN_UP_WALK, Sound.class);
        assetManager.load(SOUND_PUT_BOMB, Sound.class);
        assetManager.load(SOUND_CLEAR_ENEMIES, Sound.class);
        assetManager.load(SOUND_BOMB_EXPLODES, Sound.class);

        // Load all musics
        assetManager.load(MUSIC_TITLE_SCREEN, Music.class);
        assetManager.load(MUSIC_MAIN, Music.class);
        assetManager.load(MUSIC_ENDING, Music.class);
        assetManager.load(MUSIC_GAME_OVER, Music.class);
        assetManager.load(MUSIC_BONUS_STAGE, Music.class);

        assetManager.finishLoading();
    }

    public static GameManager getInstance() {
        return instance;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void setWorld(World world) {
        this.b2World = world;
    }

    public World getWorld() {
        return b2World;
    }

    public void playMusic(String sound, boolean loop) {
        Music music = assetManager.get(sound, Music.class);
        music.setLooping(loop);

        currentMusic = sound;
        music.play();
    }

    public void stopMusic() {
        if (currentMusic.isEmpty()) {
            return;
        }
        Music music = assetManager.get(currentMusic, Music.class);
        if (music.isPlaying()) {
            music.stop();
        }
    }

    public boolean musicIsPlaying(){
        if (currentMusic.isEmpty()) {
            return false;
        }
        Music music = assetManager.get(currentMusic, Music.class);
        return music.isPlaying();
    }

    public void playEffect(String effect) {
        assetManager.get(effect, Sound.class).play();
    }


    public void playEffect(String effect, float volume) {
        assetManager.get(effect, Sound.class).play(volume);
    }

    public static List<Position> generateSpawnArea(Position fromv, Position toV) {
        List<Position> spawnArea = new ArrayList<Position>();

        for(int y = fromv.getY(); y >= toV.getY(); y--){
                for(int x = fromv.getX(); x <= toV.getX(); x++){
                        spawnArea.add(new Position(x, y));
                }
        }

        return spawnArea;
    }

    public static List<Position> generateSpawnArea(Position fromv, int range) {
        List<Position> spawnArea = new ArrayList<Position>();
    
        int startX = fromv.getX() - range;
        int endX = fromv.getX() + range;
        int startY = fromv.getY() + range;
        int endY = fromv.getY() - range;
    
        for (int y = startY; y >= endY; y--) {
            for (int x = startX; x <= endX; x++) {
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