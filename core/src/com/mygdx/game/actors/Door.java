package com.mygdx.game.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.box2d.DoorUserData;
import com.mygdx.game.configs.EnemyConfig;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

public class Door extends GameActor {
    private TextureRegion doorTexture;
    private boolean isHit; // Door is hit by bomb
    private float lastHit; // Last time door is hit by bomb
    private boolean flagEntered; // Door is entered by bomberman

    public Door(Body body) {
        super(body);
        getUserData().setActor(this);
        TextureAtlas textureAtlas = GameManager.getInstance().getAssetManager().get(GameManager.BOMBERMAN_ATLAS_PATH);
        isHit = false;
        lastHit = 0f;
        flagEntered = false;
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
        if (isHit) {
            // Create new 4 onils
            for (int i = 0; i < 4; i++) {
                Body onilBody = WorldUtils.createEnemy(getUserData().position, EnemyConfig.onilConfig);
                getParent().addActor(new Enemy(onilBody, EnemyConfig.onilConfig));
                gameManager.enemiesLeft++;
            }

            isHit = false;
        }

        if (flagEntered) {
            ((GameStage) getStage()).nextLevel();
        }

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

    public void hit() {
        if (lastHit + 3f < stateTime) {
            this.isHit = true;
            lastHit = stateTime;
        }
    }

    public void enter() {
        System.out.println(matrixPosition);
        if (gameManager.enemiesLeft == 0) {
            this.flagEntered = true;
        }
    }

}
