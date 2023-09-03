package com.mygdx.game.stages;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.actors.Bomb;
import com.mygdx.game.actors.Brick;
import com.mygdx.game.configs.BombermanConfig;
import com.mygdx.game.configs.EnemyConfig;
import com.mygdx.game.configs.LevelConfig;
import com.mygdx.game.listeners.ServerWorldListener;
import com.mygdx.game.networking.Network;
import com.mygdx.game.networking.VirtualEnemy;
import com.mygdx.game.networking.Network.BrickPositions;
import com.mygdx.game.networking.Network.DisconnectedPlayer;
import com.mygdx.game.networking.Network.EnemyPosition;
import com.mygdx.game.networking.Network.Packet;
import com.mygdx.game.networking.Network.PlaceBomb;
import com.mygdx.game.networking.Network.PlayerPosition;
import com.mygdx.game.networking.Network.RegisterPlayer;
import com.mygdx.game.networking.Network.RegisteredEnemies;
import com.mygdx.game.networking.Network.RegisteredPlayers;
import com.mygdx.game.networking.VirtualPlayer;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.systems.RandomPlacement;
import com.mygdx.game.utils.WorldUtils;

public class ServerStage extends GameStage {
    public Server server;
    public List<VirtualPlayer> players;
    public List<VirtualEnemy> enemies;
    public List<Brick> bricks;
    private Queue<Object> notProcessedPackets;

    public ServerStage(GameScreen gameScreen, LevelConfig levelConfig, BombermanConfig bombermanConfig) {
        super(gameScreen, levelConfig, bombermanConfig);

        this.players = new ArrayList<VirtualPlayer>();
        this.notProcessedPackets = new LinkedList<Object>();
        setupKyro();
                
    }

    @Override
    protected void setupWorld(){
        super.setupWorld();
        world.setContactListener(new ServerWorldListener(this));
    }

    private void setupKyro() {
        this.server = new Server();
        Network.register(server);

        server.addListener(new NetworkingListener());

        try {
            server.bind(Network.tcpPort);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void setupViewPort() {
        gamecam = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        gamecam.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        gamecam.update();

        gameport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, gamecam);
        this.setViewport(gameport);
    }

    @Override
    protected void setupBomberman() {
    }

    @Override
    protected void setupBricks() {
        List<RandomPlacement.Position> bricksPositions = RandomPlacement.generateRandomPositions(1, spawnAreaBricks);
        // List<RandomPlacement.Position> bricksPositions = RandomPlacement.generateRandomPositions(config.amountOfBricks, spawnAreaBricks);

        for (RandomPlacement.Position pos : bricksPositions) {
            Brick brick = new Brick(WorldUtils.createBrick(pos));
            addActor(brick);
            active_bricks.put(pos, brick);
        }
    }

    @Override
    protected void setupEnemies() {
        this.enemies = new ArrayList<VirtualEnemy>();
        setupEnemyType(config.amountOfBalloms, EnemyConfig.ballonConfig);
        setupEnemyType(config.amountOfOnils, EnemyConfig.onilConfig);
        setupEnemyType(config.amountOfDolls, EnemyConfig.dollConfig);
        setupEnemyType(config.amountOfMinvos, EnemyConfig.minvoConfig);
        setupEnemyType(config.amountOfKondorias, EnemyConfig.kondoriaConfig);
        setupEnemyType(config.amountOfOvapis, EnemyConfig.ovapiConfig);
        setupEnemyType(config.amountOfPass, EnemyConfig.passConfig);
        setupEnemyType(config.amountOfPontan, EnemyConfig.pontanConfig);
    }

    @Override
    protected void setupEnemyType(int amount, EnemyConfig config) {
        List<RandomPlacement.Position> enemiesPositions = RandomPlacement.generateRandomPositions(amount, spawnAreaEnemies);
        for (RandomPlacement.Position pos : enemiesPositions) {
            VirtualEnemy enemy = new VirtualEnemy(this, pos, config);
            enemies.add(enemy);
        }
    }

    @Override
    public void act(float delta) {
        super.superAct(delta);

        while (!notProcessedPackets.isEmpty()) {
            Packet packet = (Packet) notProcessedPackets.poll();

            if (packet instanceof RegisterPlayer) {
                RegisterPlayer registerPlayer = (RegisterPlayer) packet;
                VirtualPlayer new_player = new VirtualPlayer(registerPlayer.id, registerPlayer.name, ServerStage.this);
                players.add(new_player);
                System.out.println("\tPlayer " + registerPlayer.name + " registered - Id: " + registerPlayer.id);

            } else if (packet instanceof PlayerPosition) {

                PlayerPosition playerPosition = (PlayerPosition) packet;

                for (VirtualPlayer player : players) {
                    if (player.id == packet.id) {
                        player.body.setTransform(playerPosition.x, playerPosition.y, 0);
                        System.out.println(
                                "\tPlayer " + player.name + " moved to " + playerPosition.x + ", " + playerPosition.y);
                    }
                }

            } else if (packet instanceof PlaceBomb) {

                PlaceBomb placeBomb = (PlaceBomb) packet;

                for (VirtualPlayer player : players) {
                    if (player.id == packet.id) {
                        System.out.println("\tPlayer " + player.name + " placed a bomb at " + placeBomb.position
                                + " with range " + placeBomb.range);
                        Bomb bomb = new Bomb(ServerStage.this, placeBomb.position, placeBomb.range);
                        active_bombs.put(placeBomb.position, bomb);
                        addActor(bomb);
                    }
                }

            }

        }

        // Fixed timestep
        acumullator += delta;
        stateTime += delta;

        while (acumullator >= delta) {
            world.step(TIME_STEP, 6, 2);
            sweepDeadBodies();
            acumullator -= TIME_STEP;
        }
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 0);

        gameport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.setViewport(gameport);

        box2drender.render(world, gamecam.combined);

    }

