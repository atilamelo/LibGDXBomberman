package com.mygdx.game.configs;

import com.mygdx.game.actors.enemies.Enemy;
import com.mygdx.game.utils.GameManager;

public class EnemyConfig {
    public int hp;
    public float speed;
    public short[] maskBits;
    public float intersectionChangeChance;
    public int rangePursue;
    public String[] leftAnimation;
    public String[] rightAnimation;
    public String[] dyingAnimation;

    public EnemyConfig(int hp, float speed, short[] maskBits, float intersectionChangeChance, int rangePursue,
            String[] leftAnimation, String[] rightAnimation, String[] dyingAnimation) {
        this.hp = hp;
        this.speed = speed;
        this.maskBits = maskBits;
        this.intersectionChangeChance = intersectionChangeChance;
        this.rangePursue = rangePursue;
        this.leftAnimation = leftAnimation;
        this.rightAnimation = rightAnimation;
        this.dyingAnimation = dyingAnimation;
    }

    public final static EnemyConfig ballonConfig = new EnemyConfig(
        1,
        GameManager.ENEMY_SLOW_SPEED,
        new short[] { GameManager.WALL_BIT, GameManager.BRICK_BIT, GameManager.BOMB_BIT },
        GameManager.ENEMY_LOWQI_INTERSECTION_CHANGE,
        GameManager.ENEMY_LOWQI_RANGE_PURSUE,
        GameManager.BALLON_LEFT_REGION_NAMES,
        GameManager.BALLON_RIGHT_REGION_NAMES,
        GameManager.BALLON_DYING_REGION_NAMES
    );

    public final static EnemyConfig onilConfig = new EnemyConfig(
        1,
        GameManager.ENEMY_NORMAL_SPEED,
        new short[] { GameManager.WALL_BIT, GameManager.BRICK_BIT, GameManager.BOMB_BIT },
        GameManager.ENEMY_MEDIUMQI_INTERSECTION_CHANGE,
        GameManager.ENEMY_MEDIUMQI_RANGE_PURSUE,
        GameManager.ONIL_LEFT_REGION_NAMES,
        GameManager.ONIL_RIGHT_REGION_NAMES,
        GameManager.ONIL_DYING_REGION_NAMES
    );

    public final static EnemyConfig dollConfig = new EnemyConfig(
        1,
        GameManager.ENEMY_NORMAL_SPEED,
        new short[] { GameManager.WALL_BIT, GameManager.BRICK_BIT, GameManager.BOMB_BIT },
        GameManager.ENEMY_LOWQI_INTERSECTION_CHANGE,
        GameManager.ENEMY_LOWQI_RANGE_PURSUE,
        GameManager.DOLL_LEFT_REGION_NAMES,
        GameManager.DOLL_RIGHT_REGION_NAMES,
        GameManager.DOLL_DYING_REGION_NAMES 
    );

    public final static EnemyConfig minvoConfig = new EnemyConfig(
        1,
        GameManager.ENEMY_FAST_SPEED,
        new short[] { GameManager.WALL_BIT, GameManager.BRICK_BIT, GameManager.BOMB_BIT },
        GameManager.ENEMY_MEDIUMQI_INTERSECTION_CHANGE,
        GameManager.ENEMY_MEDIUMQI_RANGE_PURSUE,
        GameManager.MINVO_LEFT_REGION_NAMES,
        GameManager.MINVO_RIGHT_REGION_NAMES,
        GameManager.MINVO_DYING_REGION_NAMES
    );

    public final static EnemyConfig kondoriaConfig = new EnemyConfig(
        1,
        GameManager.ENEMY_SLOWEST_SPEED,
        new short[] { GameManager.WALL_BIT, GameManager.BRICK_BIT, GameManager.BOMB_BIT },
        GameManager.ENEMY_HIGHQI_INTERSECTION_CHANGE,
        GameManager.ENEMY_HIGHQI_RANGE_PURSUE,
        GameManager.KONDORIA_LEFT_REGION_NAMES,
        GameManager.KONDORIA_RIGHT_REGION_NAMES,
        GameManager.KONDORIA_DYING_REGION_NAMES
    );

    public final static EnemyConfig ovapiConfig = new EnemyConfig(
        1,
        GameManager.ENEMY_SLOW_SPEED,
        new short[] { GameManager.WALL_BIT, GameManager.BRICK_BIT, GameManager.BOMB_BIT },
        GameManager.ENEMY_MEDIUMQI_INTERSECTION_CHANGE,
        GameManager.ENEMY_MEDIUMQI_RANGE_PURSUE,
        GameManager.OVAPI_LEFT_REGION_NAMES,
        GameManager.OVAPI_RIGHT_REGION_NAMES,
        GameManager.OVAPI_DYING_REGION_NAMES
    );

    public final static EnemyConfig passConfig = new EnemyConfig(
        1,
        GameManager.ENEMY_FAST_SPEED,
        new short[] { GameManager.WALL_BIT, GameManager.BRICK_BIT, GameManager.BOMB_BIT },
        GameManager.ENEMY_HIGHQI_INTERSECTION_CHANGE,
        GameManager.ENEMY_HIGHQI_RANGE_PURSUE,
        GameManager.PASS_LEFT_REGION_NAMES,
        GameManager.PASS_RIGHT_REGION_NAMES,
        GameManager.PASS_DYING_REGION_NAMES
    );

    public final static EnemyConfig pontanConfig = new EnemyConfig(
        1,
        GameManager.ENEMY_FAST_SPEED,
        new short[] { GameManager.WALL_BIT, GameManager.BOMB_BIT },
        GameManager.ENEMY_HIGHQI_INTERSECTION_CHANGE,
        GameManager.ENEMY_HIGHQI_RANGE_PURSUE,
        GameManager.PONTAN_LEFT_REGION_NAMES,
        GameManager.PONTAN_RIGHT_REGION_NAMES,
        GameManager.PONTAN_DYING_REGION_NAMES
    );
    
}
