package com.mygdx.game.stages;

import java.io.IOException;
import java.time.LocalDateTime;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.configs.BombermanConfig;
import com.mygdx.game.configs.LevelConfig;
import com.mygdx.game.networking.Network;
import com.mygdx.game.networking.Network.RegisterPlayer;
import com.mygdx.game.screens.GameScreen;

public class MultiplayerStage extends GameStage {
    public Client client;

    public MultiplayerStage(GameScreen gameScreen, LevelConfig levelConfig, BombermanConfig bombermanConfig) {
        super(gameScreen, levelConfig, bombermanConfig);
        setupNetworking();

    }
    
    private void setupNetworking() {
        client = new Client();
        client.start();

        Network.register(client);

        client.addListener(new Listener() {
            public void connected(Connection connection) {
                System.out.println("Connected to server");
                
                RegisterPlayer registerName = new RegisterPlayer();
                registerName.name = "test";
                                
                System.out.println("Sending packet at " + LocalDateTime.now());
                System.out.println("\t" + registerName);
                client.sendTCP(registerName);
            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("Disconnected from server");
            }
        });

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
        client.sendTCP(object);
    }

    private class NetworkingListener implements Listener {

    }



}
