package com.mygdx.game.actors;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.box2d.BombermanUserData;
import com.mygdx.game.configs.BombermanConfig;
import com.mygdx.game.enums.UserDataType;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

public class Bomberman extends GameActor {

    private Animation<TextureRegion> upAnimation;
    private Animation<TextureRegion> downAnimation;
    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private Animation<TextureRegion> dyingAnimation;
    private TextureRegion default_frame;
    private TextureAtlas textureAtlas;
    private GameStage game;
    private List<Bomb> bombsList;
    private Sound soundDownUp;
    private Sound soundLeftRight;
    private float lastPlayedSound;
    private int bombRange;
    private int bombCount;
    private boolean remoteControl;
    private boolean flamePass;
    private boolean brickPass;
    private boolean invencible;
    private float speed;
    private boolean bombPass;
    private final float SPACE_BETWEEN_SOUDNS = 0.25f;
    private int lifes;

    public static enum State {
        MOVE_UP,
        MOVE_DOWN,
        MOVE_LEFT,
        MOVE_RIGHT,
        IDLE_UP,
        IDLE_DOWN,
        IDLE_LEFT,
        IDLE_RIGHT,
        DYING,
        DIE
    }

    public Bomberman(Body body, GameStage game) {
        super(body);
        getUserData().setActor(this);
        this.game = game;
        this.textureAtlas = gameManager.getAssetManager().get(GameManager.BOMBERMAN_ATLAS_PATH);
        this.speed = GameManager.BOMBERMAN_INITIAL_SPEED;
        this.bombRange = 1;
        this.bombCount = 1;
        this.bombsList = new ArrayList<Bomb>();
        this.remoteControl = false;
        this.invencible = false;
        this.brickPass = false;
        this.bombPass = false;
        this.lastPlayedSound = 0f;
        this.lifes = 3;

        /* Load sounds */
        this.soundDownUp = gameManager.getAssetManager().get(GameManager.SOUND_DOWN_UP_WALK);
        this.soundLeftRight = gameManager.getAssetManager().get(GameManager.SOUND_LEFT_RIGHT_WALK);

        Array<TextureRegion> upFrames = new Array<TextureRegion>(TextureRegion.class);
        Array<TextureRegion> downFrames = new Array<TextureRegion>(TextureRegion.class);
        Array<TextureRegion> leftFrames = new Array<TextureRegion>(TextureRegion.class);
        Array<TextureRegion> rightFrames = new Array<TextureRegion>(TextureRegion.class);
        Array<TextureRegion> dyingFrames = new Array<TextureRegion>(TextureRegion.class);
        default_frame = new TextureRegion(textureAtlas.findRegion(GameManager.BOMBERMAN_DOWN_REGION_NAMES[0]));

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

    public Bomberman(Body body, GameStage game, BombermanConfig config) {
        this(body, game);
        this.speed = config.speed;
        this.bombRange = config.bombRange;
        this.bombCount = config.bombCount;
        this.remoteControl = config.remoteControl;
        this.invencible = config.invencible;
        this.lifes = config.lifes;
        if (config.brickPass)
            activateBrickPass();
        if (config.bombPass)
            activateBombPass();
        if (config.flamePass)
            activateFlamePass();
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
                if (lastPlayedSound + SPACE_BETWEEN_SOUDNS < stateTime) {
                    soundDownUp.play();
                    lastPlayedSound = stateTime;
                }
                break;
            case MOVE_DOWN:
                currentFrame = downAnimation.getKeyFrame(stateTime, true);
                if (lastPlayedSound + SPACE_BETWEEN_SOUDNS < stateTime) {
                    soundDownUp.play();
                    lastPlayedSound = stateTime;
                }
                break;
            case MOVE_LEFT:
                currentFrame = leftAnimation.getKeyFrame(stateTime, true);
                if (lastPlayedSound + SPACE_BETWEEN_SOUDNS < stateTime) {
                    soundLeftRight.play();
                    lastPlayedSound = stateTime;
                }
                break;
            case MOVE_RIGHT:
                currentFrame = rightAnimation.getKeyFrame(stateTime, true);
                if (lastPlayedSound + SPACE_BETWEEN_SOUDNS < stateTime) {
                    soundLeftRight.play();
                    lastPlayedSound = stateTime;
                }
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
                currentFrame = default_frame;
                break;
        }

        // Draw the current frame
        batch.draw(currentFrame, x, y, width, height);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (invencible == true && stateTime > GameManager.INVENCIBLE_TIME) {
            invencible = false;
        }

        if (getUserData().getState().equals(State.DYING)) {
            body.setActive(false);
        }

        if (getUserData().getState().equals(State.DYING) && dyingAnimation.isAnimationFinished(stateTime)) {
            ((GameStage) getStage()).restartLevel();
            getUserData().setState(State.DIE);
        }
    }

    @Override
    public BombermanUserData getUserData() {
        return (BombermanUserData) userData;
    }

