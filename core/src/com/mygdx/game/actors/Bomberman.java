package com.mygdx.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.box2d.BombermanUserData;
import com.mygdx.game.enums.StateBomberman;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

public class Bomberman extends GameActor {

    private Animation<TextureRegion> upAnimation;
    private Animation<TextureRegion> downAnimation;
    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private Animation<TextureRegion> dyingAnimation;
    private TextureRegion test;
    private TextureAtlas textureAtlas;
    private GameStage game;
    private float stateTime;
    private int power_bomb;

    public Bomberman(Body body, GameStage game) {
        super(body);
        getUserData().setActor(this);
        this.game = game;
        this.textureAtlas = GameManager.getInstance().getAssetManager().get(GameManager.BOMBERMAN_ATLAS_PATH);
        this.power_bomb = 2;

        Array<TextureRegion> upFrames = new Array<TextureRegion>(TextureRegion.class);
        Array<TextureRegion> downFrames = new Array<TextureRegion>(TextureRegion.class);
        Array<TextureRegion> leftFrames = new Array<TextureRegion>(TextureRegion.class);
        Array<TextureRegion> rightFrames = new Array<TextureRegion>(TextureRegion.class);
        Array<TextureRegion> dyingFrames = new Array<TextureRegion>(TextureRegion.class);
        test = new TextureRegion(textureAtlas.findRegion(GameManager.BOMBERMAN_DOWN_REGION_NAMES[0]));

        // Load up region into the animation
        for (String path : GameManager.BOMBERMAN_UP_REGION_NAMES) {
            upFrames.add(textureAtlas.findRegion(path));
        }
        upAnimation = new Animation<TextureRegion>(0.1f, upFrames);

        // Load down region into the animation
        for (String path : GameManager.BOMBERMAN_DOWN_REGION_NAMES) {
            downFrames.add(textureAtlas.findRegion(path));
        }
        downAnimation = new Animation<TextureRegion>(0.1f, downFrames);

        // Load left region into the animation
        for (String path : GameManager.BOMBERMAN_LEFT_REGION_NAMES) {
            leftFrames.add(textureAtlas.findRegion(path));
        }
        leftAnimation = new Animation<TextureRegion>(0.1f, leftFrames);

        // Load right region into the animation
        for (String path : GameManager.BOMBERMAN_RIGHT_REGION_NAMES) {
            rightFrames.add(textureAtlas.findRegion(path));
        }
        rightAnimation = new Animation<TextureRegion>(0.1f, rightFrames);

        // Load dying animation
        for (String path : GameManager.BOMBERMAN_DYING_REGION_NAMES) {
            dyingFrames.add(textureAtlas.findRegion(path));
        }
        dyingAnimation = new Animation<TextureRegion>(0.2f, dyingFrames);

        stateTime = 0f;

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
        switch (getUserData().getState()) {
            case MOVE_UP:
                currentFrame = upAnimation.getKeyFrame(stateTime, true);
                break;
            case MOVE_DOWN:
                currentFrame = downAnimation.getKeyFrame(stateTime, true);
                break;
            case MOVE_LEFT:
                currentFrame = leftAnimation.getKeyFrame(stateTime, true);
                break;
            case MOVE_RIGHT:
                currentFrame = rightAnimation.getKeyFrame(stateTime, true);
                break;
            case IDLE_UP:
                currentFrame = upAnimation.getKeyFrames()[1];
                break;
            case IDLE_DOWN:
                currentFrame = downAnimation.getKeyFrames()[1];
                break;
            case IDLE_RIGHT:
                currentFrame = rightAnimation.getKeyFrames()[1];
                break;
            case IDLE_LEFT:
                currentFrame = leftAnimation.getKeyFrames()[1];
                break;
            case DYING:
            case DIE:
                currentFrame = dyingAnimation.getKeyFrame(stateTime, false);
                break;
            default:
                currentFrame = test;
                break;
        }

        // Draw the current frame
        batch.draw(currentFrame, x, y, width, height);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getUserData().getState().equals(StateBomberman.DYING)) {
            System.out.println("Dying");
        }
        // System.out.println("BombermanUserData state: " + state);
        stateTime += Gdx.graphics.getDeltaTime();
        if (getUserData().getState().equals(StateBomberman.DYING) && dyingAnimation.isAnimationFinished(stateTime)) {
            getUserData().setState(StateBomberman.DIE);
        }
    }

    @Override
    public BombermanUserData getUserData() {
        return (BombermanUserData) userData;
    }

    @Override
    public boolean isAlive() {
        return !getUserData().getState().equals(StateBomberman.DIE);
    }

    public void moveUp() {
        if (!getUserData().getState().equals(StateBomberman.DYING) && !getUserData().getState().equals(StateBomberman.DIE)) {
            body.applyLinearImpulse(new Vector2(0, GameManager.BOMBERMAN_VELOCITY), body.getWorldCenter(), true);
            getUserData().setState(StateBomberman.MOVE_UP);
        }
    }

    public void moveDown() {
        if (!getUserData().getState().equals(StateBomberman.DYING) && !getUserData().getState().equals(StateBomberman.DIE)) {
            body.applyLinearImpulse(new Vector2(0, -GameManager.BOMBERMAN_VELOCITY), body.getWorldCenter(), true);
            getUserData().setState(StateBomberman.MOVE_DOWN);
        }
    }

    public void moveLeft() {
        if (!getUserData().getState().equals(StateBomberman.DYING) && !getUserData().getState().equals(StateBomberman.DIE)) {
            body.applyLinearImpulse(new Vector2(-GameManager.BOMBERMAN_VELOCITY, 0), body.getWorldCenter(), true);
            getUserData().setState(StateBomberman.MOVE_LEFT);
        }
    }

    public void moveRight() {
        if (!getUserData().getState().equals(StateBomberman.DYING) && !getUserData().getState().equals(StateBomberman.DIE)) {
            body.applyLinearImpulse(new Vector2(GameManager.BOMBERMAN_VELOCITY, 0), body.getWorldCenter(), true);
            getUserData().setState(StateBomberman.MOVE_RIGHT);
        }
    }

    public void move(Vector2 vector2) {
        body.setLinearVelocity(vector2);
    }

    public void placeBomb() {
        // TODO: Implement functionality
        int x, y;
        x = Math.round(screenRectangle.x);
        y = Math.round(screenRectangle.y);
        if (!WorldUtils.hasObjectAtPosition(new Vector2(x, y), GameManager.BOMB_BIT)) {
            System.out.println("Place bomb at " + x + " " + y);
            new Bomb(game, x, y, power_bomb);
        } else {
            System.out.println("Já existe uma bomba no local! " + x + " " + y);
        }
    }

    public void die() {
        if (!getUserData().equals(StateBomberman.DYING)) {
            stateTime = 0f;
            getUserData().setState(StateBomberman.DYING);
        }
    }

    public void setState(StateBomberman state){
        if (!getUserData().getState().equals(StateBomberman.DYING) && !getUserData().getState().equals(StateBomberman.DIE)) {
            getUserData().setState(state);
        }
    }    
}
