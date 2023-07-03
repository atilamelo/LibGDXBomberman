package com.mygdx.game.configs;

public class LevelConfig {
    public int amountOfBricks;
    public int amountOfBalloms;
    public int amountOfOnils;
    public int amountOfDolls;
    
    public LevelConfig(int amountOfBricks, int amountOfBalloms, int amountOfOnils, int amountOfDolls) {
        this.amountOfBricks = amountOfBricks;
        this.amountOfBalloms = amountOfBalloms;
        this.amountOfOnils = amountOfOnils;
        this.amountOfDolls = amountOfDolls;
    }

    public int getTotalOfEnemies() {
        return amountOfBalloms + amountOfOnils + amountOfDolls;
    }
}
