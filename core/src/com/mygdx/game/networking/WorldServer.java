package com.mygdx.game.networking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.listeners.WorldListener;
import com.mygdx.game.networking.Network.BrickPositions;
import com.mygdx.game.networking.Network.DisconnectedPlayer;
import com.mygdx.game.networking.Network.PlayerPosition;
import com.mygdx.game.networking.Network.RegisterPlayer;
import com.mygdx.game.networking.Network.RegisteredPlayers;
import com.mygdx.game.systems.RandomPlacement;
import com.mygdx.game.systems.RandomPlacement.Position;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import java.util.LinkedList;
import java.util.Queue;


public class WorldServer {
    public World world;
    private GameManager gameManager;
    private Server server;
    private List<RandomPlacement.Position> bricksPositions;
    private List<Position> spawnAreaBricks;
    private List<Position> spawnAreaEnemies;
    private TiledMap map;
    private List<VirtualPlayer> players;
    private static final float TIME_STEP = 1 / 300f;
    private float accumulator = 0f;

    public WorldServer() {
        this.gameManager = GameManager.getInstance();
        this.map = gameManager.getAssetManager().get("maps/map_teste.tmx");
        this.players = new ArrayList<VirtualPlayer>();

        world = WorldUtils.createWorld();
        world.setContactListener(new WorldListener());
        gameManager.setWorld(world);

        // Setups
        setupKyro();
        setupSpawn();
        setupMapCollision();
        setupBricks();
        setupEnemies();
        setupBomberman();
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

    private void setupSpawn() {
        this.spawnAreaBricks = GameManager.generateSpawnArea(new Position(1, 11), new Position(3, 9));
        this.spawnAreaEnemies = GameManager.generateSpawnArea(new Position(1, 11), new Position(8, 5));
    }

    private void setupBomberman() {
    }

    private void setupMapCollision() {
        WorldUtils.createMap(map);
    }

    private void setupBricks() {
        this.bricksPositions = RandomPlacement.generateRandomPositions(1,
                spawnAreaBricks);
        for (RandomPlacement.Position pos : this.bricksPositions) {
            WorldUtils.createBrick(pos);
        }

    }

    private void setupEnemies() {
        
    }
    
    private class NetworkingListener implements Listener{
        public void received (Connection connection, Object object) {
            accumulator += Gdx.graphics.getDeltaTime();
            
            // Update physics when receive a packet
            while (accumulator >= TIME_STEP) {
                world.step(TIME_STEP, 6, 2); 
                accumulator -= TIME_STEP;
            }

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
                server.sendToTCP(id, new BrickPositions(WorldServer.this.bricksPositions));

            } else if (object instanceof PlayerPosition) {
                PlayerPosition playerPosition = (PlayerPosition) object;
                playerPosition.id = id;

                for (VirtualPlayer player : players) {
                    if (player.id == id) {
                        player.body.setTransform(playerPosition.x, playerPosition.y, 0);
                        System.out.println("\tPlayer " + player.name + " moved to " + playerPosition.x + ", " + playerPosition.y);
                    }
                }
            }

            System.out.println("\tSending packet to all clients except id " + id);
            server.sendToAllExceptTCP(id, object);
        }

        public void disconnected(Connection connection) {
            int id = connection.getID();
            VirtualPlayer player_removed = null;
            System.out.println("Player " + id + " disconnected");
            
            for(VirtualPlayer player : players) {
                if(player.id == id) {
                    System.out.println("\tRemoving player " + player.name);
                    world.destroyBody(player.body);
                    player_removed = player;
                }
            }

            if(player_removed != null){
                DisconnectedPlayer packet = new DisconnectedPlayer(player_removed.id);
                players.remove(player_removed);
                server.sendToAllExceptTCP(id, packet);
            }

            System.out.println("\t" + "Now connected players: " + players);
        }
     
    }
}
