package com.mygdx.game.actors;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.box2d.UserData;

public class Wall extends GameActor {

    public Wall(Body body) {
        super(body);

    }

    @Override
    public UserData getUserData() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserData'");
    }

}
