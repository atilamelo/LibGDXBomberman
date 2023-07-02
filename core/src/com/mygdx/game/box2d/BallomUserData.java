package com.mygdx.game.box2d;

import com.mygdx.game.enums.UserDataType;

public class BallomUserData extends UserData {

    public BallomUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.ENEMY;
    }
    
}
