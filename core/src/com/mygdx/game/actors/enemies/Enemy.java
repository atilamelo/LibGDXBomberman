package com.mygdx.game.actors.enemies;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.actors.GameActor;
import com.mygdx.game.box2d.EnemyUserData;
import com.mygdx.game.box2d.UserData;

public abstract class Enemy extends GameActor{
    protected int hp; 
    protected float speed;
    protected float stateTime;
    protected float lastHit;

    public Enemy(Body body, int hp, float speed) {
        super(body);
        this.hp = hp;
        this.speed = speed;
        this.stateTime = 0;
        this.lastHit = 0f;
    }

    @Override
    public EnemyUserData getUserData(){
        return (EnemyUserData) userData;
    };

    @Override
    public boolean isAlive(){
        /* Wait animation of dying finish to remove Actor of stage and body of world */
        return hp > 0 || !isDyingFinished();
    };

    @Override
    public void act(float delta) {
        stateTime += delta;
        super.act(delta);
    }

    public void takeDamage(int damage){ 
        /* Add 3 second more of wait to avoid double damage */
        if(lastHit + 3f < stateTime){
            hp -= damage;
            lastHit = stateTime;
        }
    }

    public abstract boolean isDyingFinished();



}
