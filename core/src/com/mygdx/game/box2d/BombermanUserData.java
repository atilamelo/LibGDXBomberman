package com.mygdx.game.box2d;

import com.mygdx.game.enums.StateBomberman;
import com.mygdx.game.enums.UserDataType;
import com.mygdx.game.utils.GameManager;

public class BombermanUserData extends UserData {
    public StateBomberman state;
    private float linearVelocity;

    public BombermanUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.BOMBERMAN;
        linearVelocity = GameManager.BOMBERMAN_VELOCITY;
        state = StateBomberman.IDLE_DOWN; 
    }

    // public BombermanUserData() {
    //     super();
    //     userDataType = UserDataType.BOMBERMAN;
    //     linearVelocity = GameManager.BOMBERMAN_VELOCITY;
    // }

    public float getLinearVelocity() {
        return linearVelocity;
    }

    public void setLinearVelocity(float linearVelocity) {
        this.linearVelocity = linearVelocity;
    }
    
    public StateBomberman getState() {
        return state;
    }

    public void setState(StateBomberman state){
        this.state = state;
    }
}