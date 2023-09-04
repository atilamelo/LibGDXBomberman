package com.mygdx.game.actors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.box2d.EnemyUserData;
import com.mygdx.game.configs.EnemyConfig;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.stages.MultiplayerStage;
import com.mygdx.game.stages.ServerStage;
import com.mygdx.game.systems.AStarManhattan;
import com.mygdx.game.systems.RandomPlacement.Position;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

/*
 * Estrutura de movimentação e implementação dos inimgios baseado no Bomberman for LibGdx (GitHub)
 */
public class Enemy extends GameActor {
    private static final float PACKET_SEND_INTERVAL = 5f / 60f; // 60 packets per second
    private TextureAtlas textureAtlas;
    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private Animation<TextureRegion> dyingAnimation;
    protected State state;
    private short[] maskBits;
    private List<Position> pursueBombermanPath;
    private Position lastBombermanPosPursue;
    private Position lastBombermanPosInter;
    private float intersectionChangeChance;
    private int rangePursue;
    private int hp;
    private float speed;
    private float lastSendX;
    private float lastSendY;
    private float packetSendTimer;
    public UUID multiplayer_id;

    public static enum State {
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

        public static State getRandomWalkingState() {
            return values()[(int) (Math.random() * 4)];
        }

        public boolean isAttacking() {
            return this == ATTACKING_UP || this == ATTACKING_DOWN || this == ATTACKING_LEFT || this == ATTACKING_RIGHT;
        }
    }

    public Enemy(UUID id, Body body, EnemyConfig config) {
        this(body, config);
        this.multiplayer_id = id;
    }

    public Enemy(Body body, EnemyConfig config) {
        super(body);
        this.hp = config.hp;
        this.speed = config.speed;
        this.maskBits = config.maskBits;
        this.intersectionChangeChance = config.intersectionChangeChance;
        this.rangePursue = config.rangePursue;
        this.lastBombermanPosPursue = tilePosition.deepCopy();
        this.lastBombermanPosInter = tilePosition.deepCopy();
        this.state = State.getRandomWalkingState();
        this.packetSendTimer = 0f;

        getUserData().setActor(this);

        /* Load textures */
        this.textureAtlas = GameManager.getInstance().getAssetManager().get(GameManager.BOMBERMAN_ATLAS_PATH);
        Array<TextureRegion> leftFrames = new Array<TextureRegion>(TextureRegion.class);
        Array<TextureRegion> rightFrames = new Array<TextureRegion>(TextureRegion.class);
        Array<TextureRegion> dyingFrames = new Array<TextureRegion>(TextureRegion.class);

        // Load left region into the animation
        for (String path : config.leftAnimation) {
            leftFrames.add(textureAtlas.findRegion(path));
        }
        leftAnimation = new Animation<TextureRegion>(0.1f, leftFrames);

        // Load right region into the animation
        for (String path : config.rightAnimation) {
            rightFrames.add(textureAtlas.findRegion(path));
        }
        rightAnimation = new Animation<TextureRegion>(0.1f, rightFrames);

        // Load dying animation
        for (String path : config.dyingAnimation) {
            dyingFrames.add(textureAtlas.findRegion(path));
        }
        dyingAnimation = new Animation<TextureRegion>(0.2f, dyingFrames);

    }

    private void changeWalkingState() {
        state = State.getRandomWalkingState();
    }

    private boolean tilePositionChanged(Position position) {
        return !position.equals(tilePosition);
    }

    private State getPositionState(Position currentPosition, Position targetPosition) {
        int deltaX = targetPosition.getX() - currentPosition.getX();
        int deltaY = targetPosition.getY() - currentPosition.getY();

        if (deltaX < 0) {
            return State.WALKING_LEFT;
        } else if (deltaX > 0) {
            return State.WALKING_RIGHT;
        } else if (deltaY < 0) {
            return State.WALKING_UP;
        } else if (deltaY > 0) {
            return State.WALKING_DOWN;
        } else {
            // Se as posições forem iguais, você pode retornar um estado padrão
            return State.WALKING_UP;
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
                currentFrame = dyingAnimation.getKeyFrame(stateTime, false);
                break;
            default:
                break;
        }

        // Draw the current frame
        batch.draw(currentFrame, x, y, width, height);

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        

        /* Check if enemy is dead / dying */
        if (hp <= 0 && !state.equals(State.DYING) & !state.equals(State.DIE)) {
            stateTime = 0f;
            state = State.DYING;
        }

        /*
         * System for randomly changing direction when the player encounters an
         * intersection
         */
        if(!(this.getStage() instanceof MultiplayerStage)){
            if (intersectionChangeChance > 0) {
                handleIntersectChange();
            }

            /* Pursue player system */
            if (rangePursue > 0) {
                handlePursue();
            }

            /* Handle state system */
            handleState();

        } 
        
        /* If server, send information of x, y to all clients */
        if(this.getStage() instanceof ServerStage) {
            packetSendTimer += delta;
            if (packetSendTimer >= PACKET_SEND_INTERVAL) {
                packetSendTimer -= PACKET_SEND_INTERVAL;
    
                if (lastSendX != body.getPosition().x || lastSendY != body.getPosition().y) {
                    ServerStage stage = (ServerStage) this.getStage();
                    lastSendX = body.getPosition().x;
                    lastSendY = body.getPosition().y;
                    stage.sendEnemyPosition(multiplayer_id, lastSendX, lastSendY);
                }
            }
        }
    }

