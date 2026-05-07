package backend;

public class Board {
    public int row, col;
    public Tile[][] tiles;
    public Tile[] orderTiles;
    public Tile start;
    public Tile goal;
    public int maxOrder;
    public Tile firstTargetOrder;
    public Tile currentTile;

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public Board(int row, int col) {
        this.row = row;
        this.col = col;
        this.tiles = new Tile[row][col];
    }

    public Tile getTile(int row, int col) {
        return tiles[row][col];
    }

    public Board copy() {
        Board copy = new Board(row, col);

        copy.maxOrder = maxOrder;

        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                Tile oldTile = tiles[r][c];
                Tile newTile = new Tile(r, c, oldTile.type);

                newTile.order = oldTile.order;
                newTile.cost = oldTile.cost;
                newTile.hasBeenPassed = oldTile.hasBeenPassed;

                copy.tiles[r][c] = newTile;

                if (oldTile == start) {
                    copy.start = newTile;
                }
                if (oldTile == goal) {
                    copy.goal = newTile;
                }
                if (oldTile == currentTile) {
                    copy.currentTile = newTile;
                }
            }
        }

        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                Tile tile = copy.tiles[r][c];
                tile.setUp(r > 0 ? copy.tiles[r - 1][c] : null);
                tile.setDown(r + 1 < row ? copy.tiles[r + 1][c] : null);
                tile.setLeft(c > 0 ? copy.tiles[r][c - 1] : null);
                tile.setRight(c + 1 < col ? copy.tiles[r][c + 1] : null);
            }
        }

        Tile[] orderedTiles = new Tile[maxOrder + 1];
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                Tile tile = copy.tiles[r][c];
                if (tile.order >= 0 && tile.order < orderedTiles.length) {
                    orderedTiles[tile.order] = tile;
                }
            }
        }

        copy.orderTiles = orderedTiles;
        copy.firstTargetOrder = orderedTiles.length > 0 ? orderedTiles[0] : null;

        for (int i = 0; i < orderedTiles.length; i++) {
            if (orderedTiles[i] == null) {
                continue;
            }

            orderedTiles[i].setPreviousOrder(i > 0 ? orderedTiles[i - 1] : null);
            orderedTiles[i].setNextOrder(i + 1 < orderedTiles.length ? orderedTiles[i + 1] : null);
        }

        return copy;
    }

    public void printBoard() {
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                Tile tile = tiles[r][c];
                char symbol = switch (tile.type) {
                    case P -> 'P';
                    case X -> 'X';
                    case L -> 'L';
                    case O -> 'O';
                    case Z -> 'Z';
                };
                System.out.print(symbol);
            }
            System.out.println();
        }
    }

    public Tile tileIfMove(Tile startTile, Direction dir) {
        Tile temp = startTile;

        while (temp != null) {
            Tile nextTile = nextTile(temp, dir);

            if (nextTile == null) {
                return null;
            }
            if (nextTile.isWall()) {
                break;
            }

            if (nextTile.isLava()) {
                return null;
            }
            if (!nextTile.canEnter()) {
                return null;
            }
            if (nextTile.order != -1) {
                nextTile.hasBeenPassed = true;
            }
            temp = nextTile;
        }
        return temp;
    }

    public int calcCost(Tile startTile, Tile endTile, Direction dir) {
        int cost = 0;
        Tile temp = startTile;

        while (temp != endTile) {
            temp = nextTile(temp, dir);
            cost += temp.cost;
        }
        return cost;
    }

    private Tile nextTile(Tile tile, Direction dir) {
        return switch (dir) {
            case UP -> tile.up;
            case DOWN -> tile.down;
            case LEFT -> tile.left;
            case RIGHT -> tile.right;
        };
    }

    public void printOrder() {
        while (firstTargetOrder != null) {
            System.out.println("Order " + firstTargetOrder.order + ": (" + firstTargetOrder.row + ", "
                    + firstTargetOrder.col + ")");
            firstTargetOrder = firstTargetOrder.nextOrder;
        }
    }

    public boolean isGoalReached(Tile currentTile) {
        if (currentTile != goal) {
            return false;
        }

        return hasPassedOrders();
    }

    public boolean hasPassedOrders() {
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                Tile tile = tiles[r][c];
                if (tile.order != -1 && !tile.hasBeenPassed) {
                    return false;
                }
            }
        }

        return true;
    }

    public String stateKey(Tile currentTile) {
        StringBuilder key = new StringBuilder();
        key.append(currentTile.row).append(',').append(currentTile.col).append(':');

        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                Tile tile = tiles[r][c];
                if (tile.order != -1) {
                    key.append(tile.order).append(tile.hasBeenPassed ? '1' : '0');
                }
            }
        }

        return key.toString();
    }
}
