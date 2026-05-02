package backend;

public class Board {
    public int row, col;
    public Tile[][] tiles;
    public Tile[] orderTiles;
    public Tile start;
    public Tile goal;
    public int maxOrder;
    public Tile firstTargetOrder;


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

    public void printOrder(){
        while (firstTargetOrder != null) {
            System.out.println("Order " + firstTargetOrder.order + ": (" + firstTargetOrder.row + ", " + firstTargetOrder.col + ")");
            firstTargetOrder = firstTargetOrder.nextOrder;
        }
    }
}

