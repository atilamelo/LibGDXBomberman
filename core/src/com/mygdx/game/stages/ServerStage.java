package com.mygdx.game.stages;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.configs.BombermanConfig;
import com.mygdx.game.configs.LevelConfig;
import com.mygdx.game.networking.Network;
import com.mygdx.game.networking.Network.BrickPositions;
import com.mygdx.game.networking.Network.DisconnectedPlayer;
import com.mygdx.game.networking.Network.PlaceBomb;
import com.mygdx.game.networking.Network.PlayerPosition;
import com.mygdx.game.networking.Network.RegisterPlayer;
import com.mygdx.game.networking.Network.RegisteredPlayers;
import com.mygdx.game.networking.VirtualPlayer;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.systems.RandomPlacement;
import com.mygdx.game.utils.WorldUtils;

public class ServerStage extends GameStage {
    private Server server;
    private List<VirtualPlayer> players;
    private List<RandomPlacement.Position> bricksPositions;

    public ServerStage(GameScreen gameScreen, LevelConfig levelConfig, BombermanConfig bombermanConfig) {
        super(gameScreen, levelConfig, bombermanConfig);

        this.players = new ArrayList<VirtualPlayer>();
        setupKyro();

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
        this.bricksPositions = RandomPlacement.generateRandomPositions(1, spawnAreaBricks);
        for (RandomPlacement.Position pos : this.bricksPositions) {
            WorldUtils.createBrick(pos);
        }
    }

    @Override
    protected void setupEnemies() {
        
    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 0);

        gameport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.setViewport(gameport);

        box2drender.render(world, gamecam.combined);

        // super.draw();
    }

    @Override
    protected void setupInputProcessor() {
    }

    private class NetworkingListener implements Listener {
        public void received(Connection connection, Object object) {

            int id = connection.getID();
            System.out.println("Received packet at " + LocalDateTime.now());
            System.out.println("\t" + "From: " + id);
            System.out.println("\t" + object);

            if (object instanceof RegisterPlayer) {
                // Send to the new client the list of registered players
                server.sendToTCP(id, new RegisteredPlayers(players));

                RegisterPlayer packet = (RegisterPlayer) object;
                players.add(new VirtualPlayer(id, packet.name));

                // Add id at the packet before send to all clients except the sender
                packet.id = id;
                System.out.println("\tPlayer " + packet.name + " registered - Id: " + id);
                server.sendToTCP(id, new BrickPositions(ServerStage.this.bricksPositions));

            } else if (object instanceof PlayerPosition) {
                PlayerPosition playerPosition = (PlayerPosition) object;
                playerPosition.id = id;

                for (VirtualPlayer player : players) {
                    if (player.id == id) {
                        player.body.setTransform(playerPosition.x, playerPosition.y, 0);
                        System.out.println(
                                "\tPlayer " + player.name + " moved to " + playerPosition.x + ", " + playerPosition.y);
                    }
                }

            } else if (object instanceof PlaceBomb) {
                // PlaceBomb placeBomb = (PlaceBomb) object;

                // for (VirtualPlayer player : players) {
                // if (player.id == id) {
                // System.out.println("\tPlayer " + player.name + " placed a bomb at " +
                // placeBomb.x + ", " + placeBomb.y);
                // }
                // }

            }

            System.out.println("\tSending packet to all clients except id " + id);
            server.sendToAllExceptTCP(id, object);
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

}
