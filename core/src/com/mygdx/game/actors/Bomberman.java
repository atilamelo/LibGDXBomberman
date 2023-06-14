package com.mygdx.game.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.box2d.BombermanUserData;

public class Bomberman extends GameActor {
    
    public Bomberman(Body body) {
        super(body);
    }

    @Override
    public BombermanUserData getUserData() {
        return (BombermanUserData) userData;
    }

    public void move(Vector2 vector){
        body.setLinearVelocity(vector);
    }

}
