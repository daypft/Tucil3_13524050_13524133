package backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class UCS extends Algorithm {
    private class Node implements Comparable<Node> {
        Tile tile;
        List<Board.Direction> path;
        int cost;

        Node(Tile tile, List<Board.Direction> path, int cost) {
            this.tile = tile;
            this.path = path;
            this.cost = cost;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.cost, other.cost);
        }
    }

    public Result solve(Board board) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        Set<Tile> visited = new HashSet<>();
        int iterations = 0;

        queue.add(new Node(board.start, new ArrayList<>(), 0));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            iterations++;

            if (current.tile == board.goal) {
                return new Result(true, current.path, current.cost, iterations);
            }

            if (visited.contains(current.tile)) {
                continue;
            }
            visited.add(current.tile);

            for (Board.Direction dir : Board.Direction.values()) {
                Tile target = board.tileIfMove(current.tile, dir);

                if (target != null && !visited.contains(target) && target.canEnter()) {
                    int moveCost = board.calcCost(current.tile, target, dir);
                    List<Board.Direction> nextPath = new ArrayList<>(current.path);
                    nextPath.add(dir);

                    queue.add(new Node(target, nextPath, current.cost + moveCost));
                    System.out.println("Itteration: " + iterations + ", Current Tile: (" + target.row + ", "
                            + target.col + "), Cost: " + (current.cost + moveCost) + ", Path" + nextPath);
                }
            }
        }

        return new Result(false, Collections.emptyList(), 0, iterations);
    }

}
