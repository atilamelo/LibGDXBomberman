package com.mygdx.game.box2d;

import com.mygdx.game.actors.Bomberman.State;
import com.mygdx.game.enums.UserDataType;
import com.mygdx.game.utils.GameManager;

public class BombermanUserData extends UserData {
    public State state;
    private float linearVelocity;

    public BombermanUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.BOMBERMAN;
        linearVelocity = GameManager.BOMBERMAN_INITIAL_SPEED;
        state = State.IDLE_DOWN; 
    }

    public float getLinearVelocity() {
        return linearVelocity;
    }

    public void setLinearVelocity(float linearVelocity) {
        this.linearVelocity = linearVelocity;
    }
    
    public State getState() {
        return state;
    }

    public void setState(State state){
        this.state = state;
    }
}