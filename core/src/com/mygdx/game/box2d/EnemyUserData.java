package com.mygdx.game.box2d;

import com.mygdx.game.enums.UserDataType;

public class EnemyUserData extends UserData {

    public EnemyUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.ENEMY;
    }
    
}
