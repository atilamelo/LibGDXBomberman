package com.mygdx.game.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.box2d.PowerUpUserData;
import com.mygdx.game.utils.GameManager;

public class PowerUp extends GameActor {
    private boolean isPicked;
    private PowerUpType powerUpType;
    private TextureRegion texture;

    public static enum PowerUpType {
        BOMB_UP,
        FIRE_UP,
        SPEED_UP,
        REMOTE_CONTROL,
        BOMB_PASS,
        BRICK_PASS,
        FLAME_PASS,
        INVENCIBLE;
    }

    public PowerUp(Body body, PowerUpType powerUpType) {
        super(body);
        userData.setActor(this);
        this.isPicked = false;
        this.powerUpType = powerUpType;
        AssetManager assetManager = gameManager.getAssetManager();
        TextureAtlas textureAtlas = assetManager.get(GameManager.BOMBERMAN_ATLAS_PATH);

        switch (powerUpType) {
            case BOMB_UP:
                this.texture = textureAtlas.findRegion(GameManager.POWER_UP_BOMB_UP);
                break;
            case BRICK_PASS:
                this.texture = textureAtlas.findRegion(GameManager.POWER_UP_BRICK_PASS);
                break;
            case FIRE_UP:
                this.texture = textureAtlas.findRegion(GameManager.POWER_UP_FIRE_UP);
                break;
            case FLAME_PASS:
                this.texture = textureAtlas.findRegion(GameManager.POWER_UP_FLAME_PASS);
                break;
            case INVENCIBLE:
                this.texture = textureAtlas.findRegion(GameManager.POWER_UP_INVENCIBLE);
                break;
            case REMOTE_CONTROL:
                this.texture = textureAtlas.findRegion(GameManager.POWER_UP_REMOTE_CONTROL);
                break;
            case SPEED_UP:
                this.texture = textureAtlas.findRegion(GameManager.POWER_UP_SPEED_UP);
                break;
            case BOMB_PASS:
                this.texture = textureAtlas.findRegion(GameManager.POWER_UP_BOMB_PASS);
                break;
        }
    }

    @Override
    public void act(float delta) {
        stateTime += delta;
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = screenRectangle.x + (screenRectangle.width - GameManager.POWER_UP_WIDTH) / 2;
        float y = screenRectangle.y + (screenRectangle.height - GameManager.POWER_UP_HEIGHT) / 2;
        float width = screenRectangle.width;
        float height = screenRectangle.height;

        batch.draw(texture, x, y, width, height);
        super.draw(batch, parentAlpha);
    }

    @Override
    public PowerUpUserData getUserData() {
        return (PowerUpUserData) userData;
    }

    @Override
    public boolean isAlive() {
        return !isPicked;
    }

    public void pick(){
        isPicked = true;
        gameManager.playEffect(GameManager.SOUND_PICK_POWER_UP);
    }

    public PowerUpType getPowerUpType() {
        return powerUpType;
    }

}
