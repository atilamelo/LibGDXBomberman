package com.mygdx.game.configs;

import com.mygdx.game.utils.GameManager;

public class BombermanConfig {
    public int bombRange;
    public int bombCount;
    public boolean remoteControl;
    public boolean flamePass;
    public boolean brickPass;
    public boolean invencible;
    public float speed;
    public boolean bombPass;
    public int lifes;

    public BombermanConfig(int bombRange, int bombCount, boolean remoteControl, boolean flamePass, boolean brickPass,
            boolean invencible, float speed, boolean bombPass, int lifes) {
        this.bombRange = bombRange;
        this.bombCount = bombCount;
        this.remoteControl = remoteControl;
        this.flamePass = flamePass;
        this.brickPass = brickPass;
        this.invencible = invencible;
        this.speed = speed;
        this.bombPass = bombPass;
        this.lifes = lifes;
    }

    public static BombermanConfig bombermanCheatConfig = new BombermanConfig(
            10,
            10,
            true,
            true,
            true,
            true,
            2.5f,
            true,
            100);

    public static BombermanConfig initialBombermanConfig = new BombermanConfig(
            1,
            1,
            false,
            false,
            false,
            false,
            GameManager.BOMBERMAN_INITIAL_SPEED,
            false,
            3);

}
