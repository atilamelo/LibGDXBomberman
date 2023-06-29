package com.mygdx.game.box2d;

import com.mygdx.game.enums.StateBrick;
import com.mygdx.game.enums.UserDataType;

public class BrickUserData extends UserData{
    public StateBrick state;

    public BrickUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.BRICK;
        state = StateBrick.ACTIVE;
    }
    
    public StateBrick getState() {
        return state;
    }

    public void setState(StateBrick stateBrick) {
        state = stateBrick;
    }
}
