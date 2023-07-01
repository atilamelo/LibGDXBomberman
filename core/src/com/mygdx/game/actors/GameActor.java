package com.mygdx.game.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.box2d.BallomUserData;
import com.mygdx.game.box2d.BombUserData;
import com.mygdx.game.box2d.BrickUserData;
import com.mygdx.game.box2d.UserData;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;


public abstract class GameActor extends Actor {

    protected Body body;
    protected UserData userData;
    protected Rectangle screenRectangle;
    protected GameManager gameManager;

    public GameActor(Body body) {
        this.body = body;
        this.userData = (UserData) body.getUserData();
        this.gameManager = GameManager.getInstance();
        screenRectangle = new Rectangle();
        
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isAlive()) {
            updateRectangle();
        } else {
            // System.out.println("Corpo destruído: " + userData);
            userData.isFlaggedForDelete = true;
            if(userData instanceof BallomUserData){
                gameManager.enemiesLeft--;
                System.out.println("Inimigos restantes: " + gameManager.enemiesLeft);
            }else if(userData instanceof BombUserData){
                gameManager.bombsOnScreen--;
                System.out.println("Bombas ativas restantes: " + gameManager.bombsOnScreen);
            }else if(userData instanceof BrickUserData){
                BrickUserData brickUserData = (BrickUserData) userData;
                Brick brick = (Brick) brickUserData.getActor();
                if(brick.haveDoor()){
                    Body doorBody = WorldUtils.createDoor(brick.getPosition());
                    Door door = new Door(doorBody);
                    brick.getParent().addActor(door);
                }
            }
            remove();
        }
    }

    public abstract UserData getUserData();

    public abstract boolean isAlive();

    private void updateRectangle() {
        screenRectangle.x = body.getPosition().x - userData.getWidth() / 2;
        screenRectangle.y = body.getPosition().y - userData.getHeight() / 2; 
        screenRectangle.width = userData.getWidth();
        screenRectangle.height = userData.getHeight();
    }

    public Rectangle getScreenRectangle() {
        return screenRectangle;
    }


}
