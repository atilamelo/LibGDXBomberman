package com.mygdx.game.actors.enemies;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.actors.GameActor;
import com.mygdx.game.box2d.UserData;

public abstract class Enemy extends GameActor{
    protected int hp; 
    protected float speed;

    public Enemy(Body body, int hp, float speed) {
        super(body);
        this.hp = hp;
        this.speed = speed;
    }

    @Override
    public abstract UserData getUserData();

    @Override
    public boolean isAlive(){
        return hp > 0;
    };

    public void takeDamage(int damage){
        hp -= damage;
    }

}