    private void handleState() {
        Vector2 pos;
        switch (state) {
            case WALKING_UP:
                pos = new Vector2(body.getWorldCenter().x, body.getWorldCenter().y + .3f);

                if (WorldUtils.hitSomething(pos, maskBits)) {
                    changeWalkingState();
                }
            case ATTACKING_UP:
                if (body.getLinearVelocity().y != speed) {
                    body.setLinearVelocity(new Vector2(0, speed * body.getMass()));
                }
                break;

            case WALKING_DOWN:
                pos = new Vector2(body.getWorldCenter().x, body.getWorldCenter().y - .3f);
                if (WorldUtils.hitSomething(pos, maskBits)) {
                    changeWalkingState();
                }
            case ATTACKING_DOWN:
                if (body.getLinearVelocity().y != -speed) {
                    body.setLinearVelocity(new Vector2(0, -speed * body.getMass()));
                }
                break;
            case WALKING_LEFT:
                pos = new Vector2(body.getWorldCenter().x - .3f, body.getWorldCenter().y);

                if (WorldUtils.hitSomething(pos, maskBits)) {
                    changeWalkingState();
                }
            case ATTACKING_LEFT:
                if (body.getLinearVelocity().x != -speed) {
                    body.setLinearVelocity(new Vector2(-speed * body.getMass(), 0));
                }
                break;
            case WALKING_RIGHT:
                pos = new Vector2(body.getWorldCenter().x + .3f, body.getWorldCenter().y);
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
                    state = State.DIE;
                }
                break;
            case DIE:
                break;
        }

    }

    private void handlePursue() {
        if (tilePositionChanged(lastBombermanPosPursue)) {
            Position bombermanLocation = WorldUtils.bombermanWithinRange(tilePosition, rangePursue);
            pursueBombermanPath = null;
            lastBombermanPosPursue = tilePosition.deepCopy();

            if (bombermanLocation != null) {

                List<List<Position>> mapGrid = WorldUtils.getMapGrid(removePlayerBit(maskBits));
                pursueBombermanPath = AStarManhattan.findPath(matrixPosition, bombermanLocation, mapGrid);

            }

        }

        if (pursueBombermanPath != null && !state.equals(State.DIE) && !state.equals(State.DYING)) {
            if (pursueBombermanPath.size() > 0) {
                Position nextPosition = pursueBombermanPath.get(0);
                if (WorldUtils.isBodyInsideTile(body, nextPosition)) {
                    pursueBombermanPath.remove(0);
                } else {
                    if (nextPosition.getX() > matrixPosition.getX()) {
                        state = State.ATTACKING_RIGHT;
                    } else if (nextPosition.getX() < matrixPosition.getX()) {
                        state = State.ATTACKING_LEFT;
                    } else if (nextPosition.getY() < matrixPosition.getY()) {
                        state = State.ATTACKING_UP;
                    } else if (nextPosition.getY() > matrixPosition.getY()) {
                        state = State.ATTACKING_DOWN;
                    }

                }
            } else {
                pursueBombermanPath = null;
            }
        } else if (state.isAttacking()) {
            changeWalkingState();
        }
    }

    private void handleIntersectChange() {
        if (tilePositionChanged(lastBombermanPosInter) && !state.isAttacking()
                && WorldUtils.isBodyInsideTile(body, matrixPosition)) {
            lastBombermanPosInter = tilePosition.deepCopy();
            List<Position> freeAdjacentPositions = WorldUtils.getFreeAdjacentPositions(tilePosition, maskBits);
            List<Position> positionsToRemove = new ArrayList<Position>();

            switch (state) {
                case WALKING_UP:
                case WALKING_DOWN:
                    for (Position pos : freeAdjacentPositions) {
                        State posState = getPositionState(tilePosition, pos);
                        if (posState.equals(State.WALKING_DOWN) || posState.equals(State.WALKING_UP)) {
                            positionsToRemove.add(pos);
                        }
                    }
                    break;
                case WALKING_LEFT:
                case WALKING_RIGHT:
                    for (Position pos : freeAdjacentPositions) {
                        State posState = getPositionState(tilePosition, pos);
                        if (posState.equals(State.WALKING_LEFT) || posState.equals(State.WALKING_RIGHT)) {
                            positionsToRemove.add(pos);
                        }
                    }
                default:
                    break;
            }

            freeAdjacentPositions.removeAll(positionsToRemove);

            if (freeAdjacentPositions.size() > 0 && Math.random() < intersectionChangeChance) {
                Position newDirection = freeAdjacentPositions
                        .get((int) (Math.random() * freeAdjacentPositions.size()));

                state = getPositionState(tilePosition, newDirection);
            }
        }
    }

    public static short[] removePlayerBit(short[] maskBits) {
        List<Short> newMaskBits = new ArrayList<>();

        for (short bit : maskBits) {
            if (bit != GameManager.PLAYER_BIT) {
                newMaskBits.add(bit);
            }
        }

        short[] updatedMaskBits = new short[newMaskBits.size()];

        for (int i = 0; i < newMaskBits.size(); i++) {
            updatedMaskBits[i] = newMaskBits.get(i);
        }

        return updatedMaskBits;
    }

    @Override
    public EnemyUserData getUserData() {
        return (EnemyUserData) userData;
    };

    public boolean isDyingFinished() {
        if (state.equals(State.DIE) || state.equals(State.DYING)) {
            return dyingAnimation.isAnimationFinished(stateTime);
        } else {
            return false;
        }
    }

    @Override
    public boolean isAlive() {
        /* Wait animation of dying finish to remove Actor of stage and body of world */
        return hp > 0 || !isDyingFinished();
    };

    public void takeDamage(int damage) {
        if(stateTime > 1.0f){
            hp -= damage;
        }
    }

}
