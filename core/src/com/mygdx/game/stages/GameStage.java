package com.mygdx.game.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.actors.Bomberman;
import com.mygdx.game.enums.StateBomberman;
import com.mygdx.game.listeners.WorldListener;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

// Estrutura básica baseado no Runner feito por William Moura 
// http://williammora.com/a-running-game-with-libgdx-part-1
public class GameStage extends Stage {

    // This will be our viewport measurements while working with the debug renderer
    private static final int VIEWPORT_WIDTH = GameManager.APP_WIDTH;
    private static final int VIEWPORT_HEIGHT = GameManager.APP_HEIGHT;

    public GameScreen gameScreen;
    private GameManager gameManager;

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

    public GameStage(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.gameManager = GameManager.getInstance();

        // Tiled Maps
        map = GameManager.getInstance().getAssetManager().get("maps/map_teste.tmx");
        tiledRender = new OrthogonalTiledMapRenderer(map, 1 / GameManager.PPM);

        // Setups
        setupWorld();
        setupViewPort();

        // Box2d
        box2drender = new Box2DDebugRenderer();


        inputProcessor = new InputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);

    }

    private void setupViewPort() {
        gamecam = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        gamecam.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        gamecam.zoom -= GameManager.GAME_ZOOM;
        gamecam.update();

        gameport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, gamecam);
        this.setViewport(gameport);

    }

    private void setupWorld() {
        world = gameManager.getWorld();
        setupBomberman();
        setupCollision();
    }

    private void setupBomberman() {
        bomberman = new Bomberman(WorldUtils.createBomberman(), this);
        addActor(bomberman);
    }

    private void setupCollision() {
        WorldUtils.createMap(map);

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
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        handleCamera();

        gameport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.setViewport(gameport);

        tiledRender.setView(gamecam);
        tiledRender.render();
        // box2drender.render(world, gamecam.combined);

        super.draw();
    }

    /*
     * Lida com a posição da câmera baseando-se na posição do Bomberman
     * Baseado na solução do StackExchange de MattSams
     * https://gamedev.stackexchange.com/a/74934
     */
    void handleCamera() {
        // Calcula a posição da câmera
        float targetX = bomberman.getScreenRectangle().x;
        float targetY = bomberman.getScreenRectangle().y;

        // Centraliza a câmera
        gamecam.position.set(targetX, targetY, 0);

        // Calcula a metade da largura e altura da câmera
        float cameraHalfWidth = gamecam.viewportWidth * gamecam.zoom * .5f;
        float cameraHalfHeight = gamecam.viewportHeight * gamecam.zoom * .5f;

        // Calcula os limites do mapa
        int mapLeft = 0;
        int mapRight = 0 + VIEWPORT_WIDTH;
        int mapBottom = 0;
        int mapTop = 0 + VIEWPORT_HEIGHT;

        float cameraLeft = gamecam.position.x - cameraHalfWidth;
        float cameraRight = gamecam.position.x + cameraHalfWidth;
        float cameraBottom = gamecam.position.y - cameraHalfHeight;
        float cameraTop = gamecam.position.y + cameraHalfHeight;

        // Horizontal axis
        if (cameraLeft <= mapLeft) {
            targetX = mapLeft + cameraHalfWidth;
        } else if (cameraRight >= mapRight) {
            targetX = mapRight - cameraHalfWidth;
        }

        // Vertical axis
        if (cameraBottom <= mapBottom) {
            targetY = mapBottom + cameraHalfHeight;
        } else if (cameraTop >= mapTop) {
            targetY = mapTop - cameraHalfHeight;
        }

        // Atualiza a posição da câmera
        gamecam.position.set(targetX, targetY, 0);
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

            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            switch (keycode) {
                case Keys.UP:
                    bomberman.state = StateBomberman.IDLE_UP;
                    moving_y = 0;
                    break;
                case Keys.DOWN:
                    bomberman.state = StateBomberman.IDLE_DOWN;
                    moving_y = 0;
                    break;
                case Keys.LEFT:
                    bomberman.state = StateBomberman.IDLE_LEFT;
                    moving_x = 0;
                    break;
                case Keys.RIGHT:
                    bomberman.state = StateBomberman.IDLE_RIGHT;
                    moving_x = 0;
                    break;

            }

            if(keycode == Keys.X){
                bomberman.placeBomb();
            }

            bomberman.move(new Vector2(moving_x, moving_y));

            return true;
        }

    }

    @Override
    public void dispose() {
        super.dispose();
        box2drender.dispose();
        tiledRender.dispose();
        world.dispose();
        map.dispose();
    }

}
