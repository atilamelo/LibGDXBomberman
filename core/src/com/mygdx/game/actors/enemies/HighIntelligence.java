package com.mygdx.game.actors.enemies;

import java.util.List;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.box2d.OnilUserData;
import com.mygdx.game.systems.AStarManhattan;
import com.mygdx.game.systems.RandomPlacement.Position;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

/*
 * Estrutura de movimentação e implementação dos inimgios baseado no Bomberman for LibGdx (GitHub)
 * Link: TODO: INSERIR LINK
 */
public abstract class HighIntelligence extends Enemy {
    protected StateEnemyHighIntelligence state;
    private short[] maskBits;
    private List<Position> pursueBombermanPath;
    private Position lastBombermanPos;

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

    public HighIntelligence(Body body, int hp, float speed, short[] maskBits) {
        super(body, hp, speed);
        this.maskBits = maskBits;
        this.lastBombermanPos = tilePosition.deepCopy();
        this.state = StateEnemyHighIntelligence.getRandomWalkingState();
    }

    private void changeWalkingState() {
        state = StateEnemyHighIntelligence.getRandomWalkingState();
    }

    private boolean tilePositionChanged(){
        return !lastBombermanPos.equals(tilePosition);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        Vector2 pos;
        
        /* Check if enemy is dead */
        if (hp == 0 && !state.equals(StateEnemyHighIntelligence.DYING) & !state.equals(StateEnemyHighIntelligence.DIE)) {
            stateTime = 0f;
            state = StateEnemyHighIntelligence.DYING;
        }

        /* Pursue player system */
        handlePursue();

        /* Change walking state system */
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

    private void handlePursue() {
        if (tilePositionChanged()) {
            Position bombermanLocation = WorldUtils.bombermanWithinRange(tilePosition, 25);
            pursueBombermanPath = null;
            lastBombermanPos = tilePosition.deepCopy();

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
}
