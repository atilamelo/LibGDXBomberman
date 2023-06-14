package com.mygdx.game.utils;

public class Constants {
    public static final String GAME_NAME = "Bomberman NES";

    public static final int APP_WIDTH = 800;
    public static final int APP_HEIGHT = 480;
    public static final float WORLD_TO_SCREEN = 1;


    public static final float BOMBERMAN_X = 5;
    public static final float BOMBERMAN_Y = 5; 
    public static final float BOMBERMAN_WIDTH = 32f; 
    public static final float BOMBERMAN_HEIGHT = 32f;
    public static final float BOMBERMAN_VELOCITY = 32f * 5;
    public static final float BOMBERMAN_DENSITY = 0.5f;

    public static final String BOMBERMAN_ATLAS_PATH = "assets/BombermanAtlas.atlas";
    public static final String[] BOMBERMAN_UP_REGION_NAMES = new String[] {"runningUp01", "runningUp02", "runningUp03"};
    public static final String[] BOMBERMAN_DOWN_REGION_NAMES = new String[] {"runningDown01", "runningDown02", "runningDown03"};
    public static final String[] BOMBERMAN_LEFT_REGION_NAMES = new String[] {"runningLeft01", "runningLeft02", "runningLeft03"};
    public static final String[] BOMBERMAN_RIGHT_REGION_NAMES = new String[] {"runningRight01", "runningRight02", "runningRight03"};


}
