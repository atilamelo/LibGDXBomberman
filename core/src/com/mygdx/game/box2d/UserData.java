package com.mygdx.game.box2d;

import com.mygdx.game.actors.GameActor;
import com.mygdx.game.enums.UserDataType;

public abstract class UserData {

    protected UserDataType userDataType;
    protected float width;
    protected float height;
    protected GameActor actor; 

    public UserData(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public UserDataType getUserDataType() {
        return userDataType;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setActor(GameActor actor) {
        this.actor = actor;
    }

    public GameActor getActor(){
        return actor;
    }

}
