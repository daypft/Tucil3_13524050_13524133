package backend;

import java.util.*;

public class AStar extends Algorithm {

    public AStar() {
        this.heuristic = new H1();
    }

    public AStar(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public Result solve(Board board) {
        Tile goal = board.goal;

        PriorityQueue<Entry> open = new PriorityQueue<>();
        Map<SearchState, Integer> bestG = new HashMap<>();

        SearchState initial = initialState(board);
        open.add(new Entry(heuristic.estimate(initial, board), 0, initial, null, null));
        bestG.put(initial, 0);

        int i = 0;

        while (!open.isEmpty()) {
            Entry cur = open.poll();
            i++;

            System.out.println("Iteration: " + i + " | Pos: (" + cur.state.tile.row + ", " + cur.state.tile.col + ") | lastOrder: " + cur.state.lastOrder + " | gCost: " + cur.gCost + " | Path: " + String.join("", reconstructPath(cur)));

            if (cur.gCost > bestG.getOrDefault(cur.state, Integer.MAX_VALUE)) continue;

            if (cur.state.tile.isGoal() && allPassed(cur.state, board)) {
                return new Result(true, reconstructPath(cur), cur.gCost, i);
            }

            for (String dir : new String[]{"U", "D", "L", "R"}) {
                SlideResult sr = slide(cur.state, dir);
                if (sr == null) continue;

                int newG = cur.gCost + sr.cost;
                if (newG < bestG.getOrDefault(sr.state, Integer.MAX_VALUE)) {
                    bestG.put(sr.state, newG);
                    int f = newG + heuristic.estimate(sr.state, board);
                    open.add(new Entry(f, newG, sr.state, cur, dir));
                }
            }
        }

        return new Result(false, Collections.emptyList(), 0, i);
    }
}
