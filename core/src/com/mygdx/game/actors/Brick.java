package com.mygdx.game.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.actors.PowerUp.PowerUpType;
import com.mygdx.game.box2d.BrickUserData;
import com.mygdx.game.utils.GameManager;

public class Brick extends GameActor {
    private Animation<TextureRegion> breakingAnimation;
    private TextureRegion brickTexture;
    private TextureAtlas textureAtlas;
    private boolean door;
    private PowerUpType powerUp;
    
    public static enum State {
        DESTROYED,
        EXPLODING,
        ACTIVE
    }

    public Brick(Body body) {
        super(body);
        getUserData().setActor(this);
        this.textureAtlas = GameManager.getInstance().getAssetManager().get(GameManager.BOMBERMAN_ATLAS_PATH);
        this.door = false;
        this.powerUp = null;

        // Load textures
        Array<TextureRegion> breakingFrames = new Array<TextureRegion>(TextureRegion.class);
        
        // Load breaking animation 
        for (String path : GameManager.BRICK_BREAKING_REGION_NAMES) {
            breakingFrames.add(textureAtlas.findRegion(path));
        }
        breakingAnimation = new Animation<TextureRegion>(0.1f, breakingFrames);

        // Load default texture 
        brickTexture = textureAtlas.findRegion(GameManager.BRICK_TEXTURE);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = screenRectangle.x + (screenRectangle.width - GameManager.BOMB_WIDTH) / 2;
        float y = screenRectangle.y + (screenRectangle.height - GameManager.BOMB_HEIGHT) / 2;
        float width = screenRectangle.width;
        float height = screenRectangle.height;
        TextureRegion currentFrame;

        switch(getUserData().getState()){
            case DESTROYED:
            case EXPLODING:
                body.setActive(false);
                currentFrame = breakingAnimation.getKeyFrame(stateTime, false);
                break;
            default:
                currentFrame = brickTexture;
                break;
        }

        batch.draw(currentFrame, x, y, width, height);

        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {

        if(getUserData().getState().equals(State.EXPLODING) && breakingAnimation.isAnimationFinished(stateTime)){
            getUserData().setState(State.DESTROYED);
        }
        
        super.act(delta);
    }

    @Override
    public BrickUserData getUserData() {
        return (BrickUserData) userData;
    }

    @Override
    public boolean isAlive() {
        return !getUserData().getState().equals(State.DESTROYED);
    }

    public void explode(){
        if(!getUserData().getState().equals(State.DESTROYED) && !getUserData().getState().equals(State.EXPLODING)){
            getUserData().setState(State.EXPLODING);
            stateTime = 0f; 
        }
    }

    public void setDoor(boolean haveDoor){
        this.door = haveDoor;
    }

    public void setPowerUp(PowerUpType powerUp){
        this.powerUp = powerUp;
    }

    public boolean haveDoor(){
        return this.door;
    }

    public PowerUpType getPowerUpType(){
        return this.powerUp;
    }

}
