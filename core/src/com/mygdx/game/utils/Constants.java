package com.mygdx.game.utils;

public class Constants {
    public static final String GAME_NAME = "Bomberman NES";

    public static final int APP_WIDTH = 29;
    public static final int APP_HEIGHT = 13;
    public static final float PPM = 16;
    public static final int FPS = 60;
    public static final int GAME_WIDTH = APP_WIDTH * (int) PPM * 10;
    public static final int GAME_HEIGHT = APP_HEIGHT * (int) PPM * 10;

    // Bomberman properties
    public static final float BOMBERMAN_X = 2;
    public static final float BOMBERMAN_Y = 2; 
    public static final float BOMBERMAN_WIDTH = 1f; 
    public static final float BOMBERMAN_HEIGHT = 0.8f;
    public static final float BOMBERMAN_B2D_WIDTH = 0.3f; 
    public static final float BOMBERMAN_B2D_HEIGHT = 0.4f;

    public static final float BOMBERMAN_VELOCITY = 2f;
    public static final float BOMBERMAN_DENSITY = 1f;

    // Tiled Maps properties
    public static final float TILES_WIDTH = 1f;
    public static final float TILES_HEIGHT = 1f;


    public static final String BOMBERMAN_ATLAS_PATH = "assets/BombermanAtlas.atlas";
    public static final String[] BOMBERMAN_UP_REGION_NAMES = new String[] {"runningUp01", "runningUp02", "runningUp03"};
    public static final String[] BOMBERMAN_DOWN_REGION_NAMES = new String[] {"runningDown01", "runningDown02", "runningDown03"};
    public static final String[] BOMBERMAN_LEFT_REGION_NAMES = new String[] {"runningLeft01", "runningLeft02", "runningLeft03"};
    public static final String[] BOMBERMAN_RIGHT_REGION_NAMES = new String[] {"runningRight01", "runningRight02", "runningRight03"};


}
