package backend;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Parser {

    public Board parseBoard(String path) throws IOException {
        List<String> lines = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(path))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        }

        if (lines.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String[] size = lines.get(0).split("\\s+");
        if (size.length < 2) {
            throw new IllegalArgumentException("Line Col Missing");
        }

        int row = Integer.parseInt(size[0]);
        int col = Integer.parseInt(size[1]);

        validateSize(row, col);
        validateFileLength(lines, row);

        Board board = new Board(row, col);

        // stores tile order yang ada angkanya simbol
        Map<Integer, Tile> orderedTiles = new HashMap<>();
        int startCount = 0;
        int goalCount = 0;

        // 0 -> tiles
        // 1 -> tiles ...

        for (int r = 0; r < row; r++) {
            String mapLine = lines.get(r + 1);
            validateMapLine(mapLine, col, r);

            for (int c = 0; c < col; c++) {
                char symbol = mapLine.charAt(c);
                Tile tile = createTile(r, c, symbol);
                board.tiles[r][c] = tile;

                if (tile.isStart()) {
                    board.start = tile;
                    startCount++;
                } else if (tile.isGoal()) {
                    board.goal = tile;
                    goalCount++;
                }

                if (Character.isDigit(symbol)) {
                    int order = Character.getNumericValue(symbol);
                    if (orderedTiles.containsKey(order)) {
                        throw new IllegalArgumentException("Duplicate order tile");
                    }
                    tile.setOrder(order);
                    orderedTiles.put(order, tile);
                    board.maxOrder = Math.max(board.maxOrder, order);
                }
            }
        }

        validateTiles(startCount, goalCount);
        validateOrderSequence(orderedTiles, board.maxOrder);

        int startCost = 1 + row;
        for (int r = 0; r < row; r++) {
            String[] cost = lines.get(startCost + r).split("\\s+");
            validateCostLine(cost, col, r);

            for (int c = 0; c < col; c++) {
                try {
                    int parsed = Integer.parseInt(cost[c]);
                    if (parsed < 0) {
                        throw new IllegalArgumentException("Can't negative cost");
                    }
                    board.tiles[r][c].setCost(parsed);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid cost at row " + r + ", col " + c);
                }
            }
        }

        connectNeighbors(board);
        connectOrder(orderedTiles, board);

        return board;
    }

    private Tile createTile(int row, int col, char symbol) {
        Tile.Type type = switch (symbol) {
            case '*', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> Tile.Type.P;
            case 'X' -> Tile.Type.X;
            case 'L' -> Tile.Type.L;
            case 'O' -> Tile.Type.O;
            case 'Z' -> Tile.Type.Z;
            default -> throw new IllegalArgumentException("unknown");
        };

        return new Tile(row, col, type);
    }

    private void validateSize(int row, int col) {
        if (row <= 0 || col <= 0) {
            throw new IllegalArgumentException("Board size invalid");
        }
    }

    private void validateFileLength(List<String> lines, int row) {
        int should = 1 + (2 * row);
        if (lines.size() != should) {
            throw new IllegalArgumentException("Input must contain " + should + " lines");
        }
    }

    private void validateMapLine(String line, int col, int rowIndex) {
        if (line.length() != col) {
            throw new IllegalArgumentException("Map row " + (rowIndex + 1) + " has invalid length");
        }
    }

    private void validateTiles(int start, int goal) {
        if (start != 1) {
            throw new IllegalArgumentException("Board must have 1 start");
        }
        if (goal != 1) {
            throw new IllegalArgumentException("Board must have 1 goal");
        }
    }

    private void validateOrderSequence(Map<Integer, Tile> orderedTiles, int maxOrder) {
        if (orderedTiles.isEmpty()) {
            return;
        }

        for (int order = 0; order <= maxOrder; order++) {
            if (!orderedTiles.containsKey(order)) {
                throw new IllegalArgumentException("Order tiles invalid");
            }
        }
    }

    private void validateCostLine(String[] cost, int col, int rowIndex) {
        if (cost.length != col) {
            throw new IllegalArgumentException("Cost row " + (rowIndex + 1) + " has invalid length");
        }
    }

    private void connectNeighbors(Board board) {
        for (int r = 0; r < board.row; r++) {
            for (int c = 0; c < board.col; c++) {
                Tile tile = board.tiles[r][c];
                tile.setUp(r > 0 ? board.tiles[r - 1][c] : null);
                tile.setDown(r + 1 < board.row ? board.tiles[r + 1][c] : null);
                tile.setLeft(c > 0 ? board.tiles[r][c - 1] : null);
                tile.setRight(c + 1 < board.col ? board.tiles[r][c + 1] : null);
            }
        }
    }

    private void connectOrder(Map<Integer, Tile> orderedTiles, Board board) {
        board.orderTiles = new Tile[board.maxOrder + 1];

        for (Map.Entry<Integer, Tile> entry : orderedTiles.entrySet()) {
            board.orderTiles[entry.getKey()] = entry.getValue();

            if (entry.getKey() == 0) {
                board.firstTargetOrder = entry.getValue();
                board.firstTargetOrder.setNextOrder(orderedTiles.get(1));
                continue;
            }
            int order = entry.getKey();
            Tile tile = entry.getValue();

            tile.setPreviousOrder(orderedTiles.get(order - 1));
            tile.setNextOrder(orderedTiles.get(order + 1));
        }
    }

    public static void main(String[] args) throws IOException {
        try {
            Parser parser = new Parser();
            Board board = parser.parseBoard("../data/1.txt");
            System.out.print(board.printBoard());
            board.printOrder();
        } catch (Exception e) {
            System.out.println("Error");
        }
    }
}
