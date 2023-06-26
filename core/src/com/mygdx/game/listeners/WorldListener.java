package com.mygdx.game.listeners;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.actors.Explosion;
import com.mygdx.game.box2d.ExplosionUserData;
import com.mygdx.game.enums.StateExplosion;
import com.mygdx.game.utils.GameManager;

public class WorldListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        short categoryBitsA = fixtureA.getFilterData().categoryBits;
        short categoryBitsB = fixtureB.getFilterData().categoryBits;

        if(categoryBitsA == GameManager.PLAYER_BIT){
            switch(categoryBitsB){
                case GameManager.EXPLOSION_BIT:
                    
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

        if(categoryBitsA == GameManager.PLAYER_BIT && categoryBitsB == GameManager.BOMB_BIT){
            fixtureB.setSensor(false);
        } else if(categoryBitsA == GameManager.BOMB_BIT && categoryBitsB == GameManager.PLAYER_BIT) {
            fixtureA.setSensor(false);
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
