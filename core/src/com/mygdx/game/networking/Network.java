package com.mygdx.game.networking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.game.actors.Brick;
import com.mygdx.game.configs.EnemyConfig;
import com.mygdx.game.enums.UserDataType;
import com.mygdx.game.systems.RandomPlacement;
import com.mygdx.game.systems.RandomPlacement.Position;

public class Network {
    static public final int tcpPort = 54552;
    static public final int udpPort = 54777;

    static public void register(EndPoint endpoint) {
        Kryo kryo = endpoint.getKryo();
        kryo.register(Packet.class);
        kryo.register(RegisterPlayer.class);
        kryo.register(RegisteredPlayers.class);
        kryo.register(RegisteredEnemies.class);
        kryo.register(UUID.class, new UUIDSerializer(kryo));
        kryo.register(PlayerPosition.class);
        kryo.register(EnemyPosition.class);
        kryo.register(DisconnectedPlayer.class);
        kryo.register(RandomPlacement.Position.class);
        kryo.register(PlaceBomb.class);
        kryo.register(BrickPositions.class);
        kryo.register(BombermanDie.class);
        kryo.register(UserDataType.class);
        kryo.register(EnemyConfig.class);
        kryo.register(int[].class);
        kryo.register(short[].class);
        kryo.register(UUID[].class);
        kryo.register(float[].class);
        kryo.register(String[].class);
        kryo.register(EnemyConfig[].class);
        kryo.register(Position[].class);

    }

    public static class Packet {
        public int id;

        public Packet() {}
    }

    public static class RegisterPlayer extends Packet {
        public String name;

        public RegisterPlayer() {}

        public RegisterPlayer(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "RegisterPlayer [name=" + name + ", id=" + id + "]";
        }
    }

    public static class RegisteredPlayers extends Packet {
        public String[] names;
        public int[] ids;
        public Position[] positions;
        public int amountOfPlayers;

        public RegisteredPlayers() {}

        public RegisteredPlayers(List<VirtualPlayer> players) {
            this.amountOfPlayers = players.size();
            this.names = new String[amountOfPlayers];
            this.ids = new int[amountOfPlayers];
            this.positions = new Position[amountOfPlayers];

            for (int i = 0; i < amountOfPlayers; i++) {
                VirtualPlayer player = players.get(i);
                this.names[i] = player.name;
                this.ids[i] = player.id;
                this.positions[i] = new Position((int) player.body.getPosition().x, (int) player.body.getPosition().y);
            }
        }

        public int getAmountOfPlayers() {
            return amountOfPlayers;
        }

        @Override
        public String toString() {
            return "RegisteredPlayers [names=" + Arrays.toString(names) + ", ids=" + Arrays.toString(ids)
                    + ", positions=" + Arrays.toString(positions) + ", amountOfPlayers=" + amountOfPlayers + "]";
        }

    }

    public static class RegisteredEnemies extends Packet{
        public UUID[] ids;
        public EnemyConfig[] configs;
        public Position[] positions;
        public int amountOfEnemies;

        public RegisteredEnemies() {}

        public RegisteredEnemies(List<VirtualEnemy> enemies) {
            this.amountOfEnemies = enemies.size();
            this.ids = new UUID[amountOfEnemies];
            this.configs = new EnemyConfig[amountOfEnemies];
            this.positions = new Position[amountOfEnemies];

            for (int i = 0; i < amountOfEnemies; i++) {
                VirtualEnemy enemy = enemies.get(i);
                this.ids[i] = enemy.id;
                this.configs[i] = enemy.config;
                this.positions[i] = new Position((int) enemy.body.getPosition().x, (int) enemy.body.getPosition().y);
            }
        }

        @Override
        public String toString() {
            return "RegisteredEnemies [ids=" + Arrays.toString(ids) + ", configs=" + Arrays.toString(configs)
                    + ", positions=" + Arrays.toString(positions) + ", amountOfEnemies=" + amountOfEnemies + "]";
        }
    }

    static public class PlayerPosition extends Packet {
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

    static public class EnemyPosition extends Packet {
        public UUID id;
        public float x, y;

        public EnemyPosition(){}

        public EnemyPosition(UUID id, float x, float y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "EnemyPosition [id=" + id + ", x=" + x + ", y=" + y + "]";
        }
    }

    static public class DisconnectedPlayer extends Packet {
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

    static public class BrickPositions extends Packet {
        public int[] x;
        public int[] y;
        public int amountOfBricks;

        public BrickPositions(){}

        public BrickPositions(Map<Position, Brick> bricks){
            this.amountOfBricks = bricks.size();
            this.x = new int[amountOfBricks];
            this.y = new int[amountOfBricks];

            int i = 0;
            for (Position position : bricks.keySet()) {
                this.x[i] = position.getX();
                this.y[i] = position.getY();
                i++;
            }
        }

        @Override
        public String toString() {
            return "bricksPosition{" +
                "amountOfBricks=" + amountOfBricks +
                ", x=" + Arrays.toString(x) +
                ", y=" + Arrays.toString(y) +
                '}';
        }
    }

	static public class PlaceBomb extends Packet {
        public Position position;
        public int range;

        public PlaceBomb(){}

        public PlaceBomb(int x, int y, int range) {
            this.position = new Position(x, y);
            this.range = range;
        }

        @Override
        public String toString() {
            return "PlaceBomb [position=" + position + ", range=" + range + "]";
        }
        
	}

    static public class BombermanDie extends Packet {
        public int id; // What bomberman died
        public UserDataType type; // What killed him
        
        public BombermanDie(){}

        public BombermanDie(int id, UserDataType type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public String toString() {
            return "BombermanDie [id=" + id + "]";
        }
    }


}
