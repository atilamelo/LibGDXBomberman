package com.mygdx.game.systems;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utils.GameManager;
import com.mygdx.game.utils.WorldUtils;

public class BrickPlacement {

    private static final int MAP_WIDTH = 29;
    private static final int MAP_HEIGHT = 13;

    public static List<Position> generateBrickPositions() {
        List<Position> brickPositions = new ArrayList<>();

        Random random = new Random();

        while (brickPositions.size() < 25) {
            int x = random.nextInt(MAP_WIDTH);
            int y = random.nextInt(MAP_HEIGHT);
            Position position = new Position(x, y);

            if (isValidPosition(position) && !brickPositions.contains(position)) {
                brickPositions.add(position);
            }
        }

        return brickPositions;
    }

    private static boolean isValidPosition(Position position) {
        // Verifica se a posição está dentro dos limites do mapa
        if (position.getX() < 0 || position.getX() >= MAP_WIDTH || position.getY() < 0
                || position.getY() >= MAP_HEIGHT) {
            return false;
        }

        // Verifica se a posição está ocupada por uma parede
        // Substitua essa lógica com a verificação do seu mapa de paredes
        if (isWall(position)) {
            return false;
        }

        return true;
    }

    private static boolean isWall(Position position) {
        Vector2 vectorPosition = new Vector2(position.getX() + .5f, position.getY() + .5f);
        return WorldUtils.hasObjectAtPosition(vectorPosition, GameManager.WALL_BIT);
    }

    public static class Position {
        private int x;
        private int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
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
    }
}
