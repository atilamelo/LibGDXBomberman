package com.mygdx.game.listeners;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.actors.Bomb;
import com.mygdx.game.actors.Bomberman;
import com.mygdx.game.box2d.BombUserData;
import com.mygdx.game.box2d.BombermanUserData;
import com.mygdx.game.enums.StateBomb;
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
