package com.mygdx.game.stages;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.mygdx.game.actors.Enemy;
import com.mygdx.game.box2d.UserData;
import com.mygdx.game.configs.BombermanConfig;
import com.mygdx.game.configs.EnemyConfig;
import com.mygdx.game.configs.LevelConfig;
import com.mygdx.game.listeners.WorldListener;
import com.mygdx.game.networking.GameClient;
import com.mygdx.game.systems.RandomPlacement;
import com.mygdx.game.systems.RandomPlacement.Position;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;
import com.mygdx.game.networking.Network;

// Estrutura básica baseado no Runner feito por William Moura 
// http://williammora.com/a-running-game-with-libgdx-part-1
public class GameStage extends Stage {

    // This will be our viewport measurements while working with the debug renderer
    private static final int VIEWPORT_WIDTH = GameManager.MAP_WIDTH;
    private static final int VIEWPORT_HEIGHT = GameManager.MAP_HEIGHT;

    private GameScreen gameScreen;
    private GameManager gameManager;

    private World world;
    private GameClient client;
    private Bomberman bomberman;

    private InputProcessor inputProcessor;

    private final float TIME_STEP = 1 / 300f;
    private float acumullator = 0f;
    private float stateTime = 0f;

    private OrthographicCamera gamecam;
    private FitViewport gameport;
    private Box2DDebugRenderer box2drender;
    private OrthogonalTiledMapRenderer tiledRender;
    private TiledMap map;
    public Group elements;
    public Group background;
    private LevelConfig config;
    private BombermanConfig bombermanConfig = null;
    private List<Position> spawnAreaBricks;
    private List<Position> spawnAreaEnemies;
    private Hud hud;
    private boolean flagIsGameFinished = false;
    private boolean flagSpawnedTimeUp = false;
    private boolean isSoundClearEnemiesPlayed = false;

    public GameStage(GameScreen gameScreen, LevelConfig levelConfig, BombermanConfig bombermanConfig) {
        this.gameScreen = gameScreen;
        this.gameManager = GameManager.getInstance();
        this.config = levelConfig;
        this.bombermanConfig = bombermanConfig;

        gameManager.enemiesLeft = levelConfig.getTotalOfEnemies();

        // Tiled Maps
        map = gameManager.getAssetManager().get("maps/map_teste.tmx");
        tiledRender = new OrthogonalTiledMapRenderer(map, 1 / GameManager.PPM);
        hud = new Hud((SpriteBatch) getBatch(), bombermanConfig.lifes);
        // Layers
        elements = new Group();
        background = new Group();
        addActor(background);
        addActor(elements);

        // Setups
        setupWorld();
        setupViewPort();
        setupNetworking();

        // Box2d
        box2drender = new Box2DDebugRenderer();

        inputProcessor = new InputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);

