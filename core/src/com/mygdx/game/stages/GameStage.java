package com.mygdx.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.game.utils.WorldUtils;

// Estrutura básica baseado no Runner feito por William Moura 
// http://williammora.com/a-running-game-with-libgdx-part-1
public class GameStage extends Stage {

    // This will be our viewport measurements while working with the debug renderer
    private static final int VIEWPORT_WIDTH = 20;
    private static final int VIEWPORT_HEIGHT = 13;

    private World world;
    private Body bomberman;

    private InputProcessor inputProcessor;

    private final float TIME_STEP = 1 / 300f;
    private float accumulator = 0f;

    private OrthographicCamera camera;
    private Box2DDebugRenderer renderer;

    public GameStage() {
        world = WorldUtils.createWorld();
        bomberman = WorldUtils.createBomberman(world);
        renderer = new Box2DDebugRenderer();
        setupCamera();

        inputProcessor = new InputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);

    }

    private void setupCamera() {
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
        camera.update();
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

        @Override
        public boolean keyDown(int keycode) {
            // TODO: Implementar com switch case e variáveis de apoio para o movimento x e y;
            // TODO: Imple

            if (keycode == Input.Keys.UP)
                bomberman.setLinearVelocity(new Vector2(0, 1));
            if (keycode == Input.Keys.DOWN)
                bomberman.setLinearVelocity(new Vector2(0, -1));
            if (keycode == Input.Keys.LEFT)
                bomberman.setLinearVelocity(new Vector2(-1, 0));
            if (keycode == Input.Keys.RIGHT)
                bomberman.setLinearVelocity(new Vector2(1, 0));

            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            // TODO: Arrumar essa parte de quando o bomberman para, ele só deve parar se a direção do sentido 
            // é a mesma da que a tecla foi solta
            // TODO: Implementar com switch case
            if (keycode == Input.Keys.UP)
                bomberman.setLinearVelocity(new Vector2(0, 0));
            if (keycode == Input.Keys.DOWN)
                bomberman.setLinearVelocity(new Vector2(0, 0));
            if (keycode == Input.Keys.LEFT)
                bomberman.setLinearVelocity(new Vector2(0, 0));
            if (keycode == Input.Keys.RIGHT)
                bomberman.setLinearVelocity(new Vector2(0, 0));

            return true;
        }

    }

}
