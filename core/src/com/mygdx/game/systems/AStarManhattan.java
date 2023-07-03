package com.mygdx.game.systems;

import java.util.*;

import com.mygdx.game.systems.RandomPlacement.Position;

public class AStarManhattan {

    public static List<Position> findPath(Position start, Position end, List<List<Position>> grid) {
        if (start.isOccuped() || end.isOccuped()) {
            return null;
        }

        List<Node> openList = new ArrayList<>();
        List<Node> closedList = new ArrayList<>();

        Map<Position, Node> positionToNodeMap = new HashMap<>();

        Node startNode = new Node(start, null, 0, calculateManhattanDistance(start, end));
        openList.add(startNode);
        positionToNodeMap.put(start, startNode);

        while (!openList.isEmpty()) {
            Node currentNode = findLowestFCostNode(openList);
            openList.remove(currentNode);
            closedList.add(currentNode);

            if (currentNode.getPosition().equals(end)) {
                return reconstructPath(currentNode);
            }

            List<Node> neighbors = generateNeighbors(currentNode, end, grid, positionToNodeMap);

            for (Node neighbor : neighbors) {
                if (closedList.contains(neighbor)) {
                    continue;
                }

                float tentativeGCost = currentNode.getGCost()
                        + calculateManhattanDistance(currentNode.getPosition(), neighbor.getPosition());

                if (!openList.contains(neighbor)) {
                    openList.add(neighbor);
                    positionToNodeMap.put(neighbor.getPosition(), neighbor);
                } else if (tentativeGCost >= neighbor.getGCost()) {
                    continue;
                }

                neighbor.setParent(currentNode);
                neighbor.setGCost(tentativeGCost);
                neighbor.setFCost(neighbor.getGCost() + neighbor.getHCost());
            }
        }

        // Não foi encontrado um caminho válido
        return null;
    }

    private static Node findLowestFCostNode(List<Node> openList) {
        Node lowestFCostNode = openList.get(0);
        for (Node node : openList) {
            if (node.getFCost() < lowestFCostNode.getFCost()) {
                lowestFCostNode = node;
            }
        }
        return lowestFCostNode;
    }

    private static List<Node> generateNeighbors(Node currentNode, Position end, List<List<Position>> grid,
            Map<Position, Node> positionToNodeMap) {
        List<Node> neighbors = new ArrayList<>();
        Position currentPosition = currentNode.getPosition();
        int x = currentPosition.getX();
        int y = currentPosition.getY();

        addNeighbor(neighbors, x, y - 1, end, grid, positionToNodeMap); // Acima
        addNeighbor(neighbors, x, y + 1, end, grid, positionToNodeMap); // Abaixo
        addNeighbor(neighbors, x - 1, y, end, grid, positionToNodeMap); // Esquerda
        addNeighbor(neighbors, x + 1, y, end, grid, positionToNodeMap); // Direita

        return neighbors;
    }

    private static void addNeighbor(List<Node> neighbors, int x, int y, Position end, List<List<Position>> grid,
            Map<Position, Node> positionToNodeMap) {
        if (isValidPosition(x, y, grid)) {
            Position neighborPosition = grid.get(y).get(x);

            if (!neighborPosition.isOccuped()) {
                Node neighbor = positionToNodeMap.get(neighborPosition);
                if (neighbor == null) {
                    neighbor = new Node(neighborPosition, null, 0, calculateManhattanDistance(neighborPosition, end));
                    positionToNodeMap.put(neighborPosition, neighbor);
                }
                neighbors.add(neighbor);
            }
        }
    }

    private static boolean isValidPosition(int x, int y, List<List<Position>> grid) {
        return x >= 0 && x < grid.get(0).size() && y >= 0 && y < grid.size();
    }

    private static List<Position> reconstructPath(Node endNode) {
        List<Position> path = new ArrayList<>();
        Node currentNode = endNode;

        while (currentNode != null) {
            path.add(currentNode.getPosition());
            currentNode = currentNode.getParent();
        }

        Collections.reverse(path);
        return path;
    }

    private static float calculateManhattanDistance(Position position1, Position position2) {
        int dx = Math.abs(position1.getX() - position2.getX());
        int dy = Math.abs(position1.getY() - position2.getY());
        return dx + dy;
    }

    private static class Node {
        private Position position;
        private Node parent;
        private float gCost;
        private float hCost;
        private float fCost;

        public Node(Position position, Node parent, float gCost, float hCost) {
            this.position = position;
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;
        }

        public Position getPosition() {
            return position;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public float getGCost() {
            return gCost;
        }

        public void setGCost(float gCost) {
            this.gCost = gCost;
            this.fCost = gCost + hCost;
        }

        public float getHCost() {
            return hCost;
        }

        public float getFCost() {
            return fCost;
        }

        public void setFCost(float fCost) {
            this.fCost = fCost;
        }
    }

    public static void printBoardWithPath(List<List<Position>> grid, List<Position> path) {
        int sizeX = grid.get(0).size();
        int sizeY = grid.size();

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                Position position = grid.get(y).get(x);
                if (position.isOccuped()) {
                    System.out.print("X ");
                } else if (path.contains(position)) {
                    System.out.print("* ");
                } else {
                    System.out.print("- ");
                }
            }
            System.out.println();
        }
    }

    public static void printBoard(List<List<Position>> grid) {
        int sizeX = grid.get(0).size();
        int sizeY = grid.size();

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                Position position = grid.get(y).get(x);
                if (position.isOccuped()) {
                    System.out.print("X ");
                } else {
                    System.out.print("- ");
                }
            }
            System.out.println();
        }
    }
}
