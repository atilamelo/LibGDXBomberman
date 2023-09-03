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
import com.mygdx.game.networking.Network.BombermanDie;
import com.mygdx.game.stages.ServerStage;
import com.mygdx.game.utils.GameManager;

public class ServerWorldListener extends WorldListener {
    ServerStage serverStage;

    public ServerWorldListener(ServerStage serverStage) {
        this.serverStage = serverStage;
    }

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
                    BombermanUserData bombermanData = (BombermanUserData) fixB.getBody().getUserData();
                    Bomberman bombermanActor = (Bomberman) bombermanData.getActor();
                    System.out.println("BOMBERMAN ID: " + bombermanData.playerId);
                    bombermanActor.die(UserDataType.EXPLOSION);

                    BombermanDie die = new BombermanDie(bombermanData.playerId, UserDataType.EXPLOSION);
                    System.out.println("Sending packet to all clients");
                    System.out.println("\t" + die);
                    serverStage.server.sendToAllTCP(die);

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
                    UserData enemyData = (UserData) fixB.getBody().getUserData();
                    Enemy enemyActor = (Enemy) enemyData.getActor();
                    enemyActor.takeDamage(1);
                    break;
                case GameManager.DOOR_BIT:
                    UserData doorData = (UserData) fixB.getBody().getUserData();
                    Door doorActor = (Door) doorData.getActor();
                    doorActor.hit();
                    break;
            }
        }

        if (categoryBitsB == GameManager.EXPLOSION_BIT) {
            switch (categoryBitsA) {
                case GameManager.PLAYER_BIT:
                    BombermanUserData bombermanData = (BombermanUserData) fixA.getBody().getUserData();
                    Bomberman bombermanActor = (Bomberman) bombermanData.getActor();
                    bombermanActor.die(UserDataType.EXPLOSION);

                    BombermanDie die = new BombermanDie(bombermanData.playerId, UserDataType.EXPLOSION);
                    System.out.println("Sending packet to all clients");
                    System.out.println("\t" + die);
                    serverStage.server.sendToAllTCP(die);
                    
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
                    UserData enemyData = (UserData) fixA.getBody().getUserData();
                    Enemy enemyActor = (Enemy) enemyData.getActor();
                    enemyActor.takeDamage(1);
                    break;
                case GameManager.DOOR_BIT:
                    UserData doorData = (UserData) fixA.getBody().getUserData();
                    Door doorActor = (Door) doorData.getActor();
                    doorActor.hit();
                    break;
            }
        }

        if (categoryBitsA == GameManager.PLAYER_BIT) {
            switch (categoryBitsB) {
                case GameManager.ENEMY_BIT:
                    BombermanUserData bombermanData = (BombermanUserData) fixA.getBody().getUserData();
                    Bomberman bombermanActor = (Bomberman) bombermanData.getActor();
                    bombermanActor.die(UserDataType.ENEMY);
                    break;
                case GameManager.POWER_UP_BIT:
                    UserData powerUpData = (UserData) fixB.getBody().getUserData();
                    PowerUp powerUp = (PowerUp) powerUpData.getActor();
                    powerUp.pick();
                    break;
                case GameManager.DOOR_BIT:
                    UserData doorData = (UserData) fixB.getBody().getUserData();
                    Door doorActor = (Door) doorData.getActor();
                    doorActor.enter();
                    break;
            }
        }
        if (categoryBitsB == GameManager.PLAYER_BIT) {
            switch (categoryBitsA) {
                case GameManager.ENEMY_BIT:
                    BombermanUserData bombermanData = (BombermanUserData) fixB.getBody().getUserData();
                    Bomberman bombermanActor = (Bomberman) bombermanData.getActor();
                    bombermanActor.die(UserDataType.ENEMY);
                    break;
                case GameManager.POWER_UP_BIT:
                    UserData powerUpData = (UserData) fixA.getBody().getUserData();
                    PowerUp powerUp = (PowerUp) powerUpData.getActor();
                    powerUp.pick();
                    break;
                case GameManager.DOOR_BIT:
                    UserData doorData = (UserData) fixA.getBody().getUserData();
                    Door doorActor = (Door) doorData.getActor();
                    doorActor.enter();
                    break;
            }
        }

    }




}