package com.mygdx.game.actors.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.box2d.OnilUserData;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

/*
 * Estrutura de movimentação e implementação dos inimgios baseado no Bomberman for LibGdx (GitHub)
 * Link: TODO: INSERIR LINK
 */
public class Onil extends Enemy {
    private StateOnil state;
    private TextureAtlas textureAtlas;
    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private TextureRegion dyingTexture;

    public static enum StateOnil {
        WALKING_UP,
        WALKING_DOWN,
        WALKING_LEFT,
        WALKING_RIGHT,
        DYING,
        DIE;

        public static StateOnil getRandomWalkingState() {
            return values()[(int) (Math.random() * 4)];
        }
    }

    public Onil(Body body) {
        super(body, GameManager.ONIL_HP, GameManager.ONIL_SPEED);
        state = StateOnil.getRandomWalkingState();
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

    private void changeWalkingState() {
        state = StateOnil.getRandomWalkingState();
    }

    @Override
    public OnilUserData getUserData() {
        return (OnilUserData) userData;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        Vector2 pos;

        if (hp == 0 && !state.equals(StateOnil.DYING) & !state.equals(StateOnil.DIE)) {
            stateTime = 0f;
            state = StateOnil.DYING;
        }

        switch (state) {
            case WALKING_UP:
                pos = new Vector2(body.getPosition().x, body.getPosition().y + .3f);

                if (body.getLinearVelocity().y != speed) {
                    body.setLinearVelocity(new Vector2(0, speed * body.getMass()));
                }

                if (WorldUtils.hitSomething(pos)) {
                    changeWalkingState();
                }
                break;
            case WALKING_DOWN:
                pos = new Vector2(body.getPosition().x, body.getPosition().y - .3f);

                if (body.getLinearVelocity().y != -speed) {
                    body.setLinearVelocity(new Vector2(0, -speed * body.getMass()));
                }

                if (WorldUtils.hitSomething(pos)) {
                    changeWalkingState();
                }
                break;
            case WALKING_LEFT:
                pos = new Vector2(body.getPosition().x - .3f, body.getPosition().y);

                if (body.getLinearVelocity().x != -speed) {
                    body.setLinearVelocity(new Vector2(-speed * body.getMass(), 0));
                }
                if (WorldUtils.hitSomething(pos)) {
                    changeWalkingState();
                }
                break;
            case WALKING_RIGHT:
                if (body.getLinearVelocity().x != speed) {
                    // body.applyLinearImpulse(new Vector2(speed * body.getMass(), 0),
                    // body.getWorldCenter(), true);

                    body.setLinearVelocity(new Vector2(speed * body.getMass(), 0));
                }
                pos = new Vector2(body.getPosition().x + .3f, body.getPosition().y);
                if (WorldUtils.hitSomething(pos)) {
                    changeWalkingState();
                }
                break;
            case DYING:
                body.setActive(false);
                if (stateTime > GameManager.ONIL_DYING_TIME) {
                    state = StateOnil.DIE;
                }
                break;
            case DIE:
                break;
        }
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
            case WALKING_UP:
            case WALKING_RIGHT:
                currentFrame = rightAnimation.getKeyFrame(stateTime, true);
                break;
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
        return state.equals(StateOnil.DIE);
    }

}
