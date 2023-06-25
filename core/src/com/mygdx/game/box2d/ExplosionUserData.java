package com.mygdx.game.box2d;

import com.mygdx.game.enums.UserDataType;

public class ExplosionUserData extends UserData{
    public ExplosionUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.EXPLOSION;
    }
    
}
