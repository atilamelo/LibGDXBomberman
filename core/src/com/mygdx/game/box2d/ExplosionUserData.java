package com.mygdx.game.box2d;

import com.mygdx.game.actors.Explosion.State;
import com.mygdx.game.enums.UserDataType;

public class ExplosionUserData extends UserData{
    public State state;

    public ExplosionUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.EXPLOSION;
        state = State.EXPLODING;
    }
    
    public State getState() {
        return state;
    }
}
