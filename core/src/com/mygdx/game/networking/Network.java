package com.mygdx.game.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
    static public final int tcpPort = 54555;
    static public final int udpPort = 54777;

    static public void register(EndPoint endpoint) {
        Kryo kryo = endpoint.getKryo();
        kryo.register(RegisterPlayer.class);
        kryo.register(PlayerPosition.class);
        kryo.register(PlaceBomb.class);
    }

    public static class RegisterPlayer {
        public String name; 

        public RegisterPlayer(){}

        public RegisterPlayer(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "RegisterPlayer [name=" + name + "]";
        }
    }

    static public class PlayerPosition {
        float x, y;

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

	static public class PlaceBomb {
		double x, y;
	}


}
