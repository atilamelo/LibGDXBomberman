package com.mygdx.game.networking;

import java.util.UUID;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.actors.Enemy;
import com.mygdx.game.configs.EnemyConfig;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.systems.RandomPlacement.Position;
import com.mygdx.game.utils.WorldUtils;

public class VirtualEnemy {
    public UUID id;
    public Body body;
    public Enemy actor;
    public EnemyConfig config;

    public VirtualEnemy(GameStage stage, Position pos, EnemyConfig config) {
        this.id = UUID.randomUUID();
        this.config = config;
        this.body = WorldUtils.createEnemy(pos, config);
        this.actor = new Enemy(id, body, config);
        stage.addActor(actor);
    }

    public VirtualEnemy(UUID id, GameStage stage, Position pos, EnemyConfig config) {
        this.id = id;
        this.config = config;
        this.body = WorldUtils.createEnemy(pos, config);
        this.actor = new Enemy(id, body, config);
        stage.addActor(actor);
    }

    @Override
    public String toString() {
        return "VirtualEnemy [id=" + id + ", body=" + body + ", actor=" + actor + ", config=" + config + "]";
    }
}
