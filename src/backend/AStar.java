package backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class AStar extends Algorithm {

    private class Node implements Comparable<Node>{
        Tile tile;
        List<Board.Direction> path;
        int cost;
        int h;

        Node(Tile tile, List<Board.Direction> path, int cost, int h) {
            this.tile = tile;
            this.path = path;
            this.cost = cost;
            this.h = h;
        }

        @Override
        public int compareTo(Node other) {
            int thisf = this.cost + this.h;
            int otherf = other.cost + other.h;
            return Integer.compare(thisf, otherf);
        }

    }

    public Result solve(Board board) {
            PriorityQueue<Node> queue = new PriorityQueue<>();
            Set<Tile> visited = new HashSet<>();
            int iterations = 0;
    
            queue.add(new Node(board.start, new ArrayList<>(),0, calculateDistanceToGoal(board.goal, board.start)));
    
            while (!queue.isEmpty()) {
                Node current = queue.poll();
                iterations++;
    
                if (board.isGoalReached(current.tile)) {
                    return new Result(true, current.path, current.cost, iterations);
                }
    
                if (visited.contains(current.tile)) continue;
    
                visited.add(current.tile);
    
                for (Board.Direction dir : Board.Direction.values()) {
                    Tile target = board.tileIfMove(current.tile, dir);
    
                    if (target != null && !visited.contains(target) && target.canEnter()) {
                        if (target.order != -1) {
                            target.hasBeenPassed = true;
                        }
    
                        int moveCost = board.calcCost(current.tile, target, dir);
                        List<Board.Direction> nextPath = new ArrayList<>(current.path);
                        nextPath.add(dir);
                        queue.add(new Node(target, nextPath, current.cost + moveCost, calculateDistanceToGoal(board.goal, target)));
                    System.out.println("Itteration: " + iterations + ", Current Tile: (" + target.row + ", " + target.col + "), Cost: " + (current.cost + moveCost) + ", Path" + nextPath);
                    }
                }
            }
            
        return new Result(false, Collections.emptyList(), 0, iterations);
    }
    public int calculateDistanceToGoal(Tile goal, Tile curr) {
        return Math.abs(goal.row - curr.row) + Math.abs(goal.col - curr.col);
    }
}
