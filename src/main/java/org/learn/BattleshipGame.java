package org.learn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BattleshipGame {

    @Getter
    @Setter
    public static class Ship {
        char ship;
        int size;
        int x;
        int y;
        public Ship(char ship, int x, int y, int size) {
            this.ship = ship;
            this.x = x;
            this.y = y;
            this.size = size;
        }
    }

    private int dimension;
    private char[][] boardA;
    private char[][] boardB;

    private List<Ship> shipA;
    private List<Ship> shipB;

    private Set<String> regionA;
    private Set<String> regionB;

    private Set<String> firedCoordinates;
    private Random random;
    private boolean initialise;

    public BattleshipGame() {
        this.dimension = 0;
        this.initialise = false;
        this.random = new Random();
        this.shipA = new ArrayList<>();
        this.shipB = new ArrayList<>();
        this.regionA = new HashSet<>();
        this.regionB = new HashSet<>();
        this.firedCoordinates = new HashSet<>();
    }


    public void initGame(int dimension) {

        this.dimension = dimension;
        this.boardA = new char[dimension][dimension];
        this.boardB = new char[dimension][dimension];

        for(int i=0; i < dimension; i++) {
            Arrays.fill(boardA[i], '.');
            Arrays.fill(boardB[i], '.');
        }

        shipA.clear();
        shipB.clear();
        regionA.clear();
        regionB.clear();
        firedCoordinates.clear();

        int mid = dimension / 2;
        for(int x = 0; x < mid; x++) {
            for(int y=0; y < dimension; y++) {
                regionA.add(x + "," + y);
            }
        }
        for(int x = mid; x < dimension; x++) {
            for(int y=0; y < dimension; y++) {
                regionB.add(x + "," + y);
            }
        }

        this.initialise = true;
        System.out.println(" Game initialised ");
    }

    // addShip(id, size, x position PlayerA, y position PlayerA, x position PlayerB, y position
    //PlayerB)
    public void addShip(char shipChar, int size, int x1, int y1, int x2, int y2) {

        Ship a = new Ship(shipChar, x1, y1, size);
        Ship b = new Ship(shipChar, x2, y2, size);

        placeShipOnBoard(boardA, regionA, a);
        placeShipOnBoard(boardB, regionB, b);

        shipA.add(a);
        shipB.add(b);

        //System.out.println(" Added ship for a and b --- ");
    }

    private void placeShipOnBoard(char[][] board, Set<String> region, Ship ship) {
        int mid = ship.size / 2;
        int toLeftX = ship.getX() - mid;
        int toLeftY = ship.getY() - mid;

        int bottomX = ship.getX() + mid -1;
        int bottomY = ship.getY() + mid -1;

        for(int x = toLeftX; x <= bottomX; x++) {
            for(int y= toLeftY; y <= bottomY; y++) {

                if (!region.contains(x + "," + y)) {
                    throw new IllegalArgumentException(" Ship out of valid range position");
                }
                if (board[y][x] != '.') {
                    throw new IllegalArgumentException(" Overlapping of ship with exsiting ship");
                } else {
                    board[y][x] = ship.getShip();
                }
            }
        }
    }


    public void viewBattleField() {
        System.out.println(" Battle field of player A of left and player B on right");
        for(int x = 0; x < dimension; x++) {
            for(int y=0; y < dimension; y++) {
                System.out.print(boardA[x][y] + " ");
            }
            System.out.print(" | ");
            for(int y=0; y < dimension; y++) {
                System.out.print(boardB[x][y] + " ");
            }
            System.out.println();
        }
    }

    public void startGame() {
        if (shipA.isEmpty() || shipB.isEmpty()) {
            System.out.println(" At least one ship should be present");
            return;
        }

        boolean playerA = true;

        while(true) {

            List<Ship> activeShips = playerA ? shipA : shipB;
            List<Ship> enemyShips = playerA ? shipB : shipA;
            Set<String> enemyRegion = playerA ? regionB : regionA;
            char[][] enemyBoard = playerA ? boardB : boardA;
            String currentPlayer = playerA ? "Player A" : "Player B";

            List<String> possibleTargets = new ArrayList<>();
            for(String str : enemyRegion) {
                if (!firedCoordinates.contains(str)) {
                    possibleTargets.add(str);
                }
            }

            if (possibleTargets.isEmpty()) {
                System.out.println(" No more targets left, its a draw");
                return;
            }

            String target = possibleTargets.get(random.nextInt(possibleTargets.size()));
            firedCoordinates.add(target);

            String[] parts = target.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);

            char hit = enemyBoard[x][y];
            if (hit != '.') {
                removeShipByChar(enemyShips, hit, enemyBoard);
                System.out.println(currentPlayer + "'s turn: "
                    + "Fired at (" + x + ", " + y + ")=> HIT'" + hit + "'destroyed'"
                        + " Remaining - Player A: " + shipA.size() + " 'Player B: " + shipB.size());
            } else {

                System.out.println(currentPlayer + "'s turn: "
                        + "Fired at (" + x + ", " + y + ")=> Miss'"
                        + " Remaining - Player A: " + shipA.size() + " 'Player B: " + shipB.size());
            }

            if (enemyShips.isEmpty()) {
                viewBattleField();
                System.out.println(" Game over! Winner is " + currentPlayer);
                return;
            }
            playerA = !playerA;
            viewBattleField();
        }
    }

    private void removeShipByChar(List<Ship> ships, char shipChar, char[][] board) {
        for(int x =0; x < dimension; x++) {
            for(int y=0; y < dimension; y++) {
                if (board[x][y] == shipChar) {
                    board[x][y] = 'X';
                }
            }
        }
        ships.removeIf(ship -> ship.getShip() == shipChar);
    }
}
