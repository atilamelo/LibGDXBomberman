package com.mygdx.game.actors.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.box2d.BallomUserData;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

/*
 * Estrutura de movimentação e implementação dos inimgios baseado no Bomberman for LibGdx (GitHub)
 * Link: TODO: INSERIR LINK
 */
public class Ballom extends Enemy {
    private StateBallom state;
    private TextureAtlas textureAtlas;
    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private Animation<TextureRegion> dyingAnimation;

    public static enum StateBallom {
        WALKING_UP,
        WALKING_DOWN,
        WALKING_LEFT,
        WALKING_RIGHT,
        DYING,
        DIE;

        public static StateBallom getRandomWalkingState() {
            return values()[(int) (Math.random() * 4)];
        }
    }

    public Ballom(Body body, GameStage stage) {
        super(body, GameManager.BALLON_HP, GameManager.BALLON_SPEED);
        state = StateBallom.getRandomWalkingState();
        getUserData().setActor(this);

        this.textureAtlas = GameManager.getInstance().getAssetManager().get(GameManager.BOMBERMAN_ATLAS_PATH);
        Array<TextureRegion> leftFrames = new Array<TextureRegion>(TextureRegion.class);
        Array<TextureRegion> rightFrames = new Array<TextureRegion>(TextureRegion.class);
        Array<TextureRegion> dyingFrames = new Array<TextureRegion>(TextureRegion.class);

        // Load left region into the animation
        for (String path : GameManager.BALLON_LEFT_REGION_NAMES) {
            leftFrames.add(textureAtlas.findRegion(path));
        }
        leftAnimation = new Animation<TextureRegion>(0.1f, leftFrames);

        // Load right region into the animation
        for (String path : GameManager.BALLON_RIGHT_REGION_NAMES) {
            rightFrames.add(textureAtlas.findRegion(path));
        }
        rightAnimation = new Animation<TextureRegion>(0.1f, rightFrames);

        // Load dying animation
        for (String path : GameManager.BALLON_DYING_REGION_NAMES) {
            dyingFrames.add(textureAtlas.findRegion(path));
        }
        dyingAnimation = new Animation<TextureRegion>(0.2f, dyingFrames);

        stage.addActor(this);
    }

    private void changeWalkingState() {
        state = StateBallom.getRandomWalkingState();
    }

    @Override
    public BallomUserData getUserData() {
        return (BallomUserData) userData;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        Vector2 pos;

        if (hp == 0 && !state.equals(StateBallom.DYING) & !state.equals(StateBallom.DIE)) {
            stateTime = 0f;
            state = StateBallom.DYING;
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
                if (dyingAnimation.isAnimationFinished(stateTime)) {
                    state = StateBallom.DIE;
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
                currentFrame = dyingAnimation.getKeyFrame(stateTime, false);
                break;
            default:
                // currentFrame = leftAnimation.getKeyFrame(stateTime, true);
                break;
        }

        // Draw the current frame
        batch.draw(currentFrame, x, y, width, height);

    }

    @Override
    public boolean isAnimationFinished() {
        if (state.equals(StateBallom.DIE) || state.equals(StateBallom.DYING)) {
            return dyingAnimation.isAnimationFinished(stateTime);
        }else{
            return false;
        }
    }

}
