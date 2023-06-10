package com.mygdx.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.game.actors.Bomberman;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.utils.WorldUtils;

// Estrutura básica baseado no Runner feito por William Moura 
// http://williammora.com/a-running-game-with-libgdx-part-1
public class GameStage extends Stage{

    // This will be our viewport measurements while working with the debug renderer
    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;

    private World world;
    private Bomberman bomberman;

    private InputProcessor inputProcessor;

    private final float TIME_STEP = 1 / 300f;
    private float accumulator = 0f;

    private OrthographicCamera camera;
    private Box2DDebugRenderer renderer;

    public GameStage() {
        world = WorldUtils.createWorld();
        Body bombermanBody = WorldUtils.createBomberman(world);
        bomberman = new Bomberman(bombermanBody);
        renderer = new Box2DDebugRenderer();
        setupCamera();
        setupWorld();

        inputProcessor = new InputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);

    }

    private void setupCamera() {
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
        camera.update();
    }

    private void setupWorld(){
        addActor(bomberman);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Fixed timestep
        accumulator += delta;

        while (accumulator >= delta) {
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }

        // TODO: Implement interpolation

    }

    @Override
    public void draw() {
        super.draw();
        renderer.render(world, camera.combined);
    }

    private class InputProcessor extends InputAdapter {
        float moving_x, moving_y;

        public InputProcessor() {
            moving_x = moving_y = 0.0f;
        }

        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Input.Keys.UP:
                    bomberman.moveUp();
                    break;
                case Input.Keys.DOWN:
                    bomberman.moveDown();
                    break;
                case Input.Keys.LEFT:
                    bomberman.moveLeft();
                    break;
                case Input.Keys.RIGHT:
                    bomberman.moveRight();
                    break;
            }

            // bomberman.move(new Vector2(moving_x, moving_y));

            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            switch (keycode) {
                case Keys.UP:
                    bomberman.moveUp = false;
                case Keys.DOWN:
                    bomberman.moveDown = false; 
                    moving_y = 0;
                    break;
                case Keys.LEFT:
                    bomberman.moveLeft = false;
                case Keys.RIGHT:
                    bomberman.moveRight = false;
                    moving_x = 0;
                    break;
            }

            bomberman.move(new Vector2(moving_x, moving_y));

            return true;
        }

    }

}
