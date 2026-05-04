package backend;

import java.util.*;

public abstract class Algorithm {

    protected Heuristic heuristic;

    public void setHeuristic(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    protected static class SearchState {
        public final Tile tile;
        public final int lastOrder;

        public SearchState(Tile tile, int lastOrder) {
            this.tile = tile;
            this.lastOrder = lastOrder;
        }

        public SearchState(Tile tile) {
            this(tile, -1);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof SearchState s)) return false;
            return tile.row == s.tile.row && tile.col == s.tile.col && lastOrder == s.lastOrder;
        }

        @Override
        public int hashCode() {
            return Objects.hash(tile.row, tile.col, lastOrder);
        }
    }

    protected static class Entry implements Comparable<Entry> {
        public final int fCost, gCost;
        public final SearchState state;
        public final Entry parent;
        public final String move;

        public Entry(int fCost, int gCost, SearchState state, Entry parent, String move) {
            this.fCost = fCost;
            this.gCost = gCost;
            this.state = state;
            this.parent = parent;
            this.move = move;
        }

        @Override
        public int compareTo(Entry other) {
            return Integer.compare(this.fCost, other.fCost);
        }
    }

    protected static class SlideResult {
        public final SearchState state;
        public final int cost;

        public SlideResult(SearchState state, int cost) {
            this.state = state;
            this.cost = cost;
        }
    }

    public static class Result {
        public final boolean found;
        public final List<String> moves;
        public final int totalCost;
        public final int iterations;

        public Result(boolean found, List<String> moves, int totalCost, int iterations) {
            this.found = found;
            this.moves = moves;
            this.totalCost = totalCost;
            this.iterations = iterations;
        }
    }

    protected SlideResult slide(SearchState from, String dir) {
        Tile current = from.tile;
        int totalCost = 0;
        int lastOrder = from.lastOrder;

        while (true) {
            Tile next = switch (dir) {
                case "U" -> current.nextUp();
                case "D" -> current.nextDown();
                case "L" -> current.nextLeft();
                case "R" -> current.nextRight();
                default  -> null;
            };

            if (next == null) return null;

            if (next.isWall()) {
                if (current == from.tile) return null;
                return new SlideResult(new SearchState(current, lastOrder), totalCost);
            }

            current = next;
            totalCost += current.cost;

            if (current.isLava()) return null;

            if (current.order >= 0) {
                if (current.hasBeenPassed) {
                } else {
                    if (current.order > 0 && !current.hasPassedPrevious()) return null;
                    current.hasBeenPassed = true;
                    lastOrder = Math.max(lastOrder, current.order);
                }
            }
        }
    }

    protected List<String> reconstructPath(Entry entry) {
        LinkedList<String> path = new LinkedList<>();
        Entry cur = entry;
        while (cur.move != null) {
            path.addFirst(cur.move);
            cur = cur.parent;
        }
        return path;
    }

    protected boolean allPassed(SearchState state, Board board) {
        return state.lastOrder == board.maxOrder;
    }

    protected boolean allPassed(Board board) {
        Tile cur = board.firstTargetOrder;
        while (cur != null) {
            if (!cur.hasBeenPassed) return false;
            cur = cur.nextOrder;
        }
        return true;
    }

    protected SearchState initialState(Board board) {
        return new SearchState(board.start);
    }

    public abstract Result solve(Board board);
}
