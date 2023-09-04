package com.mygdx.game.stages;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.actors.Bomb;
import com.mygdx.game.actors.Brick;
import com.mygdx.game.box2d.UserData;
import com.mygdx.game.configs.BombermanConfig;
import com.mygdx.game.configs.LevelConfig;
import com.mygdx.game.enums.UserDataType;
import com.mygdx.game.listeners.ClientWorldListener;
import com.mygdx.game.networking.Network;
import com.mygdx.game.networking.VirtualEnemy;
import com.mygdx.game.networking.Network.BombermanDie;
import com.mygdx.game.networking.Network.BrickPositions;
import com.mygdx.game.networking.Network.DisconnectedPlayer;
import com.mygdx.game.networking.Network.EnemyDie;
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
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

public class MultiplayerStage extends GameStage {
    public Client client;
    private int myId;
    private Queue<Object> notProcessedPackets;
    private List<VirtualPlayer> network_players;
    private List<VirtualEnemy> network_enemies;
    private List<RandomPlacement.Position> bricksPositions;
    private boolean bricksLoaded = false;

    public MultiplayerStage(GameScreen gameScreen, LevelConfig levelConfig, BombermanConfig bombermanConfig) {
        super(gameScreen, levelConfig, bombermanConfig);
        this.network_players = new ArrayList<VirtualPlayer>();
        this.network_enemies = new ArrayList<VirtualEnemy>();
        this.notProcessedPackets = new LinkedList<Object>();
        setupNetworking();

    }

