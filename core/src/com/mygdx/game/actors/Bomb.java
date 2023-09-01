package com.mygdx.game.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.box2d.BombUserData;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.systems.RandomPlacement.Position;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

public class Bomb extends GameActor {
    public State state;
    private Animation<TextureRegion> bombAnimation;
    private TextureAtlas textureAtlas;
    private GameStage gameStage;
    private int power;
    private int x;
    private int y;
    public boolean flagWillExploded;
    public boolean flagIsSensor;

    public static enum State {
        ACTIVE,
        CREATINGEXPLOSE,
        EXPLODED
    }

    public Bomb(GameStage gameStage, Position position, int power) {
        this(gameStage, position.getX(), position.getY(), power);
    }

    public Bomb(GameStage gameStage, int x, int y, int power) {
        super(WorldUtils.createBomb(x + 0.5f, y + 0.5f));
        this.textureAtlas = GameManager.getInstance().getAssetManager().get(GameManager.BOMBERMAN_ATLAS_PATH);
        this.stateTime = 0f;
        this.state = ((BombUserData) body.getUserData()).getState();
        this.gameStage = gameStage;
        this.power = power;
        this.x = x;
        this.y = y;
        this.flagWillExploded = false;
        this.flagIsSensor = true;
        Array<TextureRegion> bombFrames = new Array<>();
        getUserData().setActor(this);

        // Load frames of animation
        for (String path : GameManager.BOMB) {
            bombFrames.add(textureAtlas.findRegion(path));
        }

        bombAnimation = new Animation<>(0.1f, bombFrames);

        gameStage.background.addActor(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float x = screenRectangle.x + (screenRectangle.width - GameManager.BOMB_WIDTH) / 2;
        float y = screenRectangle.y + (screenRectangle.height - GameManager.BOMB_HEIGHT) / 2;
        float width = screenRectangle.width;
        float height = screenRectangle.height;

        TextureRegion currentFrame = bombAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y, width, height);

    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (stateTime >= 3f && state.equals(State.ACTIVE)) {
            explode();
        }

        // Utiizado pelo WorldListener para explodir a bomba quando ela colide com outra explosão
        if (flagWillExploded && state.equals(State.ACTIVE)) {
            explode();
        }

        if(!flagIsSensor){
            body.getFixtureList().get(0).setSensor(false);
        }
    }

    @Override
    public BombUserData getUserData() {
        return (BombUserData) userData;
    }

    @Override
    public boolean isAlive() {
        return state.equals(State.ACTIVE);
    }

