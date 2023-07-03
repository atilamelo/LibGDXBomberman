package com.mygdx.game.configs;

import com.mygdx.game.actors.PowerUp.PowerUpType;

public class LevelConfig {
    public int amountOfBricks;
    public int amountOfBalloms;
    public int amountOfOnils;
    public int amountOfDolls;
    public int amountOfMinvos;
    public int amountOfKondorias;
    public int amountOfOvapis;
    public int amountOfPass;
    public int amountOfPontan;
    public PowerUpType powerUpType;

    public LevelConfig(int amountOfBricks, int amountOfBalloms, int amountOfOnils, int amountOfDolls,
            int amountOfMinvos, int amountOfKondorias, int amountOfOvapis, int amountOfPass, int amountOfPontan, PowerUpType powerUpType) {
        this.amountOfBricks = amountOfBricks;
        this.amountOfBalloms = amountOfBalloms;
        this.amountOfOnils = amountOfOnils;
        this.amountOfDolls = amountOfDolls;
        this.amountOfMinvos = amountOfMinvos;
        this.amountOfKondorias = amountOfKondorias;
        this.amountOfOvapis = amountOfOvapis;
        this.amountOfPass = amountOfPass;
        this.amountOfPontan = amountOfPontan;
        this.powerUpType = powerUpType;
    }

    public int getTotalOfEnemies() {
        return amountOfBalloms + amountOfOnils + amountOfDolls + amountOfMinvos + amountOfKondorias + amountOfOvapis
                + amountOfPass + amountOfPontan;
    }

    public static LevelConfig[] levels = {
            new LevelConfig(
                    1,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    PowerUpType.BOMB_UP),
            new LevelConfig(1, 0, 0, 0, 0, 0, 0, 0, 0, PowerUpType.BOMB_UP),
            new LevelConfig(1, 0, 0, 0, 0, 0, 0, 0, 0, PowerUpType.BOMB_UP),
            new LevelConfig(1, 0, 0, 0, 0, 0, 0, 0, 0, PowerUpType.BOMB_UP),
            new LevelConfig(1, 0, 0, 0, 0, 0, 0, 0, 0, PowerUpType.BOMB_UP),
    };
}
