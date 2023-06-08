package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class Player extends Sprite{
    

    private Vector2 velocidade = new Vector2();
    private float speed = 60 * 2, gravidade = 60 * 1.8f;
    
    public Player(Sprite sprite) {
        super(sprite);
    }

    public void draw(Batch batch) {
        update(Gdx.graphics.getDeltaTime());
        super.draw(batch);
    }

    public void update(float delta) {
        velocidade.y -= gravidade * delta;

        if(velocidade.y > speed){
            velocidade.y = speed;
        }
        else if(velocidade.y < speed){
            velocidade.y = -speed;
        }

        setX(getX() + velocidade.x * delta);
        setY(getY() + velocidade.y * delta);
    }
    
}
