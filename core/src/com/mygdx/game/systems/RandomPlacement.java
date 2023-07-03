package com.mygdx.game.systems;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

public class RandomPlacement {

    private static final int MAP_WIDTH = 29;
    private static final int MAP_HEIGHT = 13;

    public static List<Position> generateRandomPositions(int quantity, List<Position> invalidPositions) {
        List<Position> brickPositions = new ArrayList<>();

        Random random = new Random();

        while (brickPositions.size() < quantity) {
            int x = random.nextInt(MAP_WIDTH);
            int y = random.nextInt(MAP_HEIGHT);
            Position position = new Position(x, y);

            if (isValidPosition(position, invalidPositions) && !brickPositions.contains(position)) {
                brickPositions.add(position);
            }
        }

        return brickPositions;
    }

    private static boolean isValidPosition(Position position, List<Position> invalidPositions) {
        // Verifica se a posição está dentro dos limites do mapa
        if (position.getX() < 0 || position.getX() >= MAP_WIDTH || position.getY() < 0
                || position.getY() >= MAP_HEIGHT) {
            return false;
        }

        // Verifica se a posição está ocupada por uma parede
        if (alreadyOcupped(position)) {
            return false;
        }

        if(invalidPositions.contains(position)){
            return false;
        }

        return true;
    }final

    private static boolean alreadyOcupped(Position position) {
        boolean ocupped = false; 
        Vector2 vectorPosition = new Vector2(position.getX() + .5f, position.getY() + .5f);

        for(short BIT : GameManager.BITS) {
            if(WorldUtils.hasObjectAtPosition(vectorPosition, BIT)){
                ocupped = true;
                break;
            }
        }
        
        return ocupped;
    }

    public static class Position {
        private int x;
        private int y;
        private boolean ocupped;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position(int x, int y, boolean ocupped) {
            this(x, y);
            this.ocupped = ocupped;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setX(int x){
            this.x = x;
        }

        public void setY(int y){
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Position position = (Position) obj;
            return x == position.x && y == position.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        public boolean isOccuped(){
            return ocupped;
        }

        public boolean setOcuppied(boolean ocupped){
            return this.ocupped = ocupped;
        }

        @Override
        public String toString() {
            return "Position [x=" + x + ", y=" + y + "]";
        }
        
    }
}
