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
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.GameManager;

public class Bomberman extends GameActor {
    public boolean moveUp;
    public boolean moveDown;
    public boolean moveLeft;
    public boolean moveRight;
    private Animation<TextureRegion> upAnimation;
    private Animation<TextureRegion> downAnimation;
    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private TextureRegion test;
    private TextureAtlas textureAtlas;
    private GameStage game;
    private float stateTime;

    public Bomberman(Body body, GameStage game) {
        super(body);
        this.game = game;
        this.textureAtlas = GameManager.getInstance().getAssetManager().get(GameManager.BOMBERMAN_ATLAS_PATH);
        
        Array<TextureRegion> upFrames = new Array<>();
        Array<TextureRegion> downFrames = new Array<>();
        Array<TextureRegion> leftFrames = new Array<>();
        Array<TextureRegion> rightFrames = new Array<>();
        test = new TextureRegion(textureAtlas.findRegion(GameManager.BOMBERMAN_DOWN_REGION_NAMES[0]));

        // Load up region into the animation
        for (String path : GameManager.BOMBERMAN_UP_REGION_NAMES) {
            upFrames.add(textureAtlas.findRegion(path));
        }
        upAnimation = new Animation<>(0.1f, upFrames);

        // Load down region into the animation
        for (String path : GameManager.BOMBERMAN_DOWN_REGION_NAMES) {
            downFrames.add(textureAtlas.findRegion(path));
        }
        downAnimation = new Animation<>(0.1f, downFrames);

        // Load left region into the animation
        for (String path : GameManager.BOMBERMAN_LEFT_REGION_NAMES) {
            leftFrames.add(textureAtlas.findRegion(path));
        }
        leftAnimation = new Animation<>(0.1f, leftFrames);

        // Load right region into the animation
        for (String path : GameManager.BOMBERMAN_RIGHT_REGION_NAMES) {
            rightFrames.add(textureAtlas.findRegion(path));
        }
        rightAnimation = new Animation<>(0.1f, rightFrames);

        stateTime = 0f;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float x = screenRectangle.x;
        float y = screenRectangle.y;
        float width = screenRectangle.width;
        float height = screenRectangle.height;
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = null;

        // Choose the appropriate animation based on the movement direction
        if (moveUp) {
            currentFrame = upAnimation.getKeyFrame(stateTime, true);
        } else if (moveDown) {
            currentFrame = downAnimation.getKeyFrame(stateTime, true);
        } else if (moveLeft) {
            currentFrame = leftAnimation.getKeyFrame(stateTime, true);
        } else if (moveRight) {
            currentFrame = rightAnimation.getKeyFrame(stateTime, true);
        } else {
            // If no movement, use a default frame
            currentFrame = test;
        }

        // Draw the current frame
        batch.draw(currentFrame, x, y, width, height);
    }

    @Override
    public BombermanUserData getUserData() {
        return (BombermanUserData) userData;
    }

    public void moveUp() {
        body.applyLinearImpulse(new Vector2(0, GameManager.BOMBERMAN_VELOCITY), body.getWorldCenter(), true);
        moveUp = true;
    }

    public void moveDown() {
        body.applyLinearImpulse(new Vector2(0, -GameManager.BOMBERMAN_VELOCITY), body.getWorldCenter(), true);
        moveDown = true;
    }

    public void moveLeft() {
        body.applyLinearImpulse(new Vector2(-GameManager.BOMBERMAN_VELOCITY, 0), body.getWorldCenter(), true);
        moveLeft = true;
    }

    public void moveRight() {
        body.applyLinearImpulse(new Vector2(GameManager.BOMBERMAN_VELOCITY, 0), body.getWorldCenter(), true);
        moveRight = true;
    }

    public void move(Vector2 vector2) {
        body.setLinearVelocity(vector2);
    }

    public void placeBomb(){
        // TODO: Implement functionality
        int x, y; 
        x = Math.round(screenRectangle.x); 
        y = Math.round(screenRectangle.y);
        System.out.println("Place bomb screenRectangle: " + screenRectangle.x + ", " + screenRectangle.y);
        System.out.println("Place bomb at " + x + " " + y);
        new Bomb(game, x, y);
    }
    

}
