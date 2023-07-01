package com.mygdx.game.actors.enemies;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.box2d.BallomUserData;
import com.mygdx.game.stages.GameStage;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

/*
 * Estrutura de movimentação e implementação dos inimgios baseado no Bomberman for LibGdx (GitHub)
 * Link: TODO: INSERIR LINK
 */
public class Ballom extends Enemy {
    private float stateTime;
    private StateBallom state;

    public static enum StateBallom {
        WALKING_UP,
        WALKING_DOWN,
        WALKING_LEFT,
        WALKING_RIGHT,
        DYING,
        DIE;

        public static StateBallom getRandomWalkingState() {
            return values()[(int) (Math.random() * 4)];
        }
    }

    public Ballom(Body body, GameStage stage) {
        super(body, GameManager.BALLON_HP, GameManager.BALLON_SPEED);
        stateTime = 0f;
        state = StateBallom.getRandomWalkingState();

        stage.addActor(this);
    }

    private void changeWalkingState() {
        state = StateBallom.getRandomWalkingState();
    }

    @Override
    public BallomUserData getUserData() {
        return (BallomUserData) userData;
    }

    @Override
    public void act(float delta) {
        Vector2 pos;
        stateTime += delta;
        super.act(delta);

        switch (state) {
            case WALKING_UP:
                pos = new Vector2(body.getPosition().x, body.getPosition().y + .4f);

                if (body.getLinearVelocity().y != speed) {
                    body.setLinearVelocity(new Vector2(0, speed * body.getMass()));
                }

                if (WorldUtils.hitSomething(pos)) {
                    changeWalkingState();
                }
                break;
            case WALKING_DOWN:
                pos = new Vector2(body.getPosition().x, body.getPosition().y - .4f);

                if (body.getLinearVelocity().y != -speed) {
                    body.setLinearVelocity(new Vector2(0, -speed * body.getMass()));
                }

                if (WorldUtils.hitSomething(pos)) {
                    changeWalkingState();
                }
                break;
            case WALKING_LEFT:
                pos = new Vector2(body.getPosition().x - .5f, body.getPosition().y);

                if (body.getLinearVelocity().x != -speed) {
                    body.setLinearVelocity(new Vector2(-speed * body.getMass(), 0));
                }
                if (WorldUtils.hitSomething(pos)) {
                    changeWalkingState();
                }
                break;
            case WALKING_RIGHT:
                if (body.getLinearVelocity().x != speed) {
                    // body.applyLinearImpulse(new Vector2(speed * body.getMass(), 0), body.getWorldCenter(), true);
                
                    body.setLinearVelocity(new Vector2(speed * body.getMass(), 0));}
                pos = new Vector2(body.getPosition().x + .5f, body.getPosition().y);
                if (WorldUtils.hitSomething(pos)) {
                    changeWalkingState();
                }
                break;
            case DYING:
                break;
            case DIE:
                break;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

}
