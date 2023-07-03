package com.mygdx.game.systems;

import com.mygdx.game.systems.RandomPlacement.Position;
import com.mygdx.game.utils.GameManager;

public class CoordinateConverter {
    public static Position cartesianToMatrix(Position position) {
        int x = position.getX();
        int y = position.getY();

        int matrixX = x;
        int matrixY = y + (GameManager.MAP_HEIGHT - y * 2) - 1;

        return new Position(matrixX, matrixY);
    }

    public static Position matrixToCartesian(Position position) {
        int matrixX = position.getX();
        int matrixY = position.getY();

        int x = matrixX;
        int y =  GameManager.MAP_HEIGHT - matrixY - 1;

        return new Position(x, y);
    }
}
