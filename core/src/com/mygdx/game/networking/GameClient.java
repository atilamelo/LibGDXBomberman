package com.mygdx.game.networking;

import java.io.IOException;
import java.time.LocalDateTime;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.mygdx.game.networking.Network.RegisterPlayer;

public class GameClient {
    private Client client;

    public GameClient(String host) {
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
			client.connect(5000, host, Network.tcpPort);
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

    public static void main(String[] args) {
        Log.set(Log.LEVEL_DEBUG);
        new GameClient("localhost");
    }
}

