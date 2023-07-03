package com.mygdx.game.actors.enemies;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.box2d.OnilUserData;
import com.mygdx.game.systems.AStarManhattan;
import com.mygdx.game.systems.CoordinateConverter;
import com.mygdx.game.systems.RandomPlacement.Position;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

/*
 * Estrutura de movimentação e implementação dos inimgios baseado no Bomberman for LibGdx (GitHub)
 * Link: TODO: INSERIR LINK
 */
public class HighIntelligence extends Enemy {
    private StateEnemyHighIntelligence state;
    private TextureAtlas textureAtlas;
    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private TextureRegion dyingTexture;
    private short[] maskBits = { GameManager.WALL_BIT, GameManager.BRICK_BIT, GameManager.BOMB_BIT };
    private List<Position> pursueBombermanPath;
    private float lastChangedWalking;
    private float lastPursueCheck;
    private float lastChangedAttack;

    public static enum StateEnemyHighIntelligence {
        WALKING_UP,
        WALKING_DOWN,
        WALKING_LEFT,
        WALKING_RIGHT,
        ATTACKING_UP,
        ATTACKING_DOWN,
        ATTACKING_LEFT,
        ATTACKING_RIGHT,
        DYING,
        DIE;

        public static StateEnemyHighIntelligence getRandomWalkingState() {
            return values()[(int) (Math.random() * 4)];
        }

        public boolean isAttacking() {
            return this == ATTACKING_UP || this == ATTACKING_DOWN || this == ATTACKING_LEFT || this == ATTACKING_RIGHT;
        }
    }

    public HighIntelligence(Body body) {
        super(body, GameManager.ONIL_HP, GameManager.ONIL_SPEED);
        state = StateEnemyHighIntelligence.getRandomWalkingState();
        getUserData().setActor(this);
        lastChangedWalking = 0f;
        lastPursueCheck = 0f;

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
        state = StateEnemyHighIntelligence.getRandomWalkingState();
    }

    @Override
    public OnilUserData getUserData() {
        return (OnilUserData) userData;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        Vector2 pos;
        
        System.out.println("Is onil totally inside a tile?" + WorldUtils.isEnemyInsideTile(body, matrixPosition));
        System.out.println("Onil matrix position:" + matrixPosition);

        if (hp == 0 && !state.equals(StateEnemyHighIntelligence.DYING) & !state.equals(StateEnemyHighIntelligence.DIE)) {
            stateTime = 0f;
            state = StateEnemyHighIntelligence.DYING;
        }

        /* Chase player system */
        handlePursue();

        switch (state) {
            case WALKING_UP:
                pos = new Vector2(body.getPosition().x, body.getPosition().y + .3f);

                if (WorldUtils.hitSomething(pos, maskBits)) {
                    changeWalkingState();
                }
            case ATTACKING_UP:
                if (body.getLinearVelocity().y != speed) {
                    body.setLinearVelocity(new Vector2(0, speed * body.getMass()));
                }
                break;

            case WALKING_DOWN:
                pos = new Vector2(body.getPosition().x, body.getPosition().y - .3f);
                if (WorldUtils.hitSomething(pos, maskBits)) {
                    changeWalkingState();
                }
            case ATTACKING_DOWN:
                if (body.getLinearVelocity().y != -speed) {
                    body.setLinearVelocity(new Vector2(0, -speed * body.getMass()));
                }
                break;
            case WALKING_LEFT:
                pos = new Vector2(body.getPosition().x - .3f, body.getPosition().y);

                if (WorldUtils.hitSomething(pos, maskBits)) {
                    changeWalkingState();
                }
            case ATTACKING_LEFT:
                if (body.getLinearVelocity().x != -speed) {
                    body.setLinearVelocity(new Vector2(-speed * body.getMass(), 0));
                }
                break;
            case WALKING_RIGHT:
                pos = new Vector2(body.getPosition().x + .3f, body.getPosition().y);
                if (WorldUtils.hitSomething(pos, maskBits)) {
                    changeWalkingState();
                }
            case ATTACKING_RIGHT:
                if (body.getLinearVelocity().x != speed) {
                    body.setLinearVelocity(new Vector2(speed * body.getMass(), 0));
                }
                break;
            case DYING:
                body.setActive(false);
                if (stateTime > GameManager.ENEMY_DYING_TIME) {
                    state = StateEnemyHighIntelligence.DIE;
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

    private void handlePursue() {
        if (lastPursueCheck + 0.5f < stateTime) {
            Position bombermanLocation = WorldUtils.bombermanWithinRange(tilePosition, 25);
            pursueBombermanPath = null;
            if (bombermanLocation != null) {
                short[] categoryBits = { GameManager.BRICK_BIT, GameManager.WALL_BIT, GameManager.BOMB_BIT };
                List<List<Position>> mapGrid = WorldUtils.getMapGrid(categoryBits);
                pursueBombermanPath = AStarManhattan.findPath(matrixPosition, bombermanLocation, mapGrid);

                if (pursueBombermanPath != null) {
                    AStarManhattan.printBoardWithPath(mapGrid, pursueBombermanPath);
                } else {
                    System.out.println("Caminho impossível");
                }
                System.out.println("\n\n");
            }

            lastPursueCheck = stateTime;
        }

        if (pursueBombermanPath != null && !state.equals(StateEnemyHighIntelligence.DIE) && !state.equals(StateEnemyHighIntelligence.DYING)) {
            if (pursueBombermanPath.size() > 0) {
                Position nextPosition = pursueBombermanPath.get(0);
                if (WorldUtils.isEnemyInsideTile(body, nextPosition)) {
                    pursueBombermanPath.remove(0);
                } else {
                    if (nextPosition.getX() > matrixPosition.getX()) {
                        state = StateEnemyHighIntelligence.ATTACKING_RIGHT;
                    } else if (nextPosition.getX() < matrixPosition.getX()) {
                        state = StateEnemyHighIntelligence.ATTACKING_LEFT;
                    } else if (nextPosition.getY() < matrixPosition.getY()) {
                        state = StateEnemyHighIntelligence.ATTACKING_UP;
                    } else if (nextPosition.getY() > matrixPosition.getY()) {
                        state = StateEnemyHighIntelligence.ATTACKING_DOWN;
                    }

                }
            } else {
                pursueBombermanPath = null;
            }
        } else if (state.isAttacking()) {
            changeWalkingState();
        }
    }

    @Override
    public boolean isDyingFinished() {
        return state.equals(StateEnemyHighIntelligence.DIE);
    }

}
