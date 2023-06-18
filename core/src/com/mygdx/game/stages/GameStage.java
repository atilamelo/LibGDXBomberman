package com.mygdx.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.actors.Bomberman;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.utils.WorldUtils;

// Estrutura básica baseado no Runner feito por William Moura 
// http://williammora.com/a-running-game-with-libgdx-part-1
public class GameStage extends Stage {

    // This will be our viewport measurements while working with the debug renderer
    private static final int VIEWPORT_WIDTH = Constants.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = Constants.APP_HEIGHT;

    private World world;
    private Bomberman bomberman;

    private InputProcessor inputProcessor;

    private final float TIME_STEP = 1 / 300f;
    private float accumulator = 0f;

    private OrthographicCamera gamecam;
    private FitViewport gameport;
    private Box2DDebugRenderer box2drender;
    private OrthogonalTiledMapRenderer tiledRender;
    private TiledMap map;

    public GameStage() {
        // Tiled Maps
        map = new TmxMapLoader().load("maps/map_teste.tmx");
        tiledRender = new OrthogonalTiledMapRenderer(map, 1 / Constants.PPM);

        // Box2d
        world = WorldUtils.createWorld();
        box2drender = new Box2DDebugRenderer();

        // Setups
        setupCamera();
        setupWorld();

        inputProcessor = new InputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);

    }

    private void setupCamera() {
        gamecam = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        gamecam.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        gamecam.zoom -= 0.25f;
        gamecam.update();
        gameport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, gamecam);
        this.setViewport(gameport);

    }

    private void setupWorld() {
        world = WorldUtils.createWorld();
        setupBomberman();
        setupCollision();
    }

    private void setupBomberman() {
        bomberman = new Bomberman(WorldUtils.createBomberman(world));
        addActor(bomberman);
    }

    private void setupCollision() {
        WorldUtils.createMap(world, map);

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
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
        gamecam.position.x = bomberman.getScreenRectangle().x;
        gamecam.position.y = bomberman.getScreenRectangle().y;

        gameport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.setViewport(gameport);
        
        
        tiledRender.setView(gamecam);
        tiledRender.render();   
        box2drender.render(world, gamecam.combined);
        

        super.draw();
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
