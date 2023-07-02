package com.mygdx.game.box2d;

import com.mygdx.game.systems.RandomPlacement.Position;

public class PowerUpUserData extends UserData{
    public Position position;

    public PowerUpUserData(float width, float height, Position position) {
        super(width, height);
        this.position = position;
    }
}
