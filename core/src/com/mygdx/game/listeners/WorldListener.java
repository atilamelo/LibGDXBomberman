package com.mygdx.game.listeners;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.actors.Bomb;
import com.mygdx.game.actors.Bomberman;
import com.mygdx.game.actors.Brick;
import com.mygdx.game.actors.enemies.Ballom;
import com.mygdx.game.box2d.BallomUserData;
import com.mygdx.game.box2d.BombUserData;
import com.mygdx.game.box2d.BombermanUserData;
import com.mygdx.game.box2d.BrickUserData;
import com.mygdx.game.utils.GameManager;

public class WorldListener implements ContactListener {

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
                    bombermanActor.die();
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
                    BallomUserData ballomData = (BallomUserData) fixB.getBody().getUserData();
                    Ballom ballomActor = (Ballom) ballomData.getActor();
                    ballomActor.takeDamage(1);
                    break;
            }
        }

        if (categoryBitsA == GameManager.ENEMY_BIT) {
            switch (categoryBitsB) {
                case GameManager.PLAYER_BIT:
                    BombermanUserData bombermanData = (BombermanUserData) fixA.getBody().getUserData();
                    Bomberman bombermanActor = (Bomberman) bombermanData.getActor();
                    bombermanActor.die();
                    break;
            }
        }

        if (categoryBitsB == GameManager.EXPLOSION_BIT) {
            switch (categoryBitsA) {
                case GameManager.PLAYER_BIT:
                    BombermanUserData bombermanData = (BombermanUserData) fixA.getBody().getUserData();
                    Bomberman bombermanActor = (Bomberman) bombermanData.getActor();
                    bombermanActor.die();
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
                    BallomUserData ballomData = (BallomUserData) fixA.getBody().getUserData();
                    Ballom ballomActor = (Ballom) ballomData.getActor();
                    ballomActor.takeDamage(1);
                    break;
            }
        }

        if (categoryBitsB == GameManager.ENEMY_BIT) {
            switch (categoryBitsA) {
                case GameManager.PLAYER_BIT:
                    BombermanUserData bombermanData = (BombermanUserData) fixA.getBody().getUserData();
                    Bomberman bombermanActor = (Bomberman) bombermanData.getActor();
                    bombermanActor.die();
                    break;
            }
        }

    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        short categoryBitsA = fixtureA.getFilterData().categoryBits;
        short categoryBitsB = fixtureB.getFilterData().categoryBits;

        // if (categoryBitsA == GameManager.PLAYER_BIT && categoryBitsB ==
        // GameManager.BOMB_BIT) {
        // fixtureB.setSensor(false);
        // } else if (categoryBitsA == GameManager.BOMB_BIT && categoryBitsB ==
        // GameManager.PLAYER_BIT) {
        // fixtureA.setSensor(false);
        // }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
