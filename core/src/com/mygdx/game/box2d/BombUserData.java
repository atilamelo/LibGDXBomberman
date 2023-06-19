package com.mygdx.game.box2d;

import com.mygdx.game.enums.UserDataType;

public class BombUserData extends UserData{

    public BombUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.BOMB;
    }
    
}
