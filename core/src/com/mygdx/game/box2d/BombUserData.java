package com.mygdx.game.box2d;

import com.mygdx.game.actors.Bomb.State;
import com.mygdx.game.enums.UserDataType;

public class BombUserData extends UserData{
    public State state;

    public BombUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.BOMB;
        state = State.ACTIVE;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
    
    
}
