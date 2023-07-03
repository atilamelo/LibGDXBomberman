package com.mygdx.game.box2d;

import com.mygdx.game.actors.Brick.State;
import com.mygdx.game.enums.UserDataType;

public class BrickUserData extends UserData{
    public State state;

    public BrickUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.BRICK;
        state = State.ACTIVE;
    }
    
    public State getState() {
        return state;
    }

    public void setState(State stateBrick) {
        state = stateBrick;
    }
}
