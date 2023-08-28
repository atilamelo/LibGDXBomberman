package com.mygdx.game.networking;

import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.networking.Network.RegisterPlayer;
import com.esotericsoftware.kryonet.Connection;
import java.time.LocalDateTime;

public class GameServer {
    Server server;

    public GameServer() {   
        server = new Server();

        Network.register(server);

        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                System.out.println("Received packet at " + LocalDateTime.now());
                System.out.println("\t" + object); 
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

    public static void main(String[] args) {
        new GameServer();
    }
}
