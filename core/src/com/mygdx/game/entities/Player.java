package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class Player extends Sprite{
    
    private Vector2 velocidade = new Vector2();
    private float speed = 60 * 2, gravidade = 60 * 1.8f;
    private TiledMapTileLayer collisionLayer;
    
    public Player(Sprite sprite, TiledMapTileLayer collisionLayer) {
        super(sprite);
        this.collisionLayer = collisionLayer;
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

        //salvar a posição antiga
        float oldX = getX(), oldY = getY(), tileWidth = collisionLayer.getTileWidth(), tileHeight = collisionLayer.getTileHeight();
        boolean collisionX = false, collisionY = false;

        //mova-se para X
        setX(getX() + velocidade.x * delta);

        if(velocidade.x < 0){
            //Canto superior esquerdo
            collisionX = collisionLayer.getCell((int) (getX() / tileWidth),(int) ((getY() + getHeight()) / tileHeight))
                        .getTile().getProperties().containsKey("collision");
            
            //Meio esquerdo
            if(!collisionX)
                collisionX = collisionLayer.getCell((int) (getX() / tileWidth),(int) ((getY() + getHeight() / 2) / tileHeight))
                        .getTile().getProperties().containsKey("collision");
            
            //Canto inferior esquerdo
            if(!collisionX)
                collisionX = collisionLayer.getCell((int) (getX() / tileWidth),(int) (getY() / tileHeight))
                        .getTile().getProperties().containsKey("collision");
        } else if (velocidade.x > 0) {
            //Canto superior direito
            collisionX = collisionLayer.getCell((int) ((getX() + getWidth()) / tileWidth),(int) ((getY() + getHeight()) / tileHeight))
                        .getTile().getProperties().containsKey("collision");
            
            //Meio direito
            if(!collisionX)
                collisionX = collisionLayer.getCell((int) ((getX() + getWidth()) / tileWidth),(int) ((getY() + getHeight() / 2) / tileHeight))
                        .getTile().getProperties().containsKey("collision");
            
            //Canto inferior direito
            if(!collisionX)
                collisionX = collisionLayer.getCell((int) ((getX() + getWidth()) / tileWidth),(int) (getY() / tileHeight))
                        .getTile().getProperties().containsKey("collision");
            
        }

        //Reage a uma colisão de X
        if(collisionX) {
            setX(oldX);
            velocidade.x = 0;
        }

        //mova-se para Y
        setY(getY() + velocidade.y * delta);

        if(velocidade.y < 0){

            //Inferior esquerdo
            collisionY = collisionLayer.getCell((int) (getX() / tileWidth),(int) (getY() / tileHeight))
            .getTile().getProperties().containsKey("collision");
            
            //Meio inferior
            if(!collisionY)
                collisionY = collisionLayer.getCell((int) ((getX() + getWidth() / 2) / tileWidth),(int) (getY() / tileHeight)).getTile().getProperties().containsKey("collision");
            
            //Inferior direito
            if(!collisionY)
                collisionY = collisionLayer.getCell((int) ((getX() + getWidth()) / tileHeight),(int) (getY() / tileHeight)).getTile().getProperties().containsKey("collision");

        } else if (velocidade.y > 0) {
            
            //Canto superior esquerdo
            collisionY = collisionLayer.getCell((int) ((getX()) / tileWidth),(int) ((getY() + getHeight()) / tileHeight)).getTile().getProperties().containsKey("collision");
            
            //Meio superior
            if(!collisionY)
                collisionY = collisionLayer.getCell((int) ((getX() + getWidth() / 2) / tileWidth),(int) ((getY() + getHeight() / 2) / tileHeight)).getTile().getProperties().containsKey("collision");
            
            //Canto superior direito
            if(!collisionY)
                collisionY = collisionLayer.getCell((int) ((getX() + getWidth()) / tileWidth),(int) ((getY() + getHeight())/ tileHeight)).getTile().getProperties().containsKey("collision");
        }

        //Reage a uma colisão de Y
        if(collisionY){
            setY(oldY);
            velocidade.y = 0;
        }
    }

    public Vector2 getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(Vector2 velocidade) {
        this.velocidade = velocidade;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getGravidade() {
        return gravidade;
    }

    public void setGravidade(float gravidade) {
        this.gravidade = gravidade;
    }

    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    public void setCollisionLayer(TiledMapTileLayer collisionLayer) {
        this.collisionLayer = collisionLayer;
    }
    
    
}
