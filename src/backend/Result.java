package backend;

import java.util.List;

import backend.Board.Direction;

public class Result {
    public final boolean found;
    public final List<Direction> moves;
    public final int totalCost;
    public final int iterations;

    public Result(boolean found, List<Direction> moves, int totalCost, int iterations) {
        this.found = found;
        this.moves = moves;
        this.totalCost = totalCost;
        this.iterations = iterations;
    }
}
