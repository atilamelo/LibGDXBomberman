package com.mygdx.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.box2d.UserData;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

public class Bomb extends GameActor {

    public enum State {
        EXPLODED,
        ACTIVE
    }

    public State state;

    private Animation<TextureRegion> bombAnimation;
    private TextureAtlas textureAtlas;
    private float stateTime;
    private GameStage gameStage;
    private int power;
    private int x;
    private int y;

    public Bomb(GameStage gameStage, int x, int y, int power) {
        super(WorldUtils.createBomb(x + 0.5f, y + 0.5f));
        this.textureAtlas = GameManager.getInstance().getAssetManager().get(GameManager.BOMBERMAN_ATLAS_PATH);
        this.state = State.ACTIVE;
        this.stateTime = 0f;
        this.gameStage = gameStage;
        this.power = power;
        this.x = x;
        this.y = y;
        Array<TextureRegion> bombFrames = new Array<>();

        // Load frames of animation
        for (String path : GameManager.BOMB_ANIMATION) {
            bombFrames.add(textureAtlas.findRegion(path));
        }

        bombAnimation = new Animation<>(0.1f, bombFrames);

        gameStage.addActor(this);
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

        stateTime += delta;
        if (stateTime >= 3f) {
            explode();
        }
    }

    @Override
    public UserData getUserData() {
        return userData;
    }

    @Override
    public boolean isAlive() {
        return state.equals(State.ACTIVE);
    }

    public void explode() {
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

        new Explosion(gameStage, x, y, explosionAnimation);

        Vector2 position;

        // Testa posição acima
        for (int i = 0; i < power; i++) {
            position = new Vector2(x + 0.5f, y + 0.5f + i + 1);
            boolean hasWall = WorldUtils.hasObjectAtPosition(position, GameManager.WALL_BIT);
            if (hasWall) {
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
            }else{
                for (int iAnim = 0; iAnim < 4; iAnim++) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_UP_CONTINUE_REGION_NAMES[iAnim]));
                }
                for (int iAnim = 2; iAnim >= 0; iAnim--) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_UP_CONTINUE_REGION_NAMES[iAnim]));
                }
            }

            explosionAnimation = new Animation<>(0.1f, explosionFrames);

            System.out.println("Explosão criada em cima");
            new Explosion(gameStage, x, y + i + 1, explosionAnimation);
        }

        // Testa posição abaixo
        for (int i = 0; i < power; i++) {
            position = new Vector2(x + 0.5f, y + 0.5f - (i + 1));
            boolean hasWall = WorldUtils.hasObjectAtPosition(position, GameManager.WALL_BIT);
            if (hasWall) {
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
            }else{
                for (int iAnim = 0; iAnim < 4; iAnim++) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_DOWN_CONTINUE_REGION_NAMES[iAnim]));
                }
                for (int iAnim = 2; iAnim >= 0; iAnim--) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_DOWN_CONTINUE_REGION_NAMES[iAnim]));
                }
            }
            explosionAnimation = new Animation<>(0.1f, explosionFrames);

            System.out.println("Explosão criada abaixo");
            new Explosion(gameStage, x, y - (i + 1), explosionAnimation);
        }

        // Testa posição à direita
        for (int i = 0; i < power; i++) {
            position = new Vector2(x + 0.5f + (i + 1), y + 0.5f);
            boolean hasWall = WorldUtils.hasObjectAtPosition(position, GameManager.WALL_BIT);
            if (hasWall) {
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
            }else{
                for (int iAnim = 0; iAnim < 4; iAnim++) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_RIGHT_CONTINUE_REGION_NAMES[iAnim]));
                }
                for (int iAnim = 2; iAnim >= 0; iAnim--) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_RIGHT_CONTINUE_REGION_NAMES[iAnim]));
                }
            }
            explosionAnimation = new Animation<>(0.1f, explosionFrames);

            System.out.println("Explosão criada à direita");
            new Explosion(gameStage, x + (i + 1), y, explosionAnimation);
        }

        // Testa posição à esquerda
        for (int i = 0; i < power; i++) {
            position = new Vector2(x + 0.5f - (i + 1), y + 0.5f);
            boolean hasWall = WorldUtils.hasObjectAtPosition(position, GameManager.WALL_BIT);
            if (hasWall) {
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
            }else{
                for (int iAnim = 0; iAnim < 4; iAnim++) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_LEFT_CONTINUE_REGION_NAMES[iAnim]));
                }
                for (int iAnim = 2; iAnim >= 0; iAnim--) {
                    explosionFrames.add(textureAtlas.findRegion(GameManager.EXPLOSION_LEFT_CONTINUE_REGION_NAMES[iAnim]));
                }
            }
            explosionAnimation = new Animation<>(0.1f, explosionFrames);

            System.out.println("Explosão criada à esquerda");
            new Explosion(gameStage, x - (i + 1), y, explosionAnimation);
        }
    }

}
