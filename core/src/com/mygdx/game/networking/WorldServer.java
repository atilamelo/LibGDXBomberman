package com.mygdx.game.networking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.actors.Bomberman;
import com.mygdx.game.actors.Brick;
import com.mygdx.game.actors.Enemy;
import com.mygdx.game.configs.EnemyConfig;
import com.mygdx.game.listeners.WorldListener;
import com.mygdx.game.networking.Network.RegisterPlayer;
import com.mygdx.game.systems.RandomPlacement;
import com.mygdx.game.systems.RandomPlacement.Position;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;


public class WorldServer {
    public World world;
    private GameManager gameManager;
    private Server server; 
    private List<Position> spawnAreaBricks;
    private List<Position> spawnAreaEnemies;
    private TiledMap map;

    public WorldServer() {
        this.server = new Server();
        this.gameManager = GameManager.getInstance();
        this.map = gameManager.getAssetManager().get("maps/map_teste.tmx");

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
        Network.register(server);

        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                System.out.println("Received packet at " + LocalDateTime.now());
                System.out.println("\t" +object); 
                if (object instanceof RegisterPlayer) {
                    RegisterPlayer packet = (RegisterPlayer) object;
                }
            }
        });

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
        List<RandomPlacement.Position> positions = RandomPlacement.generateRandomPositions(1,
                spawnAreaBricks);
        for (RandomPlacement.Position pos : positions) {
            WorldUtils.createBrick(pos);
        }        
    }

    private void setupEnemies() {
        
    }
}
