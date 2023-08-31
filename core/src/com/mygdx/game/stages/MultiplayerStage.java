package com.mygdx.game.stages;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.badlogic.gdx.maps.Map;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.actors.Bomberman;
import com.mygdx.game.box2d.UserData;
import com.mygdx.game.configs.BombermanConfig;
import com.mygdx.game.configs.LevelConfig;
import com.mygdx.game.networking.Network;
import com.mygdx.game.networking.Network.DisconnectedPlayer;
import com.mygdx.game.networking.Network.PlayerPosition;
import com.mygdx.game.networking.Network.RegisterPlayer;
import com.mygdx.game.networking.Network.RegisteredPlayers;
import com.mygdx.game.networking.VirtualPlayer;
import com.mygdx.game.screens.GameScreen;

public class MultiplayerStage extends GameStage {
    public Client client;
    private Queue<PlayerPosition> playerPositions;
    private List<VirtualPlayer> network_players;

    public MultiplayerStage(GameScreen gameScreen, LevelConfig levelConfig, BombermanConfig bombermanConfig) {
        super(gameScreen, levelConfig, bombermanConfig);
        this.network_players = new ArrayList<VirtualPlayer>();
        this.playerPositions = new LinkedList<PlayerPosition>();
        setupNetworking();

    }

    @Override
    public void act(float delta) {

        // Update network player positions
        while(!playerPositions.isEmpty()) {
            PlayerPosition playerPosition = playerPositions.poll();
            for(VirtualPlayer player : network_players) {
                if(player.id == playerPosition.id) {
                    player.body.setTransform(playerPosition.x, playerPosition.y, 0);
                }
            }
        } 

        super.act(delta);
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

    public void sendPackage(Object object) {
        System.out.println("Sending packet at " + LocalDateTime.now());
        System.out.println("\t" + object);
        client.sendTCP(object);
    }

    private class NetworkingListener implements Listener {
        public void connected(Connection connection) {
            System.out.println("Connected to server");
            sendPackage(new RegisterPlayer("test"));
        }

        public void received(Connection connection, Object object) {
            int id = connection.getID();
            System.out.println("Received packet at " + LocalDateTime.now());
            System.out.println("\t" + "From: " + id); 
            System.out.println("\t" + object);
            
            if(object instanceof RegisterPlayer){
                RegisterPlayer registerPlayer = (RegisterPlayer) object;
                VirtualPlayer player = new VirtualPlayer(registerPlayer.id, registerPlayer.name);
                player.actor = new Bomberman(player.body, MultiplayerStage.this, true);
                network_players.add(player);
                MultiplayerStage.this.addActor(player.actor);          

            } else if(object instanceof PlayerPosition) {
                PlayerPosition playerPosition = (PlayerPosition) object;
                playerPositions.add(playerPosition);

            } else if(object instanceof RegisteredPlayers){
                RegisteredPlayers registeredPlayers = (RegisteredPlayers) object;
                for (int i = 0; i < registeredPlayers.amountOfPlayers; i++) {
                    VirtualPlayer player = new VirtualPlayer(registeredPlayers.ids[i], registeredPlayers.names[i]);
                    player.actor = new Bomberman(player.body, MultiplayerStage.this, true);
                    player.body.setTransform(registeredPlayers.xPosition[i], registeredPlayers.yPosition[i], 0);
                    network_players.add(player);
                    MultiplayerStage.this.addActor(player.actor);          
                }

            } else if(object instanceof DisconnectedPlayer){
                DisconnectedPlayer disconnectedPlayer = (DisconnectedPlayer) object;
                VirtualPlayer player_removed = null;

                for(VirtualPlayer player : network_players) {
                    if(player.id == disconnectedPlayer.id) {
                        System.out.println("\tRemoving player " + player.name);
                        UserData userdata = (UserData) player.body.getUserData();
                        userdata.isFlaggedForDelete = true;
                        player.actor.remove();
                        player_removed = player;
                    }
                }
                
                if(player_removed != null){
                    network_players.remove(player_removed);
                }
    
                System.out.println("\t" + "Now connected players: " + network_players);            
            }
        }

        

        @Override
        public void disconnected(Connection connection) {
            System.out.println("Disconnected from server");
        }
    }



}

