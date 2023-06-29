package com.mygdx.game.box2d;

import com.mygdx.game.enums.StateBomb;
import com.mygdx.game.enums.UserDataType;

public class BombUserData extends UserData{
    public StateBomb state;

    public BombUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.BOMB;
        state = StateBomb.ACTIVE;
    }

    public StateBomb getState() {
        return state;
    }

    public void setState(StateBomb state) {
        this.state = state;
    }
    
    
}
