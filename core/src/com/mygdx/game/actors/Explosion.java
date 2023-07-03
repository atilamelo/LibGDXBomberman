package com.mygdx.game.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.box2d.ExplosionUserData;
import com.mygdx.game.enums.StateExplosion;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

public class Explosion extends GameActor {
    public StateExplosion state;
    private GameStage stage;
    private Animation<TextureRegion> animation;

    public Explosion(GameStage stage, int x, int y, float width, float height, boolean isCenter) {
        super(WorldUtils.createExplosion(x + 0.5f, y + 0.5f, width, height, isCenter));
        this.state = ((ExplosionUserData) getUserData()).getState();
        this.stage = stage;
        this.animation = null;
        getUserData().setActor(this);

        this.stage.background.addActor(this);
    }

    public Explosion(GameStage stage, float x, float y, float width, float height, boolean isCenter, Animation<TextureRegion> animation) {
        super(WorldUtils.createExplosion(x + 0.5f, y + 0.5f, width, height, isCenter));
        this.state = StateExplosion.EXPLODING;
        this.stage = stage;

        this.stage.background.addActor(this);
        this.animation = animation;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (stateTime > 0.7f) {
            state = StateExplosion.DEATH;
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float x = screenRectangle.x + (screenRectangle.width - GameManager.BOMB_WIDTH) / 2;
        float y = screenRectangle.y + (screenRectangle.height - GameManager.BOMB_HEIGHT) / 2;
        float width = screenRectangle.width;
        float height = screenRectangle.height;
        if (animation != null) {
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, false);
            batch.draw(currentFrame, x, y, width, height);
        }
    }

    @Override
    public ExplosionUserData getUserData() {    
        return (ExplosionUserData) userData;
    }

    @Override
    public boolean isAlive() {
        return state == StateExplosion.EXPLODING;
    }

}