    @Override
    public void act(float delta) {
        super.superAct(delta);

        // Bricks position if not loaded
        if (!bricksLoaded) {
            setupBricks();
        }

        while (!notProcessedPackets.isEmpty()) {
            Packet packet = (Packet) notProcessedPackets.poll();

            if (packet instanceof RegisterPlayer) {

                RegisterPlayer registerPlayer = (RegisterPlayer) packet;
                VirtualPlayer player = new VirtualPlayer(registerPlayer.id, registerPlayer.name, MultiplayerStage.this);
                network_players.add(player);

            } else if (packet instanceof PlayerPosition) {

                PlayerPosition playerPosition = (PlayerPosition) packet;
                for (VirtualPlayer player : network_players) {
                    if (player.id == packet.id) {
                        player.body.setTransform(playerPosition.x, playerPosition.y, 0);
                    }
                }

            } else if (packet instanceof EnemyPosition){       

                EnemyPosition enemyPosition = (EnemyPosition) packet;
                for (VirtualEnemy enemy : network_enemies) {
                    if (enemy.id.equals(enemyPosition.id)) {
                        enemy.body.setTransform(enemyPosition.x, enemyPosition.y, 0);
                    }
                }

            } else if (packet instanceof EnemyDie) {
                    
                EnemyDie enemyDie = (EnemyDie) packet;
                for (VirtualEnemy enemy : network_enemies) {
                    if (enemy.id.equals(enemyDie.id)) {
                        enemy.actor.takeDamage(1);
                    }
                }

            } else if (packet instanceof RegisteredPlayers) {

                RegisteredPlayers registeredPlayers = (RegisteredPlayers) packet;
                for (int i = 0; i < registeredPlayers.amountOfPlayers; i++) {
                    if(registeredPlayers.isAlive[i]) {
                        VirtualPlayer player = new VirtualPlayer(registeredPlayers.ids[i], registeredPlayers.names[i],
                                MultiplayerStage.this);
                        player.body.setTransform(registeredPlayers.positions[i].getX(), registeredPlayers.positions[i].getY(), 0);
                        network_players.add(player);
                    }
                }

            } else if (packet instanceof RegisteredEnemies) {

                RegisteredEnemies registeredEnemies = (RegisteredEnemies) packet;
                for (int i = 0; i < registeredEnemies.amountOfEnemies; i++) {
                    VirtualEnemy enemy = new VirtualEnemy(registeredEnemies.ids[i], MultiplayerStage.this,
                            registeredEnemies.positions[i], registeredEnemies.configs[i]);
                    network_enemies.add(enemy);
                }

            } else if (packet instanceof DisconnectedPlayer) {

                DisconnectedPlayer disconnectedPlayer = (DisconnectedPlayer) packet;
                VirtualPlayer player_removed = null;

                for (VirtualPlayer player : network_players) {
                    if (player.id == disconnectedPlayer.id) {
                        System.out.println("\tRemoving player " + player.name);
                        UserData userdata = (UserData) player.body.getUserData();
                        userdata.isFlaggedForDelete = true;
                        player.actor.remove();
                        player_removed = player;
                    }
                }

                if (player_removed != null) {
                    network_players.remove(player_removed);
                }

                System.out.println("\t" + "Now connected players: " + network_players);

            } else if (packet instanceof BrickPositions) {

                BrickPositions packetBrickPosition = (BrickPositions) packet;
                MultiplayerStage.this.bricksPositions = new ArrayList<RandomPlacement.Position>();
                for (int i = 0; i < packetBrickPosition.amountOfBricks; i++) {
                    RandomPlacement.Position position = new RandomPlacement.Position(packetBrickPosition.x[i],
                            packetBrickPosition.y[i]);
                    MultiplayerStage.this.bricksPositions.add(position);
                }

            } else if (packet instanceof PlaceBomb) {

                PlaceBomb placeBomb = (PlaceBomb) packet;
                Bomb bomb = new Bomb(MultiplayerStage.this, placeBomb.position, placeBomb.range);
                active_bombs.put(placeBomb.position, bomb);
                addActor(bomb);

            } else if (packet instanceof BombermanDie) {
                BombermanDie bombermanDie = (BombermanDie) packet;

                if (bombermanDie.id == this.myId) {
                    System.out.println("I died");
                    bomberman.die(UserDataType.EXPLOSION);
                } else {
                    for (VirtualPlayer player : network_players) {
                        if (player.id == bombermanDie.id) {
                            player.actor.die(UserDataType.EXPLOSION);
                        }
                    }
                }
            }
        }

        // Stop song if player dies
        if (bomberman.isDying() && gameManager.musicIsPlaying()) {
            gameManager.stopMusic();
            gameManager.playEffect(GameManager.SOUND_MISS);
        }

        if (gameManager.enemiesLeft == 0 && !isSoundClearEnemiesPlayed) {
            gameManager.playEffect(GameManager.SOUND_CLEAR_ENEMIES);
            isSoundClearEnemiesPlayed = true;
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
    }

    private void setupNetworking() {
        client = new Client();
        client.start();

        Network.register(client);

        client.addListener(new NetworkingListener());

        try {
            client.connect(5000, "localhost", Network.tcpPort);
            new Thread(client).start();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error while connecting to server");
            System.exit(1);
        }
    }

    @Override
    protected void setupWorld() {
        super.setupWorld();
        world.setContactListener(new ClientWorldListener());
    }

    @Override
    protected void setupBricks() {
        if (bricksPositions != null) {
            for (RandomPlacement.Position pos : bricksPositions) {
                Body bodyBrick = WorldUtils.createBrick(pos);
                Brick newBrick = new Brick(bodyBrick);
                background.addActor(newBrick);
                bricksLoaded = true;
            }
        }
    }

    @Override
    public void nextLevel() {

    }

    @Override
    public void setupEnemies() {

    }


    public void sendPackage(Object object) {
        System.out.println("Sending packet at " + LocalDateTime.now());
        System.out.println("\t" + object);
        client.sendTCP(object);
    }

    private class NetworkingListener implements Listener {
        public void connected(Connection connection) {
            MultiplayerStage.this.myId = connection.getID();
            System.out.println("Connected to server, ID: " + MultiplayerStage.this.myId);
            sendPackage(new RegisterPlayer("test"));
        }

        public void received(Connection connection, Object object) {
            System.out.println("Received packet at " + LocalDateTime.now());
            System.out.println("\t" + "From server");
            System.out.println("\t" + object);

            if (object instanceof Packet) {
                Packet packet = (Packet) object;
                notProcessedPackets.add(packet);
            }

        }

        @Override
        public void disconnected(Connection connection) {
            System.out.println("Disconnected from server");
            Gdx.app.exit();
            System.exit(1);
        }
    }

}
