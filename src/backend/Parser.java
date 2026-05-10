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

        Board board = new Board(row, col);

        // stores tile order yang ada angkanya simbol
        Map<Integer, Tile> orderedTiles = new HashMap<>();

        // 0 -> tiles
        // 1 -> tiles ...

        for (int r = 0; r < row; r++) {
            String mapLine = lines.get(r + 1);

            for (int c = 0; c < col; c++) {
                char symbol = mapLine.charAt(c);
                Tile tile = createTile(r, c, symbol);
                board.tiles[r][c] = tile;

                if (tile.isStart()) {
                    board.start = tile;
                } else if (tile.isGoal()) {
                    board.goal = tile;
                }

                if (Character.isDigit(symbol)) {
                    int order = Character.getNumericValue(symbol);
                    tile.setOrder(order);
                    orderedTiles.put(order, tile);
                    board.maxOrder = Math.max(board.maxOrder, order);
                }
            }
        }

        int startCost = 1 + row;
        for (int r = 0; r < row; r++) {
            String[] cost = lines.get(startCost + r).split("\\s+");

            for (int c = 0; c < col; c++) {
                board.tiles[r][c].setCost(Integer.parseInt(cost[c]));
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
            board.printBoard();
            board.printOrder();
        } catch (Exception e) {
            System.out.println("Error");
        }
    }
}
