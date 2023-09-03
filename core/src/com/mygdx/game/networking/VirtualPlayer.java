package com.mygdx.game.networking;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.actors.Bomberman;
import com.mygdx.game.box2d.BombermanUserData;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.WorldUtils;

public class VirtualPlayer {
    public int id;
    public String name;
    public Body body;
    public Bomberman actor;
    
    @Override
    public String toString() {
        return "VirtualPlayer [id=" + id + ", name=" + name + ", body=" + body + ", actor=" + actor + "]";
    }

    public VirtualPlayer(int id, String name, GameStage stage) {
        this.id = id;
        this.name = name;
        this.body = WorldUtils.createBomberman();
        this.actor = new Bomberman(body, stage, true);

        BombermanUserData userData = (BombermanUserData) this.body.getUserData();
        userData.playerId = id;

        stage.addActor(actor);
    }
}
