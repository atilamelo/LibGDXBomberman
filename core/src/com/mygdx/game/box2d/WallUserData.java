package com.mygdx.game.box2d;

import com.mygdx.game.actors.Wall;
import com.mygdx.game.enums.UserDataType;

public class WallUserData extends UserData {
    
    public WallUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.WALL;
    }

}
