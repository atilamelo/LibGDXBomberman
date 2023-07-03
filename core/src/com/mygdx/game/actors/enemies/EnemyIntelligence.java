package com.mygdx.game.actors.enemies;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.systems.AStarManhattan;
import com.mygdx.game.systems.RandomPlacement.Position;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

/*
 * Estrutura de movimentação e implementação dos inimgios baseado no Bomberman for LibGdx (GitHub)
 * Link: TODO: INSERIR LINK
 */
public abstract class HighIntelligence extends Enemy {
    protected State state;
    private short[] maskBits;
    private List<Position> pursueBombermanPath;
    private Position lastBombermanPosPursue;
    private Position lastBombermanPosInter;
    private float intersectionChangeChance;
    private int rangePursue;

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

    public HighIntelligence(Body body, int hp, float speed, short[] maskBits, float intersectionChangeChance, int rangePursue) {
        super(body, hp, speed);
        this.maskBits = maskBits;
        this.intersectionChangeChance = intersectionChangeChance;
        this.rangePursue = rangePursue;
        this.lastBombermanPosPursue = tilePosition.deepCopy();
        this.lastBombermanPosInter = tilePosition.deepCopy();
        this.state = State.getRandomWalkingState();
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
    public void act(float delta) {
        super.act(delta);

        /* Check if enemy is dead / dying */
        if (hp == 0 && !state.equals(State.DYING) & !state.equals(State.DIE)) {
            stateTime = 0f;
            state = State.DYING;
        }

        /*
         * System for randomly changing direction when the player encounters an
         * intersection
         */
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

        if (pursueBombermanPath != null && !state.equals(State.DIE) && !state.equals(State.DYING)) {
            if (pursueBombermanPath.size() > 0) {
                Position nextPosition = pursueBombermanPath.get(0);
                if (WorldUtils.isEnemyInsideTile(body, nextPosition)) {
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
                && WorldUtils.isEnemyInsideTile(body, matrixPosition)) {
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
}
