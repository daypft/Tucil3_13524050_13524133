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

            if (nextTile == null || nextTile.isWall()) {
                break;
            }

            if (nextTile.isLava()) {
                return null;
            }
            temp = nextTile;
        }
        return temp;
    }

    public int calcCost(Tile startTile, Tile endTile, Direction dir) {
        int cost = 0;
        Tile temp = startTile;

        while (temp != endTile) {
            cost += temp.cost;
            temp = nextTile(temp, dir);
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

    public void printOrder(){
        while (firstTargetOrder != null) {
            System.out.println("Order " + firstTargetOrder.order + ": (" + firstTargetOrder.row + ", " + firstTargetOrder.col + ")");
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
        if (firstTargetOrder == null) {
            return true;
        }
        
        Tile cp = firstTargetOrder;
        while (cp.nextOrder != null) {
            cp = cp.nextOrder;
        }
    
        return cp.hasBeenPassed;
    }
}

