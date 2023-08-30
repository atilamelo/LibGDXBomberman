package com.mygdx.game.configs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
            int amountOfMinvos, int amountOfKondorias, int amountOfOvapis, int amountOfPass,
            int amountOfPontan, PowerUpType powerUpType) {
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
        return amountOfBalloms + amountOfOnils + amountOfDolls + amountOfMinvos + amountOfKondorias
                + amountOfOvapis
                + amountOfPass + amountOfPontan;
    }

    public static LevelConfig getLevelConfig(int level) {
        String csvFile = "core/src/com/mygdx/game/configs/LevelsConfig.csv";
        String line = "";
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);
                boolean isLevel = data[0].equals(String.valueOf(level));
                if (data.length == 11 && isLevel) {
                    int amountOfBricks = Integer.parseInt(data[1]);
                    int amountOfBalloms = Integer.parseInt(data[2]);
                    int amountOfOnils = Integer.parseInt(data[3]);
                    int amountOfDolls = Integer.parseInt(data[4]);
                    int amountOfMinvos = Integer.parseInt(data[5]);
                    int amountOfKondorias = Integer.parseInt(data[6]);
                    int amountOfOvapis = Integer.parseInt(data[7]);
                    int amountOfPass = Integer.parseInt(data[8]);
                    int amountOfPontan = Integer.parseInt(data[9]);
                    PowerUpType powerUpType = convertToPowerUpType(data[10]);

                    return new LevelConfig(amountOfBricks, amountOfBalloms, amountOfOnils,
                            amountOfDolls,
                            amountOfMinvos, amountOfKondorias, amountOfOvapis, amountOfPass,
                            amountOfPontan,
                            powerUpType);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static PowerUpType convertToPowerUpType(String powerUpTypeStr) {
        PowerUpType powerUpType;
        powerUpTypeStr = powerUpTypeStr.replace("\"", "");

        switch (powerUpTypeStr) {
            case "Bombs":
                powerUpType = PowerUpType.BOMB_UP;
                break;
            case "Flames":
                powerUpType = PowerUpType.FIRE_UP;
                break;
            case "Speed":
                powerUpType = PowerUpType.SPEED_UP;
                break;
            case "Detonator":
                powerUpType = PowerUpType.REMOTE_CONTROL;
                break;
            case "Wallpass":
                powerUpType = PowerUpType.BOMB_PASS;
                break;
            case "Brickpass":
                powerUpType = PowerUpType.BRICK_PASS;
                break;
            case "Flamepass":
                powerUpType = PowerUpType.FLAME_PASS;
                break;
            case "Invencible":
                powerUpType = PowerUpType.INVENCIBLE;
                break;
            default:
                throw new IllegalArgumentException("Invalid PowerUpType: " + powerUpTypeStr);
        }

        return powerUpType;
    }

    @Override
    public String toString() {
        return "LevelConfig{" +
                "amountOfBricks=" + amountOfBricks +
                ", amountOfBalloms=" + amountOfBalloms +
                ", amountOfOnils=" + amountOfOnils +
                ", amountOfDolls=" + amountOfDolls +
                ", amountOfMinvos=" + amountOfMinvos +
                ", amountOfKondorias=" + amountOfKondorias +
                ", amountOfOvapis=" + amountOfOvapis +
                ", amountOfPass=" + amountOfPass +
                ", amountOfPontan=" + amountOfPontan +
                ", powerUpType=" + powerUpType +
                '}';
    }
}
