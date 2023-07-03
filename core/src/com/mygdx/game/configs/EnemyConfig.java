package com.mygdx.game.configs;

import com.mygdx.game.utils.GameManager;

public class EnemyConfig {
    public int hp;
    public float speed;
    public short[] maskBits;
    public float intersectionChangeChance;
    public int rangePursue;

    public EnemyConfig(int hp, float speed, short[] maskBits, float intersectionChangeChance, int rangePursue) {
        this.hp = hp;
        this.speed = speed;
        this.maskBits = maskBits;
        this.intersectionChangeChance = intersectionChangeChance;
        this.rangePursue = rangePursue;
    }

    public final static EnemyConfig ballonConfig = new EnemyConfig(
        1,
        GameManager.ENEMY_SLOW_SPEED,
        new short[] { GameManager.WALL_BIT, GameManager.BRICK_BIT, GameManager.BOMB_BIT },
        GameManager.ENEMY_LOWQI_INTERSECTION_CHANGE,
        GameManager.ENEMY_LOWQI_RANGE_PURSUE
    );

    public final static EnemyConfig onilConfig = new EnemyConfig(
        1,
        GameManager.ENEMY_NORMAL_SPEED,
        new short[] { GameManager.WALL_BIT, GameManager.BRICK_BIT, GameManager.BOMB_BIT },
        GameManager.ENEMY_MEDIUMQI_INTERSECTION_CHANGE,
        GameManager.ENEMY_MEDIUMQI_RANGE_PURSUE
    );

    
}
