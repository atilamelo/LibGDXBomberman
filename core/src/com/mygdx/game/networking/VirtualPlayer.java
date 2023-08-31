package com.mygdx.game.networking;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.actors.Bomberman;
import com.mygdx.game.utils.WorldUtils;

public class VirtualPlayer {
    public int id;
    public String name;
    public Body body;
    public Bomberman actor;
    
    public VirtualPlayer(int id, String name) {
        this.id = id;
        this.name = name;
        this.body = WorldUtils.createBomberman();
    }
}