    @Override
    protected void setupInputProcessor() {
    }

    private class NetworkingListener implements Listener {
        public void received(Connection connection, Object object) {

            int id = connection.getID();

            if (object instanceof Packet) {
                Packet packet = (Packet) object;
                packet.id = id;
                notProcessedPackets.add(object);

                System.out.println("Received packet at " + LocalDateTime.now());
                System.out.println("\t" + "From: " + id);
                System.out.println("\t" + object);
    
                System.out.println("\tSending packet to all clients except id " + id);
                server.sendToAllExceptTCP(id, object);
            }



            if (object instanceof RegisterPlayer) {

                // Send to player informatiosn about the actual state of the game
                List<VirtualPlayer> players_list = new ArrayList<VirtualPlayer>(players);
                players_list.removeIf(player -> player.id == id);

                server.sendToTCP(id, new RegisteredPlayers(players_list));
                server.sendToTCP(id, new RegisteredEnemies(enemies));
                server.sendToTCP(id, new BrickPositions(ServerStage.this.active_bricks));

            } 

        }

        public void disconnected(Connection connection) {
            int id = connection.getID();
            VirtualPlayer player_removed = null;
            System.out.println("Player " + id + " disconnected");

            for (VirtualPlayer player : players) {
                if (player.id == id) {
                    System.out.println("\tRemoving player " + player.name);
                    world.destroyBody(player.body);
                    player_removed = player;
                }
            }

            if (player_removed != null) {
                DisconnectedPlayer packet = new DisconnectedPlayer(player_removed.id);
                players.remove(player_removed);
                server.sendToAllExceptTCP(id, packet);
            }

            System.out.println("\t" + "Now connected players: " + players);
        }

    }

    public void sendEnemyPosition(UUID multiplayer_id, float x, float y) {
        EnemyPosition packet = new EnemyPosition();
        packet.id = multiplayer_id;
        packet.x = x;
        packet.y = y;
        server.sendToAllTCP(packet);
    }

}
