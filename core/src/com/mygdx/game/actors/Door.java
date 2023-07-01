package com.mygdx.game.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.box2d.DoorUserData;
import com.mygdx.game.utils.GameManager;

public class Door extends GameActor {
    private TextureRegion doorTexture;
    private float stateTime;

    public Door(Body body) {
        super(body);
        getUserData().setActor(this);
        TextureAtlas textureAtlas = GameManager.getInstance().getAssetManager().get(GameManager.BOMBERMAN_ATLAS_PATH);

        // Load default texture 
        doorTexture = textureAtlas.findRegion(GameManager.DOOR_TEXTURE);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        float x = screenRectangle.x + (screenRectangle.width - GameManager.BOMB_WIDTH) / 2;
        float y = screenRectangle.y + (screenRectangle.height - GameManager.BOMB_HEIGHT) / 2;
        float width = screenRectangle.width;
        float height = screenRectangle.height;

        batch.draw(doorTexture, x, y, width, height);
    }

    @Override
    public void act(float delta) {
        stateTime += delta; 
        super.act(delta);
    }

    @Override
    public DoorUserData getUserData() {
        return (DoorUserData) userData;
    }

    @Override
    public boolean isAlive() {
        return true;
    }

}