        // Music of stage
        gameManager.playMusic(GameManager.MUSIC_MAIN, true);

    }
    
    private void setupWorld() {
        world = WorldUtils.createWorld();
        world.setContactListener(new WorldListener());
        gameManager.setWorld(world);

        setupSpawn();
        setupBomberman();
        setupMapCollision();
        setupBricks();
        setupEnemies();
    }

    private void setupViewPort() {
        gamecam = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        gamecam.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        gamecam.zoom -= GameManager.GAME_ZOOM;
        gamecam.update();

        gameport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, gamecam);
        this.setViewport(gameport);
    }

    private void setupNetworking() {
        client = new GameClient("localhost");
    }

    private void setupSpawn() {
        this.spawnAreaBricks = GameManager.generateSpawnArea(new Position(1, 11), new Position(3, 9));
        this.spawnAreaEnemies = GameManager.generateSpawnArea(new Position(1, 11), new Position(8, 5));
    }

    private void setupBomberman() {
        if (bombermanConfig == null) {
            bomberman = new Bomberman(WorldUtils.createBomberman(), this);
        } else {
            bomberman = new Bomberman(WorldUtils.createBomberman(), this, bombermanConfig);            
        }
        elements.addActor(bomberman);
    }

    private void setupMapCollision() {
        WorldUtils.createMap(map);

    }

    private void setupBricks() {
        Random random = new Random();

        List<RandomPlacement.Position> positions = RandomPlacement.generateRandomPositions(config.amountOfBricks,
                spawnAreaBricks);

        // Get random location for the door and power up
        RandomPlacement.Position doorPosition = positions.get(random.nextInt(positions.size()));
        RandomPlacement.Position powerUpPosition = positions.get(random.nextInt(positions.size()));

        for (RandomPlacement.Position pos : positions) {
            Body bodyBrick = WorldUtils.createBrick(pos);
            Brick newBrick = new Brick(bodyBrick);

            if (pos == doorPosition) {
                newBrick.setDoor(true);
            }

            if (pos == powerUpPosition) {
                newBrick.setPowerUp(config.powerUpType);
            }

            background.addActor(newBrick);

        }

    }

    private void setupEnemies() {
        List<RandomPlacement.Position> positions;

        /* Balloms */
        positions = RandomPlacement.generateRandomPositions(config.amountOfBalloms, spawnAreaEnemies);
        for (RandomPlacement.Position pos : positions) {
            Body bodyEnemy = WorldUtils.createEnemy(pos, EnemyConfig.ballonConfig);
            elements.addActor(new Enemy(bodyEnemy, EnemyConfig.ballonConfig));
        }

        /* Onils */
        positions = RandomPlacement.generateRandomPositions(config.amountOfOnils, spawnAreaEnemies);
        for (RandomPlacement.Position pos : positions) {
            Body bodyEnemy = WorldUtils.createEnemy(pos, EnemyConfig.onilConfig);
            elements.addActor(new Enemy(bodyEnemy, EnemyConfig.onilConfig));
        }

        /* Dolls */
        positions = RandomPlacement.generateRandomPositions(config.amountOfDolls, spawnAreaEnemies);
        for (RandomPlacement.Position pos : positions) {
            Body bodyEnemy = WorldUtils.createEnemy(pos, EnemyConfig.dollConfig);
            elements.addActor(new Enemy(bodyEnemy, EnemyConfig.dollConfig));
        }

        /* Minvos */
        positions = RandomPlacement.generateRandomPositions(config.amountOfMinvos, spawnAreaEnemies);
        for (RandomPlacement.Position pos : positions) {
            Body bodyEnemy = WorldUtils.createEnemy(pos, EnemyConfig.minvoConfig);
            elements.addActor(new Enemy(bodyEnemy, EnemyConfig.minvoConfig));
        }

        /* Kondorias */
        positions = RandomPlacement.generateRandomPositions(config.amountOfKondorias, spawnAreaEnemies);
        for (RandomPlacement.Position pos : positions) {
            Body bodyEnemy = WorldUtils.createEnemy(pos, EnemyConfig.kondoriaConfig);
            elements.addActor(new Enemy(bodyEnemy, EnemyConfig.kondoriaConfig));
        }

        /* Ovapis */
        positions = RandomPlacement.generateRandomPositions(config.amountOfOvapis, spawnAreaEnemies);
        for (RandomPlacement.Position pos : positions) {
            Body bodyEnemy = WorldUtils.createEnemy(pos, EnemyConfig.ovapiConfig);
            elements.addActor(new Enemy(bodyEnemy, EnemyConfig.ovapiConfig));
        }

        /* Pass */
        positions = RandomPlacement.generateRandomPositions(config.amountOfPass, spawnAreaEnemies);
        for (RandomPlacement.Position pos : positions) {
            Body bodyEnemy = WorldUtils.createEnemy(pos, EnemyConfig.passConfig);
            elements.addActor(new Enemy(bodyEnemy, EnemyConfig.passConfig));
        }

        /* Pontan */
        positions = RandomPlacement.generateRandomPositions(config.amountOfPontan, spawnAreaEnemies);
        for (RandomPlacement.Position pos : positions) {
            Body bodyEnemy = WorldUtils.createEnemy(pos, EnemyConfig.pontanConfig);
            elements.addActor(new Enemy(bodyEnemy, EnemyConfig.pontanConfig));
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Stop song if player dies
        if (bomberman.isDying() && gameManager.musicIsPlaying()) {
            gameManager.stopMusic();
            gameManager.playEffect(GameManager.SOUND_MISS);
        }

        if (gameManager.enemiesLeft == 0 && !isSoundClearEnemiesPlayed) {
            gameManager.playEffect(GameManager.SOUND_CLEAR_ENEMIES);
            isSoundClearEnemiesPlayed = true;
        }

        if (flagIsGameFinished) {
            if (stateTime > 3f) {
                BombermanConfig bombermanConfig = bomberman.getConfig(false);
                gameScreen.nextLevel(bombermanConfig);
            }
        }
        
        if(hud.isTimeUp() && !flagSpawnedTimeUp){
            spawnPontan();
            flagSpawnedTimeUp = true;
        }

        hud.setScore(gameManager.getScore());

        // Fixed timestep
        acumullator += delta;
        stateTime += delta;
        hud.update(delta);

        while (acumullator >= delta) {
            world.step(TIME_STEP, 6, 2);
            sweepDeadBodies();
            acumullator -= TIME_STEP;
        }

        // TODO: Implement interpolation

    }

    public void spawnPontan(){
        List<Position> notSpawnAreaPontans = GameManager.generateSpawnArea(bomberman.getPosition(), 3);
        List<RandomPlacement.Position> valid_positions = RandomPlacement.generateRandomPositions(8, notSpawnAreaPontans);
        
        for (Position pos : valid_positions) {
            Body bodyPonton = WorldUtils.createEnemy(pos, EnemyConfig.pontanConfig);
            Enemy pontan = new Enemy(bodyPonton, EnemyConfig.pontanConfig);
            elements.addActor(pontan);
            gameManager.enemiesLeft++;
        }
    }

    public void nextLevel() {
        if (!flagIsGameFinished) {
            gameManager.stopMusic();
            gameManager.playEffect(GameManager.SOUND_STAGE_CLEAR);
            bomberman.move(new Vector2(0, 0)); // Stop bomberman moviments
            bomberman.setState(Bomberman.State.IDLE_DOWN);
            stateTime = 0f;
            flagIsGameFinished = true;
        }
    }

    public void restartLevel() {
        BombermanConfig bombermanConfig = bomberman.getConfig(true);
        if (bombermanConfig.lifes > 0) {
            gameScreen.restartLevel(bombermanConfig);
        } else {
            gameScreen.gameOver();
        }
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
        Gdx.gl.glClearColor(0, 0, 0, 0);

        handleCamera();

        gameport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.setViewport(gameport);

        tiledRender.setView(gamecam);
        tiledRender.render();
        box2drender.render(world, gamecam.combined);

        super.draw();

        getBatch().setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
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
            if (!flagIsGameFinished) {
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

                Network.PlayerPosition playerPosition = new Network.PlayerPosition(bomberman.getX(), bomberman.getY());
                client.sendPackage(playerPosition);
                    
            }
            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            if (!flagIsGameFinished) {
                switch (keycode) {
                    case Keys.UP:
                        bomberman.setState(Bomberman.State.IDLE_UP);
                        moving_y = 0;
                        break;
                    case Keys.DOWN:
                        bomberman.setState(Bomberman.State.IDLE_DOWN);
                        moving_y = 0;
                        break;
                    case Keys.LEFT:
                        bomberman.setState(Bomberman.State.IDLE_LEFT);
                        moving_x = 0;
                        break;
                    case Keys.RIGHT:
                        bomberman.setState(Bomberman.State.IDLE_RIGHT);
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
            }
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
