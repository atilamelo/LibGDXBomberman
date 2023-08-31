package com.mygdx.game.networking;

import java.util.Arrays;
import java.util.List;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
    static public final int tcpPort = 54555;
    static public final int udpPort = 54777;

    static public void register(EndPoint endpoint) {
        Kryo kryo = endpoint.getKryo();
        kryo.register(RegisterPlayer.class);
        kryo.register(RegisteredPlayers.class);
        kryo.register(PlayerPosition.class);
        kryo.register(DisconnectedPlayer.class);
        kryo.register(PlaceBomb.class);
        kryo.register(int[].class);
        kryo.register(float[].class);
        kryo.register(String[].class);
    }

    public static class RegisterPlayer {
        public String name;
        public int id; 

        public RegisterPlayer() {}

        public RegisterPlayer(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "RegisterPlayer [name=" + name + ", id=" + id + "]";
        }
    }

    public static class RegisteredPlayers {
        public String[] names;
        public int[] ids;
        public float[] xPosition;
        public float[] yPosition;
        public int amountOfPlayers;

        public RegisteredPlayers() {}

        public RegisteredPlayers(List<VirtualPlayer> players) {
            this.amountOfPlayers = players.size();
            this.names = new String[amountOfPlayers];
            this.ids = new int[amountOfPlayers];
            this.xPosition = new float[amountOfPlayers];
            this.yPosition = new float[amountOfPlayers];

            for (int i = 0; i < amountOfPlayers; i++) {
                VirtualPlayer player = players.get(i);
                this.names[i] = player.name;
                this.ids[i] = player.id;
                this.xPosition[i] = player.body.getPosition().x;
                this.yPosition[i] = player.body.getPosition().y;
            }
        }

        public int getAmountOfPlayers() {
            return amountOfPlayers;
        }

        @Override
        public String toString() {
            return "RegisteredPlayers{" +
                "amountOfPlayers=" + amountOfPlayers +
                ", names=" + Arrays.toString(names) +
                ", ids=" + Arrays.toString(ids) +
                ", xPosition=" + Arrays.toString(xPosition) +
                ", yPosition=" + Arrays.toString(yPosition) +
                '}';
        }

    }

    static public class PlayerPosition {
        public int id;
        public float x, y;

        public PlayerPosition(){}

        public PlayerPosition(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "PlayerPosition [x=" + x + ", y=" + y + "]";
        }
	}

    static public class DisconnectedPlayer{
        public int id;

        public DisconnectedPlayer(){};
        
        public DisconnectedPlayer(int id){
            this.id = id;
        }

        @Override
        public String toString() {
            return "DisconnectedPlayer [id=" + id + "]";
        }
    }

	static public class PlaceBomb {
		double x, y;
	}


}
