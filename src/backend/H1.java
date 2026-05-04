package backend;

/**
 * H1: Manhattan distance melalui semua ordered tile yang belum dilalui, lalu ke goal.
 */
public class H1 implements Heuristic {

    @Override
    public int estimate(Algorithm.SearchState state, Board board) {
        int nextOrderNeeded = state.lastOrder + 1;
        Tile point = state.tile;
        int h = 0;

        while (nextOrderNeeded <= board.maxOrder) {
            Tile target = getOrderTile(nextOrderNeeded, board);
            if (target != null) {
                h += manhattan(point, target);
                point = target;
            }
            nextOrderNeeded++;
        }

        if (board.goal != null) {
            h += manhattan(point, board.goal);
        }

        return h;
    }

    private int manhattan(Tile a, Tile b) {
        return Math.abs(a.row - b.row) + Math.abs(a.col - b.col);
    }

    private Tile getOrderTile(int order, Board board) {
        if (board.orderTiles != null && order < board.orderTiles.length) {
            return board.orderTiles[order];
        }
        Tile cur = board.firstTargetOrder;
        while (cur != null) {
            if (cur.order == order) return cur;
            cur = cur.nextOrder;
        }
        return null;
    }
}
