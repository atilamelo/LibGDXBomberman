package com.mygdx.game.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.box2d.BombUserData;
import com.mygdx.game.box2d.BrickUserData;
import com.mygdx.game.box2d.PowerUpUserData;
import com.mygdx.game.box2d.UserData;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.systems.CoordinateConverter;
import com.mygdx.game.systems.RandomPlacement.Position;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;


public abstract class GameActor extends Actor {

    protected Body body;
    protected UserData userData;
    protected Rectangle screenRectangle;
    protected GameManager gameManager;
    protected Position tilePosition;
    protected Position matrixPosition;
    protected float stateTime;


    public GameActor(Body body) {
        this.body = body;
        this.userData = (UserData) body.getUserData();
        this.gameManager = GameManager.getInstance();
        this.stateTime = 0f;
        screenRectangle = new Rectangle();
        int x = Math.round(body.getPosition().x);
        int y = Math.round(body.getPosition().y);
        tilePosition = new Position(x, y);
        matrixPosition = new Position(x, (GameManager.MAP_HEIGHT / 2) - y);

        
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    
        Vector2 bodyCenter = body.getWorldCenter();
        tilePosition.setX((int) Math.floor(bodyCenter.x));
        tilePosition.setY((int) Math.floor(bodyCenter.y));
        matrixPosition = CoordinateConverter.cartesianToMatrix(tilePosition);

        if (isAlive()) {
            updateRectangle();
        } else {
            userData.isFlaggedForDelete = true;
            GameActor actor = (GameActor) userData.getActor();

            if(actor instanceof Enemy){
                gameManager.enemiesLeft--;
                System.out.println("Inimigos restantes: " + gameManager.enemiesLeft);
            }else if(userData instanceof BombUserData){
                BombUserData bombUserData = (BombUserData) userData;
                Bomb bomb = (Bomb) bombUserData.getActor();
                GameStage stage = (GameStage) bomb.getStage();
                Bomberman bomberman = stage.getBomberman();

                bomberman.getBombsList().remove(bomb);
                System.out.println("Bombas ativas restantes: " + bomberman.getBombsList().size());
            }else if(userData instanceof BrickUserData){
                BrickUserData brickUserData = (BrickUserData) userData;
                Brick brick = (Brick) brickUserData.getActor();
                if(brick.haveDoor()){
                    Position doorPosition = new Position(brick.getPosition().getX(), brick.getPosition().getY());
                    Body doorBody = WorldUtils.createDoor(doorPosition);
                    Door door = new Door(doorBody);
                    brick.getParent().addActor(door);
                }

                /* Chance to generate Power Up */
                if(Math.random() < GameManager.POWER_UP_CHANCE){
                    Body powerUpBody = WorldUtils.createPowerUp(brick.getPosition());
                    brick.getParent().addActor(new PowerUp(powerUpBody));
                }
            }else if(userData instanceof PowerUpUserData){
                PowerUpUserData powerUpUserData = (PowerUpUserData) userData;
                PowerUp powerUp = (PowerUp) powerUpUserData.getActor();
                PowerUp.PowerUpType type = powerUp.getPowerUpType();
                GameStage stage = (GameStage) powerUp.getStage();
                Bomberman bomberman = stage.getBomberman();

                switch(type){
                    case BOMB_UP:
                        bomberman.increaseBombCount();
                        break;
                    case BRICK_PASS:
                        bomberman.activateBrickPass();
                        break;
                    case FIRE_UP:
                        bomberman.increaseBombRange();
                        break;
                    case FLAME_PASS:
                        bomberman.activateFlamePass();
                        break;
                    case INVENCIBLE:
                        bomberman.invencible();
                        break;
                    case REMOTE_CONTROL:
                        bomberman.activateRemoteControl();
                        break;
                    case SPEED_UP:
                        bomberman.speedUp();
                        break;
                }
                
                System.out.println("Power Up coletado: " + type);
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

    public Position getPosition(){
        return tilePosition;
    }


}
