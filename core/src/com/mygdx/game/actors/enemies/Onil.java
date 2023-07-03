package com.mygdx.game.actors.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.configs.EnemyConfig;
import com.mygdx.game.utils.GameManager;

/*
 * Estrutura de movimentação e implementação dos inimgios baseado no Bomberman for LibGdx (GitHub)
 * Link: TODO: INSERIR LINK
 */
public class Onil extends Enemy {
    private TextureAtlas textureAtlas;
    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private TextureRegion dyingTexture;

    public Onil(Body body) {
        super(body, EnemyConfig.onilConfig);
        getUserData().setActor(this);

        this.textureAtlas = GameManager.getInstance().getAssetManager().get(GameManager.BOMBERMAN_ATLAS_PATH);
        Array<TextureRegion> leftFrames = new Array<TextureRegion>(TextureRegion.class);
        Array<TextureRegion> rightFrames = new Array<TextureRegion>(TextureRegion.class);

        // Load left region into the animation
        for (String path : GameManager.ONIL_LEFT_REGION_NAMES) {
            leftFrames.add(textureAtlas.findRegion(path));
        }
        leftAnimation = new Animation<TextureRegion>(0.1f, leftFrames);

        // Load right region into the animation
        for (String path : GameManager.ONIL_RIGHT_REGION_NAMES) {
            rightFrames.add(textureAtlas.findRegion(path));
        }
        rightAnimation = new Animation<TextureRegion>(0.1f, rightFrames);

        dyingTexture = textureAtlas.findRegion(GameManager.ONIL_DYING_TEXTURE);

    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float x = screenRectangle.x;
        float y = screenRectangle.y;
        float width = screenRectangle.width;
        float height = screenRectangle.height;
        TextureRegion currentFrame = null;

        // Choose the appropriate animation based on the movement direction
        switch (state) {
            case ATTACKING_UP:
            case ATTACKING_DOWN:
            case WALKING_UP:
            case WALKING_RIGHT:
                currentFrame = rightAnimation.getKeyFrame(stateTime, true);
                break;
            case ATTACKING_LEFT:
            case ATTACKING_RIGHT:
            case WALKING_DOWN:
            case WALKING_LEFT:
                currentFrame = leftAnimation.getKeyFrame(stateTime, true);
                break;
            case DYING:
            case DIE:
                currentFrame = dyingTexture;
                break;
            default:
                break;
        }

        // Draw the current frame
        batch.draw(currentFrame, x, y, width, height);

    }

    @Override
    public boolean isDyingFinished() {
        return state.equals(State.DIE);
    }

}
