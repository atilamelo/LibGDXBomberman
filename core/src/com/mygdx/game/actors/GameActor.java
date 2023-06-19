package com.mygdx.game.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.box2d.UserData;


public abstract class GameActor extends Actor {

    protected Body body;
    protected UserData userData;
    protected Rectangle screenRectangle;

    public GameActor(Body body) {
        this.body = body;
        this.userData = (UserData) body.getUserData();
        screenRectangle = new Rectangle();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (body.getUserData() != null) {
            updateRectangle();
        } else {
            // This means the world destroyed the body (enemy or runner went out of bounds)
            remove();
        }
    }

    public abstract UserData getUserData();

    private void updateRectangle() {
        screenRectangle.x = body.getPosition().x - userData.getWidth() / 2;
        screenRectangle.y = body.getPosition().y - userData.getHeight() / 2; 
        screenRectangle.width = userData.getWidth();
        screenRectangle.height = userData.getHeight();
        // System.out.println("UpdateRectangle: " + screenRectangle.x + " " + screenRectangle.y + " " + screenRectangle.width + " " + screenRectangle.height);
    }

    public Rectangle getScreenRectangle() {
        return screenRectangle;
    }


}
