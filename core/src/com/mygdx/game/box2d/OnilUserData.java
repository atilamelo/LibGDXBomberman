package com.mygdx.game.box2d;

import com.mygdx.game.enums.UserDataType;

public class OnilUserData extends UserData{

    public OnilUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.ENEMY;
    }
}
