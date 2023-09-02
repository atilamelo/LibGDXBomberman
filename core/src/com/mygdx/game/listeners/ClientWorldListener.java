package com.mygdx.game.listeners;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.game.actors.Bomb;
import com.mygdx.game.actors.Bomberman;
import com.mygdx.game.actors.Brick;
import com.mygdx.game.actors.Door;
import com.mygdx.game.actors.Enemy;
import com.mygdx.game.actors.PowerUp;
import com.mygdx.game.box2d.BombUserData;
import com.mygdx.game.box2d.BombermanUserData;
import com.mygdx.game.box2d.BrickUserData;
import com.mygdx.game.box2d.UserData;
import com.mygdx.game.enums.UserDataType;
import com.mygdx.game.utils.GameManager;

public class ClientWorldListener extends WorldListener{
     
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        short categoryBitsA = fixA.getFilterData().categoryBits;
        short categoryBitsB = fixB.getFilterData().categoryBits;

        if (fixA == null || fixB == null)
            return;

        if (categoryBitsA == GameManager.EXPLOSION_BIT) {
            switch (categoryBitsB) {
                case GameManager.PLAYER_BIT:
                    break;
                case GameManager.BOMB_BIT:
                    BombUserData bombData = (BombUserData) fixB.getBody().getUserData();
                    Bomb bombActor = (Bomb) bombData.getActor();
                    bombActor.flagWillExploded = true;
                    break;
                case GameManager.BRICK_BIT:
                    BrickUserData brickData = (BrickUserData) fixB.getBody().getUserData();
                    Brick brickActor = (Brick) brickData.getActor();
                    brickActor.explode();
                    break;
                case GameManager.ENEMY_BIT:
                    break;
                case GameManager.DOOR_BIT:
                    break;
            }
        }

        if (categoryBitsB == GameManager.EXPLOSION_BIT) {
            switch (categoryBitsA) {
                case GameManager.PLAYER_BIT:
                    break;
                case GameManager.BOMB_BIT:
                    BombUserData bombData = (BombUserData) fixA.getBody().getUserData();
                    Bomb bombActor = (Bomb) bombData.getActor();
                    bombActor.flagWillExploded = true;
                    break;
                case GameManager.BRICK_BIT:
                    BrickUserData brickData = (BrickUserData) fixA.getBody().getUserData();
                    Brick brickActor = (Brick) brickData.getActor();
                    brickActor.explode();
                    break;
                case GameManager.ENEMY_BIT:
                    break;
                case GameManager.DOOR_BIT:
                    break;
            }
        }

        if (categoryBitsA == GameManager.PLAYER_BIT) {
            switch (categoryBitsB) {
                case GameManager.ENEMY_BIT:
                    break;
                case GameManager.POWER_UP_BIT:
                    break;
                case GameManager.DOOR_BIT:
                    break;
            }
        }
        if (categoryBitsB == GameManager.PLAYER_BIT) {
            switch (categoryBitsA) {
                case GameManager.ENEMY_BIT:
                    break;
                case GameManager.POWER_UP_BIT:
                    break;
                case GameManager.DOOR_BIT:
            }
        }    
    }
}