    public BombermanConfig getConfig(boolean bombermanIsDead) {
        /* If bomberman is dead, he lost some items */
        /* IF bomberman is alive, get one life more */
        if (bombermanIsDead) {
            return new BombermanConfig(
                    bombRange,
                    bombCount,
                    false,
                    false,
                    false,
                    false,
                    speed,
                    false,
                    (lifes - 1));
        } else {
            return new BombermanConfig(
                    bombRange,
                    bombCount,
                    remoteControl,
                    flamePass,
                    brickPass,
                    invencible,
                    speed,
                    bombPass,
                    lifes + 1);
        }
    }

    @Override
    public boolean isAlive() {
        return !getUserData().getState().equals(State.DIE);
    }

    public boolean isDying() {
        return getUserData().getState().equals(State.DYING);
    }

    public void moveUp() {
        if (!getUserData().getState().equals(State.DYING)
                && !getUserData().getState().equals(State.DIE)) {
            body.applyLinearImpulse(new Vector2(0, speed), body.getWorldCenter(), true);
            getUserData().setState(State.MOVE_UP);
        }
    }

    public void moveDown() {
        if (!getUserData().getState().equals(State.DYING)
                && !getUserData().getState().equals(State.DIE)) {
            body.applyLinearImpulse(new Vector2(0, -speed), body.getWorldCenter(), true);
            getUserData().setState(State.MOVE_DOWN);
        }
    }

    public void moveLeft() {
        if (!getUserData().getState().equals(State.DYING)
                && !getUserData().getState().equals(State.DIE)) {
            body.applyLinearImpulse(new Vector2(-speed, 0), body.getWorldCenter(), true);
            getUserData().setState(State.MOVE_LEFT);
        }
    }

    public void moveRight() {
        if (!getUserData().getState().equals(State.DYING)
                && !getUserData().getState().equals(State.DIE)) {
            body.applyLinearImpulse(new Vector2(speed, 0), body.getWorldCenter(), true);
            getUserData().setState(State.MOVE_RIGHT);
        }
    }

    public void move(Vector2 vector2) {
        body.setLinearVelocity(vector2);
    }

    public void placeBomb() {
        if (bombsList.size() < bombCount) {
            int x, y;
            x = Math.round(screenRectangle.x);
            y = Math.round(screenRectangle.y);
            if (!WorldUtils.hasObjectAtPosition(new Vector2(x + 0.5f, y + 0.5f), GameManager.BOMB_BIT)) {
                bombsList.add(new Bomb(game, x, y, bombRange));
                gameManager.playEffect(GameManager.SOUND_PUT_BOMB);
            } 
        }
    }

    public List<Bomb> getBombsList() {
        return bombsList;
    }

    public void die(UserDataType cause) {
        if (((cause.equals(UserDataType.EXPLOSION) && !flamePass) || cause.equals(UserDataType.ENEMY)) && !invencible) {
            if (!getUserData().getState().equals(State.DYING)) {
                stateTime = 0f;
                getUserData().setState(State.DYING);
            }
        }
    }

    public void setState(State state) {
        if (!getUserData().getState().equals(State.DYING)
                && !getUserData().getState().equals(State.DIE)) {
            getUserData().setState(state);
        }
    }

    public void increaseBombRange() {
        bombRange++;
    }

    public void increaseBombCount() {
        bombCount++;
    }

    public void activateRemoteControl() {
        remoteControl = true;
    }

    public void activateFlamePass() {
        flamePass = true;
    }

    public void activateBrickPass() {
        brickPass = true;
        Filter oldFilter = body.getFixtureList().get(0).getFilterData();
        short olderMaskBits = oldFilter.maskBits;
        short newMaskBits = (short) (olderMaskBits &  (~GameManager.BRICK_BIT));
        updateMaskBits(newMaskBits);
    }

    public void activateBombPass() {
        bombPass = true;
        Filter oldFilter = body.getFixtureList().get(0).getFilterData();
        short olderMaskBits = oldFilter.maskBits;
        short newMaskBits = (short) (olderMaskBits &  (~GameManager.BOMB_BIT));
        updateMaskBits(newMaskBits);
    }

    public void updateMaskBits(short newMaskBits) {
        for (Fixture fixture : body.getFixtureList()) {
            Filter oldFilter = fixture.getFilterData();
            Filter newFilter = new Filter();

            newFilter.categoryBits = oldFilter.categoryBits; // Keep the existing category bits unchanged
            newFilter.maskBits = newMaskBits; // Set the new mask bits

            fixture.setFilterData(newFilter);
        }
    }

    public void explodeAllBombs() {
        if (remoteControl) {
            for (Bomb bomb : bombsList) {
                bomb.flagWillExploded = true;
            }
        }
    }

    public void invencible() {
        stateTime = 0f;
        invencible = true;
    }

    public void speedUp() {
        speed += GameManager.SPEED_UP_VALUE;
    }
}