    private void explode() {        
        float width = GameManager.EXPLOSION_B2D_WIDTH;
        float height = GameManager.EXPLOSION_B2D_HEIGHT;
        float gap = GameManager.GAP_EXPLOSION;
        
        gameManager.playEffect(GameManager.SOUND_BOMB_EXPLODES, 0.2f);

        // Próprio local
        state = State.EXPLODED;

        // Próprio local
        Array<TextureRegion> explosionFrames = new Array<>();
        for (int i = 0; i < 4; i++) {
            explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_CENTER_REGION_NAMES[i]));
        }
        for (int i = 2; i >= 0; i--) {
            explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_CENTER_REGION_NAMES[i]));
        }

        Animation<TextureRegion> explosionAnimation = new Animation<>(0.1f, explosionFrames);

        new Explosion(gameStage, x, y, width - gap, height - gap, true, explosionAnimation);

        Vector2 position;

        // Testa posição acima
        for (int i = 0; i < power; i++) {
            position = new Vector2(x + 0.5f, y + 0.5f + i + 1);
            boolean hasWall = WorldUtils.hasObjectAtPosition(position, GameManager.WALL_BIT);
            boolean hasBrick = WorldUtils.hasObjectAtPosition(position, GameManager.BRICK_BIT);
            if (hasWall || hasBrick) {
                break;
            }

            // Se for a última explosão, cria a animação de cima
            explosionFrames = new Array<>();
            if (i == power - 1) {
                for (int iAnim = 0; iAnim < 4; iAnim++) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_UP_REGION_NAMES[iAnim]));
                }
                for (int iAnim = 2; iAnim >= 0; iAnim--) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_UP_REGION_NAMES[iAnim]));
                }
            } else {
                for (int iAnim = 0; iAnim < 4; iAnim++) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_UP_CONTINUE_REGION_NAMES[iAnim]));
                }
                for (int iAnim = 2; iAnim >= 0; iAnim--) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_UP_CONTINUE_REGION_NAMES[iAnim]));
                }
            }

            explosionAnimation = new Animation<>(0.1f, explosionFrames);

            if (i == power - 1) {
                new Explosion(gameStage, x, y + i + 1, width - gap, height - gap, false, explosionAnimation);
            } else {
                new Explosion(gameStage, x, y + i + 1, width - gap, height, false, explosionAnimation);
            }
        }

        // Testa posição abaixo
        for (int i = 0; i < power; i++) {
            position = new Vector2(x + 0.5f, y + 0.5f - (i + 1));
            boolean hasWall = WorldUtils.hasObjectAtPosition(position, GameManager.WALL_BIT);
            boolean hasBrick = WorldUtils.hasObjectAtPosition(position, GameManager.BRICK_BIT);

            if (hasWall || hasBrick) {
                break;
            }

            // Se for a última explosão, cria a animação de cima
            explosionFrames = new Array<>();
            if (i == power - 1) {
                for (int iAnim = 0; iAnim < 4; iAnim++) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_DOWN_REGION_NAMES[iAnim]));
                }
                for (int iAnim = 2; iAnim >= 0; iAnim--) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_DOWN_REGION_NAMES[iAnim]));
                }
            } else {
                for (int iAnim = 0; iAnim < 4; iAnim++) {
                    explosionFrames
                            .add(textureAtlas.findRegion(GameManager.EXPLOSION_DOWN_CONTINUE_REGION_NAMES[iAnim]));
                }
                for (int iAnim = 2; iAnim >= 0; iAnim--) {
                    explosionFrames
                            .add(textureAtlas.findRegion(GameManager.EXPLOSION_DOWN_CONTINUE_REGION_NAMES[iAnim]));
                }
            }
            explosionAnimation = new Animation<>(0.1f, explosionFrames);


            if (i == power - 1) {
                new Explosion(gameStage, x, y - (i + 1), width - gap, height - gap, false, explosionAnimation);
            } else {
                new Explosion(gameStage, x, y - (i + 1), width - gap, height, false, explosionAnimation);
            }

        }

        // Testa posição à direita
        for (int i = 0; i < power; i++) {
            position = new Vector2(x + 0.5f + (i + 1), y + 0.5f);
            boolean hasWall = WorldUtils.hasObjectAtPosition(position, GameManager.WALL_BIT);
            boolean hasBrick = WorldUtils.hasObjectAtPosition(position, GameManager.BRICK_BIT);
            if (hasWall || hasBrick) {
                break;
            }

            explosionFrames = new Array<>();
            if (i == power - 1) {
                for (int iAnim = 0; iAnim < 4; iAnim++) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_RIGHT_REGION_NAMES[iAnim]));
                }
                for (int iAnim = 2; iAnim >= 0; iAnim--) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_RIGHT_REGION_NAMES[iAnim]));
                }
            } else {
                for (int iAnim = 0; iAnim < 4; iAnim++) {
                    explosionFrames
                            .add(textureAtlas.findRegion(GameManager.EXPLOSION_RIGHT_CONTINUE_REGION_NAMES[iAnim]));
                }
                for (int iAnim = 2; iAnim >= 0; iAnim--) {
                    explosionFrames
                            .add(textureAtlas.findRegion(GameManager.EXPLOSION_RIGHT_CONTINUE_REGION_NAMES[iAnim]));
                }
            }
            explosionAnimation = new Animation<>(0.1f, explosionFrames);

            if (i == power - 1) {
                new Explosion(gameStage, x + (i + 1), y, width - gap, height - gap, false, explosionAnimation);
            } else {
                new Explosion(gameStage, x + (i + 1), y, width, height - gap, false, explosionAnimation);
            }
        }

        // Testa posição à esquerda
        for (int i = 0; i < power; i++) {
            position = new Vector2(x + 0.5f - (i + 1), y + 0.5f);
            boolean hasWall = WorldUtils.hasObjectAtPosition(position, GameManager.WALL_BIT);
            boolean hasBrick = WorldUtils.hasObjectAtPosition(position, GameManager.BRICK_BIT);
            if (hasWall || hasBrick) {
                break;
            }

            explosionFrames = new Array<>();
            if (i == power - 1) {
                for (int iAnim = 0; iAnim < 4; iAnim++) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_LEFT_REGION_NAMES[iAnim]));
                }
                for (int iAnim = 2; iAnim >= 0; iAnim--) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_LEFT_REGION_NAMES[iAnim]));
                }
            } else {
                for (int iAnim = 0; iAnim < 4; iAnim++) {
                    explosionFrames
                            .add(textureAtlas.findRegion(GameManager.EXPLOSION_LEFT_CONTINUE_REGION_NAMES[iAnim]));
                }
                for (int iAnim = 2; iAnim >= 0; iAnim--) {
                    explosionFrames
                            .add(textureAtlas.findRegion(GameManager.EXPLOSION_LEFT_CONTINUE_REGION_NAMES[iAnim]));
                }
            }
            explosionAnimation = new Animation<>(0.1f, explosionFrames);

            if (i == power - 1) {
                new Explosion(gameStage, x - (i + 1), y, width - gap, height - gap, false, explosionAnimation);
            } else {
                new Explosion(gameStage, x - (i + 1), y, width, height - gap, false, explosionAnimation);
            }
        }
    }

}
