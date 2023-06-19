package com.mygdx.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.box2d.UserData;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.utils.WorldUtils;

public class Bomb extends GameActor {
    private Animation<TextureRegion> bombAnimation;
    private TextureAtlas textureAtlas;
    private float stateTime;

    public Bomb(GameStage gameStage, int x, int y) {
        super(WorldUtils.createBomb(gameStage.getWorld(), x + 0.5f, y + 0.5f));
        this.textureAtlas = gameStage.getAssetManager().get(Constants.BOMBERMAN_ATLAS_PATH);
        stateTime = 0f;
        Array<TextureRegion> bombFrames = new Array<>();
        
        // Load frames of animation
        for (String path : Constants.BOMB_ANIMATION) {
            bombFrames.add(textureAtlas.findRegion(path));
        }

        bombAnimation = new Animation<>(0.1f, bombFrames);

        gameStage.addActor(this);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float x = screenRectangle.x + (screenRectangle.width - Constants.BOMB_WIDTH) / 2;
        float y = screenRectangle.y + (screenRectangle.height - Constants.BOMB_HEIGHT) / 2;
        float width = screenRectangle.width;
        float height = screenRectangle.height;
        stateTime += Gdx.graphics.getDeltaTime();

        TextureRegion currentFrame = bombAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y, width, height);
    }

    @Override
    public UserData getUserData() {
        return userData;
    }

}
