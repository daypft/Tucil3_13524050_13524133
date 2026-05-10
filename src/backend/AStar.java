package backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class AStar extends Algorithm {
    public enum Heuristic {
        Manhattan,  // Manhattan
        Euclidean,  // Euclidean
        Chebyshev   // Chebyshev   
    }

    private Heuristic heuristic = Heuristic.Manhattan;

    private class Node implements Comparable<Node>{
        Tile tile;
        List<Board.Direction> path;
        int cost;
        int h;
        Board BoardCon;

        Node(Tile tile, List<Board.Direction> path, int cost, int h, Board BoardCon) {
            this.tile = tile;
            this.path = path;
            this.cost = cost;
            this.h = h;
            this.BoardCon = BoardCon;
        }

        @Override
        public int compareTo(Node other) {
            int thisf = this.cost + this.h;
            int otherf = other.cost + other.h;
            return Integer.compare(thisf, otherf);
        }

    }

    public void setHeuristic(Heuristic h) {
        this.heuristic = h;
    }

    @Override
    public Result solve(Board board) {
            long startTime = clearLog();
            clearOutput();
            PriorityQueue<Node> queue = new PriorityQueue<>();
            Set<Board.StateKey> visited = new HashSet<>();
            int iterations = 0;
            Board startBoard = board.copy();
    
            queue.add(new Node(startBoard.start, new ArrayList<>(),0, calculateDistanceToGoal(startBoard.goal, startBoard.start), startBoard));
    
            while (!queue.isEmpty()) {
                Node current = queue.poll();
                iterations++;

                printBoard("Iteration: " + iterations, current.BoardCon, current.tile);
    
                if (current.BoardCon.isGoalReached(current.tile)) {
                    return finishRun(new Result(true, current.path, current.cost, iterations), startTime);
                }
    
                Board.StateKey currentKey = current.BoardCon.stateKey(current.tile);
                if (visited.contains(currentKey)) continue;
    
                visited.add(currentKey);
    
                for (Board.Direction dir : Board.Direction.values()) {
                    Board nextBoard = current.BoardCon.copy();
                    Tile nextStart = nextBoard.getTile(current.tile.row, current.tile.col);
                    Tile target = nextBoard.tileIfMove(nextStart, dir);
    
                    if (target != null && !visited.contains(nextBoard.stateKey(target))) {
    
                        int moveCost = nextBoard.calcCost(nextStart, target, dir);
                        List<Board.Direction> nextPath = new ArrayList<>(current.path);
                        nextPath.add(dir);
                        queue.add(new Node(target, nextPath, current.cost + moveCost, calculateDistanceToGoal(nextBoard.goal, target), nextBoard));
                        log("Itteration: " + iterations + ", Current Tile: (" + target.row + ", " + target.col + "), Cost: " + (current.cost + moveCost) + ", Path" + nextPath);
                    }
                }
            }
            
        return finishRun(new Result(false, Collections.emptyList(), 0, iterations), startTime);
    }

    private int calculateDistanceToGoal(Tile goal, Tile curr) {
        return switch (heuristic) {
            case Manhattan -> Manhattan(goal, curr);
            case Euclidean -> Euclidean(goal, curr);
            case Chebyshev -> Chebyshev(goal, curr);
        };
    }

    private int Manhattan(Tile goal, Tile curr) {
        return Math.abs(goal.row - curr.row) + Math.abs(goal.col - curr.col);
    }

    private int Euclidean(Tile goal, Tile curr) {
        int dr = goal.row - curr.row;
        int dc = goal.col - curr.col;
        return (int) Math.sqrt(dr * dr + dc * dc);
    }

    private int Chebyshev(Tile goal, Tile curr) {
        return Math.max(Math.abs(goal.row - curr.row), Math.abs(goal.col - curr.col));
    }
}
