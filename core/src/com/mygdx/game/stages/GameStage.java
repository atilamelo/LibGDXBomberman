package com.mygdx.game.stages;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.actors.Bomberman;
import com.mygdx.game.actors.Brick;
import com.mygdx.game.actors.enemies.Onil;
import com.mygdx.game.box2d.UserData;
import com.mygdx.game.enums.StateBomberman;
import com.mygdx.game.systems.RandomPlacement;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

// Estrutura básica baseado no Runner feito por William Moura 
// http://williammora.com/a-running-game-with-libgdx-part-1
public class GameStage extends Stage {

    // This will be our viewport measurements while working with the debug renderer
    private static final int VIEWPORT_WIDTH = GameManager.MAP_WIDTH;
    private static final int VIEWPORT_HEIGHT = GameManager.MAP_HEIGHT;

    private GameScreen gameScreen;
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
    public Group elements;
    public Group background;

    private int amountOfBricks;
    private int amountOfBalloms;
    private int amountOfOnils;

    public GameStage(GameScreen gameScreen, LevelConfiguration levelConfiguration) {
        this.gameScreen = gameScreen;
        this.gameManager = GameManager.getInstance();

        this.amountOfBricks = levelConfiguration.amountOfBricks;
        this.amountOfBalloms = levelConfiguration.amountOfBalloms;
        this.amountOfOnils = levelConfiguration.amountOfOnils;
        gameManager.enemiesLeft = amountOfBalloms + amountOfOnils;

        // Tiled Maps
        map = gameManager.getAssetManager().get("maps/map_teste.tmx");
        tiledRender = new OrthogonalTiledMapRenderer(map, 1 / GameManager.PPM);

        // Layers
        elements = new Group();
        background = new Group();
        addActor(background);
        addActor(elements);

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
        setupMapCollision();
        setupBricks();
        setupEnemies();
    }

    private void setupBomberman() {
        bomberman = new Bomberman(WorldUtils.createBomberman(), this);
        elements.addActor(bomberman);
    }

    private void setupMapCollision() {
        WorldUtils.createMap(map);

    }

    private void setupBricks() {
        Random random = new Random();

        List<RandomPlacement.Position> positions = RandomPlacement.generateRandomPositions(amountOfBricks,
                GameManager.generateSpawnArea());

        // Get random location for the door
        RandomPlacement.Position doorPosition = positions.get(random.nextInt(positions.size()));
        

        for (RandomPlacement.Position pos : positions) {
            Body bodyBrick = WorldUtils.createBrick(pos);
            if (pos == doorPosition)
                background.addActor(new Brick(bodyBrick, true));
            else
                background.addActor(new Brick(bodyBrick));
        }
    }

    private void setupEnemies() {
        List<RandomPlacement.Position> positions;

        // /* Balloms */
        // positions = RandomPlacement.generateRandomPositions(amountOfBalloms, GameManager.generateSpawnArea());
        // for (RandomPlacement.Position pos : positions) {
        //     Body bodyEnemy = WorldUtils.createBallom(pos);
        //     elements.addActor(new Ballom(bodyEnemy));
        // }

        /* Onils */
        positions = RandomPlacement.generateRandomPositions(amountOfOnils, GameManager.generateSpawnArea());
        for (RandomPlacement.Position pos : positions) {
            Body bodyEnemy = WorldUtils.createEnemy(pos);
            elements.addActor(new Onil(bodyEnemy));
        }

    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Fixed timestep
        accumulator += delta;

        while (accumulator >= delta) {
            world.step(TIME_STEP, 6, 2);
            sweepDeadBodies();
            accumulator -= TIME_STEP;
        }

        // TODO: Implement interpolation

    }

    /*
     * Adaptado de:
     * https://gamedev.stackexchange.com/questions/27113/how-do-i-destroy-a-box2d-
     * body-on-contact-without-getting-an-islocked-assertion-e
     */
    public void sweepDeadBodies() {
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);

        if (bodies.size == 0)
            return;

        for (Iterator<Body> iter = bodies.iterator(); iter.hasNext();) {
            Body body = iter.next();
            if (body != null) {
                UserData data = (UserData) body.getUserData();
                if (data != null && data.isFlaggedForDelete) {
                    world.destroyBody(body);
                    body.setUserData(null);
                    body = null;
                }
            }
        }
    }

    public Bomberman getBomberman() {
        return bomberman;
    }

    @Override
    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        handleCamera();

        gameport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.setViewport(gameport);

        tiledRender.setView(gamecam);
        tiledRender.render();
        box2drender.render(world, gamecam.combined);

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
                    bomberman.setState(StateBomberman.IDLE_UP);
                    moving_y = 0;
                    break;
                case Keys.DOWN:
                    bomberman.setState(StateBomberman.IDLE_DOWN);
                    moving_y = 0;
                    break;
                case Keys.LEFT:
                    bomberman.setState(StateBomberman.IDLE_LEFT);
                    moving_x = 0;
                    break;
                case Keys.RIGHT:
                    bomberman.setState(StateBomberman.IDLE_RIGHT);
                    moving_x = 0;
                    break;
                case Keys.SPACE:
                    bomberman.placeBomb();
                    break;
                case Keys.Z:
                    bomberman.explodeAllBombs();
                    break;

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
