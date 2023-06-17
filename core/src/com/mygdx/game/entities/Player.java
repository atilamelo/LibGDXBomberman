package com.mygdx.game.entities;

import javax.swing.text.AbstractDocument.BranchElement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public class Player extends Sprite implements InputProcessor {
    
    private Vector2 velocidade = new Vector2();
    private float speed = 60 * 2;
    private TiledMapTileLayer collisionLayer;
    private String blockedKey = "collision";
    
    public Player(Sprite sprite, TiledMapTileLayer collisionLayer) {
        super(sprite);
        this.collisionLayer = collisionLayer;
        //setScale(3);
    }

    public void draw(Batch batch) {
        update(Gdx.graphics.getDeltaTime());
        super.draw(batch);
    }

    public void update(float delta) {
        // Salvar a posição antiga
        float oldX = getX(), oldY = getY(), tileWidth = collisionLayer.getTileWidth(), tileHeight = collisionLayer.getTileHeight();
        boolean collisionX = false, collisionY = false;

        // Movimentar-se para X
        setX(getX() + velocidade.x * delta);

        if (velocidade.x < 0) {
            // Canto superior esquerdo
            collisionX = isCellBlocked(getX(), getY() + getHeight());

            // Meio esquerdo
            if (!collisionX)
                collisionX = isCellBlocked(getX(), getY() + getHeight() / 2);

            // Canto inferior esquerdo
            if (!collisionX)
                collisionX = isCellBlocked(getX(), getY());
        } else if (velocidade.x > 0) {
            // Canto superior direito
            collisionX = isCellBlocked(getX() + getWidth(), getY() + getHeight());

            // Meio direito
            if (!collisionX)
                collisionX = isCellBlocked(getX() + getWidth(), getY() + getHeight() / 2);

            // Canto inferior direito
            if (!collisionX)
                collisionX = isCellBlocked(getX() + getWidth(), getY());
        }

        // Reagir a uma colisão em X
        if (collisionX) {
            setX(oldX);
            velocidade.x = 0;
        }

        // Movimentar-se para Y
        setY(getY() + velocidade.y * delta);

        if (velocidade.y < 0) {
            // Inferior esquerdo
            collisionY = isCellBlocked(getX(), getY());

            //TiledMapTile maptile = collisionLayer.getCell((int) (getX() / tileWidth),(int) (getY() / tileHeight)).getTile();

            // Meio inferior
            if (!collisionY)
                collisionY = isCellBlocked(getX() + getWidth() / 2, getY());

            // Inferior direito
            if (!collisionY)
                collisionY = isCellBlocked(getX() + getWidth(), getY());

        } else if (velocidade.y > 0) {

            // Canto superior esquerdo
            collisionY = isCellBlocked(getX(), getY() + getHeight());

            // Meio superior
            if (!collisionY)
                collisionY = isCellBlocked(getX() + getWidth() / 2, getY() + getHeight());

            // Canto superior direito
            if (!collisionY)
                collisionY = isCellBlocked(getX() + getWidth(), getY() + getHeight());
        }

        // Reagir a uma colisão em Y
        if (collisionY) {
            setY(oldY);
            velocidade.y = 0;
        }
    }

    private boolean isCellBlocked(float x, float y) {
        Cell cell = collisionLayer.getCell((int) (x / collisionLayer.getTileWidth()), (int) (y / collisionLayer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey(blockedKey);
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

    public TiledMapTileLayer getCollisionLayer() {
        return collisionLayer;
    }

    public void setCollisionLayer(TiledMapTileLayer collisionLayer) {
        this.collisionLayer = collisionLayer;
    }
    
    @Override
    public boolean keyDown(int keyCode) {
        switch(keyCode) {
            case Keys.W:
                velocidade.y = speed;
                break;
                // if(canJump){
                //     velocidade.y = speed;
                //     canJump = false;
                // }
                // break;
            case Keys.S:
                velocidade.y = -speed;
                break;
            case Keys.A:
                velocidade.x = -speed;
                break;
            case Keys.D:
                velocidade.x = speed;
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keyCode) {
        switch(keyCode) {
            case Keys.A:
            case Keys.D:
            case Keys.W:
            case Keys.S:
                velocidade.x = 0;
                velocidade.y = 0;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        
        return false;

    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        
        return false;
    }
}
