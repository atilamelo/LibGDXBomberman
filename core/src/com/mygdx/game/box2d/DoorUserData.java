package com.mygdx.game.box2d;

import com.mygdx.game.enums.UserDataType;
import com.mygdx.game.systems.RandomPlacement.Position;

public class DoorUserData extends UserData{
    public Position position;

    public DoorUserData(float width, float height, Position position) {
        super(width, height);
        this.position = position;
        userDataType = UserDataType.DOOR;
    }
}
